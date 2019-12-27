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
package com.kellerkindt.scs;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author kellerkindt <michael at kellerkindt.com>
 */
public class SCSConfiguration extends Configuration {
    
    public static final String KEY_DEFAULTUNIT              = "Default.Unit";
    public static final String KEY_DEFAULTSHOWTRANSMESSG    = "Default.ShowTransactionMessages";
    public static final String KEY_DEFAULTSHOWTRANSMESSGUN  = "Default.ShowTransactionMessagesOnUnlimited";
    
    public static final String KEY_ECONOMYSYSTEM            = "EconomySystem";
    public static final String KEY_ALLOWUNSAFEENCHANTMENTS  = "AllowUnsafeEnchantments";
    public static final String KEY_CANCELEXPLOSIONS         = "CancelExplosions";
    public static final String KEY_HIDEINACTIVESHOPS        = "HideInactiveShops";
    public static final String KEY_MAXAMOUNTONCREATIVE      = "MaxAmountOnCreative";
    public static final String KEY_REQUIREOBJECTTODISPLAY   = "RequireObjectToDisplay";
    public static final String KEY_ACCESSTHROUGHINVENTORY   = "AccessThroughInventory";
    
    public static final String KEY_CREATEPRICE_BUYSHOP      = "CreatePrice.BuyShop";
    public static final String KEY_CREATEPRICE_SELLSHOP     = "CreatePrice.SellShop";
    public static final String KEY_CREATEPRICE_DISPLAY      = "CreatePrice.Display";
    public static final String KEY_CREATEPRICE_EXCHANGE     = "CreatePrice.Exchange";

    public static final String KEY_HOVER_TEXT_ENABLED                       = "HoverText.Enabled";
    public static final String KEY_HOVER_TEXT_PLAYER_CUSTOM_NAME_ENABLED    = "HoverText.PlayerCustomNameEnabled";
    public static final String KEY_HOVER_TEXT_PLAYER_CUSTOM_NAME_MAX_LENGTH = "HoverText.PlayerCustomNameMaxLength";
    public static final String KEY_HOVER_TEXT_BALANCE_MAX_LENGTH            = "HoverText.BalanceMaxLength";
    public static final String KEY_HOVER_TEXT_BALANCE_FORMATTER             = "HoverText.BalanceFormatter";
    
    public static final String KEY_SAVE_INTERVAL            = "Save.Interval";
    
    public static final String KEY_DISPLAY_USESIGNS         = "Display.UseSigns";
    public static final String KEY_DISPLAY_USESTORAGE       = "Display.UseStorage";

    public static final String KEY_SPAWNING_COUNT           = "Spawning.Count";
    public static final String KEY_SPAWNING_TO_MAX          = "Spawning.ToMax";
    
    public static final String KEY_TOWNY_NEEDSRESIDENT      = "Towny.needsResident";
    public static final String KEY_TOWNY_NEEDSTOBEOWNER     = "Towny.needsToBeOwner";
    public static final String KEY_TOWNY_ALLOWINWILDERNESS  = "Towny.allowInWilderness";
    
    // debug flags, currently not in use :(
    public static final String KEY_DEBUG_THREAD             = "Debug.Thread";
    public static final String KEY_DEBUG_INTERACT           = "Debug.Interact";
    public static final String KEY_DEBUG_PERMISSIONS        = "Debug.Permissions";
    public static final String KEY_DEBUG_CHUNK              = "Debug.Chunk";
    public static final String KEY_DEBUG_SAVE               = "Debug.Save";
    public static final String KEY_DEBUG_SHOWEXTRAMESSAGES  = "Debug.ShowExtraMessages";
    public static final String KEY_DEBUG_LOG                = "Debug.Log";
    public static final String KEY_DEBUG_SHOP_CREATION      = "Debug.Shop.Creation";

    public static final String KEY_DISABLE_FBASIC_ANTIDUPE  = "Disable.FBasicsInventoryDupeListener";

    public static final String KEY_LOCALIZATION_FILE        = "Localization.File";
    public static final String KEY_LOCALIZATION_VERSION     = "Localization.Version";
    
    public static final String KEY_LIMITATION_MAXAMOUNT     = "Limitation.MaxAmount";
    
    public static final String KEY_BLOCKLIST_BLACKLIST      = "BlockList.BlackList";
    public static final String KEY_BLOCKLIST_BLOCKS         = "BlockList.Blocks";
    
    public static final String KEY_SELLITEMLIST_BLACKLIST   = "SellItemList.BlackList";
    public static final String KEY_SELLITEMLIST_ITEMS       = "SellItemList.Items";
    
    public static final String KEY_BUYITEMLIST_BLACKLIST    = "BuyItemList.BlackList";
    public static final String KEY_BUYITEMLIST_ITEMS        = "BuyItemList.Items";
    
    public static final String KEY_WORLDBLACKLIST           = "WorldBlacklist";
    
    public static final String KEY_RESIDENCE_HOOK           = "Residence.hook";
    public static final String KEY_RESIDENCE_FLAG           = "Residence.flag";
    public static final String KEY_RESIDENCE_ALLOWOWNER     = "Residence.allowOwner";
    
    
    public SCSConfiguration(FileConfiguration config) {
        super(config);
        
        // "import" old values / apply updates
        rename("DefaultUnit",                       "Default.Unit");                     // 2014-03-02
        rename("DefaultShowTransactionMessages",    "Default.ShowTransactionMessages"); // 2014-03-02
        rename("Visible.CustomName",                "HoverText.Enabled");               // 2016-11-28
        
        
        // update / set the configuration with default values
        for (Method method : getClass().getDeclaredMethods()) {
            // is a getter?
            if (Modifier.isPublic(method.getModifiers())
                    && !(method.getReturnType().equals(Void.TYPE))
                    && method.getParameterTypes().length == 0
                    && method.getExceptionTypes().length == 0) {
                try {
                    // invoke it, so it is added to the configuration
                    method.invoke(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean getResidenceHookInto () {
        return getForced(KEY_RESIDENCE_HOOK, true);
    }
    
    public String getResidenceFlag () {
        return getForced(KEY_RESIDENCE_FLAG, "shops"); // TODO
    }
    
    public boolean getResidenceAllowOwner () {
        return getForced(KEY_RESIDENCE_ALLOWOWNER, true);
    }

    /**
     * @return The default unit for an interaction with shift
     */
    public int getDefaultUnit () {
        return getForced(KEY_DEFAULTUNIT, 64);
    }
    
    public boolean getDefaultShowTransactionMessage () {
        return getForced(KEY_DEFAULTSHOWTRANSMESSG, true);
    }
    
    public boolean getDefaultShowTransactionMessageOnUnlimited () {
        return getForced(KEY_DEFAULTSHOWTRANSMESSGUN, false);
    }
    
    public String getEconomySystem () {
        return getForced(KEY_ECONOMYSYSTEM, "Vault");
    }
    
    public boolean isCancelingExplosions () {
        return getForced(KEY_CANCELEXPLOSIONS, false);
    }
    
    public boolean isHidingInactiveShops () {
        return getForced(KEY_HIDEINACTIVESHOPS, false);
    }
    
    public boolean isMaxAmountOnCreative () {
        return getForced(KEY_MAXAMOUNTONCREATIVE, true);
    }
    
    public double getCreatePriceBuyShop () {
        return getForced(KEY_CREATEPRICE_BUYSHOP, 0.0);
    }
    
    public double getCreatePriceSellShop () {
        return getForced(KEY_CREATEPRICE_SELLSHOP, 0.0);
    }
    
    public double getCreatePriceDisplay () {
        return getForced(KEY_CREATEPRICE_DISPLAY, 0.0);
    }
    
    public double getCreatePriceExchange () {
        return getForced(KEY_CREATEPRICE_EXCHANGE, 0.0);
    }
    
    public long getSaveInterval () {
        return getForced(KEY_SAVE_INTERVAL, 60l);
    }

    public boolean isHoverTextEnabled() {
        return getForced(KEY_HOVER_TEXT_ENABLED, true);
    }

    public boolean isHoverTextPlayerCustomNameEnabled() {
        return getForced(KEY_HOVER_TEXT_PLAYER_CUSTOM_NAME_ENABLED, true);
    }

    public int getHoverTextPlayerCustomNameMaxLength() {
        return getForced(KEY_HOVER_TEXT_PLAYER_CUSTOM_NAME_MAX_LENGTH, 32);
    }

    /**
     * @return The maximum allowed length of the hover text if formatted by the balance hook
     */
    public int getHoverTextBalanceMaxLength() {
        return getForced(KEY_HOVER_TEXT_BALANCE_MAX_LENGTH, 7);
    }

    public String getHoverTextBalanceFormatter() {
        return getForced(KEY_HOVER_TEXT_BALANCE_FORMATTER, "%.2f$");
    }

    public boolean isDebuggingSave() {
        return getForced(KEY_DEBUG_SAVE, false);
    }

    public boolean isDebuggingThreads () {
        return getForced(KEY_DEBUG_THREAD, false);
    }
    
    public boolean isDebuggingPermissions () {
        return getForced(KEY_DEBUG_PERMISSIONS, false);
    }
    
    public boolean isDebuggingChunks () {
        return getForced(KEY_DEBUG_CHUNK, false);
    }
    
    public boolean isDebuggingLog () {
        return getForced(KEY_DEBUG_LOG, false);
    }

    public boolean isDebuggingShopCreation() {
        return getForced(KEY_DEBUG_SHOP_CREATION, false);
    }
    
    public boolean isDisplayShopUsingSigns () {
        return getForced(KEY_DISPLAY_USESIGNS, true);
    }
    
    public boolean isDisplayShopUsingStorage () {
        return getForced(KEY_DISPLAY_USESTORAGE, true);
    }

    public boolean isSpawningToMax() {
        return getForced(KEY_SPAWNING_TO_MAX, false);
    }

    public int getSpawnCount() {
        return getForced(KEY_SPAWNING_COUNT, 0);// 0 makes it not pickupable!! o.O
    }

    public boolean isTownyAllowingInWilderness () {
        return getForced(KEY_TOWNY_ALLOWINWILDERNESS, false);
    }
    
    public boolean isTownyNeedingResident () {
        return getForced(KEY_TOWNY_NEEDSRESIDENT, true);
    }
    
    public boolean isTownyNeedingToBeOwner () {
        return getForced(KEY_TOWNY_NEEDSTOBEOWNER, false);
    }

    public boolean isDisablingFBasicsInventoryDupeListener() {
        return getForced(KEY_DISABLE_FBASIC_ANTIDUPE, true);
    }
    
    public String getLocalizationFile () {
        return getForced(KEY_LOCALIZATION_FILE, getLocalizationFileDefault());
    }

    public String getLocalizationFileDefault() {
        return "locale_EN.yml";
    }
    
    public double getLocalizationVersion () {
        return getForced(KEY_LOCALIZATION_VERSION, -1.0);
    }
    
    public void setLocalizationVersion (double version) {
        update(KEY_LOCALIZATION_VERSION, version);
    }
    
    public int getLimitationMaxAmountPerPlayer () {
        return getForced(KEY_LIMITATION_MAXAMOUNT, -1);
    }
    
    public boolean isBlockListBlacklist () {
        return getForced(KEY_BLOCKLIST_BLACKLIST, true);
    }
    
    public Collection<String> getBlockListBlocks () {
        // somewhere along the way the list got wiped,
        // so empty lists are going to be refilled
        List<String> list = getForced(KEY_BLOCKLIST_BLOCKS, Collections.emptyList());
        if (list != null && !list.isEmpty()) {
            return list;
        }
        List<String> newList = new ArrayList<>(Arrays.asList(
                Material.ACACIA_LOG.toString(),
                Material.ACACIA_PLANKS.toString(),
                Material.ACACIA_SLAB.toString(),
                Material.ACACIA_STAIRS.toString(),
                Material.ACACIA_WOOD.toString(),
                Material.STRIPPED_ACACIA_LOG.toString(),
                Material.STRIPPED_ACACIA_WOOD.toString(),
                Material.BIRCH_LOG.toString(),
                Material.BIRCH_PLANKS.toString(),
                Material.BIRCH_SLAB.toString(),
                Material.BIRCH_STAIRS.toString(),
                Material.BIRCH_WOOD.toString(),
                Material.STRIPPED_BIRCH_LOG.toString(),
                Material.STRIPPED_BIRCH_WOOD.toString(),
                Material.DARK_OAK_LOG.toString(),
                Material.DARK_OAK_PLANKS.toString(),
                Material.DARK_OAK_SLAB.toString(),
                Material.DARK_OAK_STAIRS.toString(),
                Material.DARK_OAK_WOOD.toString(),
                Material.STRIPPED_DARK_OAK_LOG.toString(),
                Material.STRIPPED_DARK_OAK_WOOD.toString(),
                Material.JUNGLE_LOG.toString(),
                Material.JUNGLE_PLANKS.toString(),
                Material.JUNGLE_SLAB.toString(),
                Material.JUNGLE_STAIRS.toString(),
                Material.JUNGLE_WOOD.toString(),
                Material.STRIPPED_JUNGLE_LOG.toString(),
                Material.STRIPPED_JUNGLE_WOOD.toString(),
                Material.OAK_LOG.toString(),
                Material.OAK_PLANKS.toString(),
                Material.OAK_SLAB.toString(),
                Material.OAK_STAIRS.toString(),
                Material.OAK_WOOD.toString(),
                Material.STRIPPED_OAK_LOG.toString(),
                Material.STRIPPED_OAK_WOOD.toString(),
                Material.SPRUCE_LOG.toString(),
                Material.SPRUCE_PLANKS.toString(),
                Material.SPRUCE_SLAB.toString(),
                Material.SPRUCE_STAIRS.toString(),
                Material.SPRUCE_WOOD.toString(),
                Material.STRIPPED_SPRUCE_LOG.toString(),
                Material.STRIPPED_SPRUCE_WOOD.toString(),
                Material.ANDESITE.toString(),
                Material.BRICKS.toString(),
                Material.COBBLESTONE.toString(),
                Material.DIORITE.toString(),
                Material.END_STONE_BRICKS.toString(),
                Material.GRANITE.toString(),
                Material.MOSSY_COBBLESTONE.toString(),
                Material.MOSSY_STONE_BRICKS.toString(),
                Material.NETHER_BRICKS.toString(),
                Material.POLISHED_ANDESITE.toString(),
                Material.POLISHED_DIORITE.toString(),
                Material.POLISHED_GRANITE.toString(),
                Material.PRISMARINE_BRICKS.toString(),
                Material.PRISMARINE.toString(),
                Material.RED_NETHER_BRICKS.toString(),
                Material.RED_SANDSTONE.toString(),
                Material.SANDSTONE.toString(),
                Material.SMOOTH_QUARTZ.toString(),
                Material.SMOOTH_RED_SANDSTONE.toString(),
                Material.SMOOTH_SANDSTONE.toString(),
                Material.STONE.toString(),
                Material.STONE_BRICKS.toString(),
                Material.ANDESITE_SLAB.toString(),
                Material.BRICK_SLAB.toString(),
                Material.COBBLESTONE_SLAB.toString(),
                Material.DIORITE_SLAB.toString(),
                Material.END_STONE_BRICK_SLAB.toString(),
                Material.GRANITE_SLAB.toString(),
                Material.MOSSY_COBBLESTONE_SLAB.toString(),
                Material.MOSSY_STONE_BRICK_SLAB.toString(),
                Material.NETHER_BRICK_SLAB.toString(),
                Material.POLISHED_ANDESITE_SLAB.toString(),
                Material.POLISHED_DIORITE_SLAB.toString(),
                Material.POLISHED_GRANITE_SLAB.toString(),
                Material.PRISMARINE_BRICK_SLAB.toString(),
                Material.PRISMARINE_SLAB.toString(),
                Material.RED_NETHER_BRICK_SLAB.toString(),
                Material.RED_SANDSTONE_SLAB.toString(),
                Material.SANDSTONE_SLAB.toString(),
                Material.SMOOTH_QUARTZ_SLAB.toString(),
                Material.SMOOTH_RED_SANDSTONE_SLAB.toString(),
                Material.SMOOTH_SANDSTONE_SLAB.toString(),
                Material.STONE_SLAB.toString(),
                Material.STONE_BRICK_SLAB.toString(),
                Material.ANDESITE_STAIRS.toString(),
                Material.BRICK_STAIRS.toString(),
                Material.COBBLESTONE_STAIRS.toString(),
                Material.DIORITE_STAIRS.toString(),
                Material.END_STONE_BRICK_STAIRS.toString(),
                Material.GRANITE_STAIRS.toString(),
                Material.MOSSY_COBBLESTONE_STAIRS.toString(),
                Material.MOSSY_STONE_BRICK_STAIRS.toString(),
                Material.NETHER_BRICK_STAIRS.toString(),
                Material.POLISHED_ANDESITE_STAIRS.toString(),
                Material.POLISHED_DIORITE_STAIRS.toString(),
                Material.POLISHED_GRANITE_STAIRS.toString(),
                Material.PRISMARINE_BRICK_STAIRS.toString(),
                Material.PRISMARINE_STAIRS.toString(),
                Material.RED_NETHER_BRICK_STAIRS.toString(),
                Material.RED_SANDSTONE_STAIRS.toString(),
                Material.SANDSTONE_STAIRS.toString(),
                Material.SMOOTH_QUARTZ_STAIRS.toString(),
                Material.SMOOTH_RED_SANDSTONE_STAIRS.toString(),
                Material.SMOOTH_SANDSTONE_STAIRS.toString(),
                Material.STONE_STAIRS.toString(),
                Material.STONE_BRICK_STAIRS.toString(),
                Material.BLACK_CONCRETE.toString(),
                Material.BLUE_CONCRETE.toString(),
                Material.BROWN_CONCRETE.toString(),
                Material.CYAN_CONCRETE.toString(),
                Material.GRAY_CONCRETE.toString(),
                Material.GREEN_CONCRETE.toString(),
                Material.LIGHT_BLUE_CONCRETE.toString(),
                Material.LIGHT_GRAY_CONCRETE.toString(),
                Material.LIME_CONCRETE.toString(),
                Material.MAGENTA_CONCRETE.toString(),
                Material.ORANGE_CONCRETE.toString(),
                Material.PINK_CONCRETE.toString(),
                Material.PURPLE_CONCRETE.toString(),
                Material.RED_CONCRETE.toString(),
                Material.WHITE_CONCRETE.toString(),
                Material.YELLOW_CONCRETE.toString(),
                Material.BLACK_GLAZED_TERRACOTTA.toString(),
                Material.BLUE_GLAZED_TERRACOTTA.toString(),
                Material.BROWN_GLAZED_TERRACOTTA.toString(),
                Material.CYAN_GLAZED_TERRACOTTA.toString(),
                Material.GRAY_GLAZED_TERRACOTTA.toString(),
                Material.GREEN_GLAZED_TERRACOTTA.toString(),
                Material.LIGHT_BLUE_GLAZED_TERRACOTTA.toString(),
                Material.LIGHT_GRAY_GLAZED_TERRACOTTA.toString(),
                Material.LIME_GLAZED_TERRACOTTA.toString(),
                Material.MAGENTA_GLAZED_TERRACOTTA.toString(),
                Material.ORANGE_GLAZED_TERRACOTTA.toString(),
                Material.PINK_GLAZED_TERRACOTTA.toString(),
                Material.PURPLE_GLAZED_TERRACOTTA.toString(),
                Material.RED_GLAZED_TERRACOTTA.toString(),
                Material.WHITE_GLAZED_TERRACOTTA.toString(),
                Material.YELLOW_GLAZED_TERRACOTTA.toString(),
                Material.BLACK_TERRACOTTA.toString(),
                Material.BLUE_TERRACOTTA.toString(),
                Material.BROWN_TERRACOTTA.toString(),
                Material.CYAN_TERRACOTTA.toString(),
                Material.GRAY_TERRACOTTA.toString(),
                Material.GREEN_TERRACOTTA.toString(),
                Material.LIGHT_BLUE_TERRACOTTA.toString(),
                Material.LIGHT_GRAY_TERRACOTTA.toString(),
                Material.LIME_TERRACOTTA.toString(),
                Material.MAGENTA_TERRACOTTA.toString(),
                Material.ORANGE_TERRACOTTA.toString(),
                Material.PINK_TERRACOTTA.toString(),
                Material.PURPLE_TERRACOTTA.toString(),
                Material.RED_TERRACOTTA.toString(),
                Material.WHITE_TERRACOTTA.toString(),
                Material.YELLOW_TERRACOTTA.toString(),
                Material.BLACK_WOOL.toString(),
                Material.BLUE_WOOL.toString(),
                Material.BROWN_WOOL.toString(),
                Material.CYAN_WOOL.toString(),
                Material.GRAY_WOOL.toString(),
                Material.GREEN_WOOL.toString(),
                Material.LIGHT_BLUE_WOOL.toString(),
                Material.LIGHT_GRAY_WOOL.toString(),
                Material.LIME_WOOL.toString(),
                Material.MAGENTA_WOOL.toString(),
                Material.ORANGE_WOOL.toString(),
                Material.PINK_WOOL.toString(),
                Material.PURPLE_WOOL.toString(),
                Material.RED_WOOL.toString(),
                Material.WHITE_WOOL.toString(),
                Material.YELLOW_WOOL.toString(),
                Material.BOOKSHELF.toString(),
                Material.CHISELED_QUARTZ_BLOCK.toString(),
                Material.CHISELED_QUARTZ_BLOCK.toString(),
                Material.CHISELED_SANDSTONE.toString(),
                Material.CHISELED_STONE_BRICKS.toString(),
                Material.CRACKED_STONE_BRICKS.toString(),
                Material.CUT_RED_SANDSTONE.toString(),
                Material.CUT_RED_SANDSTONE_SLAB.toString(),
                Material.CUT_SANDSTONE.toString(),
                Material.CUT_SANDSTONE_SLAB.toString(),
                Material.DARK_PRISMARINE.toString(),
                Material.DARK_PRISMARINE_SLAB.toString(),
                Material.DARK_PRISMARINE_STAIRS.toString(),
                Material.DIAMOND_BLOCK.toString(),
                Material.EMERALD_BLOCK.toString(),
                Material.LAPIS_BLOCK.toString(),
                Material.GOLD_BLOCK.toString(),
                Material.IRON_BLOCK.toString(),
                Material.OBSIDIAN.toString(),
                Material.PURPUR_BLOCK.toString(),
                Material.PURPUR_PILLAR.toString(),
                Material.PURPUR_SLAB.toString(),
                Material.PURPUR_STAIRS.toString(),
                Material.QUARTZ_BLOCK.toString(),
                Material.QUARTZ_PILLAR.toString(),
                Material.QUARTZ_SLAB.toString(),
                Material.QUARTZ_STAIRS.toString(),
                Material.SMOOTH_STONE.toString(),
                Material.SMOOTH_STONE_SLAB.toString(),
                Material.TERRACOTTA.toString()
        ));
        /*for (Material material : Material.values()) { // WHAT IS THIS ??? WHAT IS ITS PURPOSE ?
            String name = material.toString().toLowerCase();
            if (name.contains("_sign") || name.contains("rose")) {
                newList.add(material.toString());
            }
        }*/
        return getForced(KEY_BLOCKLIST_BLOCKS, newList);
    }
    
    public boolean isSellItemListBlacklist () {
        return getForced(KEY_SELLITEMLIST_BLACKLIST, true);
    }
    
    public boolean hasAccessThroughInventory() {
        return getForced(KEY_ACCESSTHROUGHINVENTORY, true);
    }
    
    public List<String> getSellItemListItemList () {
        return getForced(KEY_SELLITEMLIST_ITEMS, new ArrayList<String>());
    }
    
    public boolean isBuyItemListBlackList () {
        return getForced(KEY_BUYITEMLIST_BLACKLIST, true);
    }
    
    public List<String> getBuyItemListItemList () {
        return getForced(KEY_BUYITEMLIST_ITEMS, new ArrayList<String>());
    }
    
    public List<String> getWorldsBlacklisted () {
        return getForced(KEY_WORLDBLACKLIST, new ArrayList<String>());
    }
}
