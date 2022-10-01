package com.black_dog20.modpacksynchelper.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that corresponds to my custom json format
 * This is the direct deserialization object for the hosted json file
 */
public class ModsSyncInfo {

    private List<ModFileState> modsToChangeState = new ArrayList<>();
    private List<ModFile> modsToDelete = new ArrayList<>();
    private List<CurseDownload> curseModsToDownload = new ArrayList<>();
    private List<ModDownload> modsToDownload = new ArrayList<>();
    private List<ModrinthDownload> modrinthModsToDownload = new ArrayList<>();

    public ModsSyncInfo() {

    }

    public List<ModFileState> getModsToChangeState() {
        return modsToChangeState;
    }

    public List<ModFile> getModsToDelete() {
        return modsToDelete;
    }

    public List<CurseDownload> getCurseModsToDownload() {
        return curseModsToDownload;
    }

    public List<ModDownload> getModsToDownload() {
        return modsToDownload;
    }

    public List<ModrinthDownload> getModrinthModsToDownload() {
        return modrinthModsToDownload;
    }
}
