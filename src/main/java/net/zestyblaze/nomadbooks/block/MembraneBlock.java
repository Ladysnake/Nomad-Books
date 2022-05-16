package net.zestyblaze.nomadbooks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MembraneBlock extends StainedGlassBlock {
    public MembraneBlock(Properties properties) {
        super(DyeColor.PURPLE, properties);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (entity instanceof Projectile) {
            entity.setDeltaMovement(entity.getDeltaMovement().x()/2, entity.getDeltaMovement().y()/2, entity.getDeltaMovement().z()/2);
        }
        if (world.getGameTime() % 15 == 0 && entity.getType() != EntityType.ITEM) {
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.HONEY_BLOCK_SLIDE, SoundSource.BLOCKS, 1f, 1f);
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
    }
}
