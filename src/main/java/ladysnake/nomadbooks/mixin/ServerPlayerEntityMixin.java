package ladysnake.nomadbooks.mixin;

import com.mojang.authlib.GameProfile;
import ladysnake.nomadbooks.NomadBooks;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow public abstract void addChatMessage(Text message, boolean bl);

    public ServerPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/advancement/criterion/Criterions;LOCATION:Lnet/minecraft/advancement/criterion/LocationArrivalCriterion;"), method = "method_14226")
    private void enterBiome(CallbackInfo info) {
        for (int i = 0; i < this.inventory.getInvSize(); ++i) {
            ItemStack itemStack = this.inventory.getInvStack(i);
            if (itemStack.getItem().equals(NomadBooks.NOMAD_BOOK)) {
                CompoundTag tags = itemStack.getOrCreateSubTag(NomadBooks.MODID);
                // if inventory has an inked nomad book
                if (tags.getBoolean("Inked")) {
                    ListTag visitedBiomes = tags.getList("VisitedBiomes", NbtType.STRING);
                    StringTag biome = new StringTag(this.world.getBiome(this.getBlockPos()).getName().getString());
                    if (!visitedBiomes.contains(biome)) {
                        // if not the first biome (just crafted), increment progress
                        if (!visitedBiomes.isEmpty()) {
                            tags.putInt("InkProgress", tags.getInt("InkProgress") + 1);
                        }
                        // remove the bottom of the pile of the excluded biomes
                        if (visitedBiomes.size() > 9) {
                            visitedBiomes.remove(0);
                        }
                        // if goal is reached, upgrade width
                        if (tags.getInt("InkProgress") >= tags.getInt("InkGoal")) {
                            tags.putBoolean("Inked", false);
                            tags.remove("InkProgress");
                            tags.remove("InkGoal");
                            tags.remove("VisitedBiomes");
                            tags.putInt("Width", tags.getInt("Width") + 2);
                            // if camp is deployed, move the camp pos
                            BlockPos pos = TagHelper.deserializeBlockPos(tags.getCompound("CampPos")).add(-1, 0, -1);
                            tags.put("CampPos", TagHelper.serializeBlockPos(pos));
                            // show a chat message to the player
                            this.addChatMessage(new TranslatableText("info.nomadbooks.itinerant_ink_done", tags.getInt("Width")).formatted(Formatting.BLUE), false);
                            // sound effect not working because server side
                            // this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1, 1, true);
                        } else {
                            visitedBiomes.add(biome);
                            tags.put("VisitedBiomes", visitedBiomes);
                        }
                    }
                }
            }
        }
    }
}