package pet.jinx.mynx.fabric;

import pet.jinx.mynx.fabriclike.MynxFabricLike;
import net.fabricmc.api.ModInitializer;

public class MynxFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MynxFabricLike.init();
    }
}
