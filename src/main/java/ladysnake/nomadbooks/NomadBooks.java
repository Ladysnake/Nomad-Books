package ladysnake.nomadbooks;

import ladysnake.nomadbooks.common.item.NomadBookItem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public class NomadBooks implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, "nomadbooks:nomad_book", new NomadBookItem((new Item.Settings()).maxCount(1).group(ItemGroup.TOOLS)));
    }
}