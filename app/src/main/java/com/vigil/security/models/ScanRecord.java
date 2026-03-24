package com.vigil.security.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScanRecord {

    public static final String TYPE_WIFI     = "WIFI";
    public static final String TYPE_LAN      = "LAN";
    public static final String TYPE_PASSWORD = "PASSWORD";
    public static final String TYPE_SMS      = "SMS";

    public static final String RISK_SAFE     = "SAFE";
    public static final String RISK_LOW      = "LOW";
    public static final String RISK_MEDIUM   = "MEDIUM";
    public static final String RISK_HIGH     = "HIGH";
    public static final String RISK_CRITICAL = "CRITICAL";

    private long   id;
    private String scanType;
    private String summary;
    private String riskLevel;
    private int    riskScore;
    private String details;
    private long   timestamp;

    public ScanRecord(String scanType, String summary, String riskLevel,
                      int riskScore, String details) {
        this.scanType  = scanType;
        this.summary   = summary;
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.details   = details;
        this.timestamp = System.currentTimeMillis();
    }

    public long   getId()                      { return id; }
    public void   setId(long id)               { this.id = id; }

    public String getScanType()                { return scanType; }
    public void   setScanType(String t)        { this.scanType = t; }

    public String getSummary()                 { return summary; }
    public void   setSummary(String s)         { this.summary = s; }

    public String getRiskLevel()               { return riskLevel; }
    public void   setRiskLevel(String r)       { this.riskLevel = r; }

    public int    getRiskScore()               { return riskScore; }
    public void   setRiskScore(int score)      { this.riskScore = score; }

    public String getDetails()                 { return details; }
    public void   setDetails(String d)         { this.details = d; }

    public long   getTimestamp()               { return timestamp; }
    public void   setTimestamp(long t)         { this.timestamp = t; }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getShortDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getScanTypeIcon() {
        switch (scanType) {
            case TYPE_WIFI:     return "📶";
            case TYPE_LAN:      return "🖧";
            case TYPE_SMS:      return "✉";
            case TYPE_PASSWORD: return "🔑";
            default:            return "🔍";
        }
    }

    public String getScanTypeLabel() {
        switch (scanType) {
            case TYPE_WIFI:     return "WiFi Scan";
            case TYPE_LAN:      return "LAN Scan";
            case TYPE_SMS:      return "SMS Scan";
            case TYPE_PASSWORD: return "Password Check";
            default:            return "Scan";
        }
    }

    public String getRiskLabel() {
        if (riskLevel == null) return "Unknown";
        return riskLevel.charAt(0) + riskLevel.substring(1).toLowerCase(Locale.getDefault());
    }
}