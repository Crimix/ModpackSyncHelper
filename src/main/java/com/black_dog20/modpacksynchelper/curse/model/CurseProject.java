package com.black_dog20.modpacksynchelper.curse.model;

import com.black_dog20.modpacksynchelper.curse.CurseHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class corresponds to the json for the main curseforge project object
 * But only the fields that I am interested in
 */
public class CurseProject {
    private int id;
    private String name;
    private Links links;
    private transient Map<Integer, ModFile> modFiles = new HashMap<>();

    public CurseProject() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProjectUrl() {
        return Optional.ofNullable(links)
                .map(Links::getWebsiteUrl)
                .orElse(CurseHelper.CURSEFORGE_URL);
    }

    public void addModFile(ModFile modFile) {
        modFiles.put(modFile.getId(), modFile);
    }

    public ModFile getModFile(int fileId) {
        return modFiles.get(fileId);
    }

    public static class Links {
        private String websiteUrl;

        public String getWebsiteUrl() {
            return websiteUrl;
        }
    }

    public static class ModFile {
        private int id;
        private int modId;
        private String fileName;
        private String downloadUrl;

        public int getId() {
            return id;
        }

        public int getModId() {
            return modId;
        }

        public String getFileName() {
            return fileName;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }
    }
}
