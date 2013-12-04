package Clustream.addon;


import java.text.DecimalFormat;

public class StringUtils {

    public static final String newline = System.getProperty("line.separator");

    public static String doubleToString(double value, int fractionDigits) {
        return doubleToString(value, 0, fractionDigits);
    }

    public static String doubleToString(double value, int minFractionDigits,
            int maxFractionDigits) {
        DecimalFormat numberFormat = new DecimalFormat();
        numberFormat.setMinimumFractionDigits(minFractionDigits);
        numberFormat.setMaximumFractionDigits(maxFractionDigits);
        return numberFormat.format(value);
    }

    public static void appendNewline(StringBuilder out) {
        out.append(newline);
    }

    public static void appendIndent(StringBuilder out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.append(' ');
        }
    }

    public static void appendIndented(StringBuilder out, int indent, String s) {
        appendIndent(out, indent);
        out.append(s);
    }

    public static void appendNewlineIndented(StringBuilder out, int indent,
            String s) {
        appendNewline(out);
        appendIndented(out, indent, s);
    }

    public static String secondsToDHMSString(double seconds) {
        if (seconds < 60) {
            return doubleToString(seconds, 2, 2) + 's';
        }
        long secs = (int) (seconds);
        long mins = secs / 60;
        long hours = mins / 60;
        long days = hours / 24;
        secs %= 60;
        mins %= 60;
        hours %= 24;
        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days);
            result.append('d');
        }
        if ((hours > 0) || (days > 0)) {
            result.append(hours);
            result.append('h');
        }
        if ((hours > 0) || (days > 0) || (mins > 0)) {
            result.append(mins);
            result.append('m');
        }
        result.append(secs);
        result.append('s');
        return result.toString();
    }
}
