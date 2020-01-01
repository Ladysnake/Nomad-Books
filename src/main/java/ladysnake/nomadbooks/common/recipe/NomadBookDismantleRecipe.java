package ladysnake.nomadbooks.common.recipe;

import com.google.common.collect.Lists;
import ladysnake.nomadbooks.NomadBooks;
import ladysnake.nomadbooks.common.item.GrassPageItem;
import ladysnake.nomadbooks.common.item.NomadBookItem;
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

public class NomadBookDismantleRecipe extends SpecialCraftingRecipe {
    public NomadBookDismantleRecipe(Identifier identifier) {
        super(identifier);
    }

    public boolean matches(CraftingInventory craftingInventory, World world) {
        ItemStack itemStack = ItemStack.EMPTY;

        for(int i = 0; i < craftingInventory.getInvSize(); ++i) {
            ItemStack itemStack2 = craftingInventory.getInvStack(i);
            if (!itemStack2.isEmpty()) {
                if (itemStack.isEmpty() && itemStack2.getItem() instanceof NomadBookItem) {
                    itemStack = itemStack2;
                } else {
                    return false;
                }
            }
        }

        return !itemStack.isEmpty() && itemStack.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 1.0f;
    }

    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack itemStack = ItemStack.EMPTY;

        for(int i = 0; i < craftingInventory.getInvSize(); ++i) {
            ItemStack itemStack2 = craftingInventory.getInvStack(i);
            if (!itemStack2.isEmpty()) {
                if (itemStack.isEmpty() && itemStack2.getItem() instanceof NomadBookItem) {
                    itemStack = itemStack2;
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (!itemStack.isEmpty() && itemStack.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 1.0f) {
            int amount = itemStack.getOrCreateSubTag(NomadBooks.MODID).getInt("Pages");
            return new ItemStack(NomadBooks.GRASS_PAGE, amount);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Environment(EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return NomadBooks.DISMANTLE_NOMAD_BOOK;
    }
}
