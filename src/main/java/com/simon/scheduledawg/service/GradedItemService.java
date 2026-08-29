package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.GradeCategory;
import com.simon.scheduledawg.entity.GradedItem;
import com.simon.scheduledawg.exception.ResourceNotFoundException;
import com.simon.scheduledawg.repository.GradeCategoryRepository;
import com.simon.scheduledawg.repository.GradedItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradedItemService {

    private final GradedItemRepository gradedItemRepository;
    private final GradeCategoryRepository gradeCategoryRepository;

    public GradedItemService(GradedItemRepository gradedItemRepository, GradeCategoryRepository gradeCategoryRepository) {
        this.gradedItemRepository = gradedItemRepository;
        this.gradeCategoryRepository = gradeCategoryRepository;
    }

    public GradedItem createItem(GradedItem item, Long categoryId) {
        GradeCategory category = gradeCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        item.setCategory(category);
        calculatePercentScore(item);
        return gradedItemRepository.save(item);
    }

    public List<GradedItem> getItemsByCategory(Long categoryId) {
        gradeCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        return gradedItemRepository.findByCategoryId(categoryId);
    }

    public GradedItem getItemById(Long categoryId, Long itemId) {
        return getItemScopedToCategory(categoryId, itemId);
    }

    private GradedItem getItemScopedToCategory(Long categoryId, Long itemId) {
        GradedItem item = gradedItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Graded item not found with id: " + itemId));

        if (!item.getCategory().getId().equals(categoryId)) {
            throw new ResourceNotFoundException("Graded item not found with id: " + itemId);
        }

        return item;
    }

    public GradedItem fullyUpdateItem(GradedItem item, Long categoryId, Long itemId) {
        GradedItem itemToUpdate = getItemScopedToCategory(categoryId, itemId);

        itemToUpdate.setTitle(item.getTitle());
        itemToUpdate.setPointsEarned(item.getPointsEarned());
        itemToUpdate.setPointsPossible(item.getPointsPossible());
        itemToUpdate.setPercentScore(item.getPercentScore());

        calculatePercentScore(itemToUpdate);
        return gradedItemRepository.save(itemToUpdate);
    }

    public void deleteItem(Long categoryId, Long itemId) {
        GradedItem itemToDelete = getItemScopedToCategory(categoryId, itemId);
        gradedItemRepository.delete(itemToDelete);
    }

    public void deleteAllItemsByCategory(Long categoryId) {
        List<GradedItem> items = gradedItemRepository.findByCategoryId(categoryId);
        gradedItemRepository.deleteAll(items);
    }

    private void calculatePercentScore(GradedItem item) {
        if (item.getPointsEarned() != null && item.getPointsPossible() != null && item.getPointsPossible() != 0) {
            double computed = (item.getPointsEarned() / item.getPointsPossible()) * 100;
            item.setPercentScore(computed);
        }
    }
}