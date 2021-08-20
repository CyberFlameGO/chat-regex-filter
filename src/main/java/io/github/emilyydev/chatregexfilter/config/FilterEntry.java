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

import java.util.Objects;
import java.util.regex.Pattern;

public final class FilterEntry {

  private final Pattern pattern;
  private final String replacement;

  public FilterEntry(final Pattern pattern, final String replacement) {
    this.pattern = pattern;
    this.replacement = replacement;
  }

  public Pattern pattern() {
    return this.pattern;
  }

  public String replacement() {
    return this.replacement;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == this) { return true; }
    if (other == null || other.getClass() != this.getClass()) { return false; }
    final FilterEntry that = (FilterEntry) other;
    return Objects.equals(this.pattern, that.pattern) &&
           Objects.equals(this.replacement, that.replacement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.pattern, this.replacement);
  }

  @Override
  public String toString() {
    return "FilterEntry["
           + "pattern=" + this.pattern + ", "
           + "replacement=" + this.replacement
           + ']';
  }
}
