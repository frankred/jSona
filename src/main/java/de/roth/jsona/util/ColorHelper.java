package de.roth.jsona.util;

/**
 * Util class to create colors
 *
 * @author Frank Roth
 */
public class ColorHelper {

    /**
     * Convert a over given number in a javafx css background color value
     * attribute.
     *
     * @param number from 0(inclusive) to 12(exclusive)
     * @return -fx-background-color attribute as String
     */
    public static String numberToStyle(int number) {
        StringBuffer style = new StringBuffer("-fx-background-color:");
        switch (number) {
            case 0:
                style.append("#FFEBF1");
                break;
            case 1:
                style.append("#EBFFF7");
                break;
            case 2:
                style.append("#FFEBEB");
                break;
            case 3:
                style.append("#EBFDFF");
                break;
            case 4:
                style.append("#FFF5EB");
                break;
            case 5:
                style.append("#EBF3FF");
                break;
            case 6:
                style.append("#FEFFEB");
                break;
            case 7:
                style.append("#EDEBFF");
                break;
            case 8:
                style.append("#F4FFEB");
                break;
            case 9:
                style.append("#F8EBFF");
                break;
            case 10:
                style.append("#EBFFEC");
                break;
            case 11:
                style.append("#FFEBFB");
                break;
            default:
                break;
        }
        style.append(";");
        return style.toString();
    }
}
