package com.black_dog20.modpacksynchelper;

import com.black_dog20.modpacksynchelper.components.MessageConsole;
import com.black_dog20.modpacksynchelper.utils.AppProperties;
import com.black_dog20.modpacksynchelper.utils.DialogUtils;
import com.black_dog20.modpacksynchelper.utils.UrlHelper;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ComponentBuilder {

    private final URL sourceCodeURL = new URL("https://github.com/Crimix/ModpackSyncHelper");

    public ComponentBuilder() throws MalformedURLException {

    }

    public JMenuBar getMenuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("Help");
        JMenuItem viewSource = new JMenuItem("View Source");
        viewSource.addActionListener(e -> {
            try {
                UrlHelper.openLink(sourceCodeURL);
            } catch (Exception exception) {
                System.err.println(exception.getLocalizedMessage());
                DialogUtils.showErrorDialog("Something went wrong with opening the link");
            }
        });
        JMenuItem viewJson = new JMenuItem("View JSON");
        viewJson.addActionListener(e -> {
            try {
                UrlHelper.openLink(new URL(AppProperties.getUrl()));
            } catch (Exception exception) {
                System.err.println(exception.getLocalizedMessage());
                DialogUtils.showErrorDialog("Something went wrong with opening the link");
            }
        });
        menu.add(viewSource);
        menu.add(viewJson);
        menubar.add(menu);
        return menubar;
    }

    public JButton getSyncButton() {
        JButton button = new JButton("Sync mods in folder");
        button.setHorizontalAlignment(JButton.CENTER);
        button.setEnabled(false);
        button.addActionListener(e -> {
            button.setEnabled(false);
            Runnable r = () -> {
                try {
                    new ModHandler().handle();
                    button.setEnabled(true);
                } catch (IOException ioException) {
                    System.err.println(ioException.getLocalizedMessage());
                    DialogUtils.showErrorDialog("Something went wrong while trying to handle sync");
                }
            };
            new Thread(r).start();


        });
        return button;
    }

    public JLabel getWarningLabel() {
        JLabel label = new JLabel("<html>Only use this tool, if you trust the person who gave it to you.<br>It can download files directly from the internet specified by this person.</html>");
        label.setHorizontalAlignment(JButton.CENTER);
        return label;
    }

    public GridBagConstraints getConstraints(int x, int y) {
        return getConstraints(x, y, GridBagConstraints.NONE);
    }

    public GridBagConstraints getConstraints(int x, int y, int fillMode) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = fillMode;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = x;
        c.gridy = y;
        return c;
    }

    public JScrollPane getHtmlView(JButton syncButton) throws Exception {
        JEditorPane editor = new JEditorPane();
        HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("ul { margin-left: 8px; padding: 0px;}");
        editor.setEditorKit(kit);
        editor.setDocument(kit.createDefaultDocument());
        editor.setEditable(false);
        editor.setText(HtmlBuilder.getEmptyHtml());
        editor.addHyperlinkListener(e -> {
            try {
                UrlHelper.openLink(e);
            } catch (Exception exception) {
                System.err.println(exception.getLocalizedMessage());
                DialogUtils.showErrorDialog("Something went wrong with opening the link");
            }
        });
        JScrollPane scrollPane = new JScrollPane(
                editor,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setMinimumSize(new Dimension(500, 200));
        scrollPane.setPreferredSize(new Dimension(500, 300));
        editor.setCaretPosition(0);

        Runnable r = () -> {
            try {
                editor.setText(HtmlBuilder.getHtml());
                syncButton.setEnabled(true);
            } catch (IOException e) {
                System.err.println("Failed in reading online json");
                System.err.println(e.getLocalizedMessage());
            }
        };

        new Thread(r).start();

        return scrollPane;
    }

    public JScrollPane getMessageConsole() {
        JTextComponent textComponent = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(
                textComponent,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(500, 100));

        MessageConsole console = new MessageConsole(textComponent);
        console.redirectOut();
        console.redirectErr(Color.RED, null);

        return scrollPane;
    }
}
