# MDChat

### Description
MDChat is a chat management plugin for SpigotMC and Paper implementing [MineDown](https://github.com/Phoenix616/MineDown). It has a strong focus on simplicity, stability and
performance.
It is 100% free and open source, and always will be. Any and all contributions and feedback are welcome.

### Features
By default, the plugin has no features enabled to ensure new installations don't get in the way with unwanted features or rules.
* [PlaceholderAPI](https://www.spigotmc.org/resources/6245/) support.
* Full RGB support, including click, hover, and more.
  * View the [MineDown wiki](https://wiki.phoenix616.dev/library/minedown/syntax) for available formatting.
* ~~Automatic announcer with a configurable delay, text alignment, sounds, viewing permissions and more.~~ **- Coming soon™️**
* Configurable chat and command delays (in milliseconds).
* Optional movement requirements for chatting and performing commands.
* Extensive parrot, similarity and uppercase checks, with support for case sensitivity, ignoring usernames, minimum lengths, percentage thresholds and whitelisted words.
* Advanced chat formatting, including a global, per-group and per-player system with full support for RGB colors, click, hover and more.
* Every single message sent by MDChat can be modified.
* Message of the day with advanced formatting support and an optional sending delay.
* Outrageously configurable rule parsing system, supporting diacritical mark stripping (`éxámplé` -> `example`), regular expression parsing, cancelling or replacing violations, command execution upon violation and toggles for anvil, book, chat, command and sign checking per-rule.

### Commands
* `/mdchat clear [-s]` - Clear the chat (optional `silent` modifier).
* `/mdchat motd` - View the MOTD from MDChat.
* `/mdchat reload` - Reload MDChat's configuration files.

### Permissions
* You can find all accurate and up-to-date permissions on [our wiki](https://github.com/artelidiot/MDChat/wiki/Permissions).

### License
> Copyright (C) 2022-2023 Artel
>
> This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
> License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
> version.
>
> This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
> warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
> the [GNU General Public License](https://www.gnu.org/licenses/gpl-3.0.en.html) for more details.

### Embedded Resources
* [CommandAPI](https://github.com/JorelAli/CommandAPI) - We use `CommandAPI` to register all of our commands.
* [java-string-similarity](https://github.com/tdebatty/java-string-similarity) - We use `java-string-similarity` to perform our similarity checks.
* [bStats](https://github.com/Bastian/bStats) - We use `bStats` to collect metrics.