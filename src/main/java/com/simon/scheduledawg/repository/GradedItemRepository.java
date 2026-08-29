package com.simon.scheduledawg.repository;

import com.simon.scheduledawg.entity.GradedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradedItemRepository extends JpaRepository<GradedItem, Long> {
    List<GradedItem> findByCategoryId(Long categoryId);
}