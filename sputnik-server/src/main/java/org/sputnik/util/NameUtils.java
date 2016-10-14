package org.sputnik.util;

public abstract class NameUtils {
    public static void validateIdentifier(String value, String name) {
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException(name + " is empty");
        }
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (!Character.isLetterOrDigit(ch) && ch != '-' && ch != '_') {
                throw new IllegalArgumentException("Invalid " + name + " character: '" + ch + "'");
            }
        }
    }
}
