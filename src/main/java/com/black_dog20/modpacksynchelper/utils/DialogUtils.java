package com.black_dog20.modpacksynchelper.utils;

import javax.swing.JOptionPane;

public class DialogUtils {

    public static void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showErrorDialogAndClose(String message) {
        showErrorDialog(message);
        System.exit(1);
    }

    public static boolean showChoiceDialog(String message) {
        return JOptionPane.showConfirmDialog(null, message, "Error", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}
