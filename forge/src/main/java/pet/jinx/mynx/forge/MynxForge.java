package pet.jinx.mynx.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraft.client.User;
import pet.jinx.mynx.Mynx;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Mynx.MOD_ID)
public class MynxForge {
    public MynxForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Mynx.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        Mynx.init();
    }
}
