package com.vigil.security.models;

public class GeneratorConfig {
    // -------------------------------------------------------------------------
    // DEFAULT CONSTANTS
    //
    // These are the out-of-the-box settings when the app first opens.
    // Matching your mockup: length=12, uppercase✅ lowercase✅ numbers✅ symbols❌
    // -------------------------------------------------------------------------
    public static final int DEFAULT_LENGTH    = 12;
    public static final int MIN_LENGTH        = 6;
    public static final int MAX_LENGTH        = 32;

    // -------------------------------------------------------------------------
    // FIELDS
    // -------------------------------------------------------------------------
    private int length;           // How many characters (6–32)
    private boolean useUppercase; // Include A–Z
    private boolean useLowercase; // Include a–z
    private boolean useNumbers;   // Include 0–9
    private boolean useSymbols;   // Include !@#$...

    // -------------------------------------------------------------------------
    // DEFAULT CONSTRUCTOR
    //
    // Creates a config with the default values from the mockup.
    // Called when the Fragment first loads ("Reset defaults" button also uses this).
    // -------------------------------------------------------------------------
    public GeneratorConfig() {
        this.length       = DEFAULT_LENGTH;
        this.useUppercase = true;
        this.useLowercase = true;
        this.useNumbers   = true;
        this.useSymbols   = false; // Unchecked by default in your mockup
    }

    // -------------------------------------------------------------------------
    // FULL CONSTRUCTOR
    //
    // Used when you want to create a config with specific values,
    // for example when restoring saved settings later.
    // -------------------------------------------------------------------------
    public GeneratorConfig(int length, boolean useUppercase,
                           boolean useLowercase, boolean useNumbers,
                           boolean useSymbols) {
        this.length       = length;
        this.useUppercase = useUppercase;
        this.useLowercase = useLowercase;
        this.useNumbers   = useNumbers;
        this.useSymbols   = useSymbols;
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------
    public int getLength()        { return length; }
    public boolean useUppercase() { return useUppercase; }
    public boolean useLowercase() { return useLowercase; }
    public boolean useNumbers()   { return useNumbers; }
    public boolean useSymbols()   { return useSymbols; }

    // -------------------------------------------------------------------------
    // SETTERS
    // The Fragment updates these as the user interacts with the UI.
    // -------------------------------------------------------------------------
    public void setLength(int length) {
        // Clamp the value between MIN and MAX using Math.max and Math.min
        // Math.max(MIN, x) ensures value is never BELOW minimum
        // Math.min(MAX, x) ensures value is never ABOVE maximum
        // Combined: MIN_LENGTH ≤ length ≤ MAX_LENGTH always
        this.length = Math.max(MIN_LENGTH, Math.min(MAX_LENGTH, length));
    }
    public void setUseUppercase(boolean useUppercase) { this.useUppercase = useUppercase; }
    public void setUseLowercase(boolean useLowercase) { this.useLowercase = useLowercase; }
    public void setUseNumbers(boolean useNumbers)     { this.useNumbers   = useNumbers; }
    public void setUseSymbols(boolean useSymbols)     { this.useSymbols   = useSymbols; }

    // -------------------------------------------------------------------------
    // HELPER: isValid()
    //
    // A config is only valid if at least ONE character set is selected.
    // If the user unchecks everything, we can't generate a password.
    // The Fragment calls this before generating to show an error if needed.
    //
    // The || operator means OR — true if ANY of them is true
    // -------------------------------------------------------------------------
    public boolean isValid() {
        return useUppercase || useLowercase || useNumbers || useSymbols;
    }

    // -------------------------------------------------------------------------
    // HELPER: getActiveSetCount()
    //
    // Returns how many character sets are selected.
    // Used to decide how to guarantee coverage (see PasswordGenerator).
    // Each "true ? 1 : 0" is a ternary operator — a compact if/else:
    //   condition ? valueIfTrue : valueIfFalse
    // -------------------------------------------------------------------------
    public int getActiveSetCount() {
        return (useUppercase ? 1 : 0)
                + (useLowercase ? 1 : 0)
                + (useNumbers   ? 1 : 0)
                + (useSymbols   ? 1 : 0);
    }
}
