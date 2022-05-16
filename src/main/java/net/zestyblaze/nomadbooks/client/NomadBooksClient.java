package net.zestyblaze.nomadbooks.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.zestyblaze.nomadbooks.NomadBooks;
import net.zestyblaze.nomadbooks.client.particle.CampfireLimitParticle;

@SuppressWarnings("deprecation")
@Environment(EnvType.CLIENT)
public class NomadBooksClient implements ClientModInitializer {
    public static SimpleParticleType CAMP_LIMIT;

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(NomadBooks.MEMBRANE, RenderType.translucent());

        FabricModelPredicateProviderRegistry.register(new ResourceLocation(NomadBooks.MODID + ":deployed"), ((itemStack, clientLevel, livingEntity, i) -> itemStack.getOrCreateTag().getFloat(NomadBooks.MODID + ":deployed")));

        CAMP_LIMIT = Registry.register(Registry.PARTICLE_TYPE, "nomadbooks:camp_limit", FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(CAMP_LIMIT, CampfireLimitParticle.DefaultFactory::new);
    }
}
