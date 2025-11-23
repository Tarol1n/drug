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
    private MainFrame mainFrame; // 新增引用

    private DefaultListModel<String> medicineListModel;
    private DefaultListModel<SaleItem> cartModel;
    private JComboBox<Object> customerComboBox;
    private JTextField quantityField;
    private JButton addToCartBtn, checkoutBtn; // 已移除 refreshMedicineBtn

    // 修改构造函数：接收 MainFrame
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
        JList<String> medicineList = new JList<>(medicineListModel);
        medicineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftPanel.add(new JScrollPane(medicineList), BorderLayout.CENTER);

        // 右侧：购物车
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("购物车", JLabel.CENTER), BorderLayout.NORTH);

        cartModel = new DefaultListModel<>();
        JList<SaleItem> cartList = new JList<>(cartModel);
        rightPanel.add(new JScrollPane(cartList), BorderLayout.CENTER);

        // 底部：操作区
        JPanel bottomPanel = new JPanel(new FlowLayout());

        // 客户选择
        customerComboBox = new JComboBox<>();
        customerComboBox.addItem("请选择客户");

        // 自定义渲染器
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

        // 加载真实客户数据
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

        // 事件绑定
        addToCartBtn.addActionListener(e -> addToCart(medicineList.getSelectedIndex()));
        checkoutBtn.addActionListener(e -> checkout());

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateMedicineList() {
        medicineListModel.clear();
        List<Medicine> medicines = medicineManager.getAll();
        for (Medicine m : medicines) {
            medicineListModel.addElement(m.getName() + " (" + m.getId() + ")");
        }
    }

    private void addToCart(int selectedIndex) {
        if (selectedIndex == -1) {
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

        String displayText = medicineListModel.getElementAt(selectedIndex);
        int start = displayText.lastIndexOf('(');
        int end = displayText.lastIndexOf(')');
        if (start == -1 || end == -1 || start >= end) {
            MessageUtil.showError("错误", "药品格式异常");
            return;
        }
        String id = displayText.substring(start + 1, end);

        Medicine medicine = medicineManager.findById(id);
        if (medicine == null) {
            MessageUtil.showError("错误", "未找到该药品");
            return;
        }

        double unitPrice = medicine.getPrice();
        SaleItem item = new SaleItem(id, quantity, unitPrice);
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

            // 👇 通知药品面板刷新（库存可能已变）
            if (mainFrame != null) {
                mainFrame.refreshMedicinePanel();
            }

        } catch (Exception e) {
            MessageUtil.showError("错误", "结账失败：" + e.getMessage());
        }
    }

    /**
     * 刷新客户下拉框和药品列表（供 MainFrame 在切换 Tab 时调用）
     */
    public void refreshData() {
        // 重新加载客户列表
        customerComboBox.removeAllItems();
        customerComboBox.addItem("请选择客户");
        List<Customer> customers = dataManager.getCustomerManager().getAll();
        for (Customer customer : customers) {
            customerComboBox.addItem(customer);
        }

        // 重新加载药品列表
        updateMedicineList();
    }
}