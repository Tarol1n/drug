package com.ittaro.drug;

import com.ittaro.drug.manager.DataManager;
import com.ittaro.drug.ui.LoginFrame;

/**
 *
 * @className: Main
 * @author: Tarol1n
 * @date: 2025/11/22 18:35
 */

public class Main {
    public static void main(String[] args) {
        DataManager dataManager = new DataManager();
        dataManager.loadAll(); // 加载数据
        new LoginFrame(dataManager); // 启动登录界面
    }
}