package ladysnake.nomadbooks.client;

import ladysnake.nomadbooks.NomadBooks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class NomadBooksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(NomadBooks.MEMBRANE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(NomadBooks.GREEN_MUSHROOM_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(NomadBooks.GREEN_MUSHROOM_STEM, RenderLayer.getTranslucent());
    }
}
