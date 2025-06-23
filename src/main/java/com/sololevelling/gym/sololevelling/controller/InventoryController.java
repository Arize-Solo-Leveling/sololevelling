/*

 * © 2025 Praveen Kumar. All rights reserved.
 *
 * This software is licensed under the MIT License.
 * See the LICENSE file in the root directory for more information.


 */

package com.sololevelling.gym.sololevelling.controller;

import com.sololevelling.gym.sololevelling.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getInventory(Principal principal) {
        return ResponseEntity.ok(inventoryService.getInventory(principal.getName()));
    }

    @PostMapping("/{itemId}/equip")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> equip(@PathVariable Long itemId, Principal principal) {
        return ResponseEntity.ok(inventoryService.equipItem(principal.getName(), itemId));
    }

    @PostMapping("/{itemId}/unequip")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> unequip(@PathVariable Long itemId, Principal principal) {
        return ResponseEntity.ok(inventoryService.unequipItem(principal.getName(), itemId));
    }
}
