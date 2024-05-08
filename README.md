<img src="src/main/resources/assets/filtpick/icon.png" align="right" width="180px"/>

# FiltPick

[![CurseForge site](https://img.shields.io/curseforge/dt/700141?style=flat&logo=curseforge&color=orange
)](https://www.curseforge.com/minecraft/mc-mods/filtpick)
[![Dynamic JSON Badge](https://img.shields.io/badge/dynamic/json?url=https://api.modrinth.com/v2/project/filtpick&query=$.downloads&suffix=%20downloads&logo=Modrinth&label)](https://modrinth.com/mod/filtpick)

This is a fabric mod for Minecraft which enables you to pick up items selectively.
It's a lightweight mod and has a user-friendly interface.
If you need forge port, please click [here](https://github.com/APeng215/FiltPick-forge).

## How to Install

1. [Download](https://modrinth.com/mod/filtpick/versions) the Jar File of proper version.
2. Copy or cut the jar file you've just downloaded to your mod path (usually ".../.minecraft/mods" or ".../.minecraft/versions/\<versionName>/mods")
3. Make sure you have installed fabric api for proper version.

## How to use

Actually, the UI is too straightforward to require any guidance. It's recommended to try yourself __directly in game__.
- Put the item into the list, the function will take effect automatically.
- Whitelist-mode: Only the items below will be picked up
- Blacklist-mode: The items below won't be picked up

<img src="README_resources/inventory_screen_guide.png" width="320" ><img src="README_resources/mod_screen_guide.png" width="320" >

### Configure button position

You can configure button position in `/config/filtpick.json`.<br>
_Normally, there is no necessity to make any configuration except for mods conflicts._

## For developer

If you want to build the source project,
remember to delete the proxy settings in file `gradle.properties`.
```
# Used for proxy of the author, delete it if you are not the author.
systemProp.http.proxyHost=127.0.0.1
systemProp.http.proxyPort=10809
systemProp.https.proxyHost=127.0.0.1
systemProp.https.proxyPort=10809
```

## Credits

### Localization

- 日本語 - [Dulroy](https://space.bilibili.com/313723598)

## Release Pages

[CurseForge](https://www.curseforge.com/minecraft/mc-mods/filtpick)
/ [Modrinth](https://modrinth.com/mod/filtpick)
/ [GitHub](https://github.com/APeng215/FiltPick)
/ [MC百科](https://www.mcmod.cn/class/8081.html)

