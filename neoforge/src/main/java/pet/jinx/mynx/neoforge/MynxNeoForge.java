package pet.jinx.mynx.neoforge;

import pet.jinx.mynx.Mynx;
import net.neoforged.fml.common.Mod;

@Mod(Mynx.MOD_ID)
public class MynxNeoForge {
    public MynxNeoForge() {
        // Submit our event bus to let architectury register our content on the right time
        //Eventbus.registerModEventBus(ExampleMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Mynx.init();
    }
}
