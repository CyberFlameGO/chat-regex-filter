//
// ChatRegexFilter - A Bukkit plugin to filter-out what players can
//       send to chat based on regex matching & replacement
// Copyright (C) 2021  Emilia López <https://github.com/emilyy-dev>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

package io.github.emilyydev.chatregexfilter.command;

import io.github.emilyydev.chatregexfilter.ChatRegexFilterPlugin;
import io.github.emilyydev.chatregexfilter.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.permissions.PermissionAttachment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.stream.Collectors.toUnmodifiableList;

public final class CrfCommand implements TabExecutor {

  private static final List<String> FIRST_LEVEL_ARGS = List.of("notify", "reloadconfig");
  private static final List<String> NOTIFY_SECOND_LEVEL_ARGS = List.of("on", "off", "toggle");

  private final ChatRegexFilterPlugin plugin;
  private final Map<CommandSender, PermissionAttachment> attachmentMap = new WeakHashMap<>();

  public CrfCommand(final ChatRegexFilterPlugin plugin) {
    this.plugin = plugin;
  }

  public void register() {
    final @Nullable PluginCommand command = this.plugin.getCommand("chatregexfilter");
    if (command != null) {
      command.setExecutor(this);
      command.setTabCompleter(this);
      command.setUsage("/<command> notify (on|off|toggle) —— /<command> reloadconfig");
      command.setPermission(Permission.COMMAND_PERMISSION);
    }
  }

  private PermissionAttachment createAttachment(final CommandSender sender) {
    return sender.addAttachment(this.plugin);
  }

  private void setNotify(final CommandSender sender, final boolean value) {
    final boolean currentValue = sender.hasPermission(Permission.NOTIFY_PERMISSION);

    if (value && currentValue) {
      sender.sendMessage("You already have the notify permission");
    } else if (!(value || currentValue)) {
      sender.sendMessage("You do not have the notify permission");
    } else {
      final PermissionAttachment attachment = this.attachmentMap.computeIfAbsent(sender, this::createAttachment);
      attachment.setPermission(Permission.NOTIFY_PERMISSION, value);
      final char c = value ? 'w' : 't';
      sender.sendMessage("You will no" + c + " be notified of filtered messages");
    }
  }

  @Override
  public boolean onCommand(final @NotNull CommandSender sender, final @NotNull Command command,
                           final @NotNull String alias, final @NotNull String[] args) {
    if (args.length == 0) {
      sender.sendMessage(format("%s by %s - v%s (%s)",
                                this.plugin.getName(),
                                join(", ", this.plugin.getDescription().getAuthors()),
                                this.plugin.getDescription().getVersion(),
                                this.plugin.getDescription().getWebsite()));
      return true;
    }

    if (args.length > 2) {
      return false;
    }

    final String firstArg = args[0];
    if (!"notify".equalsIgnoreCase(firstArg)) {
      if ("reloadconfig".equalsIgnoreCase(firstArg)) {
        this.plugin.reloadConfig();
        return true;
      }
      return false;
    }

    final String notify = args[1];
    if ("on".equalsIgnoreCase(notify)) {
      setNotify(sender, true);
    } else if ("off".equalsIgnoreCase(notify)) {
      setNotify(sender, false);
    } else if ("toggle".equalsIgnoreCase(notify)) {
      setNotify(sender, !sender.hasPermission(Permission.NOTIFY_PERMISSION));
    } else {
      return false;
    }

    return true;
  }

  @Override
  public @NotNull List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command,
                                             final @NotNull String alias, final @NotNull String[] args) {
    final int i = args.length;
    final List<String> lowercase = Arrays.stream(args).map(String::toLowerCase).collect(toUnmodifiableList());

    if (i == 1) {
      return FIRST_LEVEL_ARGS.stream().filter(s -> s.startsWith(lowercase.get(0))).collect(toUnmodifiableList());
    } else if (i == 2 && "notify".equals(lowercase.get(0))) {
      return NOTIFY_SECOND_LEVEL_ARGS.stream().filter(s -> s.startsWith(lowercase.get(1))).collect(toUnmodifiableList());
    } else {
      return List.of();
    }
  }
}
