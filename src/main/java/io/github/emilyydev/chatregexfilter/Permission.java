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

public interface Permission {

  /**
   * Players with this permission will be ignored by the plugin filters.
   */
  String BYPASS = permission("bypass");

  /**
   * Grants access to the plugin's command.
   */
  String COMMAND = permission("command");

  /**
   * Players with this permission will receive a chat message when a player's message
   * had contents filtered. They will receive the banned word(s) in question
   * and the original message.
   */
  String NOTIFY = permission("notify");

  private static String permission(final String node) {
    return "chatregexfilter.".concat(node);
  }
}
