package rorys.library.util;

/**
 * Created by Rory on 6/22/2017.
 */
public class IntegerCheck {

    /**
     * @param arg input to check
     * @return if arg is int
     *
     * @deprecated Use NumberUtil
     */
    public static boolean isInt(String arg) {
        try {
            Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * @param arg input to check
     * @return if arg is int
     *
     * @deprecated Use NumberUtil
     */
    public static boolean isPositiveInt(String arg) {
        try {
            int i = Integer.parseInt(arg);
            return i >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
