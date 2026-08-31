package com.simon.scheduledawg.service;

import com.simon.scheduledawg.entity.GradeCategory;
import com.simon.scheduledawg.entity.GradedItem;
import com.simon.scheduledawg.entity.User;
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

    private GradeCategory getOwnedCategory(Long categoryId, User currentUser) {
        GradeCategory category = gradeCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        CourseService.verifyOwnership(category.getCourse(), currentUser);
        return category;
    }

    public GradedItem createItem(GradedItem item, Long categoryId, User currentUser) {
        GradeCategory category = getOwnedCategory(categoryId, currentUser);

        item.setCategory(category);
        calculatePercentScore(item);
        return gradedItemRepository.save(item);
    }

    public List<GradedItem> getItemsByCategory(Long categoryId, User currentUser) {
        getOwnedCategory(categoryId, currentUser);
        return gradedItemRepository.findByCategoryId(categoryId);
    }

    public GradedItem getItemById(Long categoryId, Long itemId, User currentUser) {
        return getItemScopedToCategory(categoryId, itemId, currentUser);
    }

    private GradedItem getItemScopedToCategory(Long categoryId, Long itemId, User currentUser) {
        getOwnedCategory(categoryId, currentUser);

        GradedItem item = gradedItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Graded item not found with id: " + itemId));

        if (!item.getCategory().getId().equals(categoryId)) {
            throw new ResourceNotFoundException("Graded item not found with id: " + itemId);
        }

        return item;
    }

    public GradedItem fullyUpdateItem(GradedItem item, Long categoryId, Long itemId, User currentUser) {
        GradedItem itemToUpdate = getItemScopedToCategory(categoryId, itemId, currentUser);

        itemToUpdate.setTitle(item.getTitle());
        itemToUpdate.setPointsEarned(item.getPointsEarned());
        itemToUpdate.setPointsPossible(item.getPointsPossible());
        itemToUpdate.setPercentScore(item.getPercentScore());

        calculatePercentScore(itemToUpdate);
        return gradedItemRepository.save(itemToUpdate);
    }

    public void deleteItem(Long categoryId, Long itemId, User currentUser) {
        GradedItem itemToDelete = getItemScopedToCategory(categoryId, itemId, currentUser);
        gradedItemRepository.delete(itemToDelete);
    }

    public void deleteAllItemsByCategory(Long categoryId, User currentUser) {
        getOwnedCategory(categoryId, currentUser);
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
