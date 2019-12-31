package ladysnake.nomadbooks.common.item;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class NomadBookItem extends Item {
    public static final String defaultStructurePath = "nomadbooks:camp1";

    public NomadBookItem(Settings settings) {
        super(settings);
        this.addPropertyGetter(new Identifier("nomadbooks:deployed"), (itemStack, world, livingEntity) -> itemStack.getOrCreateTag().getFloat("nomadbooks:deployed"));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        CompoundTag tags = context.getStack().getOrCreateSubTag("nomadbooks");
        boolean isDeployed = tags.getBoolean("Deployed");
        if (!isDeployed) {
            String structurePath = tags.getString("Structure");
            int level = tags.getInt("Level");

            // set default structure
            if (structurePath.equals("")) {
                tags.putString("Structure", defaultStructurePath);
                structurePath = defaultStructurePath;
            }
            if (level == 0) {
                tags.putInt("Level", 1);
                level = 1;
            }

            // set dimension
            tags.putInt("Dimension", context.getWorld().getDimension().getType().getRawId());

            BlockPos pos = context.getBlockPos().add(new BlockPos(-3, 1, -3));

            // check if there's enough space
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 7; z++) {
                    for (int y = 0; y < (level*3 + level-1); y++) {
                        if (!context.getWorld().getBlockState(pos.add(new BlockPos(x, y, z))).isAir()) {
                            // TODO: Display chat message indicating there's not enough space to set up the camp
                            return ActionResult.FAIL;
                        }
                    }
                }
            }

            // place if there's enough
            if (!context.getWorld().isClient()) {
                ServerWorld serverWorld = (ServerWorld) context.getWorld();
                Structure structure = serverWorld.getStructureManager().getStructure(new Identifier(structurePath));
                StructurePlacementData structurePlacementData = (new StructurePlacementData()).setIgnoreEntities(true).setChunkPosition((ChunkPos) null);
                structure.place(serverWorld, pos, structurePlacementData);

                // set deployed, register nbt
                tags.putBoolean("Deployed", true);
                context.getStack().getOrCreateTag().putFloat("nomadbooks:deployed", 1F);
                tags.put("CampCenter", NbtHelper.fromBlockPos(pos));
            }

            context.getWorld().playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1, 1, true);
            return ActionResult.SUCCESS;
        } else {
            this.use(context.getWorld(), context.getPlayer(), context.getHand());
            return ActionResult.FAIL;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        CompoundTag tags = itemStack.getOrCreateSubTag("nomadbooks");
        boolean isDeployed = tags.getBoolean("Deployed");
        BlockPos pos = NbtHelper.toBlockPos(tags.getCompound("CampCenter"));
        String structurePath = tags.getString("Structure");
        int level = tags.getInt("Level");

        // if structure is in another dimension, error
        if (tags.getInt("Dimension") != world.getDimension().getType().getRawId()) {
            // TODO: Display chat message indicating the camp is in another dimension
            return TypedActionResult.fail(itemStack);
        }

        // if default structure path, create a new one
        if (structurePath.equals(defaultStructurePath)) {
            structurePath = "nomadbooks:"+user.getUuid()+"/"+System.currentTimeMillis();
            tags.putString("Structure", structurePath);
        }

        if (isDeployed) {
            if (!world.isClient) {
                ServerWorld serverWorld = (ServerWorld)world;
                StructureManager structureManager = serverWorld.getStructureManager();

                // save structure
                Structure structure;
                try {
                    structure = structureManager.getStructureOrBlank(new Identifier(structurePath));
                } catch (InvalidIdentifierException var8) {
                    return TypedActionResult.fail(itemStack);
                }

                structure.method_15174(world, pos.add(new BlockPos(0, 0, 0)), new BlockPos(7, 3, 7), true, Blocks.STRUCTURE_VOID);
                structure.setAuthor(user.getEntityName());
                structureManager.saveStructure(new Identifier(structurePath));

                // clear block entities
                for (int x = 0; x < 7; x++) {
                    for (int z = 0; z < 7; z++) {
                        for (int y = 0; y < (level*3 + level-1); y++) {
                            BlockEntity blockEntity = serverWorld.getBlockEntity(pos.add(new BlockPos(x, y, z)));
                            Clearable.clear(blockEntity);
                        }
                    }
                }

                // set undeployed
                tags.putBoolean("Deployed", false);
                itemStack.getOrCreateTag().putFloat("nomadbooks:deployed", 0F);
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1, 1, true);
            }

            // remove blocks
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 7; z++) {
                    for (int y = 0; y < (level*3 + level-1); y++) {
                        world.setBlockState(pos.add(new BlockPos(x, y, z)), Blocks.AIR.getDefaultState(), 16);
                    }
                }
            }

            world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1, 0.9f, true);
            return TypedActionResult.success(itemStack);
        } else {
            return TypedActionResult.fail(itemStack);
        }
    }
}
