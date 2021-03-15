package ladysnake.nomadbooks;

import ladysnake.nomadbooks.common.block.MembraneBlock;
import ladysnake.nomadbooks.common.block.NomadMushroomBlock;
import ladysnake.nomadbooks.common.block.NomadMushroomStemBlock;
import ladysnake.nomadbooks.common.item.BookUpgradeItem;
import ladysnake.nomadbooks.common.item.NomadBookItem;
import ladysnake.nomadbooks.common.recipe.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class NomadBooks implements ModInitializer {
    public static final String MODID = "nomadbooks";

    private static final Identifier DUNGEON_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/simple_dungeon");
    private static final Identifier MINESHAFT_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/abandoned_mineshaft");
    private static final Identifier TEMPLE_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/jungle_temple");
    private static final Identifier TREASURE_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/buried_treasure");
    private static final Identifier OUTPOST_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/pillager_outpost");
    private static final Identifier STRONGHOLD_LIBRARY_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/stronghold_library");
    private static final Identifier CARTOGRAPHER_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/village/village_cartographer");
    private static final Identifier BONUS_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/spawn_bonus_chest");

    public static Item GRASS_PAGE;
    public static Item NOMAD_PAGE;
    public static Item NOMAD_BOOK;
    public static Item NETHER_NOMAD_BOOK;
    public static Item AQUATIC_MEMBRANE_PAGE;
    public static Item MYCELIUM_PAGE;
    public static Item CREATIVE_NOMAD_BOOK;

    public static Block MEMBRANE;
    public static Block NOMAD_MUSHROOM_BLOCK;
    public static Block NOMAD_MUSHROOM_STEM;

    public static SpecialRecipeSerializer<NomadBookCraftRecipe> CRAFT_NOMAD_BOOK;
    public static SpecialRecipeSerializer<NomadBookHeightUpgradeRecipe> UPGRADE_HEIGHT_NOMAD_BOOK;
    public static SpecialRecipeSerializer<NomadBookDismantleRecipe> DISMANTLE_NOMAD_BOOK;
    public static SpecialRecipeSerializer<NomadBookUpgradeRecipe> UPGRADE_NOMAD_BOOK;
    public static SpecialRecipeSerializer<NomadBookInkRecipe> INK_NOMAD_BOOK;
    public static SpecialRecipeSerializer<NetherNomadBookCraftRecipe> CRAFT_NETHER_NOMAD_BOOK;

    @Override
    public void onInitialize() {
        GRASS_PAGE = registerItem(new Item((new Item.Settings()).group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)), "grass_page");
        NOMAD_PAGE = registerItem(new NomadBookItem((new Item.Settings()).maxCount(1).rarity(Rarity.UNCOMMON)), "nomad_page");
        NOMAD_BOOK = registerItem(new NomadBookItem((new Item.Settings()).maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE)), "nomad_book");
        NETHER_NOMAD_BOOK = registerItem(new NomadBookItem((new Item.Settings()).maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE).fireproof()), "nether_nomad_book");
        AQUATIC_MEMBRANE_PAGE = registerItem(new BookUpgradeItem((new Item.Settings()).maxCount(1).group(ItemGroup.MISC).rarity(Rarity.UNCOMMON), "aquatic_membrane"), "aquatic_membrane_page");
        MYCELIUM_PAGE = registerItem(new BookUpgradeItem((new Item.Settings()).maxCount(1).group(ItemGroup.MISC).rarity(Rarity.UNCOMMON), "fungi_support"), "mycelium_page");
        CREATIVE_NOMAD_BOOK = registerItem(new NomadBookItem((new Item.Settings()).maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE).fireproof()), "creative_nomad_book");

        // add loot to dungeons, mineshafts, jungle temples, and stronghold libraries chests loot tables
        UniformLootTableRange lootTableRange = UniformLootTableRange.between(0, 1);
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if (DUNGEON_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(lootTableRange)
                        .withEntry(ItemEntry.builder(GRASS_PAGE).build());

                supplier.withPool(poolBuilder.build());
            }
            if (MINESHAFT_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(lootTableRange)
                        .withEntry(ItemEntry.builder(GRASS_PAGE).build());

                supplier.withPool(poolBuilder.build());
            }
            if (TEMPLE_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(lootTableRange)
                        .withEntry(ItemEntry.builder(GRASS_PAGE).build());

                supplier.withPool(poolBuilder.build());
            }
            if (TREASURE_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(lootTableRange)
                        .withEntry(ItemEntry.builder(GRASS_PAGE).build());

                supplier.withPool(poolBuilder.build());
            }
            if (OUTPOST_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(lootTableRange)
                        .withEntry(ItemEntry.builder(GRASS_PAGE).build());

                supplier.withPool(poolBuilder.build());
            }
            if (CARTOGRAPHER_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(lootTableRange)
                        .withEntry(ItemEntry.builder(GRASS_PAGE).build());

                supplier.withPool(poolBuilder.build());
            }
            if (STRONGHOLD_LIBRARY_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(lootTableRange)
                        .withEntry(ItemEntry.builder(NOMAD_BOOK).build())
                        .withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), (compoundTag) -> compoundTag.put(MODID, Util.make(new CompoundTag(), child -> {
                            child.putInt("Height", 1);
                            child.putInt("Width", 3);
                            child.putString("Structure", NomadBookItem.defaultStructurePath);
                        })))).build());

                supplier.withPool(poolBuilder.build());
            }
            if (BONUS_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(ConstantLootTableRange.create(1))
                        .withEntry(ItemEntry.builder(NOMAD_BOOK).build())
                        .withFunction(SetNbtLootFunction.builder(Util.make(new CompoundTag(), (compoundTag) -> compoundTag.put(MODID, Util.make(new CompoundTag(), child -> {
                            child.putInt("Height", 1);
                            child.putInt("Width", 3);
                            child.putString("Structure", NomadBookItem.defaultStructurePath);
                        })))).build());

                supplier.withPool(poolBuilder.build());
            }
        });

        MEMBRANE = Registry.register(Registry.BLOCK, MODID + ":membrane", new MembraneBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC).strength(0.6f, 0f).nonOpaque().sounds(BlockSoundGroup.HONEY).noCollision().build()));
        NOMAD_MUSHROOM_BLOCK = Registry.register(Registry.BLOCK, MODID + ":nomad_mushroom_block", new NomadMushroomBlock(FabricBlockSettings.of(Material.WOOD, MaterialColor.PURPLE).strength(0.6F, 0).sounds(BlockSoundGroup.WOOD).build()));
        NOMAD_MUSHROOM_STEM = Registry.register(Registry.BLOCK, MODID + ":nomad_mushroom_stem", new NomadMushroomStemBlock(FabricBlockSettings.of(Material.WOOD, MaterialColor.WEB).strength(0.6F, 0).sounds(BlockSoundGroup.WOOD).build()));

        NomadBookCraftRecipe.getCraftResult();
        CRAFT_NOMAD_BOOK = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "crafting_special_nomadbookcraft"), new SpecialRecipeSerializer<>(NomadBookCraftRecipe::new));
        UPGRADE_HEIGHT_NOMAD_BOOK = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "crafting_special_nomadbookupgradeheight"), new SpecialRecipeSerializer<>(NomadBookHeightUpgradeRecipe::new));
        DISMANTLE_NOMAD_BOOK = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "crafting_special_nomadbookdismantle"), new SpecialRecipeSerializer<>(NomadBookDismantleRecipe::new));
        UPGRADE_NOMAD_BOOK = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "crafting_special_nomadbookupgrade"), new SpecialRecipeSerializer<>(NomadBookUpgradeRecipe::new));
        INK_NOMAD_BOOK = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "crafting_special_nomadbookink"), new SpecialRecipeSerializer<>(NomadBookInkRecipe::new));
        CRAFT_NETHER_NOMAD_BOOK = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "crafting_special_nethernomadbookcraft"), new SpecialRecipeSerializer<>(NetherNomadBookCraftRecipe::new));
    }

    public static Item registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, MODID + ":" + name, item);
        return item;
    }
}