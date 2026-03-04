package com.vigil.security.security;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

public class WifiSecurityAnalyzer {
    private WifiManager wifiManager;

    public WifiSecurityAnalyzer(Context context) {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    // Returns the current Wi-Fi SSID
    public String getCurrentSSID() {
        if (wifiManager == null) return "Unknown";
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        if (ssid != null && !ssid.equals("<unknown ssid>")) {
            return ssid.replace("\"", "");
        }
        return "Not Connected";
    }

    public String getSecurityType() {
        if (wifiManager == null) return "UNKNOWN";

        wifiManager.startScan();
        List<ScanResult> scanResults;
        try {
            scanResults = wifiManager.getScanResults();
        } catch (SecurityException e) {
            return "PERMISSION_DENIED";
        }

        if (scanResults == null || scanResults.isEmpty()) return "UNKNOWN";

        String currentSSID = getCurrentSSID();
        for (ScanResult result : scanResults) {
            if (result.SSID.equals(currentSSID)) {
                String capabilities = result.capabilities;
                if (capabilities.contains("WEP")) return "WEP";
                else if (capabilities.contains("WPA3")) return "WPA3";
                else if (capabilities.contains("WPA2")) return "WPA2";
                else if (capabilities.contains("WPA")) return "WPA";
                else return "OPEN";
            }
        }
        return "UNKNOWN";
    }

    public String getRiskLevel(String securityType) {
        switch (securityType) {
            case "OPEN":
            case "WEP":
                return "HIGH";
            case "WPA":
                return "MEDIUM";
            case "WPA2":
            case "WPA3":
                return "SAFE";
            default:
                return "UNKNOWN";
        }
    }

    public int getRiskScore(String riskLevel) {
        switch (riskLevel) {
            case "HIGH": return 85;
            case "MEDIUM": return 45;
            case "SAFE": return 10;
            default: return 0;
        }
    }

    public String getSignalStrength() {
        if (wifiManager == null) return "Unknown";
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int rssi = wifiInfo.getRssi();
        int level = WifiManager.calculateSignalLevel(rssi, 5);
        switch (level) {
            case 4: return "Excellent";
            case 3: return "Good";
            case 2: return "Fair";
            case 1: return "Weak";
            default: return "Very Weak";
        }
    }

    public String getFrequency() {
        if (wifiManager == null) return "Unknown";
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int freq = wifiInfo.getFrequency();
        if (freq > 4900 && freq < 5900) return "5.0 GHz";
        if (freq > 2400 && freq < 2500) return "2.4 GHz";
        return freq + " MHz";
    }

    public String getEncryption(String securityType) {
        switch (securityType) {
            case "WPA3": return "SAE";
            case "WPA2": return "AES/CCMP";
            case "WPA": return "TKIP";
            case "WEP": return "WEP";
            case "OPEN": return "None";
            default: return "Unknown";
        }
    }
}