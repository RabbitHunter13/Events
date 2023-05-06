package com.rabbit13.events.objects;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.rabbit13.events.main.Misc;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public final class RabItemGenerator {
    private static final Random random = new Random();
    private static final DecimalFormat df = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    /**
     * Generator of new Modified Event items
     *
     * @param rarity          you can specify a rarity of an item, or just use ItemRarity.RANDOM for random rarity
     * @param willBeEnchanted if the new item should be enchanted by this generator or not
     * @return full custom new item
     */
    public static ItemStack generateItem(ItemRarity rarity, boolean willBeEnchanted) {
        //Variables
        Material material = null;
        boolean isWeapon = random.nextBoolean();
        int slotNumber = random.nextInt(4);
        int materialGen = random.nextInt(3);

        //Random Material
        double damage = 0;
        double attackSpeed = 0;
        double armor = 0;
        double armorToughness = 0;
        double knockbackResist = 0;
        EquipmentSlot slot = EquipmentSlot.HAND;
        if (isWeapon) {
            if (slotNumber < 3) {
                switch (materialGen) {
                    case 2:
                        material = Material.NETHERITE_SWORD;
                        damage = 8;
                        attackSpeed = -2.4;
                        slot = EquipmentSlot.HAND;
                        break;
                    case 1:
                        material = Material.DIAMOND_SWORD;
                        damage = 7;
                        attackSpeed = -2.4;
                        slot = EquipmentSlot.HAND;
                        break;
                    case 0:
                        material = Material.IRON_SWORD;
                        damage = 6;
                        attackSpeed = -2.4;
                        slot = EquipmentSlot.HAND;
                        break;
                }
            }
            else {
                switch (materialGen) {
                    case 2:
                        material = Material.NETHERITE_AXE;
                        damage = 9;
                        attackSpeed = -3;
                        slot = EquipmentSlot.HAND;
                        break;
                    case 1:
                        material = Material.DIAMOND_AXE;
                        damage = 9;
                        attackSpeed = -3;
                        slot = EquipmentSlot.HAND;
                        break;
                    case 0:
                        material = Material.IRON_AXE;
                        damage = 10;
                        attackSpeed = -3;
                        slot = EquipmentSlot.HAND;
                        break;
                }
            }
        }
        else {
            switch (slotNumber) {
                case 0:
                    switch (materialGen) {
                        case 2:
                            material = Material.NETHERITE_HELMET;
                            armor = 3;
                            armorToughness = 3;
                            knockbackResist = 1;
                            slot = EquipmentSlot.HEAD;
                            break;
                        case 1:
                            material = Material.DIAMOND_HELMET;
                            armor = 3;
                            armorToughness = 2;
                            slot = EquipmentSlot.HEAD;
                            break;
                        case 0:
                            material = Material.IRON_HELMET;
                            armor = 2;
                            slot = EquipmentSlot.HEAD;
                            break;
                    }
                    break;
                case 1:
                    switch (materialGen) {
                        case 2:
                            material = Material.NETHERITE_CHESTPLATE;
                            armor = 8;
                            armorToughness = 3;
                            knockbackResist = 1;
                            slot = EquipmentSlot.CHEST;
                            break;
                        case 1:
                            material = Material.DIAMOND_CHESTPLATE;
                            armor = 8;
                            armorToughness = 2;
                            slot = EquipmentSlot.CHEST;
                            break;
                        case 0:
                            material = Material.IRON_CHESTPLATE;
                            armor = 6;
                            slot = EquipmentSlot.CHEST;
                            break;
                    }
                    break;
                case 2:
                    switch (materialGen) {
                        case 2:
                            material = Material.NETHERITE_LEGGINGS;
                            armor = 6;
                            armorToughness = 3;
                            knockbackResist = 1;
                            slot = EquipmentSlot.LEGS;
                            break;
                        case 1:
                            material = Material.DIAMOND_LEGGINGS;
                            armor = 6;
                            armorToughness = 2;
                            slot = EquipmentSlot.LEGS;
                            break;
                        case 0:
                            material = Material.IRON_LEGGINGS;
                            armor = 5;
                            slot = EquipmentSlot.LEGS;
                            break;
                    }
                    break;
                case 3:
                    switch (materialGen) {
                        case 2:
                            material = Material.NETHERITE_BOOTS;
                            armor = 3;
                            armorToughness = 3;
                            knockbackResist = 1;
                            slot = EquipmentSlot.FEET;
                            break;
                        case 1:
                            material = Material.DIAMOND_BOOTS;
                            armor = 3;
                            armorToughness = 2;
                            slot = EquipmentSlot.FEET;
                            break;
                        case 0:
                            material = Material.IRON_BOOTS;
                            armor = 2;
                            slot = EquipmentSlot.FEET;
                            break;
                    }
                    break;
            }
        }
        Misc.debugMessage("Generated material: " + material.name());

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        //<editor-fold desc="Random Boosts">
        //percentual
        double attackSpeedBoost = Math.random() * 15;
        double luckBoost = Math.random() * 17;
        double movementSpeedBoost = Math.random() * 20;
        double knockBackResistBoost = Math.random() * 1.2;
        //normal
        double damageBoost = Math.random() * 4;
        double maxHealthBoost = Math.random() * 1.5;
        double armorBoost = Math.random() * 1;
        double armorToughnessBoost = Math.random() * 0.5;
        knockbackResist = knockbackResist / 10;
        //</editor-fold>
        //<editor-fold desc="Rarity Generator">
        if (rarity == ItemRarity.RANDOM) {
            int rarityGen = random.nextInt(21);
            rarity = ItemRarity.getItemRarityByNumber(rarityGen);
        }
        int moreAtt;
        switch (rarity) {
            case MYTHIC:
                moreAtt = random.nextInt(4);
                switch (moreAtt) {
                    case 0:
                        modifiers.put(Attribute.GENERIC_MAX_HEALTH, createModifier("generic.max_health", slot, maxHealthBoost, AttributeModifier.Operation.ADD_NUMBER));
                        break;
                    case 1:
                        modifiers.put(Attribute.GENERIC_MOVEMENT_SPEED, createModifier("generic.movement_speed", slot, movementSpeedBoost / 120, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                        modifiers.put(Attribute.GENERIC_LUCK, createModifier("generic.luck", slot, luckBoost / 100, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                        break;
                    case 2:
                        modifiers.put(Attribute.GENERIC_MOVEMENT_SPEED, createModifier("generic.movement_speed", slot, movementSpeedBoost / 120, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                        modifiers.put(Attribute.GENERIC_MAX_HEALTH, createModifier("generic.max_health", slot, maxHealthBoost, AttributeModifier.Operation.ADD_NUMBER));
                        break;
                    case 3:
                        modifiers.put(Attribute.GENERIC_MAX_HEALTH, createModifier("generic.max_health", slot, maxHealthBoost, AttributeModifier.Operation.ADD_NUMBER));
                        modifiers.put(Attribute.GENERIC_MOVEMENT_SPEED, createModifier("generic.movement_speed", slot, movementSpeedBoost / 120, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                        modifiers.put(Attribute.GENERIC_LUCK, createModifier("generic.jump_strength", slot, luckBoost / 100, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                        break;
                }
                if (isWeapon) {
                    modifiers.put(Attribute.GENERIC_ATTACK_SPEED, createModifier("generic.attack_speed", slot, (attackSpeed + attackSpeedBoost / 100), AttributeModifier.Operation.ADD_NUMBER)); //basic AS
                    modifiers.put(Attribute.GENERIC_ATTACK_DAMAGE, createModifier("generic.attack_damage", slot, (damage + damageBoost / 100), AttributeModifier.Operation.ADD_NUMBER)); //basic AD
                }
                else {
                    modifiers.put(Attribute.GENERIC_ARMOR_TOUGHNESS, createModifier("generic.armor_toughness", slot, armorToughness + armorToughnessBoost / 100, AttributeModifier.Operation.ADD_NUMBER));
                    modifiers.put(Attribute.GENERIC_ARMOR, createModifier("generic.armor", slot, armor + armorBoost, AttributeModifier.Operation.ADD_NUMBER));
                    modifiers.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, createModifier("generic.knockback_resistance", slot, knockbackResist + knockBackResistBoost / 100, AttributeModifier.Operation.ADD_NUMBER));
                }
                break;
            case LEGENDARY:
                moreAtt = random.nextInt(3);
                switch (moreAtt) {
                    case 0:
                        modifiers.put(Attribute.GENERIC_MOVEMENT_SPEED, createModifier("generic.movement_speed", slot, movementSpeedBoost / 150, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                        break;
                    case 1:
                        modifiers.put(Attribute.GENERIC_MAX_HEALTH, createModifier("generic.max_health", slot, maxHealthBoost, AttributeModifier.Operation.ADD_NUMBER));
                        break;
                    case 2:
                        modifiers.put(Attribute.GENERIC_MOVEMENT_SPEED, createModifier("generic.movement_speed", slot, movementSpeedBoost / 150, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                        modifiers.put(Attribute.GENERIC_LUCK, createModifier("generic.jump_strength", slot, luckBoost / 100, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                        break;
                }
                if (isWeapon) {
                    modifiers.put(Attribute.GENERIC_ATTACK_SPEED, createModifier("generic.attack_speed", slot, attackSpeed + attackSpeedBoost / 100, AttributeModifier.Operation.ADD_NUMBER)); //basic AS
                    modifiers.put(Attribute.GENERIC_ATTACK_DAMAGE, createModifier("generic.attack_damage", slot, damage + damageBoost / 100, AttributeModifier.Operation.ADD_NUMBER)); //basic AD
                }
                else {
                    modifiers.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, createModifier("generic.knockback_resistance", slot, knockbackResist, AttributeModifier.Operation.ADD_NUMBER));
                    modifiers.put(Attribute.GENERIC_ARMOR_TOUGHNESS, createModifier("generic.armor_toughness", slot, (armorToughness + armorToughnessBoost / 100), AttributeModifier.Operation.ADD_NUMBER));
                    modifiers.put(Attribute.GENERIC_ARMOR, createModifier("generic.armor", slot, armor + armorBoost, AttributeModifier.Operation.ADD_NUMBER));
                }
                break;
            case RARE:
                moreAtt = random.nextInt(2);
                switch (moreAtt) {
                    case 0:
                        modifiers.put(Attribute.GENERIC_MOVEMENT_SPEED, createModifier("generic.movement_speed", slot, movementSpeedBoost / 200, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                        break;
                    case 1:
                        modifiers.put(Attribute.GENERIC_MAX_HEALTH, createModifier("generic.max_health", slot, maxHealthBoost, AttributeModifier.Operation.ADD_NUMBER));
                        break;
                }
                if (isWeapon) {
                    modifiers.put(Attribute.GENERIC_ATTACK_SPEED, createModifier("generic.attack_speed", slot, attackSpeed + attackSpeedBoost / 100, AttributeModifier.Operation.ADD_NUMBER)); //basic AS
                    modifiers.put(Attribute.GENERIC_ATTACK_DAMAGE, createModifier("generic.attack_damage", slot, damage + damageBoost / 100, AttributeModifier.Operation.ADD_NUMBER)); //basic AD
                }
                else {
                    modifiers.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, createModifier("generic.knockback_resistance", slot, knockbackResist, AttributeModifier.Operation.ADD_NUMBER));
                    modifiers.put(Attribute.GENERIC_ARMOR_TOUGHNESS, createModifier("generic.armor_toughness", slot, armorToughness + armorToughnessBoost / 150, AttributeModifier.Operation.ADD_NUMBER));
                    modifiers.put(Attribute.GENERIC_ARMOR, createModifier("generic.armor", slot, armor + armorBoost, AttributeModifier.Operation.ADD_NUMBER));
                }
                break;
            case UNCOMMON:
                moreAtt = random.nextInt(2);
                if (moreAtt == 1) {
                    modifiers.put(Attribute.GENERIC_MOVEMENT_SPEED, createModifier("generic.movement_speed", slot, movementSpeedBoost / 600, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
                }
                if (isWeapon) {
                    modifiers.put(Attribute.GENERIC_ATTACK_SPEED, createModifier("generic.attack_speed", slot, attackSpeed, AttributeModifier.Operation.ADD_NUMBER)); //basic AS
                    modifiers.put(Attribute.GENERIC_ATTACK_DAMAGE, createModifier("generic.attack_damage", slot, damage + damageBoost / 200, AttributeModifier.Operation.ADD_NUMBER)); //basic AD
                }
                else {
                    modifiers.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, createModifier("generic.knockback_resistance", slot, knockbackResist, AttributeModifier.Operation.ADD_NUMBER));
                    modifiers.put(Attribute.GENERIC_ARMOR_TOUGHNESS, createModifier("generic.armor_toughness", slot, armorToughness, AttributeModifier.Operation.ADD_NUMBER));
                    modifiers.put(Attribute.GENERIC_ARMOR, createModifier("generic.armor", slot, armor + armorBoost, AttributeModifier.Operation.ADD_NUMBER));
                }
                break;
            case COMMON:
                if (isWeapon) {
                    modifiers.put(Attribute.GENERIC_ATTACK_SPEED, createModifier("generic.attack_speed", slot, attackSpeed + attackSpeedBoost / 100, AttributeModifier.Operation.ADD_NUMBER)); //basic AS
                    modifiers.put(Attribute.GENERIC_ATTACK_DAMAGE, createModifier("generic.attack_damage", slot, damage, AttributeModifier.Operation.ADD_NUMBER)); //basic AD
                }
                else {
                    modifiers.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, createModifier("generic.knockback_resistance", slot, knockbackResist, AttributeModifier.Operation.ADD_NUMBER));
                    modifiers.put(Attribute.GENERIC_ARMOR_TOUGHNESS, createModifier("generic.armor_toughness", slot, armorToughness, AttributeModifier.Operation.ADD_NUMBER));
                    modifiers.put(Attribute.GENERIC_ARMOR, createModifier("generic.armor", slot, armor + armorBoost / 8, AttributeModifier.Operation.ADD_NUMBER));
                }
                break;
        }

        meta.setAttributeModifiers(modifiers);
        item.setItemMeta(meta);
        addLore(item, Misc.textIntoColor(rarity.getRarityString()), slot);
        if (willBeEnchanted) {
            addEnchant(item, rarity, isWeapon);
        }
        //</editor-fold>
        return item;
    }

    /**
     * Rarity, that is lore in game, will be added to item @item.
     *
     * @param item   newly created item that is getting rarity (lore)
     * @param rarity text of what rarity new item will be
     */
    private static void addLore(ItemStack item, String rarity, EquipmentSlot slot) {
        ItemMeta meta = item.getItemMeta();
        List<String> loreArray = new ArrayList<>();
        loreArray.add(Misc.textIntoColor("&eRarity: ") + rarity);
        loreArray.add(" ");

        if (meta != null) {
            if (meta.hasAttributeModifiers()) {
                switch (slot) {
                    case HAND:
                        loreArray.add(Misc.textIntoColor("&7When in Main Hand:"));
                        break;
                    case HEAD:
                        loreArray.add(Misc.textIntoColor("&7When on Head:"));
                        break;
                    case CHEST:
                        loreArray.add(Misc.textIntoColor("&7When on Body:"));
                        break;
                    case LEGS:
                        loreArray.add(Misc.textIntoColor("&7When on Legs:"));
                        break;
                    case FEET:
                        loreArray.add(Misc.textIntoColor("&7When on Feet:"));
                        break;
                }
                //for every Attribute entry
                for (Map.Entry<Attribute, Collection<AttributeModifier>> attributeEntry : Objects.requireNonNull(meta.getAttributeModifiers()).asMap().entrySet()) {
                    //for every Attribute modifier connected to current Attribute
                    for (AttributeModifier attributeModifier : attributeEntry.getValue()) {
                        String[] namesSplitted = attributeEntry.getKey().toString()
                                .replace("GENERIC_", "")
                                .replace("_", " ")
                                .toLowerCase().split(" ");
                        //<editor-fold desc="First Char Upper Builder">
                        StringBuilder name = new StringBuilder();
                        for (int i = 0, namesSplittedLength = namesSplitted.length; i < namesSplittedLength; i++) {
                            String nameSplitted = namesSplitted[i];
                            Misc.debugMessage(Arrays.toString(namesSplitted));
                            Misc.debugMessage("Name Splitted: " + nameSplitted);
                            String upperFirst = Character.toString(nameSplitted.charAt(0)).toUpperCase();
                            name.append(upperFirst).append(nameSplitted.substring(1));

                            if (i + 1 < namesSplittedLength) {
                                name.append(" ");
                            }
                        }
                        //</editor-fold>
                        if (attributeEntry.getKey() == Attribute.GENERIC_ATTACK_SPEED) {
                            loreArray.add(Misc.textIntoColor("&9" + df.format(4 + attributeModifier.getAmount())
                                                                     + " "
                                                                     + name.toString()));
                        }
                        else if (attributeEntry.getKey() == Attribute.GENERIC_KNOCKBACK_RESISTANCE) {
                            loreArray.add(Misc.textIntoColor("&9+" + df.format(attributeModifier.getAmount() * 10)
                                                                     + " "
                                                                     + name.toString()));
                        }
                        else {
                            if (attributeModifier.getAmount() >= 0.01) {
                                loreArray.add(Misc.textIntoColor("&9+" + df.format(attributeModifier.getOperation() != AttributeModifier.Operation.ADD_NUMBER ? attributeModifier.getAmount() * 100 : attributeModifier.getAmount())
                                                                         + (attributeModifier.getOperation() != AttributeModifier.Operation.ADD_NUMBER ? "%" : "") + " "
                                                                         + name.toString()));
                            }
                        }
                    }
                }
            }
            meta.setDisplayName(Misc.textIntoColor("&4&lEvent " + item.getType().toString()
                    .replace("_", " ").toLowerCase()));
            meta.setLore(loreArray);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        else {
            Misc.error("Error while creating item " + item.getType().toString()
                    .replace("_", " ")
                    .toLowerCase() + ", ItemMeta was not found!");
            throw new IllegalStateException();
        }
    }

    /**
     * Adds an enchants to newly generating item
     *
     * @param item     item that is being generated
     * @param rarity   rarity of an item
     * @param isWeapon if item is held in hand
     */
    private static void addEnchant(ItemStack item, ItemRarity rarity, boolean isWeapon) {
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        int randomLvl = random.nextInt(3);

        switch (rarity) {
            case MYTHIC: {
                int randomEnch = random.nextInt(4);
                if (isWeapon) {
                    switch (randomEnch) {
                        case 0:
                            enchantments.put(Enchantment.DAMAGE_ALL, randomLvl == 0 ? 1 : randomLvl);
                            if (randomLvl >= 1)
                                enchantments.put(Enchantment.KNOCKBACK, 1);
                            enchantments.put(Enchantment.FIRE_ASPECT, 1);
                            randomLvl = random.nextInt(3);
                            enchantments.put(Enchantment.LOOT_BONUS_MOBS, randomLvl == 0 ? 1 : randomLvl);
                            break;
                        case 1:
                            enchantments.put(Enchantment.DAMAGE_ALL, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            if (randomLvl == 2)
                                enchantments.put(Enchantment.MENDING, 1);
                        case 2:
                            enchantments.put(Enchantment.DAMAGE_ALL, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                        case 3:
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            if (randomLvl == 2)
                                enchantments.put(Enchantment.MENDING, 1);
                    }
                }
                else {
                    switch (randomEnch) {
                        case 0:
                            enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, randomLvl == 0 ? 1 : randomLvl);
                            if (randomLvl >= 1)
                                enchantments.put(Enchantment.THORNS, 1);
                            randomLvl = random.nextInt(3);
                            enchantments.put(Enchantment.PROTECTION_PROJECTILE, randomLvl == 0 ? 1 : randomLvl);
                            break;
                        case 1:
                            enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            if (randomLvl == 2)
                                enchantments.put(Enchantment.MENDING, 1);
                        case 2:
                            enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                        case 3:
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            if (randomLvl == 2)
                                enchantments.put(Enchantment.MENDING, 1);
                    }
                }
                break;
            }
            case LEGENDARY: {
                int randomEnch = random.nextInt(3);
                if (isWeapon) {
                    switch (randomEnch) {
                        case 0:
                            enchantments.put(Enchantment.DAMAGE_ALL, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            if (randomLvl == 2)
                                enchantments.put(Enchantment.MENDING, 1);
                        case 1:
                            enchantments.put(Enchantment.FIRE_ASPECT, 1);
                            randomLvl = random.nextInt(3);
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                        case 2:
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            if (randomLvl == 2)
                                enchantments.put(Enchantment.MENDING, 1);
                    }
                }
                else {
                    switch (randomEnch) {
                        case 0:
                            enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            if (randomLvl == 2)
                                enchantments.put(Enchantment.MENDING, 1);
                        case 1:
                            enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                        case 2:
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            if (randomLvl == 2)
                                enchantments.put(Enchantment.MENDING, 1);
                    }
                }
                break;
            }
            case RARE: {
                int randomEnch = random.nextInt(2);
                if (isWeapon) {
                    switch (randomEnch) {
                        case 0:
                            enchantments.put(Enchantment.DAMAGE_ALL, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                        case 1:
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            if (randomLvl == 2)
                                enchantments.put(Enchantment.MENDING, 1);
                    }
                }
                else {
                    switch (randomEnch) {
                        case 0:
                            enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                        case 1:
                            enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                            randomLvl = random.nextInt(3);
                            if (randomLvl == 2)
                                enchantments.put(Enchantment.MENDING, 1);
                    }
                }
                break;
            }
            case UNCOMMON: {
                boolean randomEnch = random.nextBoolean();
                if (isWeapon) {
                    if (randomEnch) {
                        enchantments.put(Enchantment.DAMAGE_ALL, randomLvl == 0 ? 1 : randomLvl);
                    }
                    else {
                        enchantments.put(Enchantment.DURABILITY, 1);
                    }
                }
                else {
                    if (randomEnch) {
                        enchantments.put(Enchantment.DURABILITY, randomLvl == 0 ? 1 : randomLvl);
                    }
                    else {
                        enchantments.put(Enchantment.DURABILITY, 1);
                    }
                }
                break;
            }
            case COMMON: {
                enchantments.put(Enchantment.DURABILITY, 1);
                break;
            }
        }
        Misc.debugMessage("Is weapon: " + isWeapon);
        Misc.debugMessage("Enchantments: ");
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            Misc.debugMessage(enchantment.getKey().toString());
        }
        item.addUnsafeEnchantments(enchantments);
    }

    /**
     * Creates a Nbt Modifier
     */
    private static AttributeModifier createModifier(String genericAttribute,
                                                    EquipmentSlot slot,
                                                    double amount,
                                                    AttributeModifier.Operation operation) {
        return new AttributeModifier(UUID.randomUUID(), genericAttribute, amount, operation, slot);
    }
}
