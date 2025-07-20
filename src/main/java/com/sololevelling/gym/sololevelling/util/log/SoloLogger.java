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

}