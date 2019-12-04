package com.rabbit13.events.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public final class Misc {

    public Misc() {
    }

    /**
     * Sends a message to player.
     *
     * @param cs     - command sender
     * @param s      - text that will be sended to command sender
     * @param colors - if true, colors will be used through &
     */
    public static void sendLM(String s, boolean colors, CommandSender... cs) {
        for (CommandSender c : cs) {
            c.sendMessage(colors ? ChatColor.translateAlternateColorCodes('&', s) : s);
        }
    }

    /**
     * Will save all given {@link InputStream} data into determined File
     *
     * @param input data to be copied into determined File
     * @param to    file where all data will be stored
     * @throws IOException if file could not be found and created
     */
    public static void copy(InputStream input, File to) throws IOException {
        if (!to.exists()) if (!to.createNewFile()) throw new IOException("File Extraction failed!");
        FileOutputStream output = new FileOutputStream(to);
        byte[] b = new byte[8192];
        int length;
        while ((length = input.read(b)) > 0)
            output.write(b, 0, length);
    }

    /**
     * @param txt - text to be reported via console
     */
    public static void debugMessage(Object txt) {
        if (Main.isDebugMode())
            Bukkit.getServer().getConsoleSender().sendMessage(Main.getPluginPrefix() + ": " + txt);
    }

    /**
     * @param material Material of an final ItemStack
     * @param amount   amount of items in ItemStack
     * @param name     Custom name of an ItemStack, supports colors through "&" ColorChar
     * @param lore     Lores of an ItemStack
     * @return Custom-made ItemStack
     */
    public static ItemStack getSpecifiedItem(@NotNull Material material, int amount, @Nullable String name, @Nullable String... lore) {
        ItemStack item = new ItemStack(material, (amount <= 0) ? 1 : amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null)
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name + "&r"));

            if (lore != null)
                if (!Arrays.asList(lore).isEmpty()) {
                    List<String> metalore = new ArrayList<>();
                    for (String l : lore) {
                        if (l != null) {
                            metalore.add(ChatColor.translateAlternateColorCodes('&', l));
                        }
                    }
                    meta.setLore(metalore);
                }
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * /**
     *
     * @param material     Material of an final ItemStack
     * @param amount       amount of items in ItemStack
     * @param name         Custom name of an ItemStack, supports colors through "&" ColorChar
     * @param enchantments Map of enchantments to be added into item. Map should be <Enchantment,Integer>, where Integer is a level of Enchantment
     * @param lore         Lores of an ItemStack
     * @return final Custom ItemStack
     */
    public static ItemStack getSpecifiedItem(Material material, int amount, @Nullable String name, @Nullable Map<Enchantment, Integer> enchantments, @Nullable String... lore) {
        ItemStack item = new ItemStack(material, (amount <= 0) ? 1 : amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null)
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name + "&r"));
            if (lore != null)
                if (!Arrays.asList(lore).isEmpty()) {
                    List<String> metalore = new ArrayList<>();
                    for (String l : lore) {
                        if (l != null) {
                            metalore.add(ChatColor.translateAlternateColorCodes('&', l + "&r"));
                        }
                    }
                    meta.setLore(metalore);
                }
            if (enchantments != null)
                if (!enchantments.isEmpty())
                    enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
            item.setItemMeta(meta);
        }
        return item;
    }

    @Deprecated
    public static ItemStack getPlayerSkull(String owner) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        if (owner != null) {
            if (item.getItemMeta() != null) {
                SkullMeta meta = (SkullMeta) item.getItemMeta();
                meta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
                meta.setDisplayName(owner);
            }
        }
        return item;
    }

    /**
     * Merges two Lists into Map, where entries in Lists at same position is set as Key/Value into map.<br>
     * If one of the lists is longer, the entries will be lost. (smallest size of list = size of map)
     *
     * @param key   List of keys
     * @param value List of values
     * @return Map<K, V> created from merged lists
     */
    public static <K, V> Map<K, V> mergeLists(List<K> key, List<V> value) {
        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < key.size() && i < value.size(); i++) {
            map.put(key.get(i), value.get(i));
        }
        return map;
    }

}