package com.ittaro.drug.ui;

import com.ittaro.drug.manager.DataManager;
import com.ittaro.drug.pojo.Employee;
import com.ittaro.drug.ui.util.MessageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @className: LoginFrame
 * @author: Tarol1n
 * @date: 2025/11/22 18:36
 */

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private DataManager dataManager;

    public LoginFrame(DataManager dataManager) {
        this.dataManager = dataManager;
        initUI();
    }

    private void initUI() {
        setTitle("药店管理系统 - 登录");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("用户名："));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("密码："));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginBtn = new JButton("登录");
        loginBtn.addActionListener(e -> login());
        panel.add(loginBtn);

        add(panel);

        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 登录界面没有修改数据，可以直接退出
                System.exit(0);
            }
        });

    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            MessageUtil.showError("错误", "请输入用户名和密码");
            return;
        }

        // 简单预设用户（实际应从文件读取）
        Employee admin = new Employee("admin", "123", Employee.Role.ADMIN);
        Employee staff = new Employee("staff", "123", Employee.Role.STAFF);

        Employee user = null;
        if (username.equals("admin") && password.equals("123")) {
            user = admin;
        } else if (username.equals("staff") && password.equals("123")) {
            user = staff;
        }

        if (user == null) {
            MessageUtil.showError("错误", "用户名或密码错误");
            return;
        }

        // 关闭登录窗，打开主窗口
        dispose();
        new MainFrame(dataManager, user);
    }
}