package simplebuffers.fabric;

import simplebuffers.SimpleBuffers;
import net.fabricmc.api.ModInitializer;

public class SimpleBuffersFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SimpleBuffers.init();
    }
}
