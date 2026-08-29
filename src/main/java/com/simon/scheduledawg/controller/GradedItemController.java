package com.simon.scheduledawg.controller;

import com.simon.scheduledawg.entity.GradedItem;
import com.simon.scheduledawg.service.GradedItemService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<GradedItem>> getItems(@PathVariable Long categoryId) {
        return ResponseEntity.ok(gradedItemService.getItemsByCategory(categoryId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<GradedItem> getItem(@PathVariable Long categoryId, @PathVariable Long itemId) {
        return ResponseEntity.ok(gradedItemService.getItemById(categoryId, itemId));
    }

    @PostMapping
    public ResponseEntity<GradedItem> createItem(@PathVariable Long categoryId, @RequestBody GradedItem item) {
        return ResponseEntity.ok(gradedItemService.createItem(item, categoryId));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<GradedItem> updateItem(@PathVariable Long categoryId, @PathVariable Long itemId, @RequestBody GradedItem item) {
        return ResponseEntity.ok(gradedItemService.fullyUpdateItem(item, categoryId, itemId));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long categoryId, @PathVariable Long itemId) {
        gradedItemService.deleteItem(categoryId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllItems(@PathVariable Long categoryId) {
        gradedItemService.deleteAllItemsByCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}