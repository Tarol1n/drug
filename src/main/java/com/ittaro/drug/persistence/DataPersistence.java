package com.ittaro.drug.persistence;

import com.ittaro.drug.pojo.Customer;
import com.ittaro.drug.pojo.Employee;
import com.ittaro.drug.pojo.Medicine;
import com.ittaro.drug.pojo.SaleRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @className: DataPersistence
 * @author: Tarol1n
 * @date: 2025/11/22 18:36
 */

public class DataPersistence {
    private static final String MEDICINES_FILE = "data/medicines.dat";
    private static final String CUSTOMERS_FILE = "data/customers.dat";
    private static final String SALES_FILE = "data/sales.dat";

    // 在类顶部新增文件路径常量
    private static final String EMPLOYEES_FILE = "data/employees.dat";

    private void ensureDataDirExists() {
        File dir = new File("data");
        if (!dir.exists()) {
            boolean created = dir.mkdirs(); // 创建多级目录（虽然这里只有一级）
            if (created) {
                System.out.println("已创建 data 目录");
            } else {
                System.err.println("无法创建 data 目录，请检查权限");
            }
        }
    }

    public void saveMedicines(List<Medicine> medicines) {
        ensureDataDirExists(); // 👈 确保目录存在
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MEDICINES_FILE))) {
            oos.writeObject(medicines);
        } catch (IOException e) {
            System.err.println("保存药品数据失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void saveCustomers(List<Customer> customers) {
        ensureDataDirExists();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CUSTOMERS_FILE))) {
            oos.writeObject(customers);
        } catch (IOException e) {
            System.err.println("保存客户数据失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void saveEmployees(List<Employee> employees) {
        ensureDataDirExists();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EMPLOYEES_FILE))) {
            oos.writeObject(employees);
        } catch (IOException e) {
            System.err.println("保存员工数据失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void saveSales(List<SaleRecord> sales) {
        ensureDataDirExists();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SALES_FILE))) {
            oos.writeObject(sales);
        } catch (IOException e) {
            System.err.println("保存销售数据失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }



    // 加载员工列表
    public List<Employee> loadEmployees() {
        List<Employee> employees = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EMPLOYEES_FILE))) {
            employees = (List<Employee>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("员工文件不存在，初始化空列表");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载员工数据失败：" + e.getMessage());
        }
        return employees;
    }


    // 加载药品列表
    public List<Medicine> loadMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MEDICINES_FILE))) {
            medicines = (List<Medicine>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("药品文件不存在，初始化空列表");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载药品数据失败：" + e.getMessage());
        }
        return medicines;
    }


    // 加载客户列表
    public List<Customer> loadCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CUSTOMERS_FILE))) {
            customers = (List<Customer>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("客户文件不存在，初始化空列表");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载客户数据失败：" + e.getMessage());
        }
        return customers;
    }



    // 加载销售记录列表
    public List<SaleRecord> loadSales() {
        List<SaleRecord> sales = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SALES_FILE))) {
            sales = (List<SaleRecord>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("销售文件不存在，初始化空列表");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("加载销售数据失败：" + e.getMessage());
        }
        return sales;
    }
}