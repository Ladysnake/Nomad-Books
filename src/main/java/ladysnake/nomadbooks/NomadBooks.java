package ladysnake.nomadbooks;

import ladysnake.nomadbooks.common.item.EncampmentBookItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public class NomadBooks implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, "nomadbooks:encampment_book", new EncampmentBookItem((new Item.Settings()).group(ItemGroup.TOOLS)));
    }
}