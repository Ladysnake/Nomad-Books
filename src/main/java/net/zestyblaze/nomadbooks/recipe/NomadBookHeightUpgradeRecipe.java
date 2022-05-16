package net.zestyblaze.nomadbooks.recipe;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.zestyblaze.nomadbooks.NomadBooks;
import net.zestyblaze.nomadbooks.item.NomadBookItem;

import java.util.List;

public class NomadBookHeightUpgradeRecipe extends CustomRecipe {
    public NomadBookHeightUpgradeRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack itemStack = ItemStack.EMPTY;
        List<ItemStack> list = Lists.newArrayList();

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack2 = container.getItem(i);
            if (!itemStack2.isEmpty()) {
                if (itemStack2.getItem() instanceof NomadBookItem) {
                    if (!itemStack.isEmpty()) {
                        return false;
                    }

                    itemStack = itemStack2;
                } else {
                    if (!(itemStack2.getItem().equals(NomadBooks.GRASS_PAGE))) {
                        return false;
                    }

                    list.add(itemStack2);
                }
            }
        }

        return !itemStack.isEmpty() && !list.isEmpty() && itemStack.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 0.0f;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        List<Item> list = Lists.newArrayList();
        ItemStack itemStack = ItemStack.EMPTY;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack2 = container.getItem(i);
            if (!itemStack2.isEmpty()) {
                Item item = itemStack2.getItem();
                if (item instanceof NomadBookItem) {
                    if (!itemStack.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    itemStack = itemStack2.copy();
                } else {
                    if (!(item.equals(NomadBooks.GRASS_PAGE))) {
                        return ItemStack.EMPTY;
                    }

                    list.add(item);
                }
            }
        }
        if (!itemStack.isEmpty() && !list.isEmpty() && itemStack.getOrCreateTag().getFloat(NomadBooks.MODID + ":deployed") == 0.0f) {
            int height = itemStack.getOrCreateTagElement(NomadBooks.MODID).getInt("Height");
            itemStack.getOrCreateTagElement(NomadBooks.MODID).putInt("Height", height + list.size());
            return itemStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NomadBooks.UPGRADE_HEIGHT_NOMAD_BOOK;
    }
}
