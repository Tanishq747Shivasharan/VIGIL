package com.vigil.security.security;

import com.vigil.security.models.SmsMessage;

import java.util.ArrayList;
import java.util.List;

public class PhishingAnalyzer {
    private static class PhishingRule {
        String keyword;  // The text pattern to search for (lowercase)
        int weight;      // How much this adds to the risk score (1–50)
        String reason;   // Shown to the user explaining why this is suspicious

        // Constructor for easy initialization
        PhishingRule(String keyword, int weight, String reason) {
            this.keyword = keyword;
            this.weight  = weight;
            this.reason  = reason;
        }
    }

    private static final PhishingRule[] RULES = {

            // --- Prize / Lottery Scams ---
            new PhishingRule("lottery",         45, "Lottery scams promise fake prizes to steal money"),
            new PhishingRule("you have won",    45, "Prize fraud claims you've won something you never entered"),
            new PhishingRule("winner",          35, "Fake winner notifications are a common fraud tactic"),
            new PhishingRule("prize",           30, "Unsolicited prize offers are almost always fraudulent"),
            new PhishingRule("congratulation",  25, "Congratulation messages are often the start of a scam"),
            new PhishingRule("selected",        15, "Being 'selected' out of nowhere is a pressure tactic"),

            // --- Urgency / Fear Tactics ---
            new PhishingRule("urgent",          25, "Urgency language is used to pressure quick action without thinking"),
            new PhishingRule("immediately",     20, "Demands for immediate action prevent you from verifying legitimacy"),
            new PhishingRule("expire",          20, "Fake expiry deadlines create panic"),
            new PhishingRule("suspend",         30, "Account suspension threats are used to steal credentials"),
            new PhishingRule("blocked",         25, "Blocked account warnings are a common impersonation tactic"),
            new PhishingRule("deactivate",      25, "Deactivation threats impersonate banks and telecom providers"),
            new PhishingRule("last chance",     30, "Last chance language creates a false sense of scarcity"),
            new PhishingRule("action required", 20, "Vague 'action required' messages hide phishing links"),

            // --- Financial Fraud ---
            new PhishingRule("bank account",    35, "Requests involving bank accounts are a top fraud category"),
            new PhishingRule("credit card",     30, "Unsolicited credit card messages often seek card details"),
            new PhishingRule("refund",          25, "Fake refund offers are used to collect account details"),
            new PhishingRule("cashback",        20, "Fake cashback offers lead to credential theft"),
            new PhishingRule("transfer",        20, "Unexpected transfer requests are a common scam"),
            new PhishingRule("free recharge",   35, "Free recharge offers are classic fraud bait"),
            new PhishingRule("upi",             15, "UPI scams are among the most common in India"),
            new PhishingRule("rs.",             10, "Monetary amounts in unsolicited SMS are suspicious"),

            // --- Link / Malware Delivery ---
            new PhishingRule("click here",      40, "Click here links are the most common phishing delivery method"),
            new PhishingRule("click link",      40, "Unsolicited links in SMS are extremely high risk"),
            new PhishingRule("open link",       35, "Opening unknown links can install malware or steal credentials"),
            new PhishingRule("visit",           15, "Requests to visit URLs in unsolicited SMS are suspicious"),
            new PhishingRule("http://",         25, "Unencrypted HTTP links in SMS are a major red flag"),
            new PhishingRule("bit.ly",          35, "Shortened URLs hide the true destination — often malicious"),
            new PhishingRule("tinyurl",         35, "Shortened URLs hide the true destination — often malicious"),
            new PhishingRule("download",        20, "Requests to download files via SMS are highly suspicious"),

            // --- Credential Theft ---
            new PhishingRule("otp",             20, "OTP requests via SMS can indicate SIM swap or account takeover"),
            new PhishingRule("share otp",       50, "Never share OTPs — legitimate services never ask for this"),
            new PhishingRule("password",        25, "Legitimate organizations never ask for passwords via SMS"),
            new PhishingRule("pin",             20, "PIN requests via SMS are almost always fraudulent"),
            new PhishingRule("verify",          20, "Verification requests often lead to fake login pages"),
            new PhishingRule("confirm",         15, "Confirmation requests can be used to authorize fraudulent actions"),

            // --- Identity / KYC Scams (India-specific) ---
            new PhishingRule("kyc",             35, "KYC scams impersonate banks and regulators to steal identity"),
            new PhishingRule("aadhaar",         30, "Aadhaar-linked scams are among the most reported in India"),
            new PhishingRule("pan card",        30, "PAN card requests are used in identity theft"),
            new PhishingRule("trai",            35, "TRAI impersonation is used to threaten number disconnection"),
            new PhishingRule("uidai",           30, "UIDAI impersonation targets Aadhaar-linked accounts"),
            new PhishingRule("income tax",      25, "Fake income tax notices are used to steal PAN/Aadhaar data"),
            new PhishingRule("government",      15, "Fake government SMS impersonate official agencies"),

            // --- Generic Low-weight Signals ---
            new PhishingRule("free",            15, "Unsolicited 'free' offers are a common lure"),
            new PhishingRule("lucky",           20, "Lucky draw messages almost always lead to scams"),
            new PhishingRule("apply now",       15, "Unsolicited apply-now requests can lead to loan fraud"),
            new PhishingRule("claim",           20, "Claim your reward messages are used in prize scams"),
            new PhishingRule("cash",            10, "Cash promises in unsolicited SMS are suspicious"),
    };

    public static SmsMessage analyze(SmsMessage sms) {
        if (sms == null) return null;

        String body = sms.getBody();
        if (body == null || body.isEmpty()) {
            // Empty message — just return it with default SAFE values
            return sms;
        }

        // --- STEP 1: Normalize ---
        // Convert to lowercase so our rules match regardless of capitalization.
        // "LOTTERY", "Lottery", "lottery" all match "lottery"
        // .trim() removes leading/trailing whitespace
        String normalizedBody = body.toLowerCase().trim();

        // --- STEP 2: Check all rules and accumulate score ---
        int totalScore = 0;

        // matchedRuleReasons holds the "reason" text for each matched rule
        List<String> matchedReasons = new ArrayList<>();

        // matchedKeywordsList holds the keyword itself (for the badge display)
        List<String> matchedKeywordsList = new ArrayList<>();

        // Enhanced for-loop: "for each PhishingRule in the RULES array"
        for (PhishingRule rule : RULES) {
            // String.contains() checks if normalizedBody has the keyword anywhere
            if (normalizedBody.contains(rule.keyword)) {
                totalScore += rule.weight;
                matchedReasons.add(rule.reason);
                matchedKeywordsList.add(rule.keyword);
            }
        }

        // --- STEP 3: Cap score at 100 ---
        totalScore = Math.min(totalScore, 100);

        // --- STEP 4: Map score to risk level ---
        int riskLevel;
        String riskLabel;
        String colorHex;
        boolean isPhishing;

        if (totalScore == 0) {
            riskLevel  = SmsMessage.RISK_SAFE;
            riskLabel  = "Safe";
            colorHex   = "#27AE60"; // Green
            isPhishing = false;
        } else if (totalScore < 25) {
            riskLevel  = SmsMessage.RISK_LOW;
            riskLabel  = "Low Risk";
            colorHex   = "#F39C12"; // Amber
            isPhishing = false;
        } else if (totalScore < 50) {
            riskLevel  = SmsMessage.RISK_MEDIUM;
            riskLabel  = "Medium Risk";
            colorHex   = "#E67E22"; // Orange
            isPhishing = true;
        } else if (totalScore < 75) {
            riskLevel  = SmsMessage.RISK_HIGH;
            riskLabel  = "High Risk";
            colorHex   = "#E74C3C"; // Red
            isPhishing = true;
        } else {
            riskLevel  = SmsMessage.RISK_CRITICAL;
            riskLabel  = "CRITICAL";
            colorHex   = "#C0392B"; // Deep red
            isPhishing = true;
        }

        // --- STEP 5: Build the explanation string ---
        String explanation = buildExplanation(matchedReasons, totalScore);

        // --- STEP 6: Convert lists to arrays and populate the SmsMessage ---
        String[] keywordsArray = matchedKeywordsList.toArray(new String[0]);
        // .toArray(new String[0]) converts List<String> → String[]
        // new String[0] is a type hint — Java uses it to know the array type

        sms.setRiskScore(totalScore);
        sms.setRiskLevel(riskLevel);
        sms.setRiskLabel(riskLabel);
        sms.setColorHex(colorHex);
        sms.setMatchedKeywords(keywordsArray);
        sms.setExplanation(explanation);
        sms.setPhishing(isPhishing);

        return sms;
    }

    private static String buildExplanation(List<String> reasons, int score) {
        if (reasons.isEmpty()) {
            return "No phishing indicators found in this message.";
        }

        // Intro phrase varies by severity
        String intro;
        if (score < 25)       intro = "Mildly suspicious: ";
        else if (score < 50)  intro = "Warning — possible phishing: ";
        else if (score < 75)  intro = "Likely phishing: ";
        else                   intro = "Almost certainly phishing — ";

        if (reasons.size() == 1) {
            // Just one reason — show it directly after the intro
            // Lowercase the first letter so it flows naturally after the intro
            String reason = reasons.get(0);
            return intro + reason.substring(0, 1).toLowerCase() + reason.substring(1);
        }

        // Multiple reasons — show first two, mention how many more
        // reasons.get(0) = first element, reasons.get(1) = second element
        String first  = reasons.get(0);
        String second = reasons.get(1);
        int remaining = reasons.size() - 2;

        // StringBuilder to build the multi-part string
        StringBuilder sb = new StringBuilder(intro);
        sb.append(first.substring(0, 1).toLowerCase()).append(first.substring(1));
        sb.append(". Also: ");
        sb.append(second.substring(0, 1).toLowerCase()).append(second.substring(1));

        if (remaining > 0) {
            // "+ 3 more indicators" at the end
            sb.append(" — and ").append(remaining).append(" more indicator")
                    .append(remaining > 1 ? "s." : ".");
        }

        return sb.toString();
    }

    public static SmsMessage analyzeText(String sender, String body, long timestamp) {
        SmsMessage sms = new SmsMessage(sender, body, timestamp);
        return analyze(sms);
    }
}
