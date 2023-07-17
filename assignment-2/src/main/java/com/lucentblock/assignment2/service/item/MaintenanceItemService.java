package com.lucentblock.assignment2.service.item;

import com.lucentblock.assignment2.entity.item.MaintenanceItem;
import com.lucentblock.assignment2.exception.MaintenanceItemNotFoundException;
import com.lucentblock.assignment2.repository.item.MaintenanceItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class MaintenanceItemService {
    private final MaintenanceItemRepository repository;
    public List<MaintenanceItem> getAllItems() {
        return repository.findAll();
    }
    public List<MaintenanceItem> getItemsByIds(List<Long> ids) {
        return repository.findByIdIn(ids);
    }
    public MaintenanceItem getItemById(Long id) { return repository.findById(id).orElseThrow(() -> new MaintenanceItemNotFoundException(id.toString())); }
}
