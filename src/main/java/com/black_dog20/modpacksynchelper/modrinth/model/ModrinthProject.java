package com.black_dog20.modpacksynchelper.modrinth.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class corresponds to the json for the main modrinth project object
 * But only the fields that I am interested in
 */
public class ModrinthProject {
    private String id;
    private String title;
    private transient Map<String, Version> versions = new HashMap<>();

    public ModrinthProject() {
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void addVersion(Version version) {
        versions.put(version.getId(), version);
    }

    public Version.ModFile getPrimaryModFile(String versionId) {
        return versions.get(versionId).getPrimary();
    }

    public static class Version {
        private String id;
        private String project_id;
        private List<ModFile> files = new ArrayList<>();


        public String getId() {
            return id;
        }

        public String getProjectId() {
            return project_id;
        }

        public ModFile getPrimary() {
            return files.stream()
                    .filter(ModFile::isPrimary)
                    .findFirst()
                    .orElse(null);
        }

        public static class ModFile {
            private String url;
            private String filename;
            private boolean primary;

            public String getUrl() {
                return url;
            }

            public String getFilename() {
                return filename;
            }

            public boolean isPrimary() {
                return primary;
            }
        }
    }
}
