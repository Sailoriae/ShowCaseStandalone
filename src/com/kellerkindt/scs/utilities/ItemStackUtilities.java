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
package com.kellerkindt.scs.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public abstract class ItemStackUtilities {


    /**
     * Counts the Items that are compatible with the given ItemStacks
     *
     * @param inventory
     * @param allowedItems
     * @param checkNBT
     * @return
     */
    public static int countCompatibleItemStacks(Inventory inventory, List<ItemStack> allowedItems, boolean checkNBT) {
        int found = 0;

        // Thru all ItemStacks in the inventory
        for (ItemStack is1 : inventory) {

            // Check if its allowed
            for (ItemStack is2 : allowedItems) {
                if (itemsEqual(is1, is2, checkNBT)) {
                    found += is1.getAmount();
                }
            }
        }

        return found;
    }

    /**
     * Counts the Items that are compatible with the given ItemStack
     *
     * @param inventory
     * @param itemStack
     * @param checkNBT
     * @return
     */
    public static int countCompatibleItemStacks(Inventory inventory, ItemStack itemStack, boolean checkNBT) {
        List<ItemStack> list = new ArrayList<ItemStack>();
        list.add(itemStack);
        return countCompatibleItemStacks(inventory, list, checkNBT);
    }

    public static int countCouldAdd(Inventory inventory, ItemStack stack) {
        int amount = Integer.MAX_VALUE;

        amount = addToInventory(inventory, stack, amount);
        removeFromInventory(inventory, stack, amount, true);

        return amount;
    }

    /**
     * Tries to add ItemStacks of the given amount to the Inventory
     *
     * @param inventory
     * @param type
     * @param amount
     * @return The amount of items that were actually added
     */
    public static int addToInventory(Inventory inventory, ItemStack type, int amount) {
        if (amount <= 0) {
            return 0;
        }

        ItemStack is    = cloneItemStack(type);
        int       added = 0;


        while (amount > 0) {
            int toAdd = amount > is.getMaxStackSize() ? is.getMaxStackSize() : amount;
            is.setAmount(toAdd);

            // try to add
            Map<Integer, ItemStack> map = inventory.addItem(is);

            // if the map is not empty, not all could be added
            if (!map.values().isEmpty()) {
                // get the ItemStack that was not added
                ItemStack notAdded = map.values().iterator().next();

                int nowAdded = toAdd - notAdded.getAmount();

                // not all could be added
                added += nowAdded;
                amount -= nowAdded;

                // check whether zero were added
                if (nowAdded == 0) {
                    break;
                }

            } else {

                // the full item stack was added
                added += toAdd;
                amount -= toAdd;
            }
        }

        if (added > 0 && inventory.getHolder() instanceof Player) {
            ((Player) inventory.getHolder()).updateInventory();
        }

        // all added
        return added;
    }

    /**
     * Tries to remove the given amount of ItemStacks
     * from the given Inventory that are listed in allowedItems
     *
     * @param inventory
     * @param allowedItems
     * @param amount       the amount or -1 for all
     * @param checkNBT
     * @return The amount of items that were actually removed
     */
    public static int removeFromInventory(Inventory inventory, List<ItemStack> allowedItems, int amount,
                                          boolean checkNBT) {

        int     removed   = 0;
        boolean unlimited = amount < 0;
        Player  player    = inventory.getHolder() instanceof Player ? ((Player) inventory.getHolder()) : null;


        for (int i = 0; i < inventory.getSize(); ++i) {
            ItemStack is1 = inventory.getItem(i);

            if (is1 == null) {
                continue;
            }

            // checks if is1 can be removed
            for (ItemStack is2 : allowedItems) {
                if (itemsEqual(is1, is2, checkNBT)) {

                    if (unlimited) {
                        removed += is1.getAmount();
                        inventory.clear(i);
                    } else {

                        if (is1.getAmount() <= amount) {
                            inventory.clear(i);

                            amount -= is1.getAmount();
                            removed += is1.getAmount();
                            is1.setAmount(0);
                        } else {
                            is1.setAmount(is1.getAmount() - amount);
                            removed += amount;
                            amount = 0;
                        }

                        // found all
                        if (amount == 0) {
                            break;
                        }
                    }


                    break;
                }
            }
        }

        if (removed > 0 && inventory.getHolder() instanceof Player) {
            // needed...
            ((Player) inventory.getHolder()).updateInventory();
        }

        return removed;
    }

    /**
     * Tries to remove the given amount of ItemStacks
     * from the given Inventory of the given ItemStack
     *
     * @param inventory
     * @param itemStack
     * @param amount    the amount or -1 for all
     * @param checkNBT
     * @return The amount of items that were actually removed
     */
    public static int removeFromInventory(Inventory inventory, ItemStack itemStack, int amount, boolean checkNBT) {
        return removeFromInventory(inventory, Arrays.asList(itemStack), amount, checkNBT);
    }

    /**
     * Returns whether the underlying item (TypeID, Durability and enchantments) are equal.
     * This is almost identical to .equals() for ItemStack, but ignores the amount in the stack.
     *
     * @param is1
     * @param is2
     */
    public static boolean itemsEqual(ItemStack is1, ItemStack is2, boolean checkNBT) {
        if (is1 == null || is2 == null) {
            return is1 == is2;
        }

        if (checkNBT) {
            return is1.isSimilar(is2);

        } else {
            // unequal Enchantments?
            if (is1.getEnchantments().size() != is2.getEnchantments().size()) {
                return false;
            }

            for (Enchantment enchantment : is1.getEnchantments().keySet()) {
                if (is1.getEnchantmentLevel(enchantment) != is2.getEnchantmentLevel(enchantment)) {
                    return false;
                }
            }


            return is1.getType().equals(is2.getType()) && is1.getDurability() == is2.getDurability();
        }

        // TODO: remove comment
        //        if (checkNBT) {
        //            return is1.equals(is2);
        //        }
        //
        //        boolean eqId    = is1.getTypeId() == is2.getTypeId();
        //        boolean eqData    = is1.getData().equals(is2.getData());
        //        boolean eqDurab    = is1.getDurability() == is2.getDurability();
        //        boolean eqEnch    = enchantmentsEqual(is1.getEnchantments(), is2.getEnchantments());
        //        boolean eqNBT    = true;
        //        boolean is1CIS    = is1 instanceof CraftItemStack;
        //        boolean is2CIS    = is2 instanceof CraftItemStack;
        //
        //        if (is1CIS && is2CIS) {
        //
        //
        //
        //            NBTTagCompound     nbt1        = CraftItemStack.asNMSCopy(is1).tag;//((CraftItemStack)is1).get.getHandle().tag;
        //            NBTTagCompound     nbt2        = CraftItemStack.asNMSCopy(is2).tag;//((CraftItemStack)is2).getHandle().tag;
        //            boolean            nullNBT1    = nbt1 == null;
        //            boolean            nullNBT2    = nbt2 == null;
        //
        //            if (nullNBT1 == nullNBT2) {
        //                if (!nullNBT1) {
        //                    eqNBT = nbt1.equals(nbt2);
        //                }
        //            } else
        //                eqNBT = false;
        //        }
        //
        //        return eqId && eqData && eqDurab && eqEnch && ((eqNBT && (is1CIS == is2CIS)) || !checkNBT);
    }

    /**
     * @param en1
     * @param en2
     * @return Whether the given maps of enchantments are equal
     */
    public static boolean enchantmentsEqual(Map<Enchantment, Integer> en1, Map<Enchantment, Integer> en2) {
        if (en1.size() != en2.size()) {
            return false;
        }

        for (Enchantment een1 : en1.keySet()) {
            boolean found = false;

            for (Enchantment een2 : en2.keySet()) {
                if (een1.equals(een2)) {
                    found = true;
                }
            }

            if (!found) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a specific name to display
     * depending on the type of the
     * given ItemStack
     *
     * @param stack ItemStack to take as a base
     * @return The specific name to display
     */
    public static String getDisplayName(ItemStack stack) {
        switch (stack.getType()) {
            // in case of a written book, return the title of the book
            case WRITTEN_BOOK:
                if (stack.getItemMeta() instanceof BookMeta) {
                    return ((BookMeta) stack.getItemMeta()).getTitle();
                }

                // default name
            default:
                return stack.getItemMeta().getDisplayName();
        }
    }

    /**
     * Since there are some issues
     *
     * @param is
     * @return
     */
    public static ItemStack cloneItemStack(ItemStack is) {

        // hopefully fixed now
        return is.clone();

        // TODO: remove comment
        //        ItemStack cloned = is.clone();
        //
        //        if (cloned instanceof CraftItemStack) {
        //            CraftItemStack    cCloned    = (CraftItemStack)cloned;
        //            CraftItemStack    cIs        = (CraftItemStack)is;
        //
        //            if (cIs.getHandle().tag != null) {
        //                NBTTagCompound    com    = (NBTTagCompound)cIs.getHandle().tag.clone();
        //                cCloned.getHandle().setTag(com);
        //            }
        //        }
        //
        //
        //
        //        return cloned;
    }
}
