package com.ittaro.drug.manager;

import com.ittaro.drug.persistence.DataPersistence;
import com.ittaro.drug.pojo.Customer;
import com.ittaro.drug.pojo.Employee;
import com.ittaro.drug.pojo.Medicine;
import com.ittaro.drug.pojo.SaleRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @className: DataManager
 * @author: Tarol1n
 * @date: 2025/11/22 18:38
 */
@Data
@AllArgsConstructor
public class DataManager {
    // Getters
    private MedicineManager medicineManager;
    private CustomerManager customerManager;
    private SaleManager saleManager;
    private EmployeeManager employeeManager;
    private DataPersistence persistence;

    public DataManager() {
        this.persistence = new DataPersistence();
        this.medicineManager = new MedicineManager();
        this.customerManager = new CustomerManager();
        this.employeeManager = new EmployeeManager();
        this.saleManager = new SaleManager(medicineManager, customerManager);
    }

    public void loadAll() {
        List<Medicine> meds = persistence.loadMedicines();
        List<Customer> custs = persistence.loadCustomers();
        List<Employee> emps = persistence.loadEmployees();
        List<SaleRecord> sales = persistence.loadSales();

        boolean needInitEmployees = false;

        // 只有员工数据为空时，才初始化默认员工（这是合理的）
        if (emps.isEmpty()) {
            needInitEmployees = true;
            emps.add(new Employee("admin", "123456", Employee.Role.ADMIN));
            emps.add(new Employee("user", "123456", Employee.Role.STAFF));
        }

        // 设置数据到各 Manager
        medicineManager.setMedicines(meds);
        customerManager.setCustomers(custs);
        employeeManager.setEmployees(emps);
        saleManager.setSales(sales);

        // 如果初始化了员工，说明是首次运行（或员工文件丢失），此时保存全部数据
        if (needInitEmployees) {
            saveAll(); // 包含 employees + 其他可能为空的数据（但不会覆盖已有数据）
            System.out.println("检测到无员工数据，已生成默认账号并保存到 data/ 目录");
        }
    }

    public void saveAll() {
        persistence.saveMedicines(medicineManager.getMedicines());
        persistence.saveCustomers(customerManager.getCustomers());
        persistence.saveEmployees(employeeManager.getEmployees());
        persistence.saveSales(saleManager.getSales());
    }

}