package com.rabbit13.events.main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

/**
 * A library for the Bukkit API to create player skulls
 * from names, base64 strings, and texture URLs.
 * <p>
 * Does not use any NMS code, and should work across all versions.
 *
 * @author Dean B on 12/28/2016.
 */
public final class SkullCreator {

    /**
     * Creates a player skull based on a player's name.
     *
     * @param name The Player's name
     * @return The head of the Player
     * @deprecated names don't make for good identifiers
     */
    @Deprecated
    public static ItemStack itemFromName(String name) {
        ItemStack item = getPlayerSkullItem();

        return itemWithName(item, name);
    }

    /**
     * Creates a player skull based on a Mojang server URL.
     *
     * @param url The URL of the Mojang skin
     * @return The head associated with the URL
     */
    public static ItemStack itemFromUrl(String url) {
        ItemStack item = getPlayerSkullItem();
        return itemWithUrl(item, url);
    }

    @Deprecated
    private static ItemStack getPlayerSkullItem() {
        if (newerApi()) {
            return new ItemStack(Material.valueOf("PLAYER_HEAD"));
        }
        else {
            return new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (byte) 3);
        }
    }

    /**
     * Creates a player skull based on a player's name.
     *
     * @param item The item to apply the name to
     * @param name The Player's name
     * @return The head of the Player
     * @deprecated names don't make for good identifiers
     */
    @Deprecated
    private static ItemStack itemWithName(ItemStack item, String name) {
        notNull(item, "item");
        notNull(name, "name");
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if(meta != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(name));
        }
        return item;

    }

    /**
     * Creates a player skull based on a Mojang server URL.
     *
     * @param item The item to apply the skin to
     * @param url  The URL of the Mojang skin
     * @return The head associated with the URL
     */
    private static ItemStack itemWithUrl(ItemStack item, String url) {
        notNull(item, "item");
        notNull(url, "url");

        return itemWithBase64(item, urlToBase64(url));
    }


    /**
     * Applies the base64 string to the ItemStack.
     *
     * @param item   The ItemStack to put the base64 onto
     * @param base64 The base64 string containing the texture
     * @return The head with a custom texture
     */
    private static ItemStack itemWithBase64(ItemStack item, String base64) {
        notNull(item, "item");
        notNull(base64, "base64");
        UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        SkullMeta meta = (SkullMeta) Objects.requireNonNull(item.getItemMeta());
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(hashAsId));
        item.setItemMeta(meta);
        return item;
    }

    private static boolean newerApi() {
        try {
            Material.valueOf("PLAYER_HEAD");
            return true;

        } catch (IllegalArgumentException e) { // If PLAYER_HEAD doesn't exist
            return false;
        }
    }

    private static void notNull(Object o, String name) {
        if (o == null) {
            throw new NullPointerException(name + " should not be null!");
        }
    }

    private static String urlToBase64(String url) {

        URI actualUrl;
        try {
            actualUrl = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + actualUrl.toString() + "\"}}}";
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }
}

/* Format for skull
{
   display:{
      Name:"Cheese"
   },
   SkullOwner:{
      Id:"9c919b83-f3fe-456f-a824-7d1d08cc8bd2",
      Properties:{
         textures:[
            {
               Value:"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU1ZDYxMWE4NzhlODIxMjMxNzQ5YjI5NjU3MDhjYWQ5NDI2NTA2NzJkYjA5ZTI2ODQ3YTg4ZTJmYWMyOTQ2In19fQ=="
            }
         ]
      }
   }
}
 */