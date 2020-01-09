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

public class NomadPageCraftRecipe extends SpecialCraftingRecipe {
    public static final List<Item> NOMAD_PAGE_RECIPE_1 = Lists.newArrayList(
            Items.LIME_DYE, Items.ORANGE_DYE, Items.LIME_DYE,
            Items.AIR, NomadBooks.GRASS_PAGE, Items.AIR,
            Items.AIR, Items.AIR, Items.AIR
    );
    public static final List<Item> NOMAD_PAGE_RECIPE_2 = Lists.newArrayList(
            Items.AIR, Items.AIR, Items.AIR,
            Items.LIME_DYE, Items.ORANGE_DYE, Items.LIME_DYE,
            Items.AIR, NomadBooks.GRASS_PAGE, Items.AIR
    );

    public NomadPageCraftRecipe(Identifier identifier) {
        super(identifier);
    }

    public boolean matches(CraftingInventory craftingInventory, World world) {
        List<Item> list = Lists.newArrayList();

        for(int i = 0; i < craftingInventory.getInvSize(); ++i) {
             list.add(craftingInventory.getInvStack(i).getItem());
        }

        return list.equals(NOMAD_PAGE_RECIPE_1) || list.equals(NOMAD_PAGE_RECIPE_2);
    }

    public ItemStack craft(CraftingInventory craftingInventory) {
        List<Item> list = Lists.newArrayList();

        for(int i = 0; i < craftingInventory.getInvSize(); ++i) {
            list.add(craftingInventory.getInvStack(i).getItem());
        }

        if (list.equals(NOMAD_PAGE_RECIPE_1) || list.equals(NOMAD_PAGE_RECIPE_2)) {
            ItemStack ret = new ItemStack(NomadBooks.NOMAD_PAGE);
            ret.getOrCreateSubTag(NomadBooks.MODID).putInt("Height", 1);
            ret.getOrCreateSubTag(NomadBooks.MODID).putString("Structure", NomadBookItem.defaultStructurePath);
            return ret;
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Environment(EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return NomadBooks.CRAFT_NOMAD_PAGE;
    }
}
