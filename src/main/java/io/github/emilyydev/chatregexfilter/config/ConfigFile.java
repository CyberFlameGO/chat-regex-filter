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

package io.github.emilyydev.chatregexfilter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.regex.Pattern;

public final class ConfigFile {

  private static final Gson CASE_SENSITIVE = new GsonBuilder()
      .registerTypeAdapter(Pattern.class, PatternTypeAdapter.CASE_SENSITIVE)
      .create();
  private static final Gson CASE_INSENSITIVE = new GsonBuilder()
      .registerTypeAdapter(Pattern.class, PatternTypeAdapter.CASE_INSENSITIVE)
      .create();
  private static final Type FILTER_ENTRY_SET_TYPE = new TypeToken<Set<FilterEntry>>() { }.getType();

  private final boolean caseInsensitive;
  private final JsonArray filters;
  private transient Set<FilterEntry> filterEntrySet = null;

  public ConfigFile(final boolean caseInsensitive, final JsonArray filters) {
    this.caseInsensitive = caseInsensitive;
    this.filters = filters;
  }

  public @Unmodifiable Set<FilterEntry> filters() {
    if (this.filterEntrySet == null) {
      load();
    }
    return this.filterEntrySet;
  }

  private void load() {
    this.filterEntrySet = this.caseInsensitive
        ? CASE_INSENSITIVE.fromJson(this.filters, FILTER_ENTRY_SET_TYPE)
        : CASE_SENSITIVE.fromJson(this.filters, FILTER_ENTRY_SET_TYPE);
  }
}
