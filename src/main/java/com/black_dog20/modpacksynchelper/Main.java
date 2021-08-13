package com.black_dog20.modpacksynchelper;

import com.black_dog20.modpacksynchelper.handler.ModHandler;
import com.black_dog20.modpacksynchelper.json.ModsSyncInfo;
import com.black_dog20.modpacksynchelper.utils.AppProperties;
import com.black_dog20.modpacksynchelper.utils.DialogUtils;
import com.black_dog20.modpacksynchelper.utils.JsonUtil;
import com.black_dog20.modpacksynchelper.utils.UrlHelper;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private final URL sourceCodeURL = new URL("https://github.com/Crimix");

    public Main() throws MalformedURLException {
    }

    public static void main(String[] args) {
        try {
            Main main = new Main();
            main.display();
        } catch (Exception exception) {
            DialogUtils.showErrorDialogAndClose("The app encountered a unexpected error");
        }
    }

    private void display() throws Exception {
        JFrame frame = new JFrame(String.format("ModpackSyncHelper (%s) - %s", AppProperties.getModpackName(), AppProperties.getVersion()));
        frame.setJMenuBar(getMenuBar());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(Box.createHorizontalStrut(10), getConstraints(0, 0));
        panel.add(Box.createVerticalStrut(10), getConstraints(1, 0));
        panel.add(getWarningLabel(), getConstraints(1, 1));
        panel.add(Box.createVerticalStrut(10), getConstraints(1, 2));
        panel.add(getSyncButton(), getConstraints(1, 3));
        panel.add(Box.createVerticalStrut(10), getConstraints(1, 4));
        panel.add(getHtmlView(), getConstraints(1, 5));
        panel.add(Box.createVerticalStrut(10), getConstraints(1, 6));
        panel.add(Box.createHorizontalStrut(10), getConstraints(2, 0));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JMenuBar getMenuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("Help");
        JMenuItem viewSource = new JMenuItem("View Source");
        viewSource.addActionListener(e -> {
            try {
                UrlHelper.openLink(sourceCodeURL);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null,"Something went wrong with opening the link");
            }
        });
        JMenuItem viewJson = new JMenuItem("View JSON");
        viewJson.addActionListener(e -> {
            try {
                UrlHelper.openLink(new URL(AppProperties.getUrl()));
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null,"Something went wrong with opening the link");
            }
        });
        menu.add(viewSource);
        menu.add(viewJson);
        menubar.add(menu);
        return menubar;
    }

    private JButton getSyncButton() {
        JButton button = new JButton("Sync mods in folder");
        button.setHorizontalAlignment(JButton.CENTER);
        button.addActionListener(e -> {
            try {
                new ModHandler().handle();
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(null,"Something went wrong while trying to handle sync");
            }
        });
        return button;
    }

    private JLabel getWarningLabel() {
        JLabel label = new JLabel("<html>Only use this tool, if you trust the person who gave it to you.<br>It can download files directly from the internet specified by this person.</html>");
        label.setHorizontalAlignment(JButton.CENTER);
        return label;
    }

    private GridBagConstraints getConstraints(int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = x;
        c.gridy = y;
        return c;
    }

    private JScrollPane getHtmlView() throws Exception {
        ModsSyncInfo modsSyncInfo = JsonUtil.getModsSyncInfo();

        String modChangeStateHtml = modsSyncInfo.getModsToChangeState().stream()
                .map(o -> String.format("<li>%s disabled: %s</li>\n", o.getName(), !o.isActive()))
                .collect(Collectors.joining("", "<b>Mods to change state</b><ul>\n", "</ul>"));

        String modDeleteHtml = modsSyncInfo.getModsToDelete().stream()
                .map(o -> String.format("<li>%s</li>\n", o.getName()))
                .collect(Collectors.joining("", "<b>Mods to delete</b><ul>\n", "</ul>"));

        String modDownloadHtml = modsSyncInfo.getModsToDownload().stream()
                .map(o -> String.format("<li><a href=\"%s\">%s (%s)</li>\n", o.getDownloadUrl(), o.getName(), UrlHelper.getDomainName(o.getDownloadUrl())))
                .collect(Collectors.joining("", "<b>Mods to download</b><ul>\n", "</ul>"));

        List<String> elements = List.of(modChangeStateHtml, modDeleteHtml, modDownloadHtml);

        String html = "<html><body style='width: 400px;'>" + elements.stream()
                .collect(Collectors.joining("", "" ,"<br><br>"))
                + "</body></html>";

        JEditorPane editor = new JEditorPane();
        HTMLEditorKit kit = new HTMLEditorKit();
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("ul { margin-left: 8px; padding: 0px;}");
        editor.setEditorKit(kit);
        editor.setDocument(kit.createDefaultDocument());
        editor.setEditable(false);
        editor.setText(html);
        editor.addHyperlinkListener(e -> {
            try {
                UrlHelper.openLink(e);
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(null,"Something went wrong with opening the link");
            }
        });
        JScrollPane scrollPane = new JScrollPane(
                editor,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setMinimumSize(new Dimension(500, 200));
        editor.setCaretPosition(0);

        return scrollPane;
    }
}
