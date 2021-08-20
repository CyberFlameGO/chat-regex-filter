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

package io.github.emilyydev.chatregexfilter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.emilyydev.chatregexfilter.command.CrfCommand;
import io.github.emilyydev.chatregexfilter.config.ConfigEntry;
import io.github.emilyydev.chatregexfilter.config.PatternTypeAdapter;
import io.github.emilyydev.chatregexfilter.listener.ChatListener;
import io.github.emilyydev.chatregexfilter.util.ThrowingRunnable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Unmodifiable;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toUnmodifiableMap;

public final class ChatRegexFilterPlugin extends JavaPlugin implements Listener {

  public static final Gson GSON = new GsonBuilder()
      .registerTypeHierarchyAdapter(Pattern.class, PatternTypeAdapter.INSTANCE)
      .create();
  private static final TypeToken<Set<ConfigEntry>> FILTERS_TYPE = new TypeToken<>() { };

  private Map<Pattern, String> filters = Map.of();
  private final Path pluginFolder = getDataFolder().toPath();
  private final Path filtersFile = this.pluginFolder.resolve("filters.json");
  private final ChatListener chatListener = ChatListener.platformApplicable(this);
  private final CrfCommand crfCommand = new CrfCommand(this);

  @Override
  public void onLoad() {
    try {
      if (Files.notExists(this.filtersFile)) {
        Files.createDirectories(this.pluginFolder);
        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("filters.json")) {
          Files.copy(Objects.requireNonNull(inputStream, "inputStream"), this.filtersFile);
        }
      }

      reloadConfig();
    } catch (final IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  public void onEnable() {
    this.chatListener.register();
    this.crfCommand.register();
  }

  public @Unmodifiable Map<Pattern, String> getFilters() {
    return this.filters;
  }

  @Override
  public void reloadConfig() {
    ThrowingRunnable.sneaky(() -> {
      try (final Reader reader = Files.newBufferedReader(this.filtersFile)) {
        final Set<ConfigEntry> configEntries = GSON.fromJson(reader, FILTERS_TYPE.getType());
        this.filters = configEntries.stream().collect(toUnmodifiableMap(ConfigEntry::pattern, ConfigEntry::replacement));
      }

      this.chatListener.loadReplacements();
    }).run();
  }

  public <E extends Event> void registerListener(final Class<E> eventClass, final Consumer<? super E> handler) {
    registerListener(eventClass, handler, EventPriority.NORMAL, false);
  }

  public <E extends Event> void registerListener(final Class<E> eventClass, final Consumer<? super E> handler,
                                                 final boolean callIfCancelled) {
    registerListener(eventClass, handler, EventPriority.NORMAL, callIfCancelled);
  }

  public <E extends Event> void registerListener(final Class<E> eventClass, final Consumer<? super E> handler,
                                                 final EventPriority priority) {
    registerListener(eventClass, handler, priority, false);
  }

  public <E extends Event> void registerListener(final Class<E> eventClass, final Consumer<? super E> handler,
                                                 final EventPriority priority, final boolean callIfCancelled) {
    getServer().getPluginManager().registerEvent(eventClass, this, priority, (listener, event) -> {
      if (eventClass.isInstance(event)) {
        handler.accept(eventClass.cast(event));
      }
    }, this, !callIfCancelled);
  }
}
