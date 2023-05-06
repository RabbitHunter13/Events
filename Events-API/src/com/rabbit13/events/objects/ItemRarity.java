package com.rabbit13.events.objects;

import lombok.Getter;

public enum ItemRarity {
    COMMON("&8Common", 0),
    UNCOMMON("&aUncommon", 7),
    RARE("&bRare", 14),
    LEGENDARY("&e&lLegendary", 18),
    MYTHIC("&f&lMythic", 20),
    RANDOM;

    @Getter private final String rarityString;
    @Getter private final int rarityDOWNNumber;

    ItemRarity() {
        rarityString = "Random";
        rarityDOWNNumber = 0;
    }

    ItemRarity(String rarityString, int rarityDOWNNumber) {
        this.rarityString = rarityString;
        this.rarityDOWNNumber = rarityDOWNNumber;
    }

    @Override public String toString() {
        return super.toString().toLowerCase();
    }

    public static ItemRarity getItemRarityByNumber(int rarityNumber) {
        if (rarityNumber >= ItemRarity.MYTHIC.rarityDOWNNumber) {
            return ItemRarity.MYTHIC;
        }
        else if (rarityNumber >= ItemRarity.LEGENDARY.rarityDOWNNumber) {
            return ItemRarity.LEGENDARY;
        }
        else if (rarityNumber >= ItemRarity.RARE.rarityDOWNNumber) {
            return ItemRarity.RARE;
        }
        else if (rarityNumber >= ItemRarity.UNCOMMON.rarityDOWNNumber) {
            return ItemRarity.UNCOMMON;
        }
        else {
            return ItemRarity.COMMON;
        }
    }

}
