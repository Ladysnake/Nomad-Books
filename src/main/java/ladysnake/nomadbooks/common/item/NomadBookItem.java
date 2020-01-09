package ladysnake.nomadbooks.common.item;

import ladysnake.nomadbooks.NomadBooks;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.List;
import java.util.function.Predicate;

public class NomadBookItem extends Item {
    public static final String defaultStructurePath = NomadBooks.MODID + ":campfire7x1x7";

    public NomadBookItem(Settings settings) {
        super(settings);
        this.addPropertyGetter(new Identifier(NomadBooks.MODID + ":deployed"), (itemStack, world, livingEntity) -> itemStack.getOrCreateTag().getFloat(NomadBooks.MODID + ":deployed"));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        CompoundTag tags = context.getStack().getOrCreateSubTag(NomadBooks.MODID);
        boolean isDeployed = context.getStack().getOrCreateTag().getFloat(NomadBooks.MODID + ":deployed") == 1f;

        if (!isDeployed) {
            String structurePath = tags.getString("Structure");
            int height = tags.getInt("Height");
            int width = tags.getInt("Width");

            BlockPos pos = context.getBlockPos();
            while (isBlockReplaceable(context.getWorld().getBlockState(pos))
                    || isBlockUnderwaterReplaceable(context.getWorld().getBlockState(pos))
                    && tags.getList("Upgrades", NbtType.STRING).contains(StringTag.of("aquatic_membrane"))) {
                pos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
            }

            // set dimension
            tags.putInt("Dimension", context.getWorld().getDimension().getType().getRawId());

            BlockPos ogpos = pos.add(new BlockPos(0, 1, 0));
            pos = pos.add(new BlockPos(-width/2, 1, -width/2));

            // end platform upgrade
//            if (tags.getList("Upgrades", NbtType.STRING).contains(StringTag.of("end"))) {
//                boolean canPlacePlatform = false;
//                int endy = 20;
//                while (!canPlacePlatform) {
//                    canPlacePlatform = true;
//                    for (int x = 0; x < width; x++) {
//                        for (int z = 0; z < width; z++) {
//                            for (int y = 0; y < 4; y++) {
//                                BlockPos p = pos.add(new BlockPos(x, endy + y, z));
//                                BlockState bs = context.getWorld().getBlockState(p);
//                                if (!isBlockReplaceable(bs)) {
//                                    canPlacePlatform = false;
//                                }
//                            }
//                        }
//                    }
//                    if (!canPlacePlatform) {
//                        endy++;
//                    }
//                }
//                pos = pos.add(new BlockPos(0, endy+1, 0));
//            }

            // check if there's enough space
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < width; z++) {
                    for (int y = 0; y < height; y++) {
                        BlockPos p = pos.add(new BlockPos(x, y, z));
                        BlockState bs = context.getWorld().getBlockState(p);
                        if (!(isBlockReplaceable(bs) || isBlockUnderwaterReplaceable(bs) && tags.getList("Upgrades", NbtType.STRING).contains(StringTag.of("aquatic_membrane")))) {
                            context.getPlayer().addChatMessage(new TranslatableText("error.nomadbooks.no_space"), true);
                            return ActionResult.FAIL;
                        }
                    }
                }
            }

//            // if enough space and end upgrade, set up the platform
//            if (tags.getList("Upgrades", NbtType.STRING).contains(StringTag.of("end"))) {
//                for (int x = 0; x < width; x++) {
//                    for (int z = 0; z < width; z++) {
//                        for (int y = 0; y > -4; y--) {
//                            Block b = Blocks.END_STONE_BRICKS;
//                            if (y == 0 || y == -3) {
//                                b = Blocks.PURPUR_BLOCK;
//                            }
//                            if (x == 0 && z == 0 || x == 0 && z == 6 || x == 6 && z == 0 || x == 6 && z ==6) {
//                                b = Blocks.PURPUR_PILLAR;
//                            }
//                            BlockPos p = pos.add(new BlockPos(x, y-1, z));
//                            context.getWorld().setBlockState(p, b.getDefaultState());
//                            context.getWorld().playSound(p.getX(), p.getY(), p.getZ(), SoundEvents.ENTITY_SHULKER_TELEPORT, SoundCategory.BLOCKS, 1, 1, true);
//                        }
//                    }
//                }
//                // place teleporter block
//                context.getWorld().setBlockState(ogpos, NomadBooks.TELEPORTER.getDefaultState());
//                context.getWorld().playSound(ogpos.getX(), ogpos.getY(), ogpos.getZ(), SoundEvents.ENTITY_SHULKER_TELEPORT, SoundCategory.BLOCKS, 1, 1, true);
//            }

            // mushroom platform upgrade
            if (tags.getList("Upgrades", NbtType.STRING).contains(StringTag.of("fungi_support"))) {
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < width; z++) {
                        BlockPos p = pos.add(new BlockPos(x, -1, z));
                        BlockState bs = context.getWorld().getBlockState(p);
                        if (isBlockReplaceable(bs) || isBlockUnderwaterReplaceable(bs) && tags.getList("Upgrades", NbtType.STRING).contains(StringTag.of("aquatic_membrane"))) {
                            context.getWorld().setBlockState(p, Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState());
                        }

                        if (x >= width/2-1 && x <= width/2+1 && z >= width/2-1 && z <= width/2+1) {
                            int y = -2;
                            BlockPos p2 = pos.add(new BlockPos(x, y, z));
                            BlockState bs2 = context.getWorld().getBlockState(p2);
                            while ((isBlockReplaceable(bs2) || isBlockUnderwaterReplaceable(bs2) && tags.getList("Upgrades", NbtType.STRING).contains(StringTag.of("aquatic_membrane"))) && y > -6) {
                                context.getWorld().setBlockState(p2, Blocks.MUSHROOM_STEM.getDefaultState());
                                y--;
                                p2 = pos.add(new BlockPos(x, y, z));
                                bs2 = context.getWorld().getBlockState(p2);
                            }
                        }
                    }
                }
            }

            // check if the surface is valid
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < width; z++) {
                    BlockPos p = pos.add(new BlockPos(x, -1, z));
                    BlockState bs = context.getWorld().getBlockState(p);
                    if (isBlockReplaceable(bs)) {
                        context.getPlayer().addChatMessage(new TranslatableText("error.nomadbooks.invalid_surface"), true);
                        return ActionResult.FAIL;
                    }
                }
            }

            // if membrane upgrade, replace water and underwater plants with membrane
            if (tags.getList("Upgrades", NbtType.STRING).contains(StringTag.of("aquatic_membrane"))) {
                for (int x = -1; x < width+1; x++) {
                    for (int z = -1; z < width+1; z++) {
                        for (int y = 0; y < height + 1; y++) {
                            BlockPos p = pos.add(new BlockPos(x, y, z));
                            BlockState bs = context.getWorld().getBlockState(p);
                            if (isBlockUnderwaterReplaceable(bs) &&
                                    !((x == -1 && z == -1) || (x == -1 && z == width) || (x == width && z == -1) || (x == width && z == width)
                                            || (y == height && x == -1) || (y == height && x == width) || (y == height && z == -1) || (y == height && z == width)) &&
                                    (x == -1 || x == width || y == height || z == -1 || z == width)) {
                                context.getWorld().breakBlock(p, true);
                                context.getWorld().setBlockState(p, NomadBooks.MEMBRANE.getDefaultState());
                            }
                        }
                    }
                }
            }


            // destroy destroyable blocks in the way
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < width; z++) {
                    for (int y = 0; y < height; y++) {
                        context.getWorld().breakBlock(pos.add(new BlockPos(x, y, z)), true);
                        context.getWorld().setBlockState(pos.add(new BlockPos(x, y, z)), Blocks.AIR.getDefaultState());
                    }
                }
            }

            // place if there's enough
            if (!context.getWorld().isClient()) {
                ServerWorld serverWorld = (ServerWorld) context.getWorld();
                Structure structure = serverWorld.getStructureManager().getStructure(new Identifier(structurePath));
                StructurePlacementData structurePlacementData = (new StructurePlacementData()).setIgnoreEntities(true).setChunkPosition((ChunkPos) null);
                structure.place(serverWorld, pos, structurePlacementData);
            }

            // set deployed, register nbt
            context.getStack().getOrCreateTag().putFloat(NomadBooks.MODID + ":deployed", 1F);
            tags.put("CampCenter", NbtHelper.fromBlockPos(pos));

            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1, 1, true);
            return ActionResult.SUCCESS;
        } else {
            this.use(context.getWorld(), context.getPlayer(), context.getHand());
            return ActionResult.FAIL;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        CompoundTag tags = itemStack.getOrCreateSubTag(NomadBooks.MODID);
        boolean isDeployed = itemStack.getOrCreateTag().getFloat(NomadBooks.MODID + ":deployed") == 1f;
        BlockPos pos = NbtHelper.toBlockPos(tags.getCompound("CampCenter"));
        String structurePath = tags.getString("Structure");
        int height = tags.getInt("Height");
        int width = tags.getInt("Width");

        if (isDeployed) {
            // if sneaking, show camp boundaries, else, pack up
            if (user.isSneaking()) {
                // switch boundaries display on or off
                if (tags.getBoolean("DisplayBoundaries")) {
                    user.addChatMessage(new TranslatableText("info.nomadbooks.display_boundaries_off"), true);
                    tags.putBoolean("DisplayBoundaries", false);
                } else {
                    user.addChatMessage(new TranslatableText("info.nomadbooks.display_boundaries_on"), true);
                    tags.putBoolean("DisplayBoundaries", true);
                }

                return TypedActionResult.pass(itemStack);
            } else {
                // if structure is in another dimension, error
                if (tags.getInt("Dimension") != world.getDimension().getType().getRawId()) {
                    user.addChatMessage(new TranslatableText("error.nomadbooks.different_dimension"), true);
                    return TypedActionResult.fail(itemStack);
                }

                // if default structure path, create a new one
                if (structurePath.equals(defaultStructurePath)) {
                    structurePath = NomadBooks.MODID + ":"+user.getUuid()+"/"+System.currentTimeMillis();
                    tags.putString("Structure", structurePath);
                }

                if (!world.isClient) {
                    ServerWorld serverWorld = (ServerWorld) world;
                    StructureManager structureManager = serverWorld.getStructureManager();

                    // save structure
                    Structure structure;
                    try {
                        structure = structureManager.getStructureOrBlank(new Identifier(structurePath));
                    } catch (InvalidIdentifierException var8) {
                        return TypedActionResult.fail(itemStack);
                    }

                    structure.method_15174(world, pos.add(new BlockPos(0, 0, 0)), new BlockPos(width, height, width), true, Blocks.STRUCTURE_VOID);
                    structure.setAuthor(user.getEntityName());
                    structureManager.saveStructure(new Identifier(structurePath));

                    // clear block entities
                    for (int x = 0; x < width; x++) {
                        for (int z = 0; z < width; z++) {
                            for (int y = 0; y < height; y++) {
                                BlockPos p = pos.add(new BlockPos(x, y, z));
                                BlockEntity blockEntity = serverWorld.getBlockEntity(p);
                                Clearable.clear(blockEntity);
                            }
                        }
                    }

                    // set undeployed
                    itemStack.getOrCreateTag().putFloat(NomadBooks.MODID + ":deployed", 0F);
                }

                // remove blocks
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < width; z++) {
                        for (int y = 0; y < height; y++) {
                            BlockPos p = pos.add(new BlockPos(x, y, z));
                            world.breakBlock(p, false);
                        }
                    }
                }

                // if membrane upgrade, remove membrane
                if (tags.getList("Upgrades", NbtType.STRING).contains(StringTag.of("aquatic_membrane"))) {
                    for (int x = -1; x < width+1; x++) {
                        for (int z = -1; z < width+1; z++) {
                            for (int y = -1; y < height + 1; y++) {
                                BlockPos p = pos.add(new BlockPos(x, y, z));
                                BlockState bs = world.getBlockState(p);
                                if (bs.getBlock().equals(NomadBooks.MEMBRANE) &&
                                        !((x == -1 && z == -1) || (x == -1 && z == width) || (x == width && z == -1) || (x == width && z == width)
                                                || (y == height && x == -1) || (y == height && x == width) || (y == height && z == -1) || (y == height && z == width)) &&
                                        (x == -1 || x == width || y == -1 || y == height || z == -1 || z == width)) {
                                    world.breakBlock(p, true);
                                }
                            }
                        }
                    }
                }

                // if mushroom upgrade, remove shroom blocks
                if (tags.getList("Upgrades", NbtType.STRING).contains(StringTag.of("fungi_support"))) {
                    for (int x = 0; x < width; x++) {
                        for (int z = 0; z < width; z++) {
                            BlockPos p = pos.add(new BlockPos(x, -1, z));
                            BlockState bs = world.getBlockState(p);
                            if (bs.getBlock().equals(Blocks.BROWN_MUSHROOM_BLOCK)) {
                                world.breakBlock(p, false);
                            }

                            if (x >= width/2-1 && x <= width/2+1 && z >= width/2-1 && z <= width/2+1) {
                                int y = -2;
                                BlockPos p2 = pos.add(new BlockPos(x, y, z));
                                BlockState bs2 = world.getBlockState(p2);
                                while (bs2.getBlock().equals(Blocks.MUSHROOM_STEM) && y > -6) {
                                    world.breakBlock(p2, false);
                                    y--;
                                    p2 = pos.add(new BlockPos(x, y, z));
                                    bs2 = world.getBlockState(p2);
                                }
                            }
                        }
                    }
                }

//                // if end upgrade, tp platform and teleporter back to the end
//                if (tags.getList("Upgrades", NbtType.STRING).contains(StringTag.of("end"))) {
//                    for (int x = 0; x < width; x++) {
//                        for (int z = 0; z < width; z++) {
//                            for (int y = 0; y > -4; y--) {
//                                BlockPos p = pos.add(new BlockPos(x, y - 1, z));
//                                BlockState bs = world.getBlockState(p);
//                                if (bs.getBlock().equals(Blocks.PURPUR_BLOCK) || bs.getBlock().equals(Blocks.END_STONE_BRICKS) || bs.getBlock().equals(Blocks.PURPUR_PILLAR)) {
//                                    world.setBlockState(p, Blocks.AIR.getDefaultState());
//                                    world.playSound(p.getX(), p.getY(), p.getZ(), SoundEvents.ENTITY_SHULKER_TELEPORT, SoundCategory.BLOCKS, 1, 1, true);
//                                    for (float ix = 0; ix <= 1; ix += 0.5) {
//                                        for (float iy = 0; iy <= 1; iy += 0.5) {
//                                            for (float iz = 0; iz <= 1; iz += 0.5) {
//                                                world.addParticle(ParticleTypes.PORTAL, p.getX() + ix, p.getY() + iy, p.getZ() + iz, 0, 0, 0);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    int y = -20;
//                    while (!world.getBlockState(pos.add(new BlockPos(3, y, 3))).getBlock().equals(NomadBooks.TELEPORTER) && y > -200) {
//                        y--;
//                    }
//                    BlockPos p = pos.add(new BlockPos(3, y, 3));
//                    if (world.getBlockState(p).getBlock().equals(NomadBooks.TELEPORTER)) {
//                        world.breakBlock(p, false);
//                    }
//                }

                if (!world.isClient()) {
                    // remove blocks dropped by accident
                    BlockPos p2 = pos.add(new BlockPos(width, height, width));
                    List<ItemEntity> itemEntities = world.getEntities(EntityType.ITEM, new Box(pos.getX(), pos.getY(), pos.getZ(), p2.getX(), p2.getY(), p2.getZ()), new Predicate<ItemEntity>() {
                        @Override
                        public boolean test(ItemEntity itemEntity) {
                            return itemEntity.getAge() < 1;
                        }
                    });
                    itemEntities.forEach(ItemEntity::remove);
                }

                // remove boundaries display
                tags.putBoolean("DisplayBoundaries", false);

                world.playSound(user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1, 0.9f, true);
                return TypedActionResult.success(itemStack);
            }
        } else {
            return TypedActionResult.fail(itemStack);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        // height, width and upgrades
        if (stack.getItem().equals(NomadBooks.NOMAD_BOOK) || stack.getItem().equals(NomadBooks.MASTER_NOMAD_BOOK)) {
            int height = stack.getOrCreateSubTag(NomadBooks.MODID).getInt("Height");
            int width = stack.getOrCreateSubTag(NomadBooks.MODID).getInt("Width");
            tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.height", height).formatted(Formatting.GRAY));
            tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.width", width).formatted(Formatting.GRAY));
            ListTag upgrades = stack.getOrCreateSubTag(NomadBooks.MODID).getList("Upgrades", NbtType.STRING);
            upgrades.forEach(tag -> tooltip.add(new TranslatableText("upgrade.nomadbooks."+tag.asString()).formatted(Formatting.DARK_AQUA)));
        }
        // if inked, show progress
        if (stack.getOrCreateSubTag(NomadBooks.MODID).getBoolean("Inked")) {
            tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.inked").formatted(Formatting.BLUE));
            tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.ink_progress", stack.getOrCreateSubTag(NomadBooks.MODID).getInt("InkProgress"), stack.getOrCreateSubTag(NomadBooks.MODID).getInt("InkGoal")).formatted(Formatting.BLUE));
        }
        // camp coordinates if deployed
        if (stack.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 1.0f) {
            BlockPos pos = NbtHelper.toBlockPos(stack.getOrCreateSubTag(NomadBooks.MODID).getCompound("CampCenter"));
            DimensionType dim = DimensionType.byRawId(stack.getOrCreateSubTag(NomadBooks.MODID).getInt("Dimension"));
            tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.position", pos.getX()+", "+pos.getY()+", "+pos.getZ()).formatted(Formatting.DARK_GRAY));
            tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.dimension", dim).formatted(Formatting.DARK_GRAY));
        }
        // displaying boundaries
        if (stack.getItem() instanceof NomadBookItem) {
            if (stack.getOrCreateSubTag(NomadBooks.MODID).getBoolean("DisplayBoundaries")) {
                tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.boundaries_display").formatted(Formatting.GREEN).formatted(Formatting.ITALIC));
            }
        }
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        super.appendStacks(group, stacks);
        stacks.forEach(itemStack -> {
            if (itemStack.getItem() instanceof NomadBookItem) {
                CompoundTag tags = itemStack.getOrCreateSubTag(NomadBooks.MODID);
                if (itemStack.getItem().equals(NomadBooks.NOMAD_PAGE)) {
                    tags.putInt("Height", 1);
                    tags.putInt("Width", 7);
                    tags.putString("Structure", defaultStructurePath);
                }
                if (itemStack.getItem().equals(NomadBooks.NOMAD_BOOK) || itemStack.getItem().equals(NomadBooks.MASTER_NOMAD_BOOK)) {
                    tags.putInt("Height", 3);
                    tags.putInt("Width", 7);
                    tags.putString("Structure", defaultStructurePath);
//                    ListTag list = new ListTag();
//                    list.add(StringTag.of("aquatic_membrane"));
//                    tags.put("Upgrades", list);
                }
                if (itemStack.getItem().equals(NomadBooks.MASTER_NOMAD_BOOK)) {
                    tags.putInt("Height", 30);
                    tags.putInt("Width", 15);
                    tags.putString("Structure", defaultStructurePath);
                    ListTag upgradeList = new ListTag();
                    upgradeList.add(StringTag.of("aquatic_membrane"));
                    upgradeList.add(StringTag.of("fungi_support"));
                    itemStack.getOrCreateSubTag(NomadBooks.MODID).put("Upgrades", upgradeList);
                }
            }
        });
    }

    public static boolean isBlockReplaceable(BlockState blockState) {
        Material m = blockState.getMaterial();
        return blockState.isAir() || m.equals(Material.REPLACEABLE_PLANT) || m.equals(Material.SNOW);
    }

    public static boolean isBlockUnderwaterReplaceable(BlockState blockState) {
        Block b = blockState.getBlock();
        Material m = blockState.getMaterial();
        return b.equals(Blocks.WATER) || m.equals(Material.SEAGRASS) || m.equals(Material.UNDERWATER_PLANT);
    }

}
