# Chat Regex Filter
This is a very simple plugin for Bukkit-based servers to filter-out what players can say in chat, based on complete regex matching & replacing.

This plugin only runs on servers using Java 11 or newer.

## Filter configuration
You can use the bundled `filters.json` as guide/example on how filters look like. The file is a list of filters, each individual filter follows this format:
```json
{
  "pattern": "regex pattern",
  "replacement": "text replacement"
}
```
Refer to the Java documentation on [`Pattern`](https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/util/regex/Pattern.html) for specifics details on the elements you can use in these fields.

> Given the nature of this plugin, the bundled `filters.json` contains racial, sexual, and other kinds of slurs. **Consider the file's contents as NSFW!**

## Commands
* `/chatregexfilter notify (on|off|toggle)` — changes the state of whether the player will receive chat notifications or not (see `chatregexfilter.notify` in [Permissions](#Permissions))
* `/chatregexfilter reloadconfig` — discards all loaded filters and reloads them from the filters file

> `/crf` can be used in place of `/chatregexfilter`

## Permissions
* `chatregexfilter.bypass` — players with this permission will be ignored by the plugin filters
* `chatregexfilter.command` — grants access to the plugin's command
* `chatregexfilter.notify` — players with this permission will receive a chat message when a player's message had contents filtered. They will receive the banned word(s) in question and the original message.

> All permissions default to server operators only

## Compiling
For building, ChatRegexFilter requires Java 11 or newer and it also requires Git.
```shell
git clone https://github.com/emilyy-dev/chat-regex-filter.git
cd chat-regex-filter/
./gradlew build
```

## Contributing
Although there isn't much to do here, contributions are more than welcome. This project vaguely follows [Google's style guide](https://google.github.io/styleguide/javaguide.html) so try to keep it in the same way as other files in the project :)
