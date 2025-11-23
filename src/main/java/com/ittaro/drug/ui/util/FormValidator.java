package com.ittaro.drug.ui.util;

import java.time.LocalDate;

/**
 *
 * @className: FormValidator
 * @author: Tarol1n
 * @date: 2025/11/22 18:37
 */
public class FormValidator {
    // 验证字符串非空且不全为空格
    public static boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }

    // 验证价格是否有效（>=0）
    public static boolean isValidPrice(double price) {
        return price >= 0;
    }

    // 验证库存是否有效（>=0）
    public static boolean isValidStock(int stock) {
        return stock >= 0;
    }

    // 验证手机号格式（简单正则）
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }

    // 验证日期是否过期
    public static boolean isExpired(LocalDate expiryDate) {
        return expiryDate.isBefore(LocalDate.now());
    }
}