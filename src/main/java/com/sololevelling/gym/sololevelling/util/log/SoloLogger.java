/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.util.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoloLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger("SOLO_LOG");

    private SoloLogger() {
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void info(String format, Object... arguments) {
        LOGGER.info(format, arguments);
    }

    public static void debug(String message) {
        LOGGER.debug(message);
    }

    public static void debug(String format, Object... arguments) {
        LOGGER.debug(format, arguments);
    }

    public static void warn(String message) {
        LOGGER.warn(message);
    }

    public static void warn(String format, Object... arguments) {
        LOGGER.warn(format, arguments);
    }

    public static void error(String message) {
        LOGGER.error(message);
    }

    public static void error(String format, Object... arguments) {
        LOGGER.error(format, arguments);
    }

}