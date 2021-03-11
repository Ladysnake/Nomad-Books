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
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

public class NomadBookCraftRecipe extends ShapedRecipe {
    public static final List<Item> NOMAD_BOOK_RECIPE_1 = Lists.newArrayList(
            Items.AIR, Items.CAMPFIRE, Items.AIR,
            NomadBooks.GRASS_PAGE, NomadBooks.GRASS_PAGE, NomadBooks.GRASS_PAGE,
            Items.AIR, Items.AIR, Items.AIR
    );
    public static final List<Item> NOMAD_BOOK_RECIPE_2 = Lists.newArrayList(
            Items.AIR, Items.AIR, Items.AIR,
            Items.AIR, Items.CAMPFIRE, Items.AIR,
            NomadBooks.GRASS_PAGE, NomadBooks.GRASS_PAGE, NomadBooks.GRASS_PAGE
    );

    public static ItemStack getCraftResult() {
        ItemStack result = new ItemStack(NomadBooks.NOMAD_BOOK);
        result.getOrCreateSubTag(NomadBooks.MODID).putInt("Height", 1);
        result.getOrCreateSubTag(NomadBooks.MODID).putInt("Width", 3);
        result.getOrCreateSubTag(NomadBooks.MODID).putString("Structure", NomadBookItem.defaultStructurePath);

        return result;
    }

    public NomadBookCraftRecipe(Identifier identifier) {
        super(identifier, "", 3, 2,
                DefaultedList.copyOf(Ingredient.EMPTY,
                        Ingredient.ofItems(Items.AIR), Ingredient.ofItems(Items.CAMPFIRE), Ingredient.ofItems(Items.AIR),
                        Ingredient.ofItems(NomadBooks.GRASS_PAGE), Ingredient.ofItems(NomadBooks.GRASS_PAGE), Ingredient.ofItems(NomadBooks.GRASS_PAGE)
                ), getCraftResult());
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        List<Item> list = Lists.newArrayList();

        for(int i = 0; i < craftingInventory.size(); ++i) {
            list.add(craftingInventory.getStack(i).getItem());
        }

        return list.equals(NOMAD_BOOK_RECIPE_1) || list.equals(NOMAD_BOOK_RECIPE_2);
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        List<Item> list = Lists.newArrayList();

        for(int i = 0; i < craftingInventory.size(); ++i) {
            list.add(craftingInventory.getStack(i).getItem());
        }

        if (list.equals(NOMAD_BOOK_RECIPE_1) || list.equals(NOMAD_BOOK_RECIPE_2)) {
            return getCraftResult();
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Environment(EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPED;
    }
}
