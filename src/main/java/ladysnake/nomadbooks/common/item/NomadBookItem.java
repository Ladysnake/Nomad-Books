package ladysnake.nomadbooks.common.item;

import ladysnake.nomadbooks.NomadBooks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
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
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class NomadBookItem extends Item {
    public static final String defaultStructurePath = NomadBooks.MODID + ":camp1";

    public NomadBookItem(Settings settings) {
        super(settings);
        this.addPropertyGetter(new Identifier(NomadBooks.MODID + ":deployed"), (itemStack, world, livingEntity) -> itemStack.getOrCreateTag().getFloat(NomadBooks.MODID + ":deployed"));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        Material mat = context.getWorld().getBlockState(pos).getMaterial();
        while (mat.equals(Material.REPLACEABLE_PLANT) || mat.equals(Material.SNOW)) {
            pos = new BlockPos(pos.getX(), pos.getY()-1, pos.getZ());
            mat = context.getWorld().getBlockState(pos).getMaterial();
        }

        CompoundTag tags = context.getStack().getOrCreateSubTag(NomadBooks.MODID);
        boolean isDeployed = context.getStack().getOrCreateTag().getFloat(NomadBooks.MODID + ":deployed") == 1f;
        if (!isDeployed) {
            String structurePath = tags.getString("Structure");
            int pages = tags.getInt("Pages");

            // set default structure
            if (structurePath.equals("")) {
                tags.putString("Structure", defaultStructurePath);
                structurePath = defaultStructurePath;
            }
            if (pages == 0) {
                if (context.getStack().getItem().equals(NomadBooks.NOMAD_BOOK)) {
                    tags.putInt("Pages", 3);
                    pages = 3;
                } else if (context.getStack().getItem().equals(NomadBooks.NOMAD_PAGE)) {
                    tags.putInt("Pages", 1);
                    pages = 1;
                }
            }

            // set dimension
            tags.putInt("Dimension", context.getWorld().getDimension().getType().getRawId());

            pos = pos.add(new BlockPos(-3, 1, -3));

            // check if there's enough space
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 7; z++) {
                    for (int y = 0; y < pages; y++) {
                        BlockPos p = pos.add(new BlockPos(x, y, z));
                        BlockState bs = context.getWorld().getBlockState(p);
                        if (!(bs.isAir() || bs.getMaterial().equals(Material.REPLACEABLE_PLANT) || bs.getMaterial().equals(Material.SNOW))) {
                            // TODO: Display chat message indicating there's not enough space to set up the camp

                            return ActionResult.FAIL;
                        }
                    }
                }
            }

            // destroy destroyable blocks in the way
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 7; z++) {
                    for (int y = 0; y < pages; y++) {
                        context.getWorld().breakBlock(pos.add(new BlockPos(x, y, z)), true);
                    }
                }
            }

            // fill with membrane
//            for (int x = -1; x < 8; x++) {
//                for (int z = -1; z < 8; z++) {
//                    for (int y = 0; y < pages+1; y++) {
//                        BlockPos p = pos.add(new BlockPos(x, y, z));
//                        BlockState bs = context.getWorld().getBlockState(p);
//                        if ((bs.getBlock() instanceof FluidBlock || bs.getMaterial().equals(Material.SEAGRASS) || bs.getMaterial().equals(Material.UNDERWATER_PLANT)) &&
//                                !((x == -1 && z == -1) || (x == -1 && z == 7) || (x == 7 && z == -1) || (x == 7 && z == 7)
//                                || (y == pages && x == -1) || (y == pages && x == 7) || (y == pages && z == -1) || (y == pages && z == 7))) {
//                            context.getWorld().breakBlock(p, true);
//                            context.getWorld().setBlockState(p, NomadBooks.MEMBRANE.getDefaultState(), 1);
//                        }
//                    }
//                }
//            }

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
        int pages = tags.getInt("Pages");

        // if structure is in another dimension, error
        if (tags.getInt("Dimension") != world.getDimension().getType().getRawId()) {
            // TODO: Display chat message indicating the camp is in another dimension
            return TypedActionResult.fail(itemStack);
        }

        // if default structure path, create a new one
        if (structurePath.equals(defaultStructurePath)) {
            structurePath = NomadBooks.MODID + ":"+user.getUuid()+"/"+System.currentTimeMillis();
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

                structure.method_15174(world, pos.add(new BlockPos(0, 0, 0)), new BlockPos(7, pages, 7), true, Blocks.STRUCTURE_VOID);
                structure.setAuthor(user.getEntityName());
                structureManager.saveStructure(new Identifier(structurePath));

                // clear block entities
                for (int x = 0; x < 7; x++) {
                    for (int z = 0; z < 7; z++) {
                        for (int y = 0; y < pages; y++) {
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
            for (int x = 0; x < 7; x++) {
                for (int z = 0; z < 7; z++) {
                    for (int y = 0; y < pages; y++) {
                        BlockPos p = pos.add(new BlockPos(x, y, z));
                        world.breakBlock(p, false);
                    }
                }
            }

            if (!world.isClient()) {
                // remove blocks dropped by accident
                BlockPos p2 = pos.add(new BlockPos(7, pages, 7));
                List<ItemEntity> itemEntities = world.getEntities(EntityType.ITEM, new Box(pos.getX(), pos.getY(), pos.getZ(), p2.getX(), p2.getY(), p2.getZ()), new Predicate<ItemEntity>() {
                    @Override
                    public boolean test(ItemEntity itemEntity) {
                        return itemEntity.getAge() < 1;
                    }
                });
                itemEntities.forEach(ItemEntity::remove);
            }

            world.playSound(user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1, 0.9f, true);
            return TypedActionResult.success(itemStack);
        } else {
            return TypedActionResult.fail(itemStack);
        }
    }

    public void setPageAmount(ItemStack itemStack, int pageAmount) {
        itemStack.getOrCreateSubTag(NomadBooks.MODID).putInt("Pages", pageAmount);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.getItem().equals(NomadBooks.NOMAD_BOOK)) {
            int pages = stack.getOrCreateSubTag(NomadBooks.MODID).getInt("Pages");
            if (pages == 0) {
                pages = 3;
            }
            tooltip.add(new LiteralText("Pages: " + pages));
        }
    }
}
