# ModpackSyncHelper

### What is this
A tool to help people with playing modified modpacks with friends.  
<div align="center">
<img src="https://user-images.githubusercontent.com/7978180/129472690-94c574c4-60e8-4fd6-8178-46ec75208daf.png" width="329" height="342">
</div>

### Why?
We all know the usual way of playing a modified modpack with friends 
is having a chat where to tell them which version to download from CurseForge or sending them a beta file over Discord.   
This can get tiring for all over time.

### How does it work?
You get your most tech-savvy and **trusted** friend to clone this repo and open it with IntelliJ.   
Then it is as simple as copying `gradle-local.properties.example` to `gradle-local.properties`
and changing the two properties.

Where `modpack_sync_json_url` needs to be a JSON file which is accessible over the internet.

Then run the `jar` task and send the build jar under `root/build/libs/` to all who needs it.

### What are my options?
- You can specific which mods from a pack should be disabled, this could be client-only mods.
- You can specific mods to be deleted, if you disagree with the modpack author about a specific mod.
- You can add mods which should be downloaded to play on the server.
    - This can be a private alpha or beta versioned hosted somewhere.
    - Mods from CurseForge, by project and file id

### Is it safe?
This really depends on your friend as this tool can make it possible to download random files from the internet, not just jars.
So you have to trust them.

### JSON format

```
{
    "modsToChangeState": [
        {
            "name": "mod-name.jar",
            "active": false
        },
        {
            "name": "another-mod-name.jar",
            "active": true
        }
    ],
    "modsToDelete": [
        {
            "name": "unwanted-mod-name.jar"
        },
        {
            "name": "another-unwanted-mod-name.jar"
        }
    ],
    "curseModsToDownload": [
        {
            "projectId": <project id>,
            "fileId": <file di>
        }
    ],
    "modsToDownload": [
        {
            "name": "display-mod-name.jar",
            "downloadUrl": "url"
        }
    ]
}
```
