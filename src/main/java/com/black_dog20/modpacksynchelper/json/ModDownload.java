package com.black_dog20.modpacksynchelper.json;

import com.black_dog20.modpacksynchelper.json.api.IModDownload;
import com.black_dog20.modpacksynchelper.utils.UrlHelper;

import java.net.URL;
import java.util.Objects;

/**
 * Class that corresponds to my custom json format
 * This represents a mod to download from filehost such as dropbox
 */
public class ModDownload implements IModDownload {

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

    @Override
    public String getHtmlElementString() {
        return String.format("<li><a href=\"%s\">%s (%s)</li>\n", getDownloadUrl(), getResolvedName(), UrlHelper.getDomainName(getDownloadUrl()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModDownload that = (ModDownload) o;
        return Objects.equals(name, that.name) && Objects.equals(downloadUrl, that.downloadUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, downloadUrl);
    }
}
