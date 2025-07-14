/*

  * © 2025 Praveen Kumar. All rights reserved.
  *
  * This software is licensed under the MIT License.
  * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.repo.init;

import com.sololevelling.gym.sololevelling.model.init.SystemSetting;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SystemSettingRepository extends MongoRepository<SystemSetting, String> {}
