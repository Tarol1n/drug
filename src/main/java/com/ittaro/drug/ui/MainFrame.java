package com.ittaro.drug.ui;

import com.ittaro.drug.manager.DataManager;
import com.ittaro.drug.pojo.Employee;
import com.ittaro.drug.ui.panel.CustomerPanel;
import com.ittaro.drug.ui.panel.MedicinePanel;
import com.ittaro.drug.ui.panel.SalePanel;
import com.ittaro.drug.ui.util.MessageUtil;

import javax.swing.*;


public class MainFrame extends JFrame {
    private DataManager dataManager;
    private Employee currentUser;
    private JTabbedPane tabbedPane;
    private MedicinePanel medicinePanel;
    private SalePanel salePanel; // 👈 新增引用

    public MainFrame(DataManager dataManager, Employee user) {
        this.dataManager = dataManager;
        this.currentUser = user;
        initUI();
    }

    public void refreshMedicinePanel() {
        if (medicinePanel != null) {
            medicinePanel.refreshTable();
        }
    }

    // 新增：供外部调用（虽然目前未用，但结构清晰）
    public void refreshSalePanel() {
        if (salePanel != null) {
            salePanel.refreshData();
        }
    }

    private void initUI() {
        setTitle("药店管理系统");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menu = new JMenu("系统");
        menuBar.add(menu);

        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(e -> confirmAndExit());
        menu.add(exitItem);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmAndExit();
            }
        });

        tabbedPane = new JTabbedPane();
        medicinePanel = new MedicinePanel(dataManager);
        salePanel = new SalePanel(dataManager, this); // 👈 创建并保存引用

        tabbedPane.addTab("药品管理", medicinePanel);
        tabbedPane.addTab("客户管理", new CustomerPanel(dataManager));
        tabbedPane.addTab("销售管理", salePanel); // 使用已创建的实例

        if (currentUser.getRole() == Employee.Role.ADMIN) {
            JMenuItem deleteItem = new JMenuItem("删除药品");
            deleteItem.addActionListener(e -> MessageUtil.showInfo("提示", "管理员权限：可删除药品"));
            menu.add(deleteItem);
        }

        // 监听 Tab 切换，按需刷新
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            String title = tabbedPane.getTitleAt(selectedIndex);
            if ("药品管理".equals(title)) {
                refreshMedicinePanel();
            } else if ("销售管理".equals(title)) {
                refreshSalePanel(); // 👈 关键：进入销售页时刷新客户和药品
            }
        });

        add(tabbedPane);
        setVisible(true);
    }

    private void confirmAndExit() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "确定要退出系统吗？未保存的数据将会丢失！",
                "确认退出",
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            dataManager.saveAll();
            System.out.println("数据已保存，正在退出...");
            System.exit(0);
        }
    }
}