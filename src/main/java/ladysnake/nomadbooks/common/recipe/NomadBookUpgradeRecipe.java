package ladysnake.nomadbooks.common.recipe;

import com.google.common.collect.Lists;
import ladysnake.nomadbooks.NomadBooks;
import ladysnake.nomadbooks.common.item.BookUpgradeItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class NomadBookUpgradeRecipe extends SpecialCraftingRecipe {
    public NomadBookUpgradeRecipe(Identifier identifier) {
        super(identifier);
    }

    public boolean matches(CraftingInventory craftingInventory, World world) {
        List<ItemStack> list = Lists.newArrayList();

        for(int i = 0; i < craftingInventory.getInvSize(); ++i) {
            ItemStack itemStack = craftingInventory.getInvStack(i);
            if (itemStack.getItem().equals(NomadBooks.NOMAD_BOOK) || itemStack.getItem() instanceof BookUpgradeItem) {
                list.add(itemStack);
                if (list.size() > 1) {
                    ItemStack itemStack2 = list.get(0);
                    if (!(itemStack2.getItem().equals(NomadBooks.NOMAD_BOOK) && itemStack.getItem() instanceof BookUpgradeItem
                    || itemStack2.getItem() instanceof BookUpgradeItem && itemStack.getItem().equals(NomadBooks.NOMAD_BOOK))) {
                        return false;
                    }
                }
            }
        }

        return list.size() == 2;
    }

    public ItemStack craft(CraftingInventory craftingInventory) {
        List<ItemStack> list = Lists.newArrayList();

        for(int i = 0; i < craftingInventory.getInvSize(); ++i) {
            ItemStack itemStack = craftingInventory.getInvStack(i);
            if (itemStack.getItem().equals(NomadBooks.NOMAD_BOOK) || itemStack.getItem() instanceof BookUpgradeItem) {
                list.add(itemStack);
                if (list.size() > 1) {
                    ItemStack itemStack2 = list.get(0);
                    if (!(itemStack2.getItem().equals(NomadBooks.NOMAD_BOOK) && itemStack.getItem() instanceof BookUpgradeItem
                            || itemStack2.getItem() instanceof BookUpgradeItem && itemStack.getItem().equals(NomadBooks.NOMAD_BOOK))) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if (list.size() == 2) {
            ItemStack itemStack1 = list.get(0);
            ItemStack itemStack2 = list.get(1);
            ItemStack book;
            ItemStack upgrade;
            if (itemStack1.getItem().equals(NomadBooks.NOMAD_BOOK)) {
                book = itemStack1;
                upgrade = itemStack2;
            } else {
                book = itemStack2;
                upgrade = itemStack1;
            }

            ItemStack ret = book.copy();
            if (upgrade.getItem().equals(NomadBooks.ITINERANT_INK)) {
                if (!book.getOrCreateSubTag(NomadBooks.MODID).getBoolean("Inked")) {
                    ret.getOrCreateSubTag(NomadBooks.MODID).putBoolean("Inked", true);
                    ret.getOrCreateSubTag(NomadBooks.MODID).putInt("InkGoal", 10000);
                    ret.getOrCreateSubTag(NomadBooks.MODID).putInt("InkProgress", 0);
                } else {
                    return ItemStack.EMPTY;
                }
            } else {
                ListTag upgradeList = ret.getOrCreateSubTag(NomadBooks.MODID).getList("Upgrades", NbtType.STRING);
                upgradeList.add(StringTag.of(((BookUpgradeItem) upgrade.getItem()).getUpgrade()));
                ret.getOrCreateSubTag(NomadBooks.MODID).put("Upgrades", upgradeList);
            }

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
