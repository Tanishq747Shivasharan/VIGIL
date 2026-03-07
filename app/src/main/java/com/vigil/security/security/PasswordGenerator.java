package com.vigil.security.security;

import com.vigil.security.models.GeneratorConfig;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBER_CHARS    = "0123456789";
    private static final String SYMBOL_CHARS    = "!@#$%^&*()-_=+[]{};<>,.?/";

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generate(GeneratorConfig config) {

        if (!config.isValid()) {
            return "Select at least one option";
        }

        StringBuilder pool = new StringBuilder();

        if (config.useUppercase()) pool.append(UPPERCASE_CHARS);
        if (config.useLowercase()) pool.append(LOWERCASE_CHARS);
        if (config.useNumbers())   pool.append(NUMBER_CHARS);
        if (config.useSymbols())   pool.append(SYMBOL_CHARS);

        String characterPool = pool.toString();

        List<Character> resultChars = new ArrayList<>();

        if (config.useUppercase()) {
            resultChars.add(randomCharFrom(UPPERCASE_CHARS));
        }
        if (config.useLowercase()) {
            resultChars.add(randomCharFrom(LOWERCASE_CHARS));
        }
        if (config.useNumbers()) {
            resultChars.add(randomCharFrom(NUMBER_CHARS));
        }
        if (config.useSymbols()) {
            resultChars.add(randomCharFrom(SYMBOL_CHARS));
        }

        int remaining = config.getLength() - config.getActiveSetCount();

        for (int i = 0; i < remaining; i++) {
            resultChars.add(randomCharFrom(characterPool));
        }

        Collections.shuffle(resultChars, secureRandom);

        StringBuilder result = new StringBuilder();
        for (Character c : resultChars) {
            result.append(c);
        }

        return result.toString();
    }

    private static char randomCharFrom(String charSet) {
        int randomIndex = secureRandom.nextInt(charSet.length());
        return charSet.charAt(randomIndex);
    }

    public static String[] generateMultiple(GeneratorConfig config, int count) {
        String[] passwords = new String[count];
        for (int i = 0; i < count; i++) {
            passwords[i] = generate(config);
        }
        return passwords;
    }
}
