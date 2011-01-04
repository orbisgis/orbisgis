package org.orbisgis.utils;

import java.math.BigDecimal;

/**
 * Provides formatting utilities
 */
public final class FormatUtils {

        /**
         * Rounds a double to the specified decimal
         * @param d a double
         * @param decimalPlace the decimal limit
         * @return a new rounded double
         */
        public static double round(double d, int decimalPlace) {
                BigDecimal bd = new BigDecimal(Double.toString(d));
                bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
                return bd.doubleValue();
        }

        private FormatUtils() {
        }
}
