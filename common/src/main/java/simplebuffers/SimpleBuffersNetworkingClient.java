package simplebuffers;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

@Environment(EnvType.CLIENT)
public class SimpleBuffersNetworkingClient {
    @ExpectPlatform
    public static void send(String id, SimpleBuffersNetworkingServer.BlockConfigUpdateMsg msg) {
        throw new AssertionError();
    }
}
