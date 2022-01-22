package simplebuffers.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import simplebuffers.SimpleBuffers;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SimpleBuffers.MOD_ID)
public class SimpleBuffersForge {
    public SimpleBuffersForge() {
        // Submit our event bus to let architectury register our content on the right time
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(SimpleBuffers.MOD_ID, modbus);
        SimpleBuffers.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modbus.addListener(SimpleBuffersClientForge::init));
    }
}
