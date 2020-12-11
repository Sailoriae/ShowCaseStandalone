/*
 * ShowCaseStandalone - A Minecraft-Bukkit-API Shop Plugin
 * Copyright (C) 2016-08-16 22:43 +02 kellerkindt (Michael Watzko) <copyright at kellerkindt.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kellerkindt.scs.listeners;

import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.events.ShowCaseCreateEvent;
import com.kellerkindt.scs.exceptions.InsufficientPermissionException;
import com.kellerkindt.scs.utilities.Term;
import com.gmail.jameshealey1994.simpletowns.SimpleTowns;
import com.gmail.jameshealey1994.simpletowns.permissions.STPermission;
import com.gmail.jameshealey1994.simpletowns.utils.TownUtils;
import com.gmail.jameshealey1994.simpletowns.object.Town;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.block.Block;

public class SimpleTownsListener implements Listener {

    protected ShowCaseStandalone scs;
    protected SimpleTowns simpleTowns;

    public SimpleTownsListener(ShowCaseStandalone scs, Plugin sTowns) {
        this.scs = scs;

        if (sTowns instanceof SimpleTowns ) {
            this.simpleTowns = (SimpleTowns) sTowns;
        } else {
            throw new ClassCastException("Given Plugin is not SimpleTowns");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onShopCreate(ShowCaseCreateEvent event) {
        if (!event.verify()) {
            // nothing to do
            return;
        }

        if (scs.getConfiguration().isDebuggingShopCreation()) {
            scs.getLogger().info("Entered ShowCaseExecutingListener::onShowCaseCreateEvent");
        }

        Block block = event.getShop().getLocation().getBlock();
        Player player = event.getPlayer();

        boolean isAllowed = false;

        // Same as :
        // https://github.com/JamesHealey94/SimpleTowns/blob/master/SimpleTowns/src/com/gmail/jameshealey1994/simpletowns/listeners/STListener.java
        if (player.hasPermission(STPermission.ADMIN.getPermission())) {
            isAllowed = true;
        } else {
            final Town town = simpleTowns.getTown(block.getChunk());
            if (town == null) {
                if (block.getLocation().getBlockY() <= new TownUtils(simpleTowns).getMineRoofY()) {
                    isAllowed = player.hasPermission(STPermission.BUILD_MINES.getPermission());
                } else {
                    isAllowed = player.hasPermission(STPermission.BUILD_WILDERNESS.getPermission());
                }
            } else {
                isAllowed = town.hasMember(player.getUniqueId()) && player.hasPermission(STPermission.BUILD_TOWNS.getPermission());
            }
        }

        if (!isAllowed) {
            event.setCancelled(true);
            event.setCause(new InsufficientPermissionException(
                    Term.ERROR_INSUFFICIENT_PERMISSION_SIMPLETOWNS.get()
            ));

            if (scs.getConfiguration().isDebuggingShopCreation()) {
                scs.getLogger().info("Declined cause player is not allowed to build here");
            }
        }

        if (scs.getConfiguration().isDebuggingShopCreation()) {
            scs.getLogger().info("Leaving ShowCaseExecutingListener::onShowCaseCreateEvent");
        }
    }
}
