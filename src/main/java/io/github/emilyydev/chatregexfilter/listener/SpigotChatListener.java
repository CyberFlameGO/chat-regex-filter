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
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class SpigotChatListener extends ChatListener {

  private Map<Pattern, String> filters = Map.of();

  SpigotChatListener(final ChatRegexFilterPlugin plugin) {
    super(plugin);
  }

  @Override
  public void loadReplacements() {
    this.filters = this.plugin.getFilters();
  }

  @Override
  public void register() {
    this.plugin.registerListener(AsyncPlayerChatEvent.class, this::asyncPlayerChat, EventPriority.HIGH);
  }

  private void asyncPlayerChat(final AsyncPlayerChatEvent event) {
    if (event.getPlayer().hasPermission(Permission.BYPASS_PERMISSION)) {
      return;
    }

    final Map<Pattern, String> filters = this.filters;
    final Map<String, String> groupToPatternMap = new LinkedHashMap<>();
    final String originalMessage = event.getMessage();

    String message = originalMessage;
    for (final Map.Entry<Pattern, String> entry : filters.entrySet()) {
      final Matcher matcher = entry.getKey().matcher(message);
      if (matcher.find()) {
        message = matcher.replaceAll(entry.getValue());
        groupToPatternMap.put(entry.getValue(), entry.getKey().pattern());
      }
    }

    if (!groupToPatternMap.isEmpty()) {
      log(event.getPlayer().getName(), originalMessage, groupToPatternMap);
    }

    event.setMessage(message);
  }
}
