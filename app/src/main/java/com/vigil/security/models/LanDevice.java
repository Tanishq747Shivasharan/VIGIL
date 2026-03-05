package com.vigil.security.models;

public class LanDevice {
    private String ipAddress;
    private String macAddress;
    private String vendor;
    private String hostname;
    private boolean isOnline;
    private boolean isGateway;
    private boolean isCurrentDevice;

    public LanDevice(String ipAddress) {
        this.ipAddress = ipAddress;

        this.macAddress = "Unknown";
        this.vendor = "Unknown";
        this.hostname = "";
        this.isOnline = false;
        this.isGateway = false;
        this.isCurrentDevice = false;
    }

    // Getters
    public String getIpAddress() {
        return ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getVendor() {
        return vendor;
    }

    public String getHostname() {
        return hostname;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public boolean isGateway() {
        return isGateway;
    }

    public boolean isCurrentDevice() {
        return isCurrentDevice;
    }

    // Setters
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public void setGateway(boolean gateway) {
        isGateway = gateway;
    }

    public void setCurrentDevice(boolean currentDevice) {
        isCurrentDevice = currentDevice;
    }

    // Helper methods
    public String getDisplayName() {
        if (hostname != null && !hostname.isEmpty()) {
            return hostname;
        }
        if (isGateway) return "Router / Gateway";
        if (isCurrentDevice) return "This Device";
        return ipAddress; // last resort
    }

    public String getStatusLabel() {
        if (isCurrentDevice) return "YOU";
        if (isGateway) return "ROUTER";
        return "DEVICE";
    }
}
