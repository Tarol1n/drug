package com.ittaro.drug.manager;

import com.ittaro.drug.pojo.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @className: EmployeeManager
 * @author: Tarol1n
 * @date: 2025/11/22 19:22
 */

public class EmployeeManager {
    private List<Employee> employees = new ArrayList<>();

    public void setEmployees(List<Employee> employees) {
        this.employees = employees != null ? new ArrayList<>(employees) : new ArrayList<>();
    }

    public List<Employee> getEmployees() {
        return new ArrayList<>(employees); // 返回副本，避免外部修改
    }

    /**
     * 根据用户名查找员工
     */
    public Employee findByUsername(String username) {
        return employees.stream()
                .filter(emp -> emp.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * 验证登录：用户名 + 密码
     */
    public Employee login(String username, String password) {
        return employees.stream()
                .filter(emp -> emp.getUsername().equals(username)
                        && emp.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    /**
     * 添加员工（可选，用于注册或管理员添加）
     */
    public boolean add(Employee employee) {
        if (findByUsername(employee.getUsername()) != null) {
            return false; // 用户名已存在
        }
        employees.add(employee);
        return true;
    }

    /**
     * 删除员工（按用户名）
     */
    public boolean remove(String username) {
        return employees.removeIf(emp -> emp.getUsername().equals(username));
    }
}