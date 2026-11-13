package com.example.demo.service;

import com.example.demo.entity.InventoryItem;
import com.example.demo.entity.WriteOff;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.Require;
import com.example.demo.repository.InventoryItemRepository;
import com.example.demo.repository.WriteOffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    // Репозитории склада и списаний
    private final InventoryItemRepository inventoryRepository;

    private final WriteOffRepository writeOffRepository;

    // Репозитории внедряются через конструктор
    public InventoryService(InventoryItemRepository inventoryRepository, WriteOffRepository writeOffRepository) {
        this.inventoryRepository = inventoryRepository;
        this.writeOffRepository = writeOffRepository;
    }

    /**
     * Все позиции склада.
     */
    public List<InventoryItem> getAllItems() {
        return inventoryRepository.findAll();
    }

    /**
     * Установить новый остаток позиции (например, после пересчёта).
     */
    public InventoryItem updateStock(Long itemId, Integer quantity) {
        Require.nonNegative(quantity, "Остаток не может быть отрицательным");
        InventoryItem item = inventoryRepository.getOrThrow(itemId);
        item.setQuantity(quantity);
        return inventoryRepository.save(item);
    }

    /**
     * Списать часть позиции со склада.
     * Нельзя списать больше, чем есть. Остаток уменьшается, а само списание сохраняется
     * отдельной записью WriteOff — из этой истории считается статистика для администратора.
     */
    @Transactional
    public InventoryItem writeOff(Long itemId, Integer quantity, String reason) {
        Require.positive(quantity, "Количество для списания должно быть больше нуля");
        InventoryItem item = inventoryRepository.getOrThrow(itemId);
        if (item.getQuantity() < quantity) {
            throw new BusinessException("Недостаточно товара на складе для списания");
        }
        item.setQuantity(item.getQuantity() - quantity);
        item.setWriteOffReason(reason);
        writeOffRepository.save(WriteOff.of(item, quantity, reason));
        return inventoryRepository.save(item);
    }
}
