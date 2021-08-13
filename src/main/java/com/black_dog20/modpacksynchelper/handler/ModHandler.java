package com.black_dog20.modpacksynchelper.handler;

import com.black_dog20.modpacksynchelper.Main;
import com.black_dog20.modpacksynchelper.json.ModDownload;
import com.black_dog20.modpacksynchelper.json.ModFile;
import com.black_dog20.modpacksynchelper.json.ModFileState;
import com.black_dog20.modpacksynchelper.json.ModsSyncInfo;
import com.black_dog20.modpacksynchelper.utils.DialogUtils;
import com.black_dog20.modpacksynchelper.utils.JsonUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                DialogUtils.showErrorDialogAndClose(String.format("Could not find mods folder in %s", root.getPath()));
            }

        } catch (Exception e) {
            DialogUtils.showErrorDialogAndClose("Something went wrong handling the sync");
        }
    }

    public void handle() throws IOException {
        if (modsFolder == null)
            DialogUtils.showErrorDialogAndClose("Could not find mods folder");
        ModsSyncInfo modsSyncInfo = JsonUtil.getModsSyncInfo();

        changeState(modsSyncInfo);
        modFiles = findAllMods(modsFolder);

        deleteMods(modsSyncInfo);
        modFiles = findAllMods(modsFolder);

        downloadMods(modsSyncInfo);
    }

    private void changeState(ModsSyncInfo modsSyncInfo) throws IOException {
        for (ModFileState modToChange : modsSyncInfo.getModsToChangeState()) {
            List<File> mods = findMod(modToChange.getName());
            for (File mod : mods) {
                if (mod.getName().endsWith(".disabled") && modToChange.isActive()) {
                    Path source = mod.toPath();
                    Files.move(source, source.resolveSibling(mod.getName().split(".disabled")[0]));
                } else if (!mod.getName().endsWith(".disabled") && !modToChange.isActive()){
                    Path source = mod.toPath();
                    Files.move(source, source.resolveSibling(mod.getName() + ".disabled"));
                }
            }
        }
    }

    private void deleteMods(ModsSyncInfo modsSyncInfo) {
        for (ModFile modToDelete : modsSyncInfo.getModsToDelete()) {
            List<File> mods = findMod(modToDelete.getName());
            for (File mod : mods) {
                mod.delete();
            }
        }
    }

    private void downloadMods(ModsSyncInfo modsSyncInfo) {
        for (ModDownload modToDownload : modsSyncInfo.getModsToDownload()) {
            List<File> mods = findMod(modToDownload.getName());
            if (mods.isEmpty()) {
                try {
                    downloadFile(modToDownload.getDownloadUrl());
                } catch (Exception ignored) {
                    System.out.println(ignored);
                }
            }
        }
    }

    public void downloadFile(String url) throws IOException, URISyntaxException {
        Connection.Response res = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36")
                .timeout(30000)
                .followRedirects(true)
                .ignoreContentType(true)
                .maxBodySize(20000000)//Increase value if download is more than 20MB
                .execute();

        String remoteUrl = res.url().toURI().getPath();
        String remoteFilename = remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1);

        if (!remoteFilename.endsWith(".jar")) {
            String header = res.header("Content-Disposition");
            if (header != null && !header.isEmpty()) {
                remoteFilename = header.replaceFirst("(?i)^.*filename=\"?([^\"]+)\"?.*$", "$1");
            }
            if (!remoteFilename.endsWith(".jar")) {
                return;
            }
        }

        String filename = addSeparatorIfNeeded(modsFolder.getPath()) + remoteFilename;
        FileOutputStream out = (new FileOutputStream(filename));
        out.write( res.bodyAsBytes());
        out.close();
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
}
