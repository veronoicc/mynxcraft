package pet.jinx.mynx.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Mutable
    @Accessor("user")
    void setUser(User user);

    @Mutable
    @Accessor("profileKeyPairManager")
    void setProfileKeyPairManager(ProfileKeyPairManager profileKeyPairManager);
}
