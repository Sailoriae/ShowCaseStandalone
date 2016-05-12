/*
 * ShowCaseStandalone
 * Copyright (c) 2016-02-14 19:19 +01 by Kellerkindt, <copyright at kellerkindt.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.kellerkindt.scs.events;

import com.kellerkindt.scs.shops.Shop;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

/**
 * @author Michael <michael at kellerkindt.com>
 *
 * This event cannot be cancelled
 */
public class ShowCaseItemSpawnEvent extends ShowCaseShopEvent {

    protected Location location;

    public ShowCaseItemSpawnEvent(Player player, Shop shop, Location location) {
        super(player, shop);

        this.location = location;
    }

    /**
     * @return The {@link Location} the {@link Item} is spawned
     */
    public Location getLocation() {
        return location;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        // cannot be cancelled
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}