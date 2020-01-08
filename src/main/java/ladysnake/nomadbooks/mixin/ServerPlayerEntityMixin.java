package ladysnake.nomadbooks.mixin;

import com.mojang.authlib.GameProfile;
import ladysnake.nomadbooks.NomadBooks;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/advancement/criterion/Criterions;LOCATION:Lnet/minecraft/advancement/criterion/LocationArrivalCriterion;"), method = "playerTick")
    private void enterBiome(CallbackInfo info) {
        for(int i = 0; i < this.inventory.getInvSize(); ++i) {
            ItemStack itemStack = this.inventory.getInvStack(i);
            if (itemStack.getItem().equals(NomadBooks.NOMAD_BOOK)) {
                CompoundTag tags = itemStack.getOrCreateSubTag(NomadBooks.MODID);
                if (tags.getBoolean("Inked")) {
                    ListTag visitedBiomes = tags.getList("VisitedBiomes", NbtType.STRING);
                    StringTag biome = StringTag.of(this.world.getBiome(this.getBlockPos()).getName().getString());
                    if (!visitedBiomes.contains(biome)) {
                        if (visitedBiomes.size() > 9) {
                            visitedBiomes.remove(0);
                        }
                        visitedBiomes.add(biome);
                        tags.put("VisitedBiomes", visitedBiomes);
                    }
                }
            }
        }
    }
}