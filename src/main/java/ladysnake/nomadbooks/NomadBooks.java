package ladysnake.nomadbooks;

import ladysnake.nomadbooks.common.block.MembraneBlock;
import ladysnake.nomadbooks.common.item.GrassPageItem;
import ladysnake.nomadbooks.common.item.NomadBookItem;
import ladysnake.nomadbooks.common.recipe.NomadBookUpgradeRecipe;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.block.Block;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class NomadBooks implements ModInitializer {
    public static final String MODID = "nomadbooks";

    private static final Identifier DUNGEON_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/simple_dungeon");
    private static final Identifier MINESHAFT_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/abandoned_mineshaft");
    private static final Identifier TEMPLE_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/jungle_temple");
    private static final Identifier PYRAMID_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/desert_pyramid");
    private static final Identifier STRONGHOLD_LIBRARY_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/stronghold_library");

    public static Item GRASS_PAGE;
    public static Item NOMAD_PAGE;
    public static Item NOMAD_BOOK;

    public static Block MEMBRANE;

    public static SpecialRecipeSerializer<NomadBookUpgradeRecipe> UPGRADE_NOMAD_BOOK;

    @Override
    public void onInitialize() {
        GRASS_PAGE = registerItem(new GrassPageItem((new Item.Settings()).group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)), "grass_page");
        NOMAD_PAGE = registerItem(new NomadBookItem((new Item.Settings()).maxCount(1).group(ItemGroup.MISC).rarity(Rarity.UNCOMMON)), "nomad_page");
        NOMAD_BOOK = registerItem(new NomadBookItem((new Item.Settings()).maxCount(1).group(ItemGroup.MISC).rarity(Rarity.RARE)), "nomad_book");

        // add grass pages to dungeons, mineshafts, jungle temples, desert pyramids and stronghold libraries chests loot tables
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if (DUNGEON_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .withRolls(ConstantLootTableRange.create(1))
                        .withEntry(ItemEntry.builder(GRASS_PAGE));

                supplier.withPool(poolBuilder);
            }
            if (MINESHAFT_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .withRolls(ConstantLootTableRange.create(1))
                        .withEntry(ItemEntry.builder(GRASS_PAGE));

                supplier.withPool(poolBuilder);
            }
            if (TEMPLE_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .withRolls(ConstantLootTableRange.create(1))
                        .withEntry(ItemEntry.builder(GRASS_PAGE));

                supplier.withPool(poolBuilder);
            }
            if (PYRAMID_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .withRolls(ConstantLootTableRange.create(1))
                        .withEntry(ItemEntry.builder(GRASS_PAGE));

                supplier.withPool(poolBuilder);
            }
            if (STRONGHOLD_LIBRARY_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .withRolls(ConstantLootTableRange.create(1))
                        .withEntry(ItemEntry.builder(GRASS_PAGE));

                supplier.withPool(poolBuilder);
            }
        });

        MEMBRANE = Registry.register(Registry.BLOCK, MODID + ":membrane", new MembraneBlock(DyeColor.GRAY, FabricBlockSettings.of(Material.GLASS).strength(0.3f, 0.3f).nonOpaque().sounds(BlockSoundGroup.field_21214).noCollision().build()));

        UPGRADE_NOMAD_BOOK = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "crafting_special_nomadbookupgrade"), new SpecialRecipeSerializer<>(NomadBookUpgradeRecipe::new));
    }

    public static Item registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, MODID + ":" + name, item);
        return item;
    }
}