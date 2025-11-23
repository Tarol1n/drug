package com.ittaro.drug.manager;

import com.ittaro.drug.exception.DuplicateIdException;
import com.ittaro.drug.exception.InvalidInputException;
import com.ittaro.drug.pojo.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @className: CustomerManager
 * @author: Tarol1n
 * @date: 2025/11/22 18:37
 */
public class CustomerManager {
    private List<Customer> customers = new ArrayList<>();

    public CustomerManager() {}

    // 添加客户（手机号为主键）
    public void add(Customer customer) throws DuplicateIdException, InvalidInputException {
        if (customer == null) {
            throw new InvalidInputException("客户不能为空");
        }
        if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
            throw new InvalidInputException("手机号不能为空");
        }
        if (findByPhone(customer.getPhone()) != null) {
            throw new DuplicateIdException("手机号已存在：" + customer.getPhone());
        }
        customers.add(customer);
    }

    // 删除客户
    public boolean delete(String phone) {
        Customer c = findByPhone(phone);
        if (c != null) {
            customers.remove(c);
            return true;
        }
        return false;
    }

    // 修改客户信息
    public void update(String phone, Customer newCustomer) throws InvalidInputException, DuplicateIdException {
        Customer oldCustomer = findByPhone(phone);
        if (oldCustomer == null) {
            throw new InvalidInputException("未找到客户：" + phone);
        }

        if (!phone.equals(newCustomer.getPhone())) {
            if (findByPhone(newCustomer.getPhone()) != null) {
                throw new DuplicateIdException("新手机号已存在：" + newCustomer.getPhone());
            }
        }

        oldCustomer.setName(newCustomer.getName());
        oldCustomer.setLevel(newCustomer.getLevel());
        oldCustomer.setTotalSpent(newCustomer.getTotalSpent());
    }

    // 按手机号查找
    public Customer findByPhone(String phone) {
        for (Customer c : customers) {
            if (c.getPhone().equals(phone)) return c;
        }
        return null;
    }

    // 按姓名模糊查询
    public List<Customer> searchByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return customers;
        }
        List<Customer> result = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(c);
            }
        }
        return result;
    }

    // 获取全部客户
    public List<Customer> getAll() {
        return new ArrayList<>(customers);
    }

    // 设置客户列表（用于加载）
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    // 获取客户列表（供持久化使用）
    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }
}