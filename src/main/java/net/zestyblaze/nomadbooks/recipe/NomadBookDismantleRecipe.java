package net.zestyblaze.nomadbooks.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.zestyblaze.nomadbooks.NomadBooks;
import net.zestyblaze.nomadbooks.item.NomadBookItem;

public class NomadBookDismantleRecipe extends CustomRecipe {
    public NomadBookDismantleRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack book = null;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.getItem() instanceof NomadBookItem) {
                book = itemStack;
            } else if (!itemStack.isEmpty()) {
                return false;
            }
        }

        return book != null && book.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 1.0f;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        ItemStack book = null;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.getItem() instanceof NomadBookItem) {
                book = itemStack;
            } else if (!itemStack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        if (book != null && book.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 1.0f) {
            int amount = 3 + book.getOrCreateTagElement(NomadBooks.MODID).getInt("Height")-1 + (book.getOrCreateTagElement(NomadBooks.MODID).getInt("Width")-3)/2 + book.getOrCreateTagElement(NomadBooks.MODID).getList("Upgrades", NbtType.STRING).size();
            return new ItemStack(NomadBooks.GRASS_PAGE, amount);
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
        return NomadBooks.DISMANTLE_NOMAD_BOOK;
    }
}
