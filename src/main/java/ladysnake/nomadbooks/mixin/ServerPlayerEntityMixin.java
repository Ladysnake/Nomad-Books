package ladysnake.nomadbooks.mixin;

import com.mojang.authlib.GameProfile;
import ladysnake.nomadbooks.NomadBooks;
import ladysnake.nomadbooks.common.item.NomadBookItem;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.text.Style.EMPTY;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Shadow public abstract void sendMessage(Text message, boolean bl);

    @Shadow public abstract void playSound(SoundEvent event, SoundCategory category, float volume, float pitch);

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/advancement/criterion/Criteria;LOCATION:Lnet/minecraft/advancement/criterion/LocationArrivalCriterion;"), method = "playerTick")
    private void enterBiome(CallbackInfo info) {
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack itemStack = this.inventory.getStack(i);
            if (itemStack.getItem() instanceof NomadBookItem) {
                CompoundTag tags = itemStack.getOrCreateSubTag(NomadBooks.MODID);
                // if inventory has an inked nomad book
                if (tags.getBoolean("Inked")) {
                    ListTag visitedBiomes = tags.getList("VisitedBiomes", NbtType.STRING);
                    if (this.world.getBiome(this.getBlockPos()).toString() != null) {
                        StringTag biome = StringTag.of(this.world.getBiome(this.getBlockPos()).toString());
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
                                BlockPos pos = NbtHelper.toBlockPos(tags.getCompound("CampPos")).add(-1, 0, -1);
                                tags.put("CampPos", NbtHelper.fromBlockPos(pos));
                                // show a chat message to the player
                                this.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.1f, 0.75f);
                                this.sendMessage(new TranslatableText("info.nomadbooks.itinerant_ink_done", tags.getInt("Width")).setStyle(EMPTY.withColor(Formatting.BLUE)), false);
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
}