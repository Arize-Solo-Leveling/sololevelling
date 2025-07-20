/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.auth;

import lombok.Getter;

@Getter
public class TokenValidationResult {
    // Getters
    private final boolean valid;
    private final String message;
    private final String username;
    private final String status;
    private final boolean expired;

    public TokenValidationResult(boolean valid, String message, String username, String status, boolean expired) {
        this.valid = valid;
        this.message = message;
        this.username = username;
        this.status = status;
        this.expired = expired;
    }

}