package net.zestyblaze.nomadbooks.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.zestyblaze.nomadbooks.NomadBooks;
import net.zestyblaze.nomadbooks.item.BookUpgradeItem;
import net.zestyblaze.nomadbooks.item.NomadBookItem;

public class NomadBookUpgradeRecipe extends CustomRecipe {
    public NomadBookUpgradeRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack book = null;
        String upgrade = null;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if (book == null && itemStack.getItem() instanceof NomadBookItem) {
                book = itemStack;
            } else if (upgrade == null && itemStack.getItem() instanceof BookUpgradeItem) {
                upgrade = ((BookUpgradeItem)itemStack.getItem()).getUpgrade();
            } else if (!itemStack.equals(ItemStack.EMPTY)) {
                return false;
            }
        }

        return book != null && upgrade != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        ItemStack book = null;
        String upgrade = null;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if (book == null && itemStack.getItem() instanceof NomadBookItem) {
                book = itemStack;
            } else if (upgrade == null && itemStack.getItem() instanceof BookUpgradeItem) {
                upgrade = ((BookUpgradeItem)itemStack.getItem()).getUpgrade();
            } else if (!itemStack.equals(ItemStack.EMPTY)) {
                return ItemStack.EMPTY;
            }
        }

        if (book != null && upgrade != null) {
            ItemStack ret = book.copy();
            ListTag upgradeList = ret.getOrCreateTagElement(NomadBooks.MODID).getList("Upgrades", NbtType.STRING);
            if (!upgradeList.contains(StringTag.valueOf(upgrade))) {
                upgradeList.add(StringTag.valueOf(upgrade));
            }
            ret.getOrCreateTagElement(NomadBooks.MODID).put("Upgrades", upgradeList);

            return ret;
        }

        return ItemStack.EMPTY;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NomadBooks.UPGRADE_NOMAD_BOOK;
    }
}
