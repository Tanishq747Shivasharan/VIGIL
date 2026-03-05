package com.vigil.security.security;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.vigil.security.models.LanDevice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LanScanner {
    private static final String TAG = "LanScanner";
    private static final int THREAD_POOL_SIZE = 50;
    private static final int PING_TIMEOUT_MS = 1000;

    private final Context context;

    private volatile boolean isCancelled = false;

    public interface ScanCallback {
        void onDeviceFound(LanDevice device);

        void onScanComplete(List<LanDevice> allDevices);

        void onProgress(int scanned, int total);

        void onError(String errorMessage);
    }

    public LanScanner(Context context) {
        this.context = context.getApplicationContext();
    }

    public void startScan(ScanCallback callback) {
        isCancelled = false;

        new Thread(() -> {
            try {
                String myIp = getLocalIpAddress();
                if (myIp == null || myIp.equals("0.0.0.0")) {
                    callback.onError("Not connected to WiFi");
                    return; // Exit the thread
                }

                String subnet = getSubnet(myIp);
                Log.d(TAG, "Scanning subnet: " + subnet);

                String gatewayIp = subnet + "1";

                List<LanDevice> foundDevices = Collections.synchronizedList(new ArrayList<>());

                ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

                AtomicInteger scannedCount = new AtomicInteger(0);
                int totalHosts = 254;

                for (int i = 1; i <= 254; i++) {
                    if (isCancelled) break;

                    final String targetIp = subnet + i;
                    final boolean isGateway = targetIp.equals(gatewayIp);
                    final boolean isMe = targetIp.equals(myIp);

                    executor.submit(() -> {
                        try {
                            boolean reachable = pingHost(targetIp);

                            String mac = getMacFromArp(targetIp);
                            boolean arpKnows = !mac.equals("Unknown");

                            if (reachable || arpKnows || isMe) {
                                LanDevice device = new LanDevice(targetIp);
                                device.setOnline(reachable || isMe);
                                device.setMacAddress(mac);
                                device.setGateway(isGateway);
                                device.setCurrentDevice(isMe);

                                String hostname = resolveHostname(targetIp);
                                device.setHostname(hostname);

                                String vendor = getVendorFromMac(mac);
                                device.setVendor(vendor);

                                foundDevices.add(device);

                                callback.onDeviceFound(device);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error scanning " + targetIp + ": " + e.getMessage());
                        }

                        int done = scannedCount.incrementAndGet();
                        callback.onProgress(done, totalHosts);
                    });
                }

                executor.shutdown();

                boolean finished = executor.awaitTermination(60, TimeUnit.SECONDS);

                if (!isCancelled) {
                    callback.onScanComplete(foundDevices);
                }

            } catch (InterruptedException e) {
                Log.e(TAG, "Scan interrupted: " + e.getMessage());
                callback.onError("Scan was interrupted");
            } catch (Exception e) {
                Log.e(TAG, "Scan error: " + e.getMessage());
                callback.onError("Scan failed: " + e.getMessage());
            }

        }).start();
    }

    public void cancelScan() {
        isCancelled = true;
    }
    private String getLocalIpAddress() {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) return null;

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipInt = wifiInfo.getIpAddress();

            // Formatter.formatIpAddress converts the int to "192.168.1.101"
            // It handles the little-endian byte reversal automatically
            return android.text.format.Formatter.formatIpAddress(ipInt);
        } catch (Exception e) {
            Log.e(TAG, "Failed to get local IP: " + e.getMessage());
            return null;
        }
    }

    private String getSubnet(String ipAddress) {
        int lastDot = ipAddress.lastIndexOf('.');
        return ipAddress.substring(0, lastDot + 1);
    }

    private boolean pingHost(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.isReachable(PING_TIMEOUT_MS);
        } catch (IOException e) {
            return false; // Unreachable or error = treat as offline
        }
    }

    /**
     * getMacFromArp()
     *
     * Reads the ARP cache from the Linux kernel's /proc/net/arp file.
     *
     * What is ARP?
     *   When your phone sends data to 192.168.1.5, it first broadcasts:
     *   "Hey everyone! Who has IP 192.168.1.5? Reply with your MAC address."
     *   The device replies, and the kernel stores: IP → MAC in the ARP table.
     *   This file persists as long as the device is reachable.
     *
     * The file looks like this:
     *   IP address       HW type  Flags  HW address          Mask  Device
     *   192.168.1.1      0x1      0x2    a4:c3:f0:12:34:56   *     wlan0
     *   192.168.1.5      0x1      0x2    b8:27:eb:aa:bb:cc   *     wlan0
     *
     * We read each line, split by spaces, and match the IP in column 0.
     * If found, column 3 is the MAC address.
     */
    private String getMacFromArp(String ip) {
        try {
            // BufferedReader reads text files line by line efficiently
            BufferedReader reader = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;

            // reader.readLine() returns null at end of file
            while ((line = reader.readLine()) != null) {
                // Split by one or more whitespace characters (\s+)
                // "192.168.1.1  0x1  0x2  aa:bb:cc..." → ["192.168.1.1","0x1","0x2","aa:bb:cc..."]
                String[] parts = line.trim().split("\\s+");

                // We need at least 4 columns (index 0=IP, 3=MAC)
                if (parts.length >= 4 && parts[0].equals(ip)) {
                    String mac = parts[3];
                    reader.close();
                    // Validate MAC format: should look like "aa:bb:cc:dd:ee:ff"
                    if (mac.matches("([0-9a-fA-F]{2}:){5}[0-9a-fA-F]{2}")) {
                        return mac.toUpperCase();
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e(TAG, "ARP read error: " + e.getMessage());
        }
        return "Unknown";
    }

    private String resolveHostname(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            String hostname = address.getCanonicalHostName();
            // If resolution failed, it just returns the IP — not useful
            return hostname.equals(ip) ? "" : hostname;
        } catch (Exception e) {
            return "";
        }
    }

    public String getVendorFromMac(String mac) {
        if (mac == null || mac.equals("Unknown") || mac.length() < 8) {
            return "Unknown";
        }

        // Extract first 3 octets and normalize to uppercase
        String oui = mac.substring(0, 8).toUpperCase();

        // OUI lookup table — common device manufacturers
        switch (oui) {
            // Apple
            case "A4:C3:F0": case "F0:18:98": case "BC:92:6B":
            case "3C:06:30": case "AC:BC:32": case "F4:F1:5A":
                return "Apple Inc.";

            // Samsung
            case "8C:F5:A3": case "40:0E:85": case "54:88:0E":
            case "B0:72:BF": case "E8:03:9A":
                return "Samsung Electronics";

            // Google
            case "54:60:09": case "F4:F5:E8": case "3C:5A:B4":
                return "Google LLC";

            // Xiaomi
            case "64:09:80": case "28:6C:07": case "AC:F7:F3":
                return "Xiaomi Communications";

            // OnePlus
            case "AC:AB:59": case "94:65:2D":
                return "OnePlus Technology";

            // Raspberry Pi
            case "B8:27:EB": case "DC:A6:32": case "E4:5F:01":
                return "Raspberry Pi Foundation";

            // Routers
            case "C8:3A:35": case "E8:94:F6":
                return "Tenda Technology";
            case "10:BF:48": case "CC:40:D0":
                return "TP-Link Technologies";
            case "A0:F3:C1": case "C0:3E:BA":
                return "Netgear";
            case "68:7F:74": case "20:E5:2A":
                return "D-Link Corporation";
            case "04:A1:51": case "50:C7:BF":
                return "Huawei Technologies";

            // Amazon
            case "40:B4:CD": case "74:C2:46": case "A4:08:01":
                return "Amazon Technologies";

            // Intel (common in laptops)
            case "8C:8D:28": case "A4:C3:F1": case "00:1B:21":
                return "Intel Corporate";

            default:
                return "Unknown Vendor";
        }
    }
}
