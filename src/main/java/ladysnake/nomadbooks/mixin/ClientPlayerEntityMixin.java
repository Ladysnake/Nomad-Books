package ladysnake.nomadbooks.mixin;

import com.mojang.authlib.GameProfile;
import ladysnake.nomadbooks.NomadBooks;
import ladysnake.nomadbooks.common.item.NomadBookItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(at = @At(value = "HEAD"), method = "tick")
    private void displayBoundaries(CallbackInfo info) {
        if (world.getTime() % 10 == 0) {
            for (int i = 0; i < this.inventory.getInvSize(); ++i) {
                ItemStack itemStack = this.inventory.getInvStack(i);
                if (itemStack.getItem() instanceof NomadBookItem) {
                    CompoundTag tags = itemStack.getOrCreateSubTag(NomadBooks.MODID);
                    if (tags.getBoolean("DisplayBoundaries")) {
                        int height = tags.getInt("Height");
                        int width = tags.getInt("Width");
                        BlockPos pos = NbtHelper.toBlockPos(tags.getCompound("CampPos"));
                        for (int x = 0; x <= width; x++) {
                            for (int z = 0; z <= width; z++) {
                                for (int y = 0; y <= height; y++) {
                                    if (x == 0 && z == 0 || x == 0 && z == width || x == width && z == 0 || x == width && z == width
                                            || y == height && x == 0 || y == height && x == width || y == height && z == 0 || y == height && z == width
                                            || y == 0 && x == 0 || y == 0 && x == width || y == 0 && z == 0 || y == 0 && z == width) {
                                        BlockPos p = pos.add(new BlockPos(x, y, z));
                                        world.addParticle(ParticleTypes.HAPPY_VILLAGER, true, p.getX(), p.getY()+0.02, p.getZ(), 0, 0, 0);
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
