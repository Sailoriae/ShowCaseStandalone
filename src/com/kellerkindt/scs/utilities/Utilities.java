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


import com.kellerkindt.scs.SCSConfiguration;
import com.kellerkindt.scs.ShowCaseStandalone;
import com.kellerkindt.scs.exceptions.MissingOrIncorrectArgumentException;
import com.kellerkindt.scs.interfaces.RunLater;
import com.kellerkindt.scs.interfaces.TriggerableRunLater;
import com.kellerkindt.scs.shops.Shop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;


public class Utilities {

    public static final String ITEM_NAME_ITEM_IN_HAND = "this";
    public static final String ITEM_NAME_ITEM_LATER   = "that";

    private Utilities() {
    }


    /*
     * Quelle: http://www.anyexample.com/programming/java/java_simple_class_to_compute_sha_1_hash.xml
     */
    public static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte  = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if (halfbyte <= 9) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static byte[] fromHexString(String hex) {
        int    len  = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                  + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static String sha1(String text) throws IOException {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            byte[] sha1hash = new byte[40];
            md.update(text.getBytes(StandardCharsets.ISO_8859_1), 0, text.length());
            sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    public static String getRandomSha1() {
        byte bytes[] = new byte[64];
        new Random().nextBytes(bytes);

        return getRandomSha1(new String(bytes));
    }

    public static String getRandomSha1(String s) {
        String sha1 = new Random().nextDouble() + s + System.nanoTime();

        try {
            sha1 = sha1(sha1);
        } catch (IOException ioe) {
        }

        return sha1;
    }


    /**
     * Returns MaterialData for log:2,Wool:4 etc
     */
    public static ItemStack getItemStackFromString(String material) throws IOException {
        String args[] = new String[2];
        if (material.contains(":")) {
            args = material.split(":");
        } else {
            args[0] = material;
            args[1] = "0";
        }

        try {
            Material         m   = Material.matchMaterial(args[0].toUpperCase(), true);
            SCSConfiguration cfg = ShowCaseStandalone.get().getConfiguration(); // singleton, bad...

            int data   = Integer.parseInt(args[1]);
            int amount = 0;


            if (cfg.isSpawningToMax()) {
                amount = m.getMaxStackSize();
            } else {
                amount = cfg.getSpawnCount();
            }

            // for the books (getHandle().tag...)
            return new ItemStack(m, amount, (short) data);
            //            return new ItemStack(m, 1, (short)data);
            //return new MaterialData (m, (byte)data);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * @param player           {@link Player} to get the {@link ItemStack} for
     * @param arg              Either {@link #ITEM_NAME_ITEM_IN_HAND} indicating to return the current item in hand / on cursor,
     *                         or {@link #ITEM_NAME_ITEM_LATER} to return the item in hand / on cursor later on, or a valid item name
     * @param consumer         {@link Consumer} that is going to receive the {@link ItemStack} that has been identified
     * @param runLaterConsumer {@link Consumer} to register a {@link RunLater} instance which will only be needed for {@link #ITEM_NAME_ITEM_LATER}
     * @throws MissingOrIncorrectArgumentException
     */
    public static void getItemStack(Player player, String arg, Consumer<ItemStack> consumer,
                                    Consumer<TriggerableRunLater> runLaterConsumer)
            throws MissingOrIncorrectArgumentException {
        // replace bukkits ItemStack with the generic one, this will get rid of various errors later on
        TriggerableRunLater runLater = new TriggerableRunLater() {
            @Override
            public void trigger() {
                consumer.accept(new ItemStack(player.getItemOnCursor()));
            }

            @Override
            public void abort(Player player) {
                // nothing to do
            }
        };

        if (ITEM_NAME_ITEM_LATER.equalsIgnoreCase(arg)) {
            // trigger later
            runLaterConsumer.accept(runLater);

        } else if (ITEM_NAME_ITEM_IN_HAND.equalsIgnoreCase(arg)) {
            // trigger now
            runLater.trigger();
        } else {
            try {
                // try to parse the string
                consumer.accept(Utilities.getItemStackFromString(arg.toUpperCase()));
            } catch (Exception e) {
                throw new MissingOrIncorrectArgumentException();
            }
        }
    }

    public static ItemStack getItemStack(Player player, String arg) throws MissingOrIncorrectArgumentException {
        try {
            if (arg.equalsIgnoreCase(ITEM_NAME_ITEM_IN_HAND)) {
                // get rid of the CraftBukkit version ... causes errors later
                return new ItemStack(player.getItemInHand());
            } else {
                return Utilities.getItemStackFromString(arg.toUpperCase());
            }
        } catch (Exception ex) {
            throw new MissingOrIncorrectArgumentException();
        }
    }

    /**
     * From https://github.com/davboecki/SignCodePad/blob/master/de/davboecki/signcodepad/event/SignCreate.java
     * but changed by kellerkindt
     *
     * @param sign The Sign
     * @return The block where the sign is placed on
     */
    public static Block getBlockBehind(Sign sign) {
        Location signloc = sign.getBlock().getLocation();
        double   x       = -1;
        double   y       = signloc.getY();
        double   z       = -1;

        switch ((int) sign.getRawData()) {
            //west
            case 2:
                x = signloc.getX();
                z = signloc.getZ() + 1;

                break;

            //east
            case 3:
                x = signloc.getX();
                z = signloc.getZ() - 1;

                break;

            //south
            case 4:
                x = signloc.getX() + 1;
                z = signloc.getZ();

                break;

            //north
            case 5:
                x = signloc.getX() - 1;
                z = signloc.getZ();

                break;
            default:
                return null;

        }

        return sign.getBlock().getWorld().getBlockAt(new Location(sign.getBlock().getWorld(), x, y, z));
    }


    /**
     * Checks if the Shop is behind the Sign
     *
     * @param sign
     * @param shop
     * @return
     */
    public static boolean isShopBehind(Sign sign, Shop shop) {

        Block block = getBlockBehind(sign);

        if (block == null) {
            return false;
        }

        int x = block.getX() - shop.getLocation().getBlockX();
        int y = block.getY() - shop.getLocation().getBlockY();
        int z = block.getZ() - shop.getLocation().getBlockZ();

        return x == 0 && y == 0 && z == 0;
    }

    //    /**
    //     * Converts  the given String into
    //     * the NBTBase it was before
    //     * @param string String to load from
    //     * @return Loaded NBTBase
    //     */
    //    public static NBTBase getNBTBaseFromString (String string) {
    //
    //        // create streams
    //        byte[]                    bytes    = new HexBinaryAdapter().unmarshal(string);
    //        ByteArrayInputStream    bais    = new ByteArrayInputStream(bytes);
    //        DataInputStream            dis        = new DataInputStream(bais);
    //
    //        // load
    //        return NBTBase.b(dis);
    //    }


    /**
     * Serializes the MetaData of an ItemStack
     * and marshals it then in a String
     *
     * @param itemMeta The MetaData so save
     * @return The marshaled ItemStack as String
     * @throws IOException On any internal Exception
     */
    public static String toHexString(ItemMeta itemMeta) throws IOException {

        // create streams
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream    oos  = new ObjectOutputStream(baos);

        // write map
        oos.writeObject(itemMeta.serialize());
        oos.flush();

        // toHexString
        return convertToHex(baos.toByteArray());
    }

    /**
     * Deserializes the ItemMeta of an ItemStack from a marshaled String
     *
     * @param hex ItemMeta marshaled in a String
     * @return The deserialized ItemMeta
     * @throws IOException On any internal exception
     */
    @SuppressWarnings("unchecked")
    public static ItemMeta toItemMeta(String hex) throws IOException {

        // create streams
        byte[]               bytes = fromHexString(hex);
        ByteArrayInputStream bais  = new ByteArrayInputStream(bytes);
        ObjectInputStream    ois   = new ObjectInputStream(bais);

        try {
            return (ItemMeta) ConfigurationSerialization.deserializeObject((Map<String, Object>) ois.readObject());

        } catch (ClassNotFoundException cnfe) {
            throw new IOException(cnfe);
        }
    }
}
