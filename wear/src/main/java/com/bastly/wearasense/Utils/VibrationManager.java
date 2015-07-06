package com.bastly.wearasense.Utils;

/**
 * Created by goofyahead on 6/07/15.
 */
public class VibrationManager {

    private static final int INTERVAL = 50;
    private static final long[] none = {0, 0, 0, 0};
    private static final long[] PATTERN_MINIMAL = {INTERVAL, INTERVAL, INTERVAL, 0, INTERVAL};
    private static final long[] patternLow = {INTERVAL, INTERVAL, INTERVAL, INTERVAL};
    private static final long[] patternMedium = {0, INTERVAL, 0, INTERVAL, INTERVAL, INTERVAL};
    private static final long[] patternHigh = {0, INTERVAL, 0, INTERVAL, 0, INTERVAL, 0, INTERVAL};

    public static long[] getPattern(double azimut) {
        if (azimut < 0) { // half pointing north
            if (azimut > -45 || azimut < -135) {
                return none;
            } else if (azimut > -67 || azimut < -123) {
                return none;
            } else if (azimut > -87 || azimut < -93) {
                return patternMedium;
            } else {
                return patternHigh;
            }
        }
        else {
            return none;
        }
    }
}
