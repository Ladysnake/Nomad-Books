package ladysnake.nomadbooks.common.item;

import net.minecraft.item.Item;

public class BookUpgradeItem extends Item {
    private String upgrade;

    public BookUpgradeItem(Settings settings, String upgrade) {
        super(settings);
        this.upgrade = upgrade;
    }

    public String getUpgrade() {
        return upgrade;
    }
}
