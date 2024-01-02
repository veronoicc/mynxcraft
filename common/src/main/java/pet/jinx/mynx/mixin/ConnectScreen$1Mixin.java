package pet.jinx.mynx.mixin;

import net.minecraft.client.User;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pet.jinx.mynx.Mynx;
import pet.jinx.mynx.http.request.session.SessionNewRequest;

import java.util.Optional;

@Mixin(targets = "net/minecraft/client/gui/screens/ConnectScreen$1")
public abstract class ConnectScreen$1Mixin {
    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;initiateServerboundPlayConnection(Ljava/lang/String;ILnet/minecraft/network/protocol/login/ClientLoginPacketListener;)V", shift = At.Shift.AFTER))
    public void run(CallbackInfo ci) {
        var request = SessionNewRequest.builder().build();
        var response = request.doRequest(Mynx.getMYNX_CLIENT()).getSuccess();
        ((MinecraftAccessor) Mynx.getMINECRAFT()).setUser(new User(response.username(), response.uuid(), "§MYNX§" + response.continuation(), Optional.empty(), Optional.empty(), User.Type.MOJANG));
        ((MinecraftAccessor) Mynx.getMINECRAFT()).setProfileKeyPairManager(ProfileKeyPairManager.EMPTY_KEY_MANAGER);
    }
}
