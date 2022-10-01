package com.black_dog20.modpacksynchelper.json;

import com.black_dog20.modpacksynchelper.curse.CurseHelper;
import com.black_dog20.modpacksynchelper.json.api.IModDownload;
import com.black_dog20.modpacksynchelper.utils.UrlHelper;

import java.util.Objects;

/**
 * Class that corresponds to my custom json format
 * This represents a mod to download from curseforge
 */
public class CurseDownload implements IModDownload {

    private int projectId;
    private int fileId;

    public CurseDownload() {

    }

    public int getProjectId() {
        return projectId;
    }

    public int getFileId() {
        return fileId;
    }

    @Override
    public String getHtmlElementString() {
        String url = CurseHelper.getCurseProjectUrl(this);
        String modName = CurseHelper.getModName(this);
        return String.format("<li><a href=\"%s\">%s (%s)</li>\n", url, modName, UrlHelper.getDomainName(url));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurseDownload that = (CurseDownload) o;
        return projectId == that.projectId && fileId == that.fileId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, fileId);
    }
}
