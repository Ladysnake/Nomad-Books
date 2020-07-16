package ladysnake.nomadbooks.common.item;

import ladysnake.nomadbooks.NomadBooks;
import ladysnake.nomadbooks.common.block.NomadMushroomBlock;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

import static net.minecraft.text.Style.EMPTY;

public class NomadBookItem extends Item {
    public static final int CAMP_RETRIEVAL_RADIUS = 10;

    public static final String defaultStructurePath = NomadBooks.MODID + ":campfire7x1x7";
    public static final String netherDefaultStructurePath = NomadBooks.MODID + ":nethercampfire7x1x7";

    public NomadBookItem(Settings settings) {
        super(settings);
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
            World.CODEC.encodeStart(NbtOps.INSTANCE, context.getWorld().getRegistryKey()).result().ifPresent(tag -> tags.put("Dimension", tag));

            World.CODEC.encodeStart(NbtOps.INSTANCE, context.getWorld().getRegistryKey());

            BlockPos ogpos = pos.add(new BlockPos(0, 1, 0));
            pos = pos.add(new BlockPos(-width/2, 1, -width/2));

            // check if there's enough space
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < width; z++) {
                    for (int y = 0; y < height; y++) {
                        BlockPos p = pos.add(new BlockPos(x, y, z));
                        BlockState bs = context.getWorld().getBlockState(p);
                        if (!(isBlockReplaceable(bs) || isBlockUnderwaterReplaceable(bs) && tags.getList("Upgrades", NbtType.STRING).contains(StringTag.of("aquatic_membrane")))) {
                            context.getPlayer().sendMessage(new TranslatableText("error.nomadbooks.no_space"), true);
                            return ActionResult.FAIL;
                        }
                    }
                }
            }

            // mushroom platform upgrade
            if (tags.getList("Upgrades", NbtType.STRING).contains(StringTag.of("fungi_support"))) {
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < width; z++) {
                        BlockPos p = pos.add(new BlockPos(x, -1, z));
                        BlockState bs = context.getWorld().getBlockState(p);
                        if (isBlockReplaceable(bs) || isBlockUnderwaterReplaceable(bs)) {
                            context.getWorld().setBlockState(p, NomadBooks.NOMAD_MUSHROOM_BLOCK.getDefaultState());
                        }

                        if (x >= width/2-1 && x <= width/2+1 && z >= width/2-1 && z <= width/2+1) {
                            for (int y = -2; y > -6; y--) {
                                BlockPos p2 = pos.add(new BlockPos(x, y, z));
                                if (isBlockReplaceable(context.getWorld().getBlockState(p2)) || isBlockUnderwaterReplaceable(context.getWorld().getBlockState(p2))) {
                                    context.getWorld().setBlockState(p2, NomadBooks.NOMAD_MUSHROOM_STEM.getDefaultState());
                                }
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
                        context.getPlayer().sendMessage(new TranslatableText("error.nomadbooks.invalid_surface"), true);
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

                // if structure is smaller than the camp size, center the structure
                int offsetWidth = (width - structure.getSize().getX())/2;

                StructurePlacementData structurePlacementData = (new StructurePlacementData()).setIgnoreEntities(true).setChunkPosition((ChunkPos) null);
                structure.place(serverWorld, pos.add(offsetWidth, 0, offsetWidth), structurePlacementData, serverWorld.getRandom());
            }

            // set deployed, register nbt
            context.getStack().getOrCreateTag().putFloat(NomadBooks.MODID + ":deployed", 1F);
            tags.put("CampPos", NbtHelper.fromBlockPos(pos));

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
        BlockPos pos = NbtHelper.toBlockPos(tags.getCompound("CampPos"));
        String structurePath = tags.getString("Structure");
        int height = tags.getInt("Height");
        int width = tags.getInt("Width");

        if (isDeployed) {
            // if sneaking, show camp boundaries, else, pack up
            if (user.isSneaking()) {
                // switch boundaries display on or off
                if (tags.getBoolean("DisplayBoundaries")) {
                    user.sendMessage(new TranslatableText("info.nomadbooks.display_boundaries_off"), true);
                    tags.putBoolean("DisplayBoundaries", false);
                } else {
                    user.sendMessage(new TranslatableText("info.nomadbooks.display_boundaries_on"), true);
                    tags.putBoolean("DisplayBoundaries", true);
                }

                return TypedActionResult.pass(itemStack);
            } else {
                Optional dimension = World.CODEC.parse(NbtOps.INSTANCE, tags.get("Dimension")).result();
                int distanceFromCamp = user.getBlockPos().getManhattanDistance(pos.add(new Vec3i(width/2, 0, width/2)))-(CAMP_RETRIEVAL_RADIUS+width/2);

                // if in correct dimension
                if (dimension.isPresent() && (dimension.get() == world.getRegistryKey())) {
                    // if the camp is too far
                    if (distanceFromCamp > 0) {
                        int enderPrice = (int) Math.ceil(((double) distanceFromCamp) / 100);

                        // if the player is holding enough ender pearls in his off hand, tp to camp
                        if (user.getOffHandStack().getItem() == Items.ENDER_PEARL && user.getOffHandStack().getCount() >= enderPrice) {
                            world.playSound(user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f, true);
                            user.refreshPositionAndAngles(pos.getX() + width / 2 + 0.5, pos.getY(), pos.getZ() + width / 2 + 0.5, user.yaw, user.pitch);
                            if (!user.isCreative()) {
                                user.getOffHandStack().decrement(enderPrice);
                            }
                            world.playSound(pos.getX() + width / 2 + 0.5, pos.getY(), pos.getZ() + width / 2 + 0.5, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f, true);
                            return TypedActionResult.success(itemStack);
                        } else {
                            user.sendMessage(new TranslatableText("error.nomadbooks.camp_too_far"), true);
                            return TypedActionResult.success(itemStack);
                        }
                    }
                } else {
                    user.sendMessage(new TranslatableText("error.nomadbooks.different_dimension"), true);
                    return TypedActionResult.success(itemStack);
                }

                // if default structure path, create a new one
                if (structurePath.equals(defaultStructurePath) || structurePath.equals(netherDefaultStructurePath)) {
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
                        return TypedActionResult.success(itemStack);
                    }

                    structure.saveFromWorld(world, pos.add(new BlockPos(0, 0, 0)), new BlockPos(width, height, width), true, Blocks.STRUCTURE_VOID);
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
                            removeBlock(world, p);
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
                            if (bs.getBlock().equals(NomadBooks.NOMAD_MUSHROOM_BLOCK)) {
                                removeBlock(world, p);
                            }

                            if (x >= width/2-1 && x <= width/2+1 && z >= width/2-1 && z <= width/2+1) {
                                for (int y = -2; y > -6; y--) {
                                    BlockPos p2 = pos.add(new BlockPos(x, y, z));
                                    if (world.getBlockState(p2).getBlock() instanceof NomadMushroomBlock) {
                                        removeBlock(world, p2);
                                    }
                                }
                            }
                        }
                    }
                }

                if (!world.isClient()) {
                    // remove blocks dropped by accident
                    BlockPos p2 = pos.add(new BlockPos(width, height, width));
                    List<ItemEntity> itemEntities = world.getEntities(EntityType.ITEM, new Box(pos.getX(), pos.getY(), pos.getZ(), p2.getX(), p2.getY(), p2.getZ()), itemEntity -> true);
                    itemEntities.forEach(itemEntity -> {
                        if (itemEntity.getAge() < 1) {
                            itemEntity.remove();
                        }
                    });
                }

                // remove boundaries display
                tags.putBoolean("DisplayBoundaries", false);

                world.playSound(user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1, 0.9f, true);
                return TypedActionResult.success(itemStack);
            }
        } else {
            return TypedActionResult.success(itemStack);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        CompoundTag tags = stack.getOrCreateSubTag(NomadBooks.MODID);

        // height, width and upgrades
        int height = tags.getInt("Height");
        int width = tags.getInt("Width");
        tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.height", height).setStyle(EMPTY.withColor(Formatting.GRAY)));
        tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.width", width).setStyle(EMPTY.withColor(Formatting.GRAY)));
        ListTag upgrades = tags.getList("Upgrades", NbtType.STRING);
        upgrades.forEach(tag -> tooltip.add(new TranslatableText("upgrade.nomadbooks."+tag.asString()).setStyle(EMPTY.withColor(Formatting.DARK_AQUA))));

        // if inked, show progress
        if (tags.getBoolean("Inked")) {
            tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.itinerant_ink", tags.getInt("InkProgress"), tags.getInt("InkGoal")).setStyle(EMPTY.withColor(Formatting.BLUE)));
        }
        // camp coordinates if deployed
        if (stack.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 1.0f) {
            BlockPos pos = NbtHelper.toBlockPos(tags.getCompound("CampPos"));


            Optional dimension = World.CODEC.parse(NbtOps.INSTANCE, tags.get("Dimension")).result();

            if (dimension.isPresent() && dimension.get() == world.getRegistryKey()) {
                tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.position", pos.getX()+", "+pos.getY()+", "+pos.getZ()).setStyle(EMPTY.withColor(Formatting.DARK_GRAY)));
            } else {
                tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.position", pos.getX()+", "+pos.getY()+", "+pos.getZ()).setStyle(EMPTY.withColor(Formatting.DARK_GRAY).withFormatting(Formatting.OBFUSCATED)));
            }
//            tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.dimension", dimension).setStyle(EMPTY.withColor(Formatting.DARK_GRAY)));
        }
        // displaying boundaries
        if (stack.getItem() instanceof NomadBookItem) {
            if (tags.getBoolean("DisplayBoundaries")) {
                tooltip.add(new TranslatableText("item.nomadbooks.nomad_book.tooltip.boundaries_display").setStyle(EMPTY.withColor(Formatting.GREEN).withItalic(true)));
            }
        }
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        super.appendStacks(group, stacks);
        stacks.forEach(itemStack -> {
            if (itemStack.getItem() instanceof NomadBookItem) {
                CompoundTag tags = itemStack.getOrCreateSubTag(NomadBooks.MODID);
                tags.putInt("Height", 3);
                tags.putInt("Width", 7);
                if (itemStack.getItem() == NomadBooks.NETHER_NOMAD_BOOK) {
                    tags.putString("Structure", netherDefaultStructurePath);
                } else {
                    tags.putString("Structure", defaultStructurePath);
                }
            }
        });
    }

    public static boolean isBlockReplaceable(BlockState blockState) {
        Material m = blockState.getMaterial();
        return blockState.isAir() || m.equals(Material.REPLACEABLE_PLANT) || m.equals(Material.PLANT) || m.equals(Material.SNOW_LAYER);
    }

    public static boolean isBlockUnderwaterReplaceable(BlockState blockState) {
        Block b = blockState.getBlock();
        Material m = blockState.getMaterial();
        return b.equals(Blocks.WATER) || m.equals(Material.REPLACEABLE_UNDERWATER_PLANT) || m.equals(Material.UNDERWATER_PLANT);
    }

    @Override
    public boolean isFireproof() {
        return super.isFireproof();
    }

    public void removeBlock(World world, BlockPos blockPos) {
        world.breakBlock(blockPos, false);
        world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
    }
}
