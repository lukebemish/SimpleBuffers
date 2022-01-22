package simplebuffers.fabric;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import simplebuffers.SimpleBuffers;
import net.fabricmc.api.ModInitializer;

public class SimpleBuffersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SimpleBuffers.init();
    }
}
