package com.ittaro.drug.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 *
 * @className: Medicine
 * @author: Tarol1n
 * @date: 2025/11/22 18:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicine implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private String id;           // 药品编号（唯一）
    private String name;         // 药品名称
    private String spec;         // 规格（如：0.2g*12片）
    private String manufacturer; // 生产厂商
    private double price;        // 单价
    private int stock;           // 库存数量
    private LocalDate expiryDate;// 有效期
}