package net.zestyblaze.nomadbooks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootPool.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.zestyblaze.nomadbooks.block.MembraneBlock;
import net.zestyblaze.nomadbooks.block.NomadMushroomBlock;
import net.zestyblaze.nomadbooks.block.NomadMushroomStemBlock;
import net.zestyblaze.nomadbooks.item.BookUpgradeItem;
import net.zestyblaze.nomadbooks.item.NomadBookItem;
import net.zestyblaze.nomadbooks.recipe.*;

@SuppressWarnings("deprecation")
public class NomadBooks implements ModInitializer {
	public static final String MODID = "nomadbooks";

	private static final ResourceLocation DUNGEON_CHEST_LOOT_TABLE_ID = new ResourceLocation("minecraft", "chests/simple_dungeon");
	private static final ResourceLocation MINESHAFT_CHEST_LOOT_TABLE_ID = new ResourceLocation("minecraft", "chests/abandoned_mineshaft");
	private static final ResourceLocation TEMPLE_CHEST_LOOT_TABLE_ID = new ResourceLocation("minecraft", "chests/jungle_temple");
	private static final ResourceLocation TREASURE_CHEST_LOOT_TABLE_ID = new ResourceLocation("minecraft", "chests/buried_treasure");
	private static final ResourceLocation OUTPOST_CHEST_LOOT_TABLE_ID = new ResourceLocation("minecraft", "chests/pillager_outpost");
	private static final ResourceLocation STRONGHOLD_LIBRARY_CHEST_LOOT_TABLE_ID = new ResourceLocation("minecraft", "chests/stronghold_library");
	private static final ResourceLocation CARTOGRAPHER_CHEST_LOOT_TABLE_ID = new ResourceLocation("minecraft", "chests/village/village_cartographer");
	private static final ResourceLocation BONUS_CHEST_LOOT_TABLE_ID = new ResourceLocation("minecraft", "chests/spawn_bonus_chest");

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

	public static SimpleRecipeSerializer<NomadBookCraftRecipe> CRAFT_NOMAD_BOOK;
	public static SimpleRecipeSerializer<NomadBookHeightUpgradeRecipe> UPGRADE_HEIGHT_NOMAD_BOOK;
	public static SimpleRecipeSerializer<NomadBookDismantleRecipe> DISMANTLE_NOMAD_BOOK;
	public static SimpleRecipeSerializer<NomadBookUpgradeRecipe> UPGRADE_NOMAD_BOOK;
	public static SimpleRecipeSerializer<NomadBookInkRecipe> INK_NOMAD_BOOK;
	public static SimpleRecipeSerializer<NetherNomadBookCraftRecipe> CRAFT_NETHER_NOMAD_BOOK;

	@Override
	public void onInitialize() {
		GRASS_PAGE = registerItem(new Item(new FabricItemSettings().group(CreativeModeTab.TAB_MISC).rarity(Rarity.UNCOMMON)), "grass_page");
		NOMAD_PAGE = registerItem(new NomadBookItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)), "nomad_page");
		NOMAD_BOOK = registerItem(new NomadBookItem(new FabricItemSettings().maxCount(1).group(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE)), "nomad_book");
		NETHER_NOMAD_BOOK = registerItem(new NomadBookItem(new FabricItemSettings().maxCount(1).group(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE).fireproof()), "nether_nomad_book");
		AQUATIC_MEMBRANE_PAGE = registerItem(new BookUpgradeItem(new FabricItemSettings().maxCount(1).group(CreativeModeTab.TAB_MISC).rarity(Rarity.UNCOMMON), "aquatic_membrane"), "aquatic_membrane_page");
		MYCELIUM_PAGE = registerItem(new BookUpgradeItem(new FabricItemSettings().maxCount(1).group(CreativeModeTab.TAB_MISC).rarity(Rarity.UNCOMMON), "fungi_support"), "mycelium_page");
		CREATIVE_NOMAD_BOOK = registerItem(new NomadBookItem(new FabricItemSettings().maxCount(1).group(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE).fireproof()), "creative_nomad_book");

		UniformGenerator lootTableRange = UniformGenerator.between(0, 1);
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {
			if (DUNGEON_CHEST_LOOT_TABLE_ID.equals(id)) {
				LootPool.Builder poolBuilder = LootPool.lootPool()
						.setRolls(lootTableRange)
						.add(LootItem.lootTableItem(GRASS_PAGE));

				supplier.withPool(poolBuilder);
			}
			if (MINESHAFT_CHEST_LOOT_TABLE_ID.equals(id)) {
				LootPool.Builder poolBuilder = LootPool.lootPool()
						.setRolls(lootTableRange)
						.add(LootItem.lootTableItem(GRASS_PAGE));

				supplier.withPool(poolBuilder);
			}
			if (TEMPLE_CHEST_LOOT_TABLE_ID.equals(id)) {
				LootPool.Builder poolBuilder = LootPool.lootPool()
						.setRolls(lootTableRange)
						.add(LootItem.lootTableItem(GRASS_PAGE));

				supplier.withPool(poolBuilder);
			}
			if (TREASURE_CHEST_LOOT_TABLE_ID.equals(id)) {
				LootPool.Builder poolBuilder = LootPool.lootPool()
						.setRolls(lootTableRange)
						.add(LootItem.lootTableItem(GRASS_PAGE));

				supplier.withPool(poolBuilder);
			}
			if (OUTPOST_CHEST_LOOT_TABLE_ID.equals(id)) {
				LootPool.Builder poolBuilder = LootPool.lootPool()
						.setRolls(lootTableRange)
						.add(LootItem.lootTableItem(GRASS_PAGE));

				supplier.withPool(poolBuilder);
			}
			if (CARTOGRAPHER_CHEST_LOOT_TABLE_ID.equals(id)) {
				LootPool.Builder poolBuilder = LootPool.lootPool()
						.setRolls(lootTableRange)
						.add(LootItem.lootTableItem(GRASS_PAGE));

				supplier.withPool(poolBuilder);
			}
			if (STRONGHOLD_LIBRARY_CHEST_LOOT_TABLE_ID.equals(id)) {
				LootPool.Builder poolBuilder = LootPool.lootPool()
						.setRolls(UniformGenerator.between(0, 3))
						.add(LootItem.lootTableItem(GRASS_PAGE));

				supplier.withPool(poolBuilder);
			}
			if (BONUS_CHEST_LOOT_TABLE_ID.equals(id)) {
				LootPool.Builder poolBuilder = LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(NOMAD_BOOK))
						.apply(SetNbtFunction.setTag(Util.make(new CompoundTag(), (compoundTag) -> compoundTag.put(MODID, Util.make(new CompoundTag(), child -> {
							child.putInt("Height", 1);
							child.putInt("Width", 3);
							child.putString("Structure", NomadBookItem.defaultStructurePath);
						})))).build());

				supplier.withPool(poolBuilder);
			}
		});

		MEMBRANE = Registry.register(Registry.BLOCK, MODID + ":membrane", new MembraneBlock(FabricBlockSettings.of(Material.GRASS).strength(0.6f, 0f).nonOpaque().sounds(SoundType.HONEY_BLOCK).noCollision()));
		NOMAD_MUSHROOM_BLOCK = Registry.register(Registry.BLOCK, MODID + ":nomad_mushroom_block", new NomadMushroomBlock(FabricBlockSettings.of(Material.WOOD, MaterialColor.COLOR_PURPLE).strength(0.6F, 0).sounds(SoundType.WOOD)));
		NOMAD_MUSHROOM_STEM = Registry.register(Registry.BLOCK, MODID + ":nomad_mushroom_stem", new NomadMushroomStemBlock(FabricBlockSettings.of(Material.WOOD, MaterialColor.TERRACOTTA_WHITE).strength(0.6F, 0).sounds(SoundType.WOOD)));

		NomadBookCraftRecipe.getCraftResult();
		CRAFT_NOMAD_BOOK = Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(MODID, "crafting_special_nomadbookcraft"), new SimpleRecipeSerializer<>(NomadBookCraftRecipe::new));
		UPGRADE_HEIGHT_NOMAD_BOOK = Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(MODID, "crafting_special_nomadbookupgradeheight"), new SimpleRecipeSerializer<>(NomadBookHeightUpgradeRecipe::new));
		DISMANTLE_NOMAD_BOOK = Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(MODID, "crafting_special_nomadbookdismantle"), new SimpleRecipeSerializer<>(NomadBookDismantleRecipe::new));
		UPGRADE_NOMAD_BOOK = Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(MODID, "crafting_special_nomadbookupgrade"), new SimpleRecipeSerializer<>(NomadBookUpgradeRecipe::new));
		INK_NOMAD_BOOK = Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(MODID, "crafting_special_nomadbookink"), new SimpleRecipeSerializer<>(NomadBookInkRecipe::new));
		CRAFT_NETHER_NOMAD_BOOK = Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(MODID, "crafting_special_nethernomadbookcraft"), new SimpleRecipeSerializer<>(NetherNomadBookCraftRecipe::new));
	}

	public static Item registerItem(Item item, String name) {
		Registry.register(Registry.ITEM, MODID + ":" + name, item);
		return item;
	}
}
