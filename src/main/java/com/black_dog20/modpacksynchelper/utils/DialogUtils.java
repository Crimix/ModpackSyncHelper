package com.black_dog20.modpacksynchelper.utils;

import javax.swing.JOptionPane;

public class DialogUtils {

    public static void showErrorDialogAndClose(String message) {
        JOptionPane.showMessageDialog(null,message);
        System.exit(1);
    }
}
