package ladysnake.nomadbooks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TeleporterBlock extends Block {
    public TeleporterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockPos pp = player.getBlockPos();
        int y = 20;
        while (!world.getBlockState(pp.add(new BlockPos(0, y, 0))).getBlock().equals(Blocks.PURPUR_BLOCK) && y < 200) {
            y++;
        }
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1, true);
        world.playSound(pp.getX(), pp.getY(), pp.getZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1, true);
        player.teleport(pp.getX(), pp.getY()+y+2, pp.getZ(), true);
        return ActionResult.SUCCESS;
    }
}
