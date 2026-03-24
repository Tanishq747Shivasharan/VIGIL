package com.vigil.security.models;

public class SmsMessage {
    public static final int RISK_SAFE     = 0;
    public static final int RISK_LOW      = 1;
    public static final int RISK_MEDIUM   = 2;
    public static final int RISK_HIGH     = 3;
    public static final int RISK_CRITICAL = 4;

    private String sender;      // Phone number or contact name e.g. "VM-HDFCBK"
    private String body;        // Full message text
    private long timestamp;     // Unix time in milliseconds (from SMS database)

    private int riskScore;          // 0–100
    private int riskLevel;          // One of the RISK_ constants above
    private String riskLabel;       // "Safe" / "Low" / "Medium" / "High" / "Critical"
    private String colorHex;        // Color for the badge
    private String[] matchedKeywords; // Which keywords triggered the alert
    private String explanation;     // Human-readable reason e.g. "Contains lottery scam patterns"
    private boolean isPhishing;     // true if riskLevel >= RISK_MEDIUM

    public SmsMessage(String sender, String body, long timestamp) {
        this.sender    = sender;
        this.body      = body;
        this.timestamp = timestamp;

        // Default analysis values — overwritten after analysis
        this.riskScore        = 0;
        this.riskLevel        = RISK_SAFE;
        this.riskLabel        = "Safe";
        this.colorHex         = "#27AE60";
        this.matchedKeywords  = new String[0];
        this.explanation      = "No threats detected.";
        this.isPhishing       = false;
    }

    public String getSender()           { return sender; }
    public String getBody()             { return body; }
    public long getTimestamp()          { return timestamp; }
    public int getRiskScore()           { return riskScore; }
    public int getRiskLevel()           { return riskLevel; }
    public String getRiskLabel()        { return riskLabel; }
    public String getColorHex()         { return colorHex; }
    public String[] getMatchedKeywords(){ return matchedKeywords; }
    public String getExplanation()      { return explanation; }
    public boolean isPhishing()         { return isPhishing; }

    public void setRiskScore(int riskScore)               { this.riskScore = riskScore; }
    public void setRiskLevel(int riskLevel)               { this.riskLevel = riskLevel; }
    public void setRiskLabel(String riskLabel)            { this.riskLabel = riskLabel; }
    public void setColorHex(String colorHex)              { this.colorHex = colorHex; }
    public void setMatchedKeywords(String[] matchedKeywords){ this.matchedKeywords = matchedKeywords; }
    public void setExplanation(String explanation)        { this.explanation = explanation; }
    public void setPhishing(boolean phishing)             { this.isPhishing = phishing; }

    public String getFormattedTimestamp() {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a",
                        java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }

    public String getBodyPreview() {
        if (body == null || body.isEmpty()) return "(empty message)";
        int end = Math.min(body.length(), 120);
        String preview = body.substring(0, end);
        // If we cut it short, add "..." to indicate there's more
        return body.length() > 120 ? preview + "..." : preview;
    }

    public String getMatchedKeywordsDisplay() {
        if (matchedKeywords == null || matchedKeywords.length == 0) return "None";
        return String.join(", ", matchedKeywords);
    }
}
