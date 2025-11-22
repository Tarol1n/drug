package com.ittaro.drug.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @className: saleItem
 * @author: Tarol1n
 * @date: 2025/11/22 18:38
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String medicineId;   // 药品ID
    private int quantity;        // 数量
    private double unitPrice;    // 单价（销售时记录当时价格）
}