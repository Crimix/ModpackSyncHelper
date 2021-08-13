package com.black_dog20.modpacksynchelper.json;

import java.util.ArrayList;
import java.util.List;

public class ModsSyncInfo {

    private List<ModFileState> modsToChangeState = new ArrayList<>();
    private List<ModFile> modsToDelete = new ArrayList<>();
    private List<ModDownload> modsToDownload = new ArrayList<>();

    public ModsSyncInfo() {

    }

    public List<ModFileState> getModsToChangeState() {
        return modsToChangeState;
    }

    public List<ModFile> getModsToDelete() {
        return modsToDelete;
    }

    public List<ModDownload> getModsToDownload() {
        return modsToDownload;
    }
}
