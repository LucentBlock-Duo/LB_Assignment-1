package com.lucentblock.assignment2.service.item;

import com.lucentblock.assignment2.model.RepairManInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ItemDetailDTO implements Comparable {
    private Long id;
    private Long maintenanceItemId;
    private RepairManInfo repairManInfo;
    private Integer price;

    @Override
    public int compareTo(Object o) {
        if (this == o) {
            return 0;
        }

        if (o == null || getClass() != o.getClass()) {
            return -1; // 또는 다른 비교 결과를 반환할 수 있습니다.
        }

        ItemDetailDTO other = (ItemDetailDTO) o;
        return this.getPrice().compareTo(other.getPrice());
    }
}
