package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.GradedItem;
import com.simon.scheduledawg.entity.User;
import com.simon.scheduledawg.service.GradedItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories/{categoryId}/items")
public class GradedItemController {

    private final GradedItemService gradedItemService;

    public GradedItemController(GradedItemService gradedItemService) {
        this.gradedItemService = gradedItemService;
    }

    @GetMapping
    public ResponseEntity<List<GradedItem>> getItems(@PathVariable Long categoryId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(gradedItemService.getItemsByCategory(categoryId, currentUser));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<GradedItem> getItem(@PathVariable Long categoryId, @PathVariable Long itemId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(gradedItemService.getItemById(categoryId, itemId, currentUser));
    }

    @PostMapping
    public ResponseEntity<GradedItem> createItem(@PathVariable Long categoryId, @Valid @RequestBody GradedItem item, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(gradedItemService.createItem(item, categoryId, currentUser));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<GradedItem> updateItem(@PathVariable Long categoryId, @PathVariable Long itemId, @Valid @RequestBody GradedItem item, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(gradedItemService.fullyUpdateItem(item, categoryId, itemId, currentUser));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long categoryId, @PathVariable Long itemId, @AuthenticationPrincipal User currentUser) {
        gradedItemService.deleteItem(categoryId, itemId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllItems(@PathVariable Long categoryId, @AuthenticationPrincipal User currentUser) {
        gradedItemService.deleteAllItemsByCategory(categoryId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
