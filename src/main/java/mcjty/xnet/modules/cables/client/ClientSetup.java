package mcjty.xnet.modules.cables.client;

import mcjty.xnet.XNet;
import mcjty.xnet.modules.cables.CableModule;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;

public class ClientSetup {
    public static void initClient() {
        ItemBlockRenderTypes.setRenderLayer(CableModule.CONNECTOR.get(), (RenderType) -> true);
        ItemBlockRenderTypes.setRenderLayer(CableModule.ADVANCED_CONNECTOR.get(), (RenderType) -> true);
        MinecraftForge.EVENT_BUS.addListener(CableWorldRenderer::tick);
    }

    public static void modelInit(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(XNet.MODID, "cableloader"), new CableModelLoader());
    }
}
