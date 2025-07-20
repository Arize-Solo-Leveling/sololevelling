/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.util.pass;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {

    private static final String REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    public boolean isStrongEnough(String password) {
        return password != null && password.matches(REGEX);
    }

    public boolean isSecureWithZxcvbn(String password) {
        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(password);
        return strength.getScore() >= 3; // 0–4 scale
    }

    public void validate(String password) {
        if (!isStrongEnough(password)) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters, include uppercase, lowercase, number, and special character"
            );
        }

        if (!isSecureWithZxcvbn(password)) {
            throw new IllegalArgumentException("Password is too weak. Use a stronger password.");
        }
    }
}
