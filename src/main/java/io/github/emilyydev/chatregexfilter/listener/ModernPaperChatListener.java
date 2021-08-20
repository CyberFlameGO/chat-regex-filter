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
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventPriority;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;

final class ModernPaperChatListener extends ChatListener {

  private static final ComponentSerializer<Component, TextComponent, String> PLAIN_COMPONENT_SERIALIZER;

  static {
    ComponentSerializer<Component, TextComponent, String> plainComponentSerializer;

    try {
      Class.forName("net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer");
      plainComponentSerializer = PlainTextComponentSerializer.plainText();
    } catch (final ClassNotFoundException exception) {
      plainComponentSerializer = PlainComponentSerializer.plain();
    }

    PLAIN_COMPONENT_SERIALIZER = plainComponentSerializer;
  }

  private Set<TextReplacementConfig> replacementConfigs = Set.of();
  // someone come up with a better idea, PLEASE!
  private final ThreadLocal<Map<String, String>> currentGroupToPatternMap = ThreadLocal.withInitial(LinkedHashMap::new);

  ModernPaperChatListener(final ChatRegexFilterPlugin plugin) {
    super(plugin);
  }

  @Override
  public void loadReplacements() {
    this.replacementConfigs = this.plugin.getFilters().entrySet().stream()
                                         .map(entry -> TextReplacementConfig
                                             .builder()
                                             .match(entry.getKey())
                                             .replacement((matchResult, builder) -> {
                                               this.currentGroupToPatternMap.get().put(matchResult.group(), entry.getKey().pattern());
                                               return builder.content(entry.getKey().matcher(builder.content()).replaceAll(entry.getValue()));
                                             })
                                             .build())
                                         .collect(toUnmodifiableSet());
  }

  @Override
  public void register() {
    this.plugin.registerListener(AsyncChatEvent.class, this::asyncChat, EventPriority.HIGH);
  }

  private void asyncChat(final AsyncChatEvent event) {
    if (event.getPlayer().hasPermission(Permission.BYPASS_PERMISSION)) {
      return;
    }

    final Set<TextReplacementConfig> replacementConfigs = this.replacementConfigs;
    final String originalMessagePlain = PLAIN_COMPONENT_SERIALIZER.serialize(event.message());

    Component message = event.message();
    for (final TextReplacementConfig config : replacementConfigs) {
      message = message.replaceText(config);
    }

    if (!this.currentGroupToPatternMap.get().isEmpty()) {
      log(event.getPlayer().getName(), originalMessagePlain, this.currentGroupToPatternMap.get());
      this.currentGroupToPatternMap.remove();
    }

    event.message(message);
  }
}
