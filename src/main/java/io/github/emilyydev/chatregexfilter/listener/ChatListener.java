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

package io.github.emilyydev.chatregexfilter.listener;

import io.github.emilyydev.chatregexfilter.ChatRegexFilterPlugin;
import io.github.emilyydev.chatregexfilter.Permission;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

public abstract class ChatListener {

  private static final MessageFormat LOG_ENTRY_SINGLE_FILTER_FORMAT = new MessageFormat("Player {0} tried to say ''{1}'' but it''s a banned word (matches filter ''{2}'')", Locale.US);
  private static final MessageFormat LOG_ENTRY_MULTIPLE_FILTERS_FORMAT = new MessageFormat("Player {0} tried to say {1} but they are banned words (match filters respectively {2})", Locale.US);
  private static final MessageFormat LOG_ENTRY_ORIGINAL_MESSAGE_FORMAT = new MessageFormat("Original message: {0}", Locale.US);

  private static Object[] args(final String... args) {
    return args;
  }

  private static String single(final String player, final String group, final String pattern) {
    return LOG_ENTRY_SINGLE_FILTER_FORMAT.format(args(player, group, pattern));
  }

  private static String multiple(final String player, final Map<String, String> groupToPatternMap) {
    return LOG_ENTRY_MULTIPLE_FILTERS_FORMAT.format(args(player, String.valueOf(groupToPatternMap.keySet()), String.valueOf(groupToPatternMap.values())));
  }

  private static String original(final String message) {
    return LOG_ENTRY_ORIGINAL_MESSAGE_FORMAT.format(args(message));
  }

  public static ChatListener platformApplicable(final ChatRegexFilterPlugin plugin) {
    try {
      Class.forName("io.papermc.paper.event.player.AbstractChatEvent");
      return new ModernPaperChatListener(plugin);
    } catch (final ClassNotFoundException exception) {
      return new SpigotChatListener(plugin);
    }
  }

  protected final ChatRegexFilterPlugin plugin;

  protected ChatListener(final ChatRegexFilterPlugin plugin) {
    this.plugin = plugin;
  }

  public abstract void register();
  public abstract void loadReplacements();

  protected void log(final String player, final String message, final Map<String, String> groupToPatternMap) {
    if (groupToPatternMap.isEmpty()) {
      throw new IllegalArgumentException("group -> pattern map must not be empty");
    }

    if (groupToPatternMap.size() == 1) {
      final Map.Entry<String, String> entry = groupToPatternMap.entrySet().iterator().next();
      this.plugin.getServer().broadcast(single(player, entry.getKey(), entry.getValue()), Permission.NOTIFY_PERMISSION);
    } else {
      this.plugin.getServer().broadcast(multiple(player, groupToPatternMap), Permission.NOTIFY_PERMISSION);
    }

    this.plugin.getServer().broadcast(original(message), Permission.NOTIFY_PERMISSION);
  }
}
