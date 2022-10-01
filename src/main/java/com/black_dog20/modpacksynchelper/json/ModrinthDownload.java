package com.black_dog20.modpacksynchelper.json;

import com.black_dog20.modpacksynchelper.json.api.IModDownload;
import com.black_dog20.modpacksynchelper.modrinth.ModrinthHelper;
import com.black_dog20.modpacksynchelper.utils.UrlHelper;

import java.util.Objects;

/**
 * Class that corresponds to my custom json format
 * This represents a mod to download from modrinth
 */
public class ModrinthDownload implements IModDownload {

    private String projectId;
    private String versionId;

    public ModrinthDownload() {

    }

    public String getProjectId() {
        return projectId;
    }

    public String getVersionId() {
        return versionId;
    }

    @Override
    public String getHtmlElementString() {
        String url = ModrinthHelper.getProjectUrl(this);
        String modName = ModrinthHelper.getModName(this);
        return String.format("<li><a href=\"%s\">%s (%s)</li>\n", url, modName, UrlHelper.getDomainName(url));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModrinthDownload that = (ModrinthDownload) o;
        return projectId == that.projectId && versionId == that.versionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, versionId);
    }
}
