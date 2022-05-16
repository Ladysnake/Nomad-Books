package net.zestyblaze.nomadbooks.recipe;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.zestyblaze.nomadbooks.NomadBooks;
import net.zestyblaze.nomadbooks.item.NomadBookItem;

import java.util.List;

public class NomadBookInkRecipe extends CustomRecipe {
    public NomadBookInkRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        List<Item> ingredients = Lists.newArrayList();
        ItemStack book = null;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            Item item = itemStack.getItem();
            if (item instanceof NomadBookItem) {
                book = itemStack;
            } else if (item.equals(Items.GHAST_TEAR) || item.equals(Items.CHARCOAL) || item.equals(Items.BLUE_DYE)) {
                ingredients.add(item);
            }
        }

        return book != null && ingredients.size() == 3 && ingredients.contains(Items.GHAST_TEAR) && ingredients.contains(Items.CHARCOAL) && ingredients.contains(Items.BLUE_DYE);
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        List<Item> ingredients = Lists.newArrayList();
        ItemStack book = null;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            Item item = itemStack.getItem();
            if (item instanceof NomadBookItem) {
                book = itemStack;
            } else if (item.equals(Items.GHAST_TEAR) || item.equals(Items.CHARCOAL) || item.equals(Items.BLUE_DYE)) {
                ingredients.add(item);
            }
        }

        if (book != null && ingredients.size() == 3 && ingredients.contains(Items.GHAST_TEAR) && ingredients.contains(Items.CHARCOAL) && ingredients.contains(Items.BLUE_DYE)) {
            ItemStack ret = book.copy();
            int width = ret.getOrCreateTagElement(NomadBooks.MODID).getInt("Width");
            ret.getOrCreateTagElement(NomadBooks.MODID).putBoolean("Inked", true);
            ret.getOrCreateTagElement(NomadBooks.MODID).putInt("InkGoal", ((width+2)*(width+2) - width*width)/2);
            ret.getOrCreateTagElement(NomadBooks.MODID).putInt("InkProgress", 0);

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
