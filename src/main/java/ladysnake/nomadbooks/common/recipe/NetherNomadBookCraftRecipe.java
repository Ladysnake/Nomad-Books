package ladysnake.nomadbooks.common.recipe;

import ladysnake.nomadbooks.NomadBooks;
import ladysnake.nomadbooks.common.item.NomadBookItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class NetherNomadBookCraftRecipe extends SpecialCraftingRecipe {
    public NetherNomadBookCraftRecipe(Identifier identifier) {
        super(identifier);
    }

    public static ItemStack getCraftResult() {
        ItemStack result = new ItemStack(NomadBooks.NETHER_NOMAD_BOOK);
        result.getOrCreateSubTag(NomadBooks.MODID).putInt("Height", 3);
        result.getOrCreateSubTag(NomadBooks.MODID).putInt("Width", 7);

        return result;
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        ItemStack book = null;
        boolean ingot = false;

        for(int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            if (book == null && itemStack.getItem() == NomadBooks.NOMAD_BOOK) {
                book = itemStack;
            } else if (!ingot && itemStack.getItem() == Items.NETHERITE_INGOT) {
                ingot = true;
            } else if (!itemStack.equals(ItemStack.EMPTY)) {
                return false;
            }
        }

        return book != null && ingot && book.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 0.0f;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack book = null;
        boolean ingot = false;

        for(int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack = craftingInventory.getStack(i);
            if (book == null && itemStack.getItem() instanceof NomadBookItem) {
                book = itemStack;
            } else if (!ingot && itemStack.getItem() == Items.NETHERITE_INGOT) {
                ingot = true;
            } else if (!itemStack.equals(ItemStack.EMPTY)) {
                return ItemStack.EMPTY;
            }
        }

        if (book != null && ingot && book.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 0.0f) {
            ItemStack ret = book.copy();
            ListTag upgradeList = ret.getOrCreateSubTag(NomadBooks.MODID).getList("Upgrades", NbtType.STRING);

            ItemStack result = getCraftResult();

            result.getOrCreateSubTag(NomadBooks.MODID).putInt("Height", book.getOrCreateSubTag(NomadBooks.MODID).getInt("Height"));
            result.getOrCreateSubTag(NomadBooks.MODID).putInt("Width", book.getOrCreateSubTag(NomadBooks.MODID).getInt("Width"));
            result.getOrCreateSubTag(NomadBooks.MODID).put("Upgrades", book.getOrCreateSubTag(NomadBooks.MODID).getList("Upgrades", NbtType.STRING));
            if (book.getOrCreateSubTag(NomadBooks.MODID).getString("Structure").equals(NomadBookItem.defaultStructurePath)) {
                result.getOrCreateSubTag(NomadBooks.MODID).putString("Structure", NomadBookItem.netherDefaultStructurePath);
            } else {
                result.getOrCreateSubTag(NomadBooks.MODID).putString("Structure", book.getOrCreateSubTag(NomadBooks.MODID).getString("Structure"));
            }

            return result;
        }

        return ItemStack.EMPTY;
    }

    @Environment(EnvType.CLIENT)
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return NomadBooks.CRAFT_NETHER_NOMAD_BOOK;
    }
}
