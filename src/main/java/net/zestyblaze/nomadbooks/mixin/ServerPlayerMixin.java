package net.zestyblaze.nomadbooks.mixin;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.zestyblaze.nomadbooks.NomadBooks;
import net.zestyblaze.nomadbooks.item.NomadBookItem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    @Shadow public abstract void playNotifySound(@NotNull SoundEvent soundEvent, @NotNull SoundSource soundSource, float f, float g);

    @Shadow public abstract void displayClientMessage(@NotNull Component chatComponent, boolean actionBar);

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "doTick", at = @At(value = "FIELD", target = "Lnet/minecraft/advancements/CriteriaTriggers;LOCATION:Lnet/minecraft/advancements/critereon/LocationTrigger;"))
    private void enterBiome(CallbackInfo info) {
        for(int i = 0; i < this.getInventory().getContainerSize(); ++i) {
            ItemStack itemStack = this.getInventory().getItem(i);
            if(itemStack.getItem() instanceof NomadBookItem) {
                CompoundTag tags = itemStack.getOrCreateTagElement(NomadBooks.MODID);
                // if inventory has an inked nomad book
                if(tags.getBoolean("Inked")) {
                    ListTag visitedBiomes = tags.getList("VisitedBiomes", NbtType.STRING);
                    if(this.level.getBiome(this.blockPosition()).toString() != null) {
                        StringTag biome = StringTag.valueOf(this.level.getBiome(this.blockPosition()).toString());
                        if(!visitedBiomes.contains(biome)) {
                            // if not first biome (just crafted), increment progress
                            if(!visitedBiomes.isEmpty()) {
                                tags.putInt("InkProgress", tags.getInt("InkProgress") + 1);
                            }
                            // remove the bottom of the pile of the excluded biomes
                            if(visitedBiomes.size() > 9) {
                                visitedBiomes.remove(0);
                            }
                            //if goal is reached, upgrade width
                            if(tags.getInt("InkProgress") >= tags.getInt("InkGoal")) {
                                tags.putBoolean("Inked", false);
                                tags.remove("InkProgress");
                                tags.remove("InkGoal");
                                tags.remove("VisitedBiomes");
                                tags.putInt("Width", tags.getInt("Width") + 2);
                                // if camp is deployed, move the camp pos
                                BlockPos pos = NbtUtils.readBlockPos(tags.getCompound("CampPos")).offset(-1, 0, -1);
                                tags.put("CampPos", NbtUtils.writeBlockPos(pos));
                                // show a chat message to the player
                                this.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.1f, 0.75f);
                                this.displayClientMessage(new TranslatableComponent("info.nomadbooks.itinerant_ink_done", tags.getInt("Width")).setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)), false);
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
}
