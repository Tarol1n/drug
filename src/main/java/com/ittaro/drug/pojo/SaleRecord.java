package com.ittaro.drug.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @className: SaleRecord
 * @author: Tarol1n
 * @date: 2025/11/22 18:38
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private String recordId;          // 记录编号（可自动生成）
    private String customerId;        // 客户手机号
    private List<SaleItem> items;     // 销售明细列表
    private double totalAmount;       // 总金额（含折扣）
    private LocalDateTime saleTime;   // 销售时间
    private String operator;          // 操作员姓名
}