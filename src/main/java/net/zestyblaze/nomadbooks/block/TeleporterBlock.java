package net.zestyblaze.nomadbooks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TeleporterBlock extends Block {
    public TeleporterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockPos pp = player.blockPosition();
        int y = 20;
        while (!world.getBlockState(pp.offset(new BlockPos(0, y, 0))).getBlock().equals(Blocks.PURPUR_BLOCK) && y < 200) {
            y++;
        }
        world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1, 1);
        world.playSound(player, pp.getX(), pp.getY(), pp.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1, 1);
        player.teleportTo(pp.getX(), pp.getY()+y+2, pp.getZ());
        return InteractionResult.SUCCESS;
    }
}
