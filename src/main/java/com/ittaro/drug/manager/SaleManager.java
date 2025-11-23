package com.ittaro.drug.manager;

import com.ittaro.drug.exception.InsufficientStockException;
import com.ittaro.drug.exception.InvalidInputException;
import com.ittaro.drug.pojo.Customer;
import com.ittaro.drug.pojo.Medicine;
import com.ittaro.drug.pojo.SaleItem;
import com.ittaro.drug.pojo.SaleRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @className: SaleManager
 * @author: Tarol1n
 * @date: 2025/11/22 18:38
 */

public class SaleManager {
    private MedicineManager medicineManager;
    private CustomerManager customerManager;
    private List<SaleRecord> sales; // 新增：用于存储所有销售记录

    public SaleManager(MedicineManager medicineManager, CustomerManager customerManager) {
        this.medicineManager = medicineManager;
        this.customerManager = customerManager;
        this.sales = new ArrayList<>();
    }

    // 新增：用于从 DataManager 注入已加载的销售记录
    public void setSales(List<SaleRecord> sales) {
        this.sales = sales != null ? new ArrayList<>(sales) : new ArrayList<>();
    }

    // 新增：获取所有销售记录（供 DataManager 保存用）
    public List<SaleRecord> getSales() {
        return new ArrayList<>(this.sales); // 返回副本，避免外部修改内部状态
    }

    // 创建销售记录
    public SaleRecord createSale(String customerId, List<SaleItem> items) throws InsufficientStockException, InvalidInputException {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new InvalidInputException("客户手机号不能为空");
        }
        if (items == null || items.isEmpty()) {
            throw new InvalidInputException("销售明细不能为空");
        }

        Customer customer = customerManager.findByPhone(customerId);
        if (customer == null) {
            throw new InvalidInputException("客户不存在：" + customerId);
        }

        double totalAmount = 0.0;
        Map<String, Integer> stockNeeded = new HashMap<>();

        // 预计算所需库存并校验
        for (SaleItem item : items) {
            if (item == null || item.getQuantity() <= 0) {
                continue;
            }
            Medicine medicine = medicineManager.findById(item.getMedicineId());
            if (medicine == null) {
                throw new InvalidInputException("药品不存在：" + item.getMedicineId());
            }

            int availableStock = medicineManager.getStock(item.getMedicineId());
            if (availableStock < item.getQuantity()) {
                throw new InsufficientStockException("药品 '" + medicine.getName() + "' 库存不足，当前：" + availableStock + "，需：" + item.getQuantity());
            }

            stockNeeded.merge(item.getMedicineId(), item.getQuantity(), Integer::sum);
            totalAmount += item.getQuantity() * item.getUnitPrice();
        }

        // 扣减库存
        for (Map.Entry<String, Integer> entry : stockNeeded.entrySet()) {
            medicineManager.reduceStock(entry.getKey(), entry.getValue());
        }

        // 计算会员折扣
        double discountRate = customer.getLevel() == Customer.Level.VIP ? 0.95 : 1.0;
        totalAmount *= discountRate;

        // 生成销售记录
        String recordId = generateRecordId();
        LocalDateTime now = LocalDateTime.now();
        SaleRecord saleRecord = new SaleRecord(
                recordId,
                customerId,
                items,
                totalAmount,
                now,
                "admin" // 操作员暂定为 admin，实际可传入登录用户
        );

        // 更新客户累计消费
        customer.setTotalSpent(customer.getTotalSpent() + totalAmount);

        // 添加新创建的销售记录到内存中的列表
        this.sales.add(saleRecord);

        return saleRecord;
    }

    // 生成唯一销售记录ID（简单实现）
    private String generateRecordId() {
        return "SR" + System.currentTimeMillis();
    }

    // 获取销售记录（返回内存中的所有销售记录）
    public List<SaleRecord> getAllSales() {
        return new ArrayList<>(this.sales);
    }
}