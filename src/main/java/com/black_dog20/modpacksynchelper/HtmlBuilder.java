package com.black_dog20.modpacksynchelper;

import com.black_dog20.modpacksynchelper.curse.CurseHelper;
import com.black_dog20.modpacksynchelper.json.CurseDownload;
import com.black_dog20.modpacksynchelper.json.ModDownload;
import com.black_dog20.modpacksynchelper.json.ModsSyncInfo;
import com.black_dog20.modpacksynchelper.utils.JsonUtil;
import com.black_dog20.modpacksynchelper.utils.UrlHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlBuilder {

    public static String getEmptyHtml() {
        return String.join("<html><body style='width: 400px;'>",
                "<b>Mods to change state</b><ul>\n</ul>",
                "<b>Mods to delete</b><ul>\n</ul>",
                "<b>Mods to download</b><ul>\n</ul>",
                "</body></html>");
    }

    public static String getHtml() throws IOException {
        ModsSyncInfo modsSyncInfo = JsonUtil.getModsSyncInfo();

        String modChangeStateHtml = getModChangeStateHtml(modsSyncInfo);
        String modDeleteHtml = getModDeleteHtml(modsSyncInfo);
        String modDownloadHtml = getModDownloadHtml(modsSyncInfo);

        List<String> elements = List.of(modChangeStateHtml, modDeleteHtml, modDownloadHtml);

        return String.join("<html><body style='width: 400px;'>",
                elements.stream()
                        .collect(Collectors.joining("", "" ,"<br><br>")),
                "</body></html>");
    }

    private static String getModChangeStateHtml(ModsSyncInfo modsSyncInfo) {
        return modsSyncInfo.getModsToChangeState().stream()
                .map(o -> String.format("<li>%s disabled: %s</li>\n", o.getName(), !o.isActive()))
                .collect(Collectors.joining("", "<b>Mods to change state</b><ul>\n", "</ul>"));
    }

    private static String getModDeleteHtml(ModsSyncInfo modsSyncInfo) {
        return modsSyncInfo.getModsToDelete().stream()
                .map(o -> String.format("<li>%s</li>\n", o.getName()))
                .collect(Collectors.joining("", "<b>Mods to delete</b><ul>\n", "</ul>"));
    }

    private static String getModDownloadHtml(ModsSyncInfo modsSyncInfo) {
        List<Object> objects = new ArrayList<>();
        objects.addAll(modsSyncInfo.getCurseModsToDownload());
        objects.addAll(modsSyncInfo.getModsToDownload());

       return objects.stream()
                .map(HtmlBuilder::getSingleModDownloadHtml)
                .collect(Collectors.joining("", "<b>Mods to download</b><ul>\n", "</ul>"));
    }

    private static String getSingleModDownloadHtml(Object object) {
        if (object instanceof ModDownload o) {
            return String.format("<li><a href=\"%s\">%s (%s)</li>\n", o.getDownloadUrl(), o.getResolvedName(), UrlHelper.getDomainName(o.getDownloadUrl()));
        } else if (object instanceof CurseDownload o) {
            return String.format("<li><a href=\"%s\">%s (%s)</li>\n", CurseHelper.getCurseProjectUrl(o.getProjectId()), CurseHelper.getModName(o.getProjectId(), o.getFileId()), UrlHelper.getDomainName(CurseHelper.getCurseProjectUrl(o.getProjectId())));
        } else {
            System.err.printf("Got not supported download class %s%n", object.getClass().getSimpleName());
            return "";
        }
    }
}
