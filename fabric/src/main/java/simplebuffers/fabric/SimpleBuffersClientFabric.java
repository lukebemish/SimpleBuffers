package simplebuffers.fabric;

import net.fabricmc.api.ClientModInitializer;
import simplebuffers.SimpleBuffersClient;

public class SimpleBuffersClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SimpleBuffersClient.init();
    }
}
