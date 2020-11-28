package ladysnake.nomadbooks.common.recipe;

import com.google.common.collect.Lists;
import ladysnake.nomadbooks.NomadBooks;
import ladysnake.nomadbooks.common.item.NomadBookItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class NomadBookInkRecipe extends SpecialCraftingRecipe {
    public NomadBookInkRecipe(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        List<Item> ingredients = Lists.newArrayList();
        ItemStack book = null;

        for(int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
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
    public ItemStack craft(CraftingInventory craftingInventory) {
        List<Item> ingredients = Lists.newArrayList();
        ItemStack book = null;

        for(int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            Item item = itemStack.getItem();
            if (item instanceof NomadBookItem) {
                book = itemStack;
            } else if (item.equals(Items.GHAST_TEAR) || item.equals(Items.CHARCOAL) || item.equals(Items.BLUE_DYE)) {
                ingredients.add(item);
            }
        }

        if (book != null && ingredients.size() == 3 && ingredients.contains(Items.GHAST_TEAR) && ingredients.contains(Items.CHARCOAL) && ingredients.contains(Items.BLUE_DYE)) {
            ItemStack ret = book.copy();
            int width = ret.getOrCreateSubTag(NomadBooks.MODID).getInt("Width");
            ret.getOrCreateSubTag(NomadBooks.MODID).putBoolean("Inked", true);
            ret.getOrCreateSubTag(NomadBooks.MODID).putInt("InkGoal", ((width+2)*(width+2) - width*width)/2);
            ret.getOrCreateSubTag(NomadBooks.MODID).putInt("InkProgress", 0);

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
