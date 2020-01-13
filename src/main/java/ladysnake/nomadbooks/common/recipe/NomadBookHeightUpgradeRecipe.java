package ladysnake.nomadbooks.common.recipe;

import com.google.common.collect.Lists;
import ladysnake.nomadbooks.NomadBooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class NomadBookHeightUpgradeRecipe extends SpecialCraftingRecipe {
    public NomadBookHeightUpgradeRecipe(Identifier identifier) {
        super(identifier);
    }

    public boolean matches(CraftingInventory craftingInventory, World world) {
        ItemStack itemStack = ItemStack.EMPTY;
        List<ItemStack> list = Lists.newArrayList();

        for(int i = 0; i < craftingInventory.getInvSize(); ++i) {
            ItemStack itemStack2 = craftingInventory.getInvStack(i);
            if (!itemStack2.isEmpty()) {
                if (itemStack2.getItem().equals(NomadBooks.NOMAD_BOOK)) {
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

        return !itemStack.isEmpty() && !list.isEmpty() && itemStack.getOrCreateSubTag(NomadBooks.MODID).getInt("Height") + list.size() <= itemStack.getOrCreateSubTag(NomadBooks.MODID).getInt("Width");
    }

    public ItemStack craft(CraftingInventory craftingInventory) {
        List<Item> list = Lists.newArrayList();
        ItemStack itemStack = ItemStack.EMPTY;

        for(int i = 0; i < craftingInventory.getInvSize(); ++i) {
            ItemStack itemStack2 = craftingInventory.getInvStack(i);
            if (!itemStack2.isEmpty()) {
                Item item = itemStack2.getItem();
                if (item.equals(NomadBooks.NOMAD_BOOK)) {
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

        if (!itemStack.isEmpty() && !list.isEmpty()) {
            int height = itemStack.getOrCreateSubTag(NomadBooks.MODID).getInt("Height");
            itemStack.getOrCreateSubTag(NomadBooks.MODID).putInt("Height", height + list.size());
            return itemStack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Environment(EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return NomadBooks.UPGRADE_HEIGHT_NOMAD_BOOK;
    }
}
