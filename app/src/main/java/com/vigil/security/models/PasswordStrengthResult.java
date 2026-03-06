package com.vigil.security.models;

public class PasswordStrengthResult {
    public static final int LEVEL_VERY_WEAK = 0;
    public static final int LEVEL_WEAK      = 1;
    public static final int LEVEL_FAIR      = 2;
    public static final int LEVEL_STRONG    = 3;
    public static final int LEVEL_VERY_STRONG = 4;

    private int score;

    private int level;

    private String label;

    private String colorHex;

    private String feedbackMessage;

    private String[] tips;

    public PasswordStrengthResult(int score, int level, String label, String colorHex, String feedbackMessage, String[] tips) {
        this.score           = score;
        this.level           = level;
        this.label           = label;
        this.colorHex        = colorHex;
        this.feedbackMessage = feedbackMessage;
        this.tips            = tips;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public String getLabel() {
        return label;
    }

    public String getColorHex() {
        return colorHex;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public String[] getTips() {
        return tips;
    }

    public boolean hasTips() {
        return tips != null && tips.length > 0;
    }
}
