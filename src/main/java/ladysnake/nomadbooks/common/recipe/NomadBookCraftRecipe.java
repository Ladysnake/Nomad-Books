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
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class NomadBookCraftRecipe extends ShapedRecipe {
    public static final List<Item> NOMAD_BOOK_RECIPE = Lists.newArrayList(
            Items.LIME_DYE, Items.ORANGE_DYE, Items.LIME_DYE,
            NomadBooks.GRASS_PAGE, NomadBooks.GRASS_PAGE, NomadBooks.GRASS_PAGE,
            Items.GRAY_DYE, Items.BROWN_DYE, Items.GRAY_DYE
    );
    public static final ItemStack CRAFT_RESULT = new ItemStack(NomadBooks.NOMAD_BOOK);

    public static void initCraftResult() {
        CRAFT_RESULT.getOrCreateSubTag(NomadBooks.MODID).putInt("Height", 3);
        CRAFT_RESULT.getOrCreateSubTag(NomadBooks.MODID).putString("Structure", NomadBookItem.defaultStructurePath);
    }


    public NomadBookCraftRecipe(Identifier identifier) {
        super(identifier, "", 3, 3,
                DefaultedList.copyOf(Ingredient.EMPTY,
                        Ingredient.ofItems(Items.LIME_DYE), Ingredient.ofItems(Items.ORANGE_DYE), Ingredient.ofItems(Items.LIME_DYE),
                        Ingredient.ofItems(NomadBooks.GRASS_PAGE), Ingredient.ofItems(NomadBooks.GRASS_PAGE), Ingredient.ofItems(NomadBooks.GRASS_PAGE),
                        Ingredient.ofItems(Items.GRAY_DYE), Ingredient.ofItems(Items.BROWN_DYE), Ingredient.ofItems(Items.GRAY_DYE)
                ), CRAFT_RESULT);
    }

    public boolean matches(CraftingInventory craftingInventory, World world) {
        List<Item> list = Lists.newArrayList();

        for(int i = 0; i < craftingInventory.getInvSize(); ++i) {
             list.add(craftingInventory.getInvStack(i).getItem());
        }

        return list.equals(NOMAD_BOOK_RECIPE);
    }

    public ItemStack craft(CraftingInventory craftingInventory) {
        List<Item> list = Lists.newArrayList();

        for(int i = 0; i < craftingInventory.getInvSize(); ++i) {
            list.add(craftingInventory.getInvStack(i).getItem());
        }

        if (list.equals(NOMAD_BOOK_RECIPE)) {
            return CRAFT_RESULT;
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
