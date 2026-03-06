package com.vigil.security.security;

import com.vigil.security.models.PasswordStrengthResult;

import java.util.ArrayList;
import java.util.List;

public class PasswordAnalyzer {
    private static final int POINTS_LENGTH_MINIMUM  = 20; // Has at least 8 chars
    private static final int POINTS_LENGTH_GOOD     = 10; // Has at least 12 chars (bonus)
    private static final int POINTS_LENGTH_GREAT    = 5;  // Has at least 16 chars (bonus)
    private static final int POINTS_UPPERCASE       = 15; // Has A–Z
    private static final int POINTS_LOWERCASE       = 15; // Has a–z
    private static final int POINTS_DIGITS          = 15; // Has 0–9
    private static final int POINTS_SYMBOLS         = 20; // Has !@#$... etc.
    private static final int POINTS_NO_COMMON       = 5;  // Avoids common patterns
    private static final int POINTS_NO_REPEAT       = 5;  // Avoids aaa, 111 repeats

    private static final String[] COMMON_PATTERNS = {
            "123456", "12345", "1234", "123",
            "abcdef", "abcd", "abc",
            "qwerty", "qwert",
            "password", "pass",
            "111111", "000000",
            "iloveyou", "admin", "login",
            "welcome", "monkey", "dragon"
    };

    public static PasswordStrengthResult analyze(String password) {

        if (password == null || password.isEmpty()) {
            return buildResult(0, new ArrayList<>());
        }

        int score = 0;

        List<String> tips = new ArrayList<>();

        if (password.length() >= 8) {
            score += POINTS_LENGTH_MINIMUM;
        } else {
            tips.add("Use at least 8 characters (currently " + password.length() + ")");
        }

        if (password.length() >= 12) {
            score += POINTS_LENGTH_GOOD;
        }

        if (password.length() >= 16) {
            score += POINTS_LENGTH_GREAT;
        }

        if (containsUppercase(password)) {
            score += POINTS_UPPERCASE;
        } else {
            tips.add("Add uppercase letters (A–Z)");
        }

        if (containsLowercase(password)) {
            score += POINTS_LOWERCASE;
        } else {
            tips.add("Add lowercase letters (a–z)");
        }

        if (containsDigits(password)) {
            score += POINTS_DIGITS;
        } else {
            tips.add("Add numbers (0–9)");
        }

        if (containsSymbols(password)) {
            score += POINTS_SYMBOLS;
        } else {
            tips.add("Add symbols like !@#$%^&*");
        }

        if (!hasCommonPattern(password)) {
            score += POINTS_NO_COMMON;
        } else {
            tips.add("Avoid common words like 'password', '123456', 'qwerty'");
        }

        if (!hasExcessiveRepetition(password)) {
            score += POINTS_NO_REPEAT;
        } else {
            tips.add("Avoid repeating characters (e.g. 'aaa', '111')");
        }

        score = Math.min(score, 100);

        return buildResult(score, tips);
    }

    private static PasswordStrengthResult buildResult(int score, List<String> tipsList) {

        int level;
        String label;
        String colorHex;
        String feedback;

        if (score < 20) {
            level    = PasswordStrengthResult.LEVEL_VERY_WEAK;
            label    = "Very Weak";
            colorHex = "#C0392B";  // Strong red
            feedback = "This password is very easy to crack. Change it immediately.";
        } else if (score < 40) {
            level    = PasswordStrengthResult.LEVEL_WEAK;
            label    = "Weak";
            colorHex = "#E74C3C";  // Softer red
            feedback = "This password offers little protection. Improve it.";
        } else if (score < 60) {
            level    = PasswordStrengthResult.LEVEL_FAIR;
            label    = "Fair";
            colorHex = "#F39C12";  // Orange/amber
            feedback = "Decent start, but this password could be stronger.";
        } else if (score < 80) {
            level    = PasswordStrengthResult.LEVEL_STRONG;
            label    = "Strong";
            colorHex = "#27AE60";  // Green (matches app theme)
            feedback = "Good job! This is a secure password.";
        } else {
            level    = PasswordStrengthResult.LEVEL_VERY_STRONG;
            label    = "Very Strong";
            colorHex = "#1E8449";  // Deep green
            feedback = "Excellent! This password is very hard to crack.";
        }

        String[] tipsArray = tipsList.toArray(new String[0]);

        return new PasswordStrengthResult(score, level, label, colorHex, feedback, tipsArray);
    }

    private static boolean containsUppercase(String password) {
        return password.matches(".*[A-Z].*");
    }

    private static boolean containsLowercase(String password) {
        return password.matches(".*[a-z].*");
    }

    private static boolean containsDigits(String password) {
        return password.matches(".*[0-9].*");
    }

    private static boolean containsSymbols(String password) {
        return password.matches(".*[^a-zA-Z0-9].*");
    }

    private static boolean hasCommonPattern(String password) {
        String lower = password.toLowerCase();
        // Enhanced for-loop: "for each pattern in the COMMON_PATTERNS array"
        for (String pattern : COMMON_PATTERNS) {
            if (lower.contains(pattern)) {
                return true; // Found a weak pattern → return true immediately
            }
        }
        return false; // No common patterns found
    }
    private static boolean hasExcessiveRepetition(String password) {
        if (password.length() < 3) return false; // Can't have 3 repeats in < 3 chars

        int repeatCount = 1; // Start at 1 — the current char counts as one occurrence

        // Loop from index 1 to end (we look BACK at i-1 to compare with i)
        for (int i = 1; i < password.length(); i++) {
            if (password.charAt(i) == password.charAt(i - 1)) {
                // Same character as previous → increment repeat counter
                repeatCount++;
                if (repeatCount >= 3) {
                    return true; // 3+ in a row → excessive repetition found
                }
            } else {
                // Different character → reset counter back to 1
                repeatCount = 1;
            }
        }
        return false; // No excessive repetition found
    }

    public static String getStrengthDescription(int level) {
        switch (level) {
            case PasswordStrengthResult.LEVEL_VERY_WEAK:
                return "Crackable in under a second by any attacker.";
            case PasswordStrengthResult.LEVEL_WEAK:
                return "Can be cracked in minutes with basic tools.";
            case PasswordStrengthResult.LEVEL_FAIR:
                return "Takes hours to crack. Good for low-risk accounts.";
            case PasswordStrengthResult.LEVEL_STRONG:
                return "Very resistant. Suitable for important accounts.";
            case PasswordStrengthResult.LEVEL_VERY_STRONG:
                return "Extremely resistant. Would take centuries to crack.";
            default:
                return "Unknown strength level.";
        }
    }
}
