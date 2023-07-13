package com.lucentblock.assignment2.service.maintenanceItem;

import com.lucentblock.assignment2.entity.maintenanceItem.MaintenanceItem;
import com.lucentblock.assignment2.exception.MaintenanceItemNotFoundException;
import com.lucentblock.assignment2.repository.maintenanceItem.MaintenanceItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @RequiredArgsConstructor
public class MaintenanceItemService {
    private final MaintenanceItemRepository repository;
    public List<MaintenanceItem> fetchItems() {
        return repository.findAll();
    }
}
