package net.zestyblaze.nomadbooks.item;

import net.minecraft.world.item.Item;

public class BookUpgradeItem extends Item {
    private String upgrade;

    public BookUpgradeItem(Properties properties, String upgrade) {
        super(properties);
        this.upgrade = upgrade;
    }

    public String getUpgrade() {
        return upgrade;
    }
}
