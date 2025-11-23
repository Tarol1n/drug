package com.ittaro.drug.manager;

import com.ittaro.drug.exception.DuplicateIdException;
import com.ittaro.drug.exception.InvalidInputException;
import com.ittaro.drug.pojo.Medicine;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @className: MedicineManager
 * @author: Tarol1n
 * @date: 2025/11/22 18:36
 */

public class MedicineManager {
    private List<Medicine> medicines = new ArrayList<>();

    public MedicineManager() {
        // 构造函数可留空，由 DataManager 统一初始化
    }

    // 添加药品（检查ID唯一性）
    public void add(Medicine medicine) throws DuplicateIdException, InvalidInputException {
        if (medicine == null) {
            throw new InvalidInputException("药品不能为空");
        }
        if (medicine.getId() == null || medicine.getId().trim().isEmpty()) {
            throw new InvalidInputException("药品编号不能为空");
        }
        if (medicine.getPrice() < 0) {
            throw new InvalidInputException("单价不能为负");
        }
        if (medicine.getStock() < 0) {
            throw new InvalidInputException("库存不能为负");
        }

        if (findById(medicine.getId()) != null) {
            throw new DuplicateIdException("药品编号已存在：" + medicine.getId());
        }

        medicines.add(medicine);
    }

    // 删除药品
    public boolean delete(String id) {
        Medicine medicine = findById(id);
        if (medicine != null) {
            medicines.remove(medicine);
            return true;
        }
        return false;
    }

    // 修改药品信息
    public void update(String id, Medicine newMedicine) throws InvalidInputException, DuplicateIdException {
        Medicine oldMedicine = findById(id);
        if (oldMedicine == null) {
            throw new InvalidInputException("未找到药品：" + id);
        }

        if (!id.equals(newMedicine.getId())) {
            if (findById(newMedicine.getId()) != null) {
                throw new DuplicateIdException("新药品编号已存在：" + newMedicine.getId());
            }
        }

        oldMedicine.setName(newMedicine.getName());
        oldMedicine.setSpec(newMedicine.getSpec());
        oldMedicine.setManufacturer(newMedicine.getManufacturer());
        oldMedicine.setPrice(newMedicine.getPrice());
        oldMedicine.setStock(newMedicine.getStock());
        oldMedicine.setExpiryDate(newMedicine.getExpiryDate());
    }

    // 按名称模糊查询
    public List<Medicine> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return medicines;
        }
        List<Medicine> result = new ArrayList<>();
        for (Medicine m : medicines) {
            if (m.getName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(m);
            }
        }
        return result;
    }

    // 根据ID查找
    public Medicine findById(String id) {
        for (Medicine m : medicines) {
            if (m.getId().equals(id)) {
                return m;
            }
        }
        return null;
    }

    // 获取全部药品
    public List<Medicine> getAll() {
        return new ArrayList<>(medicines); // 返回副本避免外部修改
    }

    // 扣减库存（用于销售）
    public boolean reduceStock(String id, int quantity) {
        Medicine medicine = findById(id);
        if (medicine == null || medicine.getStock() < quantity) {
            return false;
        }
        medicine.setStock(medicine.getStock() - quantity);
        return true;
    }

    // 获取库存
    public int getStock(String id) {
        Medicine m = findById(id);
        return m != null ? m.getStock() : 0;
    }

    // 设置药品列表（用于从文件加载）
    public void setMedicines(List<Medicine> medicines) {
        this.medicines = medicines;
    }

    // 获取药品列表（供持久化使用）
    public List<Medicine> getMedicines() {
        return new ArrayList<>(medicines);
    }
}