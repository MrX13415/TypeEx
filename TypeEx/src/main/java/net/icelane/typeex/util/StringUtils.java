package net.icelane.typeex.util;

public final class StringUtils {

    public static int countMatches(final CharSequence str, final char ch) {
        if (str.length() <= 0) return 0;

        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (ch == str.charAt(i)) {
                count++;
            }
        }
        return count;
    }
    
    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }
}
