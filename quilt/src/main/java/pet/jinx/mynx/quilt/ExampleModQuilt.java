package pet.jinx.mynx.quilt;

import pet.jinx.mynx.fabriclike.MynxFabricLike;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class ExampleModQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        MynxFabricLike.init();
    }
}
