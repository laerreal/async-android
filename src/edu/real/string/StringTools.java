package edu.real.string;

public class StringTools {

    /* Based on
     * https://stackoverflow.com/questions/1995439/get-android-phone-model-programmatically
     * */
    public static String capitalize(String str) {
        if (str.length() == 0) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }
}
