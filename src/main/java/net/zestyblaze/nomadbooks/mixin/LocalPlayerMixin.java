package net.zestyblaze.nomadbooks.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.zestyblaze.nomadbooks.NomadBooks;
import net.zestyblaze.nomadbooks.client.NomadBooksClient;
import net.zestyblaze.nomadbooks.item.NomadBookItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void displayBoundaries(CallbackInfo info) {
        if(level.getGameTime() % 10 == 0) {
            for(int i = 0; i < this.getInventory().getContainerSize(); ++i) {
                ItemStack stack = this.getInventory().getItem(i);
                if(stack.getItem() instanceof NomadBookItem) {
                    CompoundTag tags = stack.getOrCreateTagElement(NomadBooks.MODID);
                    if(tags.getBoolean("DisplayBoundaries")) {
                        int height = tags.getInt("Height");
                        int width = tags.getInt("Width");
                        BlockPos pos = NbtUtils.readBlockPos(tags.getCompound("CampPos"));
                        for (int x = 0; x <= width; x++) {
                            for (int z = 0; z <= width; z++) {
                                for (int y = 0; y <= height; y++) {
                                    if (x == 0 && z == 0 || x == 0 && z == width || x == width && z == 0 || x == width && z == width
                                            || y == height && x == 0 || y == height && x == width || y == height && z == 0 || y == height && z == width
                                            || y == 0 && x == 0 || y == 0 && x == width || y == 0 && z == 0 || y == 0 && z == width) {
                                        BlockPos p = pos.offset(new BlockPos(x, y, z));
                                        level.addParticle(NomadBooksClient.CAMP_LIMIT, true, p.getX(), p.getY()+0.02, p.getZ(), 0, 0, 0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
