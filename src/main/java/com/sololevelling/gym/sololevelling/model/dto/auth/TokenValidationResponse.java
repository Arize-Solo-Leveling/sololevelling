/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.dto.auth;

public record TokenValidationResponse(
    boolean valid,
    boolean expired,
    String message,
    String username,
    String status
) {}