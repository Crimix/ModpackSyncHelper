package com.black_dog20.modpacksynchelper;

import com.black_dog20.modpacksynchelper.curse.CurseHelper;
import com.black_dog20.modpacksynchelper.json.CurseDownload;
import com.black_dog20.modpacksynchelper.json.ModDownload;
import com.black_dog20.modpacksynchelper.json.ModFile;
import com.black_dog20.modpacksynchelper.json.ModFileState;
import com.black_dog20.modpacksynchelper.json.ModrinthDownload;
import com.black_dog20.modpacksynchelper.json.ModsSyncInfo;
import com.black_dog20.modpacksynchelper.modrinth.ModrinthHelper;
import com.black_dog20.modpacksynchelper.utils.AppProperties;
import com.black_dog20.modpacksynchelper.utils.DialogUtils;
import com.black_dog20.modpacksynchelper.utils.JsonUtil;
import com.black_dog20.modpacksynchelper.utils.ProgressBarUtil;
import com.black_dog20.modpacksynchelper.utils.UrlHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Class to (de)activate, delete and download mods
 */
public class ModHandler {

    private File modsFolder;
    private List<File> modFiles = new ArrayList<>();

    public ModHandler() {
        try {
            File root = getRootFolder();
            if (!root.isDirectory())
                DialogUtils.showErrorDialogAndClose(String.format("%s is not a folder", root.getPath()));

            Optional<File> modsFolder = findModsFolder(root);
            if (modsFolder.isPresent()) {
                this.modsFolder = modsFolder.get();
                modFiles = findAllMods(this.modsFolder);
            } else {
                if (AppProperties.isDebug()) {
                    boolean yes = DialogUtils.showChoiceDialog(String.format("Could not find mods folder in %s%nDo you wan to create it", root.getPath()));
                    if (yes) {
                        File newDir = new File(addSeparatorIfNeeded(root.getPath()) + "mods");
                        if (!newDir.exists()){
                            boolean success = newDir.mkdirs();
                            if (success) {
                                modsFolder = findModsFolder(root);
                                if (modsFolder.isPresent()) {
                                    this.modsFolder = modsFolder.get();
                                    modFiles = findAllMods(this.modsFolder);
                                }
                            } else {
                                DialogUtils.showErrorDialogAndClose("Could not create the mods folder");
                            }
                        }
                    } else {
                        DialogUtils.showErrorDialogAndClose("Cannot continue without mods folder");
                    }
                } else {
                    DialogUtils.showErrorDialogAndClose(String.format("Could not find mods folder in %s", root.getPath()));
                }
            }
        } catch (Exception exception) {
            System.err.println(exception.getLocalizedMessage());
            DialogUtils.showErrorDialogAndClose("Something went wrong handling the sync");
        }
    }

    /**
     * The main method of the handler it ensures that all actions happen in the correct order.
     */
    public void handle() throws IOException {
        if (modsFolder == null)
            DialogUtils.showErrorDialogAndClose("Could not find mods folder");
        ModsSyncInfo modsSyncInfo = JsonUtil.getModsSyncInfo();

        System.out.println("Handling mod sync");
        changeState(modsSyncInfo);
        modFiles = findAllMods(modsFolder);

        deleteMods(modsSyncInfo);
        modFiles = findAllMods(modsFolder);

        downloadCurseMods(modsSyncInfo);
        modFiles = findAllMods(modsFolder);

        downloadModrinthMods(modsSyncInfo);
        modFiles = findAllMods(modsFolder);

        downloadMods(modsSyncInfo);

        System.out.println("Done handling mod sync");
    }

    private void changeState(ModsSyncInfo modsSyncInfo) throws IOException {
        if (modsSyncInfo.getModsToChangeState().isEmpty()) {
            return;
        }

        for (ModFileState modToChange : ProgressBarUtil.wrapWithProgressBar(modsSyncInfo.getModsToChangeState(), "Change state")) {
            List<File> mods = findMod(modToChange.getName());
            for (File mod : mods) {
                if (mod.getName().endsWith(".disabled") && modToChange.isActive()) {
                    Path source = mod.toPath();
                    String newName = mod.getName().split(".disabled")[0];
                    Files.move(source, source.resolveSibling(newName), REPLACE_EXISTING);
                    System.out.printf("Enabled %s%n", newName);
                } else if (!mod.getName().endsWith(".disabled") && !modToChange.isActive()){
                    Path source = mod.toPath();
                    Files.move(source, source.resolveSibling(mod.getName() + ".disabled"), REPLACE_EXISTING);
                    System.out.printf("Disabled %s%n", mod.getName());
                }
            }
        }
    }

    private void deleteMods(ModsSyncInfo modsSyncInfo) {
        if (modsSyncInfo.getModsToDelete().isEmpty()) {
            return;
        }

        for (ModFile modToDelete : ProgressBarUtil.wrapWithProgressBar(modsSyncInfo.getModsToDelete(), "Delete mods")) {
            List<File> mods = findMod(modToDelete.getName());
            for (File mod : mods) {
                boolean success = mod.delete();
                if (success)
                    System.out.printf("Deleted %s%n", mod.getName());
                else
                    System.err.printf("Failed to delete %s%n", mod.getName());
            }
        }
    }

    private void downloadCurseMods(ModsSyncInfo modsSyncInfo) {
        if (modsSyncInfo.getCurseModsToDownload().isEmpty()) {
            return;
        }

        for (CurseDownload modToDownload : ProgressBarUtil.wrapWithProgressBar(modsSyncInfo.getCurseModsToDownload(), "Download Curse mod")) {
            String name = CurseHelper.getModName(modToDownload);
            if (CurseHelper.UNKNONW.equals(name)) {
                System.err.printf("Could not find mod with project id: %s and file id: %s %n", modToDownload.getProjectId(), modToDownload.getFileId());
                continue;
            }
            List<File> mods = findMod(name);
            if (mods.isEmpty()) {
                try {
                    String url = CurseHelper.getCurseDownloadUrl(modToDownload);
                    downloadFile(url);
                } catch (Exception exception) {
                    System.err.println(String.format("Failed to download %s because %s", modToDownload.getProjectId(), exception.getLocalizedMessage()));
                }
            }
        }
    }

    private void downloadModrinthMods(ModsSyncInfo modsSyncInfo) {
        if (modsSyncInfo.getModrinthModsToDownload().isEmpty()) {
            return;
        }

        for (ModrinthDownload modToDownload : ProgressBarUtil.wrapWithProgressBar(modsSyncInfo.getModrinthModsToDownload(), "Download Modrinth mod")) {
            String name = ModrinthHelper.getModName(modToDownload);
            if (ModrinthHelper.UNKNONW.equals(name)) {
                System.err.printf("Could not find mod with project id: %s and version id: %s %n", modToDownload.getProjectId(), modToDownload.getVersionId());
                continue;
            }
            List<File> mods = findMod(name);
            if (mods.isEmpty()) {
                try {
                    String url = ModrinthHelper.getDownloadUrl(modToDownload);
                    downloadFile(url);
                } catch (Exception exception) {
                    System.err.println(String.format("Failed to download %s because %s", modToDownload.getProjectId(), exception.getLocalizedMessage()));
                }
            }
        }
    }

    private void downloadMods(ModsSyncInfo modsSyncInfo) {
        if (modsSyncInfo.getModsToDownload().isEmpty()) {
            return;
        }

        for (ModDownload modToDownload : ProgressBarUtil.wrapWithProgressBar(modsSyncInfo.getModsToDownload(), "Download mod")) {
            if (modToDownload.getDownloadUrl().toLowerCase().contains(CurseHelper.CURSEFORGE_DOMAIN)) {
                System.err.println(String.format("Tried to download %s directly from curseforge.com, this does not work", modToDownload.getName()));
                continue;
            } else if (modToDownload.getDownloadUrl().toLowerCase().contains(ModrinthHelper.MODRINTH_DOMAIN)){
                System.err.println(String.format("Tried to download %s directly from modrinth.com, this does not work", modToDownload.getName()));
                continue;
            }

            List<File> mods = findMod(modToDownload.getResolvedName());
            if (mods.isEmpty()) {
                try {
                    downloadFile(fixUrlIfNeeded(modToDownload.getDownloadUrl()));
                } catch (Exception exception) {
                    System.err.println(String.format("Failed to download %s because %s", modToDownload.getName(), exception.getLocalizedMessage()));
                }
            }
        }
    }

    public void downloadFile(String url) throws IOException, URISyntaxException {
        URL downloadUrl = new URL(url);
        String remoteFilename = UrlHelper.getModNameFromUrl(downloadUrl);

        String filename = addSeparatorIfNeeded(modsFolder.getPath()) + remoteFilename;
        File destination = new File(filename);
        FileUtils.copyURLToFile(downloadUrl, destination);
        System.out.printf("Downloaded %s%n", remoteFilename);
    }

    private File getRootFolder() throws URISyntaxException {
        String jarFilePath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        File file = new File(jarFilePath);

        return file.getParentFile();
    }

    private Optional<File> findModsFolder(File root) {
        File[] listOfFiles = root.listFiles();
        if (listOfFiles == null)
            return Optional.empty();
        return Arrays.stream(listOfFiles)
                .filter(f -> f.getName().equalsIgnoreCase("mods"))
                .findFirst();
    }

    private List<File> findAllMods(File folder) {
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null)
            return new ArrayList<>();
        return Arrays.stream(listOfFiles)
                .filter(f -> f.getName().contains(".jar"))
                .collect(Collectors.toList());
    }

    private List<File> findMod(String name) {
        return modFiles.stream()
                .filter(f -> getNameWithOutDisabled(name).toLowerCase().endsWith(".jar") ? getNameWithOutDisabled(f).equalsIgnoreCase(getNameWithOutDisabled(name)) : getNameWithOutDisabled(f).toLowerCase().contains(getNameWithOutDisabled(name).toLowerCase()))
                .collect(Collectors.toList());
    }

    private String getNameWithOutDisabled(File mod) {
        return mod.getName().endsWith(".jar.disabled") ? mod.getName().split(".disabled")[0] : mod.getName();
    }

    private String getNameWithOutDisabled(String modName) {
        return modName.endsWith(".jar.disabled") ? modName.split(".disabled")[0] : modName;
    }

    private String addSeparatorIfNeeded(String path) {
        if (path.endsWith(File.separator))
            return path;
        return path + File.separator;
    }

    private String fixUrlIfNeeded(String url) throws MalformedURLException {
        String domainName = UrlHelper.getDomainName(url);

        if (domainName.toLowerCase().contains("dropbox.com")) {
            return String.format("%s?dl=1", url.split("\\?")[0]);
        }

        return url;
    }
}
