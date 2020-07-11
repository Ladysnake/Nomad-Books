package ladysnake.nomadbooks.client;

import ladysnake.nomadbooks.NomadBooks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class NomadBooksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(NomadBooks.MEMBRANE, RenderLayer.getTranslucent());

        FabricModelPredicateProviderRegistry.register(new Identifier(NomadBooks.MODID + ":deployed"), (itemStack, world, livingEntity) -> itemStack.getOrCreateTag().getFloat(NomadBooks.MODID + ":deployed"));
    }
}
