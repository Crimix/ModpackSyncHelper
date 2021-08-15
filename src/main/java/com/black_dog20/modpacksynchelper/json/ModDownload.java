package com.black_dog20.modpacksynchelper.json;

import com.black_dog20.modpacksynchelper.utils.UrlHelper;

import java.net.URL;

public class ModDownload {

    private String name;
    private String downloadUrl;

    public ModDownload() {

    }

    public String getName() {
        return name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getResolvedName() {
        try {
            String urlName = UrlHelper.getModNameFromUrl(new URL(downloadUrl));
            if (urlName.endsWith(".jar")) {
                return urlName;
            } else {
                return name;
            }
        } catch (Exception ignored) {
            return name;
        }
    }
}
