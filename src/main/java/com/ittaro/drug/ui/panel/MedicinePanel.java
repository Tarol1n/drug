package com.ittaro.drug.ui.panel;

import com.ittaro.drug.manager.DataManager;
import com.ittaro.drug.manager.MedicineManager;
import com.ittaro.drug.pojo.Medicine;
import com.ittaro.drug.ui.util.MessageUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * @className: MedicinePanel
 * @author: Tarol1n
 * @date: 2025/11/22 18:37
 */
public class MedicinePanel extends JPanel {
    private MedicineManager medicineManager;
    private DefaultTableModel tableModel;
    private JTable table;
    private DataManager dataManager;

    public MedicinePanel(DataManager dataManager) {
        this.dataManager = dataManager;
        this.medicineManager = dataManager.getMedicineManager();
        initUI();
        refreshTable();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("药品管理"));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchBtn = new JButton("搜索");
        JButton addBtn = new JButton("新增");
        JButton editBtn = new JButton("修改");
        JButton deleteBtn = new JButton("删除");

        topPanel.add(searchBtn);
        topPanel.add(addBtn);
        topPanel.add(editBtn);
        topPanel.add(deleteBtn);

        String[] columnNames = {"编号", "名称", "规格", "厂商", "单价", "库存", "有效期"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> showEditDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        searchBtn.addActionListener(e -> search());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        // 底部 formPanel 已移除
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Medicine> medicines = medicineManager.getAll();
        for (Medicine m : medicines) {
            Object[] row = {
                    m.getId(),
                    m.getName(),
                    m.getSpec(),
                    m.getManufacturer(),
                    m.getPrice(),
                    m.getStock(),
                    m.getExpiryDate()
            };
            tableModel.addRow(row);
        }
    }

    private void search() {
        String keyword = JOptionPane.showInputDialog(this, "请输入药品名称关键字：");
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        List<Medicine> results = medicineManager.searchByName(keyword);
        tableModel.setRowCount(0);
        for (Medicine m : results) {
            Object[] row = {
                    m.getId(),
                    m.getName(),
                    m.getSpec(),
                    m.getManufacturer(),
                    m.getPrice(),
                    m.getStock(),
                    m.getExpiryDate()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddDialog() {
        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JTextField spec = new JTextField();
        JTextField manufacturer = new JTextField();
        JTextField price = new JTextField();
        JTextField stock = new JTextField();
        JTextField expiry = new JTextField();

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.add(new JLabel("编号："));
        panel.add(id);
        panel.add(new JLabel("名称："));
        panel.add(name);
        panel.add(new JLabel("规格："));
        panel.add(spec);
        panel.add(new JLabel("厂商："));
        panel.add(manufacturer);
        panel.add(new JLabel("单价："));
        panel.add(price);
        panel.add(new JLabel("库存："));
        panel.add(stock);
        panel.add(new JLabel("有效期："));
        panel.add(expiry);

        int result = JOptionPane.showConfirmDialog(this, panel, "新增药品", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Medicine medicine = new Medicine(
                    id.getText().trim(),
                    name.getText().trim(),
                    spec.getText().trim(),
                    manufacturer.getText().trim(),
                    Double.parseDouble(price.getText()),
                    Integer.parseInt(stock.getText()),
                    LocalDate.parse(expiry.getText())
            );

            medicineManager.add(medicine);
            refreshTable();
            MessageUtil.showInfo("成功", "药品添加成功！");
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

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        Medicine medicine = medicineManager.findById(id);
        if (medicine == null) {
            return;
        }

        JTextField name = new JTextField(medicine.getName());
        JTextField spec = new JTextField(medicine.getSpec());
        JTextField manufacturer = new JTextField(medicine.getManufacturer());
        JTextField price = new JTextField(String.valueOf(medicine.getPrice()));
        JTextField stock = new JTextField(String.valueOf(medicine.getStock()));
        JTextField expiry = new JTextField(medicine.getExpiryDate().toString());

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("名称："));
        panel.add(name);
        panel.add(new JLabel("规格："));
        panel.add(spec);
        panel.add(new JLabel("厂商："));
        panel.add(manufacturer);
        panel.add(new JLabel("单价："));
        panel.add(price);
        panel.add(new JLabel("库存："));
        panel.add(stock);
        panel.add(new JLabel("有效期："));
        panel.add(expiry);

        int result = JOptionPane.showConfirmDialog(this, panel, "修改药品", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Medicine updated = new Medicine(
                    id,
                    name.getText().trim(),
                    spec.getText().trim(),
                    manufacturer.getText().trim(),
                    Double.parseDouble(price.getText()),
                    Integer.parseInt(stock.getText()),
                    LocalDate.parse(expiry.getText())
            );
            medicineManager.update(id, updated);
            refreshTable();
            MessageUtil.showInfo("成功", "药品修改成功！");
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

        String id = (String) tableModel.getValueAt(selectedRow, 0);
        if (MessageUtil.confirm("确认", "确定要删除该药品吗？")) {
            if (medicineManager.delete(id)) {
                refreshTable();
                MessageUtil.showInfo("成功", "药品删除成功！");
            } else {
                MessageUtil.showError("错误", "删除失败");
            }
        }
    }
}