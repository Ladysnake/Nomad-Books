package net.zestyblaze.nomadbooks.item;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Material;
import net.zestyblaze.nomadbooks.NomadBooks;
import net.zestyblaze.nomadbooks.block.NomadMushroomBlock;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class NomadBookItem extends Item {
    public static final int CAMP_RETRIEVAL_RADIUS = 10;

    public static final String defaultStructurePath = NomadBooks.MODID + ":campfire3x1x3";
    public static final String netherDefaultStructurePath = NomadBooks.MODID + ":nethercampfire3x1x3";

    public NomadBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        CompoundTag tags = context.getItemInHand().getOrCreateTagElement(NomadBooks.MODID);
        boolean isDeployed = context.getItemInHand().getOrCreateTag().getFloat(NomadBooks.MODID + ":deployed") == 1f;

        if (!isDeployed) {
            String structurePath = tags.getString("Structure");
            int height = tags.getInt("Height");
            int width = tags.getInt("Width");

            BlockPos pos = context.getClickedPos();
            while (isBlockReplaceable(context.getLevel().getBlockState(pos))
                    || isBlockUnderwaterReplaceable(context.getLevel().getBlockState(pos))
                    && tags.getList("Upgrades", NbtType.STRING).contains(StringTag.valueOf("aquatic_membrane"))) {
                pos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
            }

            // set dimension
            Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, context.getLevel().dimension()).result().ifPresent(tag -> tags.put("Dimension", tag));

            Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, context.getLevel().dimension());

            pos = pos.offset(new BlockPos(-width / 2, 1, -width / 2));

            // check if there's enough space
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < width; z++) {
                    for (int y = 0; y < height; y++) {
                        BlockPos p = pos.offset(new BlockPos(x, y, z));
                        BlockState bs = context.getLevel().getBlockState(p);
                        if (!(isBlockReplaceable(bs) || isBlockUnderwaterReplaceable(bs) && tags.getList("Upgrades", NbtType.STRING).contains(StringTag.valueOf("aquatic_membrane")))) {
                            Objects.requireNonNull(context.getPlayer()).displayClientMessage(new TranslatableComponent("error.nomadbooks.no_space"), true);
                            return InteractionResult.FAIL;
                        }
                    }
                }
            }

            // mushroom platform upgrade
            if (tags.getList("Upgrades", NbtType.STRING).contains(StringTag.valueOf("fungi_support"))) {
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < width; z++) {
                        BlockPos p = pos.offset(new BlockPos(x, -1, z));
                        BlockState bs = context.getLevel().getBlockState(p);
                        if (isBlockReplaceable(bs) || isBlockUnderwaterReplaceable(bs)) {
                            context.getLevel().setBlock(p, NomadBooks.NOMAD_MUSHROOM_BLOCK.defaultBlockState(), 2);
                        }

                        if (x >= width / 2 - 1 && x <= width / 2 + 1 && z >= width / 2 - 1 && z <= width / 2 + 1) {
                            for (int y = -2; y > -6; y--) {
                                BlockPos p2 = pos.offset(new BlockPos(x, y, z));
                                if (isBlockReplaceable(context.getLevel().getBlockState(p2)) || isBlockUnderwaterReplaceable(context.getLevel().getBlockState(p2))) {
                                    context.getLevel().setBlock(p2, NomadBooks.NOMAD_MUSHROOM_STEM.defaultBlockState(), 2);
                                }
                            }
                        }
                    }
                }
            }

            // check if the surface is valid
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < width; z++) {
                    BlockPos p = pos.offset(new BlockPos(x, -1, z));
                    BlockState bs = context.getLevel().getBlockState(p);
                    if (isBlockReplaceable(bs)) {
                        Objects.requireNonNull(context.getPlayer()).displayClientMessage(new TranslatableComponent("error.nomadbooks.invalid_surface"), true);
                        return InteractionResult.FAIL;
                    }
                }
            }

            // if membrane upgrade, replace water and underwater plants with membrane
            if (tags.getList("Upgrades", NbtType.STRING).contains(StringTag.valueOf("aquatic_membrane"))) {
                for (int x = -1; x < width + 1; x++) {
                    for (int z = -1; z < width + 1; z++) {
                        for (int y = 0; y < height + 1; y++) {
                            BlockPos p = pos.offset(new BlockPos(x, y, z));
                            BlockState bs = context.getLevel().getBlockState(p);
                            if (isBlockUnderwaterReplaceable(bs) &&
                                    !((x == -1 && z == -1) || (x == -1 && z == width) || (x == width && z == -1) || (x == width && z == width)
                                            || (y == height && x == -1) || (y == height && x == width) || (y == height && z == -1) || (y == height && z == width)) &&
                                    (x == -1 || x == width || y == height || z == -1 || z == width)) {
                                context.getLevel().destroyBlock(p, true);
                                context.getLevel().setBlock(p, NomadBooks.MEMBRANE.defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }


            // destroy destroyable blocks in the way
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < width; z++) {
                    for (int y = 0; y < height; y++) {
                        context.getLevel().destroyBlock(pos.offset(new BlockPos(x, y, z)), true);
                        context.getLevel().setBlock(pos.offset(new BlockPos(x, y, z)), Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }

            // place if there's enough
            if(!context.getLevel().isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) context.getLevel();
                Optional<StructureTemplate> structure = serverLevel.getStructureManager().get(new ResourceLocation(structurePath));

                // if structure is smaller than the camp size, center the structure
                int offsetWidth = (width - structure.get().getSize().getX()) / 2;

                StructurePlaceSettings structurePlacementData = (new StructurePlaceSettings()).setIgnoreEntities(true);
                structure.get().placeInWorld(serverLevel, pos.offset(offsetWidth, 0, offsetWidth), pos.offset(offsetWidth, 0, offsetWidth), structurePlacementData, serverLevel.getRandom(), 2);
            }

            // set deployed, register nbt
            context.getItemInHand().getOrCreateTag().putFloat(NomadBooks.MODID + ":deployed", 1F);
            tags.put("CampPos", NbtUtils.writeBlockPos(pos));

            context.getLevel().playSound(null, Objects.requireNonNull(context.getPlayer()).getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1, 1);
            return InteractionResult.SUCCESS;
        } else {
            this.use(context.getLevel(), Objects.requireNonNull(context.getPlayer()), context.getHand());
            return InteractionResult.FAIL;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level world, Player user, @NotNull InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        CompoundTag tags = itemStack.getOrCreateTagElement(NomadBooks.MODID);
        boolean isDeployed = itemStack.getOrCreateTag().getFloat(NomadBooks.MODID + ":deployed") == 1f;
        BlockPos pos = NbtUtils.readBlockPos(tags.getCompound("CampPos"));
        String structurePath = tags.getString("Structure");
        int height = tags.getInt("Height");
        int width = tags.getInt("Width");

        if (isDeployed) {
            // if sneaking, show camp boundaries, else, pack up
            if (user.isShiftKeyDown()) {
                // switch boundaries display on or off
                if (tags.getBoolean("DisplayBoundaries")) {
                    user.displayClientMessage(new TranslatableComponent("info.nomadbooks.display_boundaries_off"), true);
                    tags.putBoolean("DisplayBoundaries", false);
                } else {
                    user.displayClientMessage(new TranslatableComponent("info.nomadbooks.display_boundaries_on"), true);
                    tags.putBoolean("DisplayBoundaries", true);
                }

                return InteractionResultHolder.pass(itemStack);
            } else {
                Optional<ResourceKey<Level>> dimension = Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tags.get("Dimension")).result();
                int distanceFromCamp = user.blockPosition().distManhattan(pos.offset(new Vec3i(width / 2, 0, width / 2))) - (CAMP_RETRIEVAL_RADIUS + width / 2);
                // if in correct dimension
                if (dimension.isPresent() && (dimension.get() == world.dimension())) {
                    // if the camp is too far
                    if (distanceFromCamp > 0) {
                        int enderPrice = (int) Math.ceil(((double) distanceFromCamp) / 100);

                        // if the player is holding enough ender pearls in his off hand, tp to camp
                        if (user.getOffhandItem().getItem() == Items.ENDER_PEARL && user.getOffhandItem().getCount() >= enderPrice) {
                            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1f, 1f);
                            user.moveTo(pos.getX() + width / 2 + 0.5, pos.getY(), pos.getZ() + width / 2 + 0.5, user.yRotO, user.xRotO);
                            if (!user.isCreative()) {
                                user.getOffhandItem().shrink(enderPrice);
                            }
                            world.playSound(null, pos.getX() + width / 2 + 0.5, pos.getY(), pos.getZ() + width / 2 + 0.5, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1f, 1f);
                        } else {
                            user.displayClientMessage(new TranslatableComponent("error.nomadbooks.camp_too_far"), true);
                        }
                        return InteractionResultHolder.success(itemStack);
                    }
                } else {
                    user.displayClientMessage(new TranslatableComponent("error.nomadbooks.different_dimension"), true);
                    return InteractionResultHolder.success(itemStack);
                }

                // if default structure path, create a new one
                if (structurePath.equals(defaultStructurePath) || structurePath.equals(netherDefaultStructurePath)) {
                    structurePath = NomadBooks.MODID + ":" + user.getUUID() + "/" + System.currentTimeMillis();
                    tags.putString("Structure", structurePath);
                }

                if (!world.isClientSide) {
                    ServerLevel serverLevel = (ServerLevel) world;
                    StructureManager structureManager = serverLevel.getStructureManager();

                    // free beds so they don't redeploy as occupied
                    for (int x = 0; x < width; x++) {
                        for (int z = 0; z < width; z++) {
                            for (int y = 0; y < height; y++) {
                                BlockPos p = pos.offset(new BlockPos(x, y, z));
                                if (world.getBlockState(p).getBlock() instanceof BedBlock) {
                                    System.out.println(world.getBlockState(p));
                                    BlockState bed = world.getBlockState(p);
                                    world.setBlock(p, bed.setValue(BedBlock.OCCUPIED, false), 2);
                                }
                            }
                        }
                    }

                    // save structure
                    StructureTemplate structure;
                    try {
                        structure = structureManager.getOrCreate(new ResourceLocation(structurePath));
                    } catch (ResourceLocationException var8) {
                        return InteractionResultHolder.success(itemStack);
                    }

                    structure.fillFromWorld(world, pos.offset(new BlockPos(0, 0, 0)), new BlockPos(width, height, width), true, Blocks.STRUCTURE_VOID);
                    structure.setAuthor(user.getScoreboardName());
                    structureManager.save(new ResourceLocation(structurePath));

                    // clear block entities
                    for (int x = 0; x < width; x++) {
                        for (int z = 0; z < width; z++) {
                            for (int y = 0; y < height; y++) {
                                BlockPos p = pos.offset(new BlockPos(x, y, z));
                                world.removeBlockEntity(p);
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
                            BlockPos p = pos.offset(new BlockPos(x, y, z));
                            world.setBlock(p, Blocks.AIR.defaultBlockState(), 0b0010000);
                        }
                    }
                }

                // update neighbors
                // now I know, this is against the geneva convention, but updateNeighbors doesn't work, so fuck it
                // only problem is I still have some fucking lighting bugs when reclaiming a camp, but I give up
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < width; z++) {
                        for (int y = 0; y < height; y++) {
                            BlockPos p = pos.offset(new BlockPos(x, y, z));
                            world.setBlock(p, Blocks.BEDROCK.defaultBlockState(), 2);
                        }
                    }
                }
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < width; z++) {
                        for (int y = 0; y < height; y++) {
                            BlockPos p = pos.offset(new BlockPos(x, y, z));
                            world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
                }

                // if membrane upgrade, remove membrane
                if (tags.getList("Upgrades", NbtType.STRING).contains(StringTag.valueOf("aquatic_membrane"))) {
                    for (int x = -1; x < width + 1; x++) {
                        for (int z = -1; z < width + 1; z++) {
                            for (int y = -1; y < height + 1; y++) {
                                BlockPos p = pos.offset(new BlockPos(x, y, z));
                                BlockState bs = world.getBlockState(p);
                                if (bs.getBlock().equals(NomadBooks.MEMBRANE) &&
                                        !((x == -1 && z == -1) || (x == -1 && z == width) || (x == width && z == -1) || (x == width && z == width)
                                                || (y == height && x == -1) || (y == height && x == width) || (y == height && z == -1) || (y == height && z == width)) &&
                                        (x == -1 || x == width || y == -1 || y == height || z == -1 || z == width)) {
                                    removeBlock(world, p);
                                }
                            }
                        }
                    }
                }

                // if mushroom upgrade, remove shroom blocks
                if (tags.getList("Upgrades", NbtType.STRING).contains(StringTag.valueOf("fungi_support"))) {
                    for (int x = 0; x < width; x++) {
                        for (int z = 0; z < width; z++) {
                            BlockPos p = pos.offset(new BlockPos(x, -1, z));
                            BlockState bs = world.getBlockState(p);
                            if (bs.getBlock().equals(NomadBooks.NOMAD_MUSHROOM_BLOCK)) {
                                removeBlock(world, p);
                            }

                            if (x >= width / 2 - 1 && x <= width / 2 + 1 && z >= width / 2 - 1 && z <= width / 2 + 1) {
                                for (int y = -2; y > -6; y--) {
                                    BlockPos p3 = pos.offset(new BlockPos(x, y, z));
                                    if (world.getBlockState(p3).getBlock() instanceof NomadMushroomBlock) {
                                        removeBlock(world, p3);
                                    }
                                }
                            }
                        }
                    }
                }

                // remove boundaries display
                tags.putBoolean("DisplayBoundaries", false);

                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1, 0.9f);
                return InteractionResultHolder.success(itemStack);
            }
        } else {
            return InteractionResultHolder.fail(itemStack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, @NotNull TooltipFlag context) {
        CompoundTag tags = stack.getOrCreateTagElement(NomadBooks.MODID);

        // height, width and upgrades
        int height = tags.getInt("Height");
        int width = tags.getInt("Width");
        tooltip.add(new TranslatableComponent("item.nomadbooks.nomad_book.tooltip.height", height).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        tooltip.add(new TranslatableComponent("item.nomadbooks.nomad_book.tooltip.width", width).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        ListTag upgrades = tags.getList("Upgrades", NbtType.STRING);
        upgrades.forEach(tag -> tooltip.add(new TranslatableComponent("upgrade.nomadbooks."+tag.getAsString()).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA))));

        // if inked, show progress
        if (tags.getBoolean("Inked")) {
            tooltip.add(new TranslatableComponent("item.nomadbooks.nomad_book.tooltip.itinerant_ink", tags.getInt("InkProgress"), tags.getInt("InkGoal")).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
        }
        // camp coordinates if deployed
        if (stack.getOrCreateTag().getFloat(NomadBooks.MODID+":deployed") == 1.0f) {
            BlockPos pos = NbtUtils.readBlockPos(tags.getCompound("CampPos"));


            Optional<ResourceKey<Level>> dimension = Level.RESOURCE_KEY_CODEC.parse(NbtOps.INSTANCE, tags.get("Dimension")).result();

            if (dimension.isPresent() && dimension.get() == world.dimension()) {
                tooltip.add(new TranslatableComponent("item.nomadbooks.nomad_book.tooltip.position", pos.getX()+", "+pos.getY()+", "+pos.getZ()).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)));
            } else {
                tooltip.add(new TranslatableComponent("item.nomadbooks.nomad_book.tooltip.position", pos.getX()+", "+pos.getY()+", "+pos.getZ()).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).applyFormats(ChatFormatting.OBFUSCATED)));
            }
//            tooltip.add(new TranslatableComponent("item.nomadbooks.nomad_book.tooltip.dimension", dimension).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)));
        }
        // displaying boundaries
        if (stack.getItem() instanceof NomadBookItem) {
            if (tags.getBoolean("DisplayBoundaries")) {
                tooltip.add(new TranslatableComponent("item.nomadbooks.nomad_book.tooltip.boundaries_display").setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN).withItalic(true)));
            }
        }
    }

    @Override
    public void fillItemCategory(@NotNull CreativeModeTab group, @NotNull NonNullList<ItemStack> stacks) {
        super.fillItemCategory(group, stacks);
        stacks.forEach(itemStack -> {
            if (itemStack.getItem() == NomadBooks.CREATIVE_NOMAD_BOOK) {
                CompoundTag tags = itemStack.getOrCreateTagElement(NomadBooks.MODID);
                tags.putInt("Height", 15);
                tags.putInt("Width", 15);
                tags.putString("Structure", netherDefaultStructurePath);
                // upgrades
                ListTag upgradeList = new ListTag();
                upgradeList.add(StringTag.valueOf("aquatic_membrane"));
                upgradeList.add(StringTag.valueOf("fungi_support"));
                tags.put("Upgrades", upgradeList);
            } else if (itemStack.getItem() instanceof NomadBookItem) {
                CompoundTag tags = itemStack.getOrCreateTagElement(NomadBooks.MODID);
                tags.putInt("Height", 1);
                tags.putInt("Width", 3);
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
        return blockState.isAir() || m.equals(Material.REPLACEABLE_PLANT) || m.equals(Material.PLANT) || m.equals(Material.TOP_SNOW);
    }

    public static boolean isBlockUnderwaterReplaceable(BlockState blockState) {
        Block b = blockState.getBlock();
        Material m = blockState.getMaterial();
        return b.equals(Blocks.WATER) || m.equals(Material.REPLACEABLE_WATER_PLANT) || m.equals(Material.WATER_PLANT);
    }

    @Override
    public boolean isFireResistant() {
        return super.isFireResistant();
    }

    public void removeBlock(Level world, BlockPos blockPos) {
        world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 2);
    }
}
