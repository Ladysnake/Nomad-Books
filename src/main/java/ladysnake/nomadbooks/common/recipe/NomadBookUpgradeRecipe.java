package ladysnake.nomadbooks.common.recipe;

import ladysnake.nomadbooks.NomadBooks;
import ladysnake.nomadbooks.common.item.BookUpgradeItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class NomadBookUpgradeRecipe extends SpecialCraftingRecipe {
    public NomadBookUpgradeRecipe(Identifier identifier) {
        super(identifier);
    }

    public boolean matches(CraftingInventory craftingInventory, World world) {
        ItemStack book = null;
        String upgrade = null;

        for(int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            if (book == null && itemStack.getItem().equals(NomadBooks.NOMAD_BOOK)) {
                book = itemStack;
            } else if (upgrade == null && itemStack.getItem() instanceof BookUpgradeItem) {
                upgrade = ((BookUpgradeItem) itemStack.getItem()).getUpgrade();
            } else if (!itemStack.equals(ItemStack.EMPTY)) {
                return false;
            }
        }

        return book != null && upgrade != null;
    }

    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack book = null;
        String upgrade = null;

        for(int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            if (book == null && itemStack.getItem().equals(NomadBooks.NOMAD_BOOK)) {
                book = itemStack;
            } else if (upgrade == null && itemStack.getItem() instanceof BookUpgradeItem) {
                upgrade = ((BookUpgradeItem) itemStack.getItem()).getUpgrade();
            } else if (!itemStack.equals(ItemStack.EMPTY)) {
                return ItemStack.EMPTY;
            }
        }

        if (book != null && upgrade != null) {
            ItemStack ret = book.copy();
            ListTag upgradeList = ret.getOrCreateSubTag(NomadBooks.MODID).getList("Upgrades", NbtType.STRING);
            if (!upgradeList.contains(StringTag.of(upgrade))) {
                upgradeList.add(StringTag.of(upgrade));
            }
            ret.getOrCreateSubTag(NomadBooks.MODID).put("Upgrades", upgradeList);

            return ret;
        }

        return ItemStack.EMPTY;
    }

    @Environment(EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return NomadBooks.UPGRADE_NOMAD_BOOK;
    }
}
