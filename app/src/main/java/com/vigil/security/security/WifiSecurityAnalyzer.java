package com.vigil.security.security;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import  android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.core.app.ActivityCompat;

import java.util.List;
public class WifiSecurityAnalyzer {
    private WifiManager wifiManager;

        public WifiSecurityAnalyzer(Context context) {

            wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
        // This will return the name of the Wi-Fi.
        public String getCurrentSSID() {

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            if(ssid != null) {
                return  ssid.replace("\"","");
            }
            return  "Unknown";
        }

    public String getSecurityType() {

        if (wifiManager == null)
            return "UNKNOWN";

        wifiManager.startScan(); // --> This will start the scan using wifiManager.

        List<ScanResult> scanResults;

        try {
            scanResults = wifiManager.getScanResults();
        } catch (SecurityException e) {
            return "PERMISSION_DENIED";
        }

        if (scanResults == null || scanResults.isEmpty())
            return "UNKNOWN";

        String currentSSID = getCurrentSSID();

        for (ScanResult result : scanResults) {

            if (result.SSID.equals(currentSSID)) {

                String capabilities = result.capabilities;

                if (capabilities.contains("WEP"))
                    return "WEP";   // Denotes very weak Wi-Fi
                else if (capabilities.contains("WPA3"))
                    return "WPA3";  // Denotes Best Wi-Fi
                else if (capabilities.contains("WPA2"))
                    return "WPA2";  // Denotes Good Wi-Fi.
                else if (capabilities.contains("WPA"))
                    return "WPA";  // Denotes Old Wi-Fi.
                else
                    return "OPEN"; // Wi-Fi is open(No password)
            }
        }

        return "UNKNOWN";
    }

        public String getRiskLevel(String securityType) {

            switch (securityType) {
                case"OPEN":
                case"WEP":
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
}