package net.zestyblaze.nomadbooks.recipe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.zestyblaze.nomadbooks.NomadBooks;
import net.zestyblaze.nomadbooks.item.NomadBookItem;

public class NetherNomadBookCraftRecipe extends CustomRecipe {
    public NetherNomadBookCraftRecipe(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    public static ItemStack getCraftResult() {
        ItemStack result = new ItemStack(NomadBooks.NETHER_NOMAD_BOOK);
        result.getOrCreateTagElement(NomadBooks.MODID).putInt("Height", 3);
        result.getOrCreateTagElement(NomadBooks.MODID).putInt("Width", 7);

        return result;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack book = null;
        boolean ingot = false;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if(book == null && itemStack.getItem() == NomadBooks.NOMAD_BOOK) {
                book = itemStack;
            } else if(!ingot && itemStack.getItem() == Items.NETHERITE_INGOT) {
                ingot = true;
            } else if(!itemStack.equals(ItemStack.EMPTY)) {
                return false;
            }
        }
        return book != null && ingot && book.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 0.0f;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        ItemStack book = null;
        boolean ingot = false;

        for(int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if (book == null && itemStack.getItem() instanceof NomadBookItem) {
                book = itemStack;
            } else if (!ingot && itemStack.getItem() == Items.NETHERITE_INGOT) {
                ingot = true;
            } else if (!itemStack.equals(ItemStack.EMPTY)) {
                return ItemStack.EMPTY;
            }
        }

        if (book != null && ingot && book.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 0.0f) {
            ItemStack result = getCraftResult();

            result.getOrCreateTagElement(NomadBooks.MODID).putInt("Height", book.getOrCreateTagElement(NomadBooks.MODID).getInt("Height"));
            result.getOrCreateTagElement(NomadBooks.MODID).putInt("Width", book.getOrCreateTagElement(NomadBooks.MODID).getInt("Width"));
            result.getOrCreateTagElement(NomadBooks.MODID).put("Upgrades", book.getOrCreateTagElement(NomadBooks.MODID).getList("Upgrades", NbtType.STRING));
            if (book.getOrCreateTagElement(NomadBooks.MODID).getString("Structure").equals(NomadBookItem.defaultStructurePath)) {
                result.getOrCreateTagElement(NomadBooks.MODID).putString("Structure", NomadBookItem.netherDefaultStructurePath);
            } else {
                result.getOrCreateTagElement(NomadBooks.MODID).putString("Structure", book.getOrCreateTagElement(NomadBooks.MODID).getString("Structure"));
            }

            return result;
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
        return NomadBooks.CRAFT_NETHER_NOMAD_BOOK;
    }
}
