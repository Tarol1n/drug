package com.ittaro.drug.ui.panel;

import com.ittaro.drug.manager.DataManager;
import com.ittaro.drug.manager.MedicineManager;
import com.ittaro.drug.manager.SaleManager;
import com.ittaro.drug.pojo.Customer;
import com.ittaro.drug.pojo.Medicine;
import com.ittaro.drug.pojo.SaleItem;
import com.ittaro.drug.pojo.SaleRecord;
import com.ittaro.drug.ui.MainFrame;
import com.ittaro.drug.ui.util.MessageUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @className: SalePanel
 * @author: Tarol1n
 * @date: 2025/11/22 18:39
 */
public class SalePanel extends JPanel {
    private SaleManager saleManager;
    private MedicineManager medicineManager;
    private DataManager dataManager;
    private MainFrame mainFrame;

    // 👇 改为存储 Medicine 对象，而不是 String
    private DefaultListModel<Medicine> medicineListModel;
    private DefaultListModel<SaleItem> cartModel;
    private JComboBox<Object> customerComboBox;
    private JTextField quantityField;
    private JButton addToCartBtn, checkoutBtn;

    public SalePanel(DataManager dataManager, MainFrame mainFrame) {
        this.dataManager = dataManager;
        this.mainFrame = mainFrame;
        this.saleManager = dataManager.getSaleManager();
        this.medicineManager = dataManager.getMedicineManager();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("销售管理"));

        // 左侧：药品列表
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("药品列表", JLabel.CENTER), BorderLayout.NORTH);

        medicineListModel = new DefaultListModel<>();
        updateMedicineList();

        JList<Medicine> medicineList = new JList<>(medicineListModel);
        medicineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 自定义渲染器：显示名称 + 库存
        medicineList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Medicine) {
                    Medicine m = (Medicine) value;
                    setText(m.getName() + " (库存: " + m.getStock() + ")");
                } else {
                    setText(value == null ? "" : value.toString());
                }
                return this;
            }
        });

        leftPanel.add(new JScrollPane(medicineList), BorderLayout.CENTER);

        // 右侧：购物车
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("购物车", JLabel.CENTER), BorderLayout.NORTH);

        cartModel = new DefaultListModel<>();
        JList<SaleItem> cartList = new JList<>(cartModel);
        // 可选：为购物车项添加渲染器
        cartList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SaleItem) {
                    SaleItem item = (SaleItem) value;
                    Medicine med = dataManager.getMedicineManager().findById(item.getMedicineId());
                    String name = med != null ? med.getName() : "未知药品";
                    setText(name + " × " + item.getQuantity() + " = ¥" + String.format("%.2f", item.getTotalPrice()));
                } else {
                    setText(value == null ? "" : value.toString());
                }
                return this;
            }
        });
        rightPanel.add(new JScrollPane(cartList), BorderLayout.CENTER);

        // 底部：操作区
        JPanel bottomPanel = new JPanel(new FlowLayout());

        customerComboBox = new JComboBox<>();
        customerComboBox.addItem("请选择客户");

        customerComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Customer) {
                    Customer c = (Customer) value;
                    setText(c.getName() + " (" + c.getPhone() + ")");
                } else {
                    setText(value == null ? "" : value.toString());
                }
                return this;
            }
        });

        List<Customer> customers = dataManager.getCustomerManager().getAll();
        for (Customer customer : customers) {
            customerComboBox.addItem(customer);
        }

        quantityField = new JTextField("1", 5);
        addToCartBtn = new JButton("加入购物车");
        checkoutBtn = new JButton("结账");

        bottomPanel.add(new JLabel("客户："));
        bottomPanel.add(customerComboBox);
        bottomPanel.add(new JLabel("数量："));
        bottomPanel.add(quantityField);
        bottomPanel.add(addToCartBtn);
        bottomPanel.add(checkoutBtn);

        addToCartBtn.addActionListener(e -> addToCart(medicineList.getSelectedValue()));
        checkoutBtn.addActionListener(e -> checkout());

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateMedicineList() {
        medicineListModel.clear();
        List<Medicine> medicines = medicineManager.getAll();
        for (Medicine m : medicines) {
            medicineListModel.addElement(m);
        }
    }

    // 👇 直接传 Medicine 对象，避免解析字符串
    private void addToCart(Medicine selectedMedicine) {
        if (selectedMedicine == null) {
            MessageUtil.showError("错误", "请先选择药品");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                MessageUtil.showError("错误", "数量必须大于0");
                return;
            }
        } catch (NumberFormatException e) {
            MessageUtil.showError("错误", "请输入有效数字");
            return;
        }

        // 检查库存
        if (quantity > selectedMedicine.getStock()) {
            MessageUtil.showError("错误", "库存不足！当前库存：" + selectedMedicine.getStock());
            return;
        }

        SaleItem item = new SaleItem(selectedMedicine.getId(), quantity, selectedMedicine.getPrice());
        cartModel.addElement(item);
        MessageUtil.showInfo("成功", "已加入购物车");
    }

    private void checkout() {
        if (cartModel.isEmpty()) {
            MessageUtil.showError("错误", "购物车为空");
            return;
        }

        Object selected = customerComboBox.getSelectedItem();
        if (!(selected instanceof Customer)) {
            MessageUtil.showError("错误", "请选择有效客户");
            return;
        }

        String customerId = ((Customer) selected).getPhone();
        List<SaleItem> cartItems = new ArrayList<>();
        for (int i = 0; i < cartModel.getSize(); i++) {
            cartItems.add(cartModel.getElementAt(i));
        }

        try {
            SaleRecord record = saleManager.createSale(customerId, cartItems);
            MessageUtil.showInfo("成功", "销售完成！订单号：" + record.getRecordId());
            cartModel.clear();

            // 👇 关键三连：刷新自身 + 主面板药品 + 主面板客户
            updateMedicineList(); // ✅ 立即更新当前页面的库存显示！
            if (mainFrame != null) {
                mainFrame.refreshMedicinePanel();
                mainFrame.refreshCustomerPanel();
            }

        } catch (Exception e) {
            MessageUtil.showError("错误", "结账失败：" + e.getMessage());
        }
    }

    public void refreshData() {
        customerComboBox.removeAllItems();
        customerComboBox.addItem("请选择客户");
        List<Customer> customers = dataManager.getCustomerManager().getAll();
        for (Customer customer : customers) {
            customerComboBox.addItem(customer);
        }

        updateMedicineList();
    }
}