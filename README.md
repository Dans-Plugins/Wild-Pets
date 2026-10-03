# Wild Pets

## Description
Wild Pets is an open source minecraft plugin that allows players to tame any entity in the game so long as their configuration is enabled.

## Supported Minecraft Versions
This plugin is supported on the Minecraft versions listed in [`minecraft-versions.json`](minecraft-versions.json): currently **1.19.4**, **1.21.11** and **26.2** (Spigot and its forks). Every stable release is booted on a real server of each of these versions before it is published, and every build checks that the plugin only uses Bukkit API that exists on all of them. Other versions from 1.19.4 onwards are expected to work but are not tested. To support another version, add it to the file: both checks pick it up.

## Installation
1) You can download the plugin from [this page](https://www.spigotmc.org/resources/wild-pets.95800/).

2) Once downloaded, place the jar in the plugins folder of your server files.

3) Restart your server.

## Works Well With
Wild Pets is part of the **survival flavour** set of Dan's Plugins. These are companion plugins that suit the same kind of server and run side by side; Wild Pets does not depend on or call into any of them.

- [Food Spoilage](https://github.com/Dans-Plugins/FoodSpoilage) ([SpigotMC](https://www.spigotmc.org/resources/food-spoilage.81507/), `/dpm get foodspoilage`): food goes bad over time.
- [SimpleSkills](https://github.com/Dans-Plugins/SimpleSkills) ([SpigotMC](https://www.spigotmc.org/resources/simpleskills.98039/), `/dpm get simpleskills`): skills that level up as players play and unlock benefits.
- [More Recipes](https://github.com/Dans-Plugins/More-Recipes) ([SpigotMC](https://www.spigotmc.org/resources/more-recipes.81832/), `/dpm get morerecipes`): recipes for items that cannot be crafted in vanilla.
- [Medieval Cookery](https://github.com/Dans-Plugins/Medieval-Cookery) (no SpigotMC page, no stable release yet): cooking recipes for custom foods, defined by the server owner.

Running a medieval roleplay server? The [Medieval Roleplay Engine](https://github.com/Dans-Plugins/Medieval-Roleplay-Engine#works-well-with) set lists the plugins for that.

Every plugin above is listed on [dansplugins.com](https://dansplugins.com). Wild Pets is listed at [dansplugins.com/resources/wild-pets](https://dansplugins.com/resources/wild-pets) and can be installed in game with [Dan's Plugin Manager](https://github.com/Dans-Plugins/Dans-Plugin-Manager): `/dpm get wildpets`.

## Usage

### Documentation

- [User Guide](USER_GUIDE.md) - Getting started and common scenarios
- [Commands Reference](COMMANDS.md) - Complete list of all commands
- [Configuration Guide](CONFIG.md) - Detailed config options

### Wiki & Additional Resources

- [Wiki Guide](https://github.com/dmccoystephenson/Wild-Pets/wiki/Guide)
- [FAQ](https://github.com/dmccoystephenson/Wild-Pets/wiki/FAQ)

## Support
You can find the support discord server [here](https://discord.gg/xXtuAQ2).

### Experiencing a bug?
Please fill out a bug report [here](https://github.com/dmccoystephenson/Wild-Pets/issues?q=is%3Aissue+is%3Aopen+label%3Abug).

## Roadmap
- [Known Bugs](https://github.com/dmccoystephenson/Wild-Pets/issues?q=is%3Aopen+is%3Aissue+label%3Abug)
- [Planned Features](https://github.com/dmccoystephenson/Wild-Pets/issues?q=is%3Aopen+is%3Aissue+label%3AEpic)
- [Planned Improvements](https://github.com/dmccoystephenson/Wild-Pets/issues?q=is%3Aopen+is%3Aissue+label%3Aenhancement)

## Contributing
- [Contributing.md](./CONTRIBUTING.md)

## Testing
1. Ensure docker is installed.
2. Reopen the project in the provided dev container with the following commands:
```
cd ./.devcontainer
./start_dev_container.sh
```
3. Run `mvn test`.

## Authors and acknowledgement
Name | Main Contributions
------------ | -------------
Daniel Stephenson | Creator

This plugin was developed in response to a player request from TheeFallen100, who expressed a desire to tame foxes in the game. Upon proposing the idea of creating such a plugin, they suggested the name Wild Pets.

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

### Why MIT?
We chose the MIT License because it’s **simple, permissive, and widely used**. It allows anyone to use, modify, and distribute the code — even in proprietary projects — as long as the original copyright and license notice are included.  
This helps maximize **adoption**, **collaboration**, and **contribution** by keeping barriers low while still ensuring attribution.

## Project Status
This project is in active development.

### bStats
You can view the bStats page for the plugin [here](https://bstats.org/plugin/bukkit/Wild%20Pets/12332).





## Usage reporting

Wild Pets reports its usage by default: when the plugin is enabled, and each time one of its commands is used, it sends its name, its version and the command's name to https://trace.danielstephenson.dev, so it is known which plugins are actually in use. Nothing about players, worlds or IP addresses is sent, and neither is anything typed after a command.

Each event also carries a random server ID (the `server-id` line in `plugins/trace/config.yml`) so
servers can be counted rather than events. It identifies no person, account or IP address; delete
the line to get a new one.

To turn it off:

- for this plugin only: set `usage-reporting.enabled: false` in `plugins/WildPets/config.yml`;
- for every plugin on the server that reports to trace: set `enabled: false` in `plugins/trace/config.yml` (created on the first start);
- for the whole server process: set the environment variable `TRACE_USAGE_REPORTING=off` or `DO_NOT_TRACK=1`.

Details: https://github.com/Stephenson-Software/trace#usage-reporting
