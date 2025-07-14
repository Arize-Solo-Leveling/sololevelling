/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.model.init;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class SystemSetting {
    @Id
    private String id;
    private boolean initialSetupDone;

    public SystemSetting(String s, boolean b) {
        this.id = s;
        this.initialSetupDone = b;
    }
}
