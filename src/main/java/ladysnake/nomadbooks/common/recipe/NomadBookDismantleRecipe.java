package ladysnake.nomadbooks.common.recipe;

import ladysnake.nomadbooks.NomadBooks;
import ladysnake.nomadbooks.common.item.NomadBookItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class NomadBookDismantleRecipe extends SpecialCraftingRecipe {
    public NomadBookDismantleRecipe(Identifier identifier) {
        super(identifier);
    }

    public boolean matches(CraftingInventory craftingInventory, World world) {
        ItemStack book = null;

        for(int i = 0; i < craftingInventory.getInvSize(); ++i) {
            ItemStack itemStack = craftingInventory.getInvStack(i);
            if (itemStack.getItem().equals(NomadBooks.NOMAD_BOOK)) {
                book = itemStack;
            } else if (!itemStack.isEmpty()) {
                return false;
            }
        }

        return book != null && book.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 1.0f;
    }

    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack book = null;

        for(int i = 0; i < craftingInventory.getInvSize(); ++i) {
            ItemStack itemStack = craftingInventory.getInvStack(i);
            if (itemStack.getItem().equals(NomadBooks.NOMAD_BOOK)) {
                book = itemStack;
            } else if (!itemStack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        if (book != null && book.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 1.0f) {
        int amount = book.getOrCreateSubTag(NomadBooks.MODID).getInt("Height") + (book.getOrCreateSubTag(NomadBooks.MODID).getInt("Width")-7)/2 + book.getOrCreateSubTag(NomadBooks.MODID).getList("Upgrades", NbtType.STRING).size();
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
