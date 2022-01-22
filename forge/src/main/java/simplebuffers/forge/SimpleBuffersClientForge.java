package simplebuffers.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import simplebuffers.SimpleBuffers;
import simplebuffers.SimpleBuffersClient;

@Mod.EventBusSubscriber(modid = SimpleBuffers.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SimpleBuffersClientForge {
    public static void init(final FMLClientSetupEvent event) {
        SimpleBuffersClient.init();
    }
}
