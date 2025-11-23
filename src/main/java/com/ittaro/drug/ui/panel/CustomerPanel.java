package com.ittaro.drug.ui.panel;

import com.ittaro.drug.manager.CustomerManager;
import com.ittaro.drug.manager.DataManager;
import com.ittaro.drug.pojo.Customer;
import com.ittaro.drug.ui.util.MessageUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * @className: CustomerPanel
 * @author: Tarol1n
 * @date: 2025/11/22 18:39
 */
public class CustomerPanel extends JPanel {
    private CustomerManager customerManager;
    private DefaultTableModel tableModel;
    private JTable table;
    private DataManager dataManager;

    public CustomerPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        this.customerManager = dataManager.getCustomerManager();
        initUI();
        refreshTable();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("客户管理"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("新增");
        JButton editBtn = new JButton("修改");
        JButton deleteBtn = new JButton("删除");
        JButton searchBtn = new JButton("搜索");

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(searchBtn);

        String[] columns = {"手机号", "姓名", "等级", "累计消费"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        searchBtn.addActionListener(e -> search());

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        // 底部 formPanel 已移除
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerManager.getAll();
        for (Customer c : customers) {
            Object[] row = {
                    c.getPhone(),
                    c.getName(),
                    c.getLevel(),
                    String.format("%.2f", c.getTotalSpent())
            };
            tableModel.addRow(row);
        }
    }

    private void search() {
        String keyword = JOptionPane.showInputDialog(this, "请输入客户姓名关键字：");
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        List<Customer> results = customerManager.searchByName(keyword);
        tableModel.setRowCount(0);
        for (Customer c : results) {
            Object[] row = {
                    c.getPhone(),
                    c.getName(),
                    c.getLevel(),
                    String.format("%.2f", c.getTotalSpent())
            };
            tableModel.addRow(row);
        }
    }

    private void showAddDialog() {
        JTextField phone = new JTextField();
        JTextField name = new JTextField();
        JTextField level = new JTextField();
        JTextField spent = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("手机号："));
        panel.add(phone);
        panel.add(new JLabel("姓名："));
        panel.add(name);
        panel.add(new JLabel("等级："));
        panel.add(level);
        panel.add(new JLabel("累计消费："));
        panel.add(spent);

        int result = JOptionPane.showConfirmDialog(this, panel, "新增客户", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Customer customer = new Customer(
                    phone.getText().trim(),
                    name.getText().trim(),
                    Customer.Level.valueOf(level.getText().toUpperCase()),
                    Double.parseDouble(spent.getText())
            );

            customerManager.add(customer);
            refreshTable();
            MessageUtil.showInfo("成功", "客户添加成功！");
        } catch (Exception e) {
            MessageUtil.showError("错误", "添加失败：" + e.getMessage());
        }
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            MessageUtil.showError("错误", "请先选择一条记录");
            return;
        }

        String phone = (String) tableModel.getValueAt(selectedRow, 0);
        Customer customer = customerManager.findByPhone(phone);
        if (customer == null) {
            return;
        }

        JTextField name = new JTextField(customer.getName());
        JTextField level = new JTextField(customer.getLevel().toString());
        JTextField spent = new JTextField(String.valueOf(customer.getTotalSpent()));

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("姓名："));
        panel.add(name);
        panel.add(new JLabel("等级："));
        panel.add(level);
        panel.add(new JLabel("累计消费："));
        panel.add(spent);

        int result = JOptionPane.showConfirmDialog(this, panel, "修改客户", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Customer updated = new Customer(
                    phone,
                    name.getText().trim(),
                    Customer.Level.valueOf(level.getText().toUpperCase()),
                    Double.parseDouble(spent.getText())
            );
            customerManager.update(phone, updated);
            refreshTable();
            MessageUtil.showInfo("成功", "客户修改成功！");
        } catch (Exception e) {
            MessageUtil.showError("错误", "修改失败：" + e.getMessage());
        }
    }

    private void deleteSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            MessageUtil.showError("错误", "请先选择一条记录");
            return;
        }

        String phone = (String) tableModel.getValueAt(selectedRow, 0);
        if (MessageUtil.confirm("确认", "确定要删除该客户吗？")) {
            if (customerManager.delete(phone)) {
                refreshTable();
                MessageUtil.showInfo("成功", "客户删除成功！");
            } else {
                MessageUtil.showError("错误", "删除失败");
            }
        }
    }
}