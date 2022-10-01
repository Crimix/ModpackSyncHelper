package com.black_dog20.modpacksynchelper;

import com.black_dog20.modpacksynchelper.curse.CurseHelper;
import com.black_dog20.modpacksynchelper.json.ModFile;
import com.black_dog20.modpacksynchelper.json.ModFileState;
import com.black_dog20.modpacksynchelper.json.ModsSyncInfo;
import com.black_dog20.modpacksynchelper.json.api.IModDownload;
import com.black_dog20.modpacksynchelper.utils.JsonUtil;
import com.black_dog20.modpacksynchelper.utils.ProgressBarUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlBuilder {

    /**
     * Builds the empty html for the html view
     * @return the empty html
     */
    public static String getEmptyHtml() {
        return String.join("<html><body style='width: 400px;'>",
                "<b>Mods to change state</b><ul>\n</ul>",
                "<b>Mods to delete</b><ul>\n</ul>",
                "<b>Mods to download</b><ul>\n</ul>",
                "</body></html>");
    }

    /**
     * Creates the html from the json file and metadata of the mods to change, delete and download
     * @return the full html to show to the user
     */
    public static String getHtml() throws IOException {
        System.out.println("Fetching json");
        ModsSyncInfo modsSyncInfo = JsonUtil.getModsSyncInfo();
        System.out.println("Done fetching json");

        CurseHelper.fetchMetadata(modsSyncInfo.getCurseModsToDownload());
        System.out.println("Parsing json and metadata");
        String modChangeStateHtml = getModChangeStateHtml(modsSyncInfo);
        String modDeleteHtml = getModDeleteHtml(modsSyncInfo);
        String modDownloadHtml = getModDownloadHtml(modsSyncInfo);
        System.out.println("Done parsing json and metadata");

        List<String> elements = List.of(modChangeStateHtml, modDeleteHtml, modDownloadHtml);

        return String.join("<html><body style='width: 400px;'>",
                elements.stream()
                        .collect(Collectors.joining("", "" ,"<br><br>")),
                "</body></html>");
    }

    private static String getModChangeStateHtml(ModsSyncInfo modsSyncInfo) {
        return ProgressBarUtil.wrapWithProgressBar(modsSyncInfo.getModsToChangeState().stream(), "Mods to change")
                .map(ModFileState::getHtmlElementString)
                .collect(Collectors.joining("", "<b>Mods to change state</b><ul>\n", "</ul>"));
    }

    private static String getModDeleteHtml(ModsSyncInfo modsSyncInfo) {
        return ProgressBarUtil.wrapWithProgressBar(modsSyncInfo.getModsToDelete().stream(), "Mods to delete")
                .map(ModFile::getHtmlElementString)
                .collect(Collectors.joining("", "<b>Mods to delete</b><ul>\n", "</ul>"));
    }

    private static String getModDownloadHtml(ModsSyncInfo modsSyncInfo) {
        List<IModDownload> objects = new ArrayList<>();
        objects.addAll(modsSyncInfo.getCurseModsToDownload());
        objects.addAll(modsSyncInfo.getModsToDownload());

       return ProgressBarUtil.wrapWithProgressBar(objects.stream(), "Mods to fetch")
                .map(IModDownload::getHtmlElementString)
                .collect(Collectors.joining("", "<b>Mods to download</b><ul>\n", "</ul>"));
    }
}
