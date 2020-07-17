package ladysnake.nomadbooks.client;

import ladysnake.nomadbooks.NomadBooks;
import ladysnake.nomadbooks.client.particle.CampLimitParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NomadBooksClient implements ClientModInitializer {
    public static DefaultParticleType CAMP_LIMIT;

    @Override
    public void onInitializeClient() {
        // block renders
        BlockRenderLayerMap.INSTANCE.putBlock(NomadBooks.MEMBRANE, RenderLayer.getTranslucent());

        // model predicates
        FabricModelPredicateProviderRegistry.register(new Identifier(NomadBooks.MODID + ":deployed"), (itemStack, world, livingEntity) -> itemStack.getOrCreateTag().getFloat(NomadBooks.MODID + ":deployed"));

        // particles
        CAMP_LIMIT = Registry.register(Registry.PARTICLE_TYPE, "nomadbooks:camp_limit", FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(CAMP_LIMIT, CampLimitParticle.DefaultFactory::new);
    }
}
