package com.ittaro.drug.ui.util;

import javax.swing.*;

/**
 *
 * @className: MessageUtil
 * @author: Tarol1n
 * @date: 2025/11/22 18:39
 */
public class MessageUtil {
    public static void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(String title, String message) {
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}