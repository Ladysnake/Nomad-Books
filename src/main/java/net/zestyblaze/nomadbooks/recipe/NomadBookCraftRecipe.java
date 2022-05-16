package net.zestyblaze.nomadbooks.recipe;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.zestyblaze.nomadbooks.NomadBooks;
import net.zestyblaze.nomadbooks.item.NomadBookItem;

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
        result.getOrCreateTagElement(NomadBooks.MODID).putInt("Height", 1);
        result.getOrCreateTagElement(NomadBooks.MODID).putInt("Width", 3);
        result.getOrCreateTagElement(NomadBooks.MODID).putString("Structure", NomadBookItem.defaultStructurePath);

        return result;
    }

    public NomadBookCraftRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation, "", 3, 2, NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.AIR), Ingredient.of(Items.CAMPFIRE), Ingredient.of(Items.AIR), Ingredient.of(NomadBooks.GRASS_PAGE), Ingredient.of(NomadBooks.GRASS_PAGE), Ingredient.of(NomadBooks.GRASS_PAGE)), getCraftResult());
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        List<Item> list = Lists.newArrayList();

        for(int i = 0; i < container.getContainerSize(); ++i) {
            list.add(container.getItem(i).getItem());
        }

        return list.equals(NOMAD_BOOK_RECIPE_1) || list.equals(NOMAD_BOOK_RECIPE_2);
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        List<Item> list = Lists.newArrayList();

        for(int i = 0; i < container.getContainerSize(); ++i) {
            list.add(container.getItem(i).getItem());
        }

        if (list.equals(NOMAD_BOOK_RECIPE_1) || list.equals(NOMAD_BOOK_RECIPE_2)) {
            return getCraftResult();
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
        return RecipeSerializer.SHAPED_RECIPE;
    }
}
