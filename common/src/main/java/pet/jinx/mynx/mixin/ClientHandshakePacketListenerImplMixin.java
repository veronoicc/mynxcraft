package pet.jinx.mynx.mixin;

import com.mojang.authlib.exceptions.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pet.jinx.mynx.Mynx;
import pet.jinx.mynx.http.request.session.SessionJoinRequest;
import pet.jinx.mynx.http.request.session.SessionNewRequest;

import java.net.MalformedURLException;

@Mixin(ClientHandshakePacketListenerImpl.class)
public abstract class ClientHandshakePacketListenerImplMixin {
    @Inject(method = "authenticateServer", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/minecraft/MinecraftSessionService;joinServer(Ljava/util/UUID;Ljava/lang/String;Ljava/lang/String;)V"), cancellable = true)
    private void authenticateServer(String serverHash, CallbackInfoReturnable<Component> cir) {
        Mynx.getLOGGER().info("Username: " + Mynx.getMINECRAFT().getUser().getName() + " | UUID: " + Mynx.getMINECRAFT().getUser().getProfileId() + " | Token: " + Mynx.getMINECRAFT().getUser().getAccessToken());
        if (Mynx.getMINECRAFT().getUser().getAccessToken().startsWith("§MYNX§")) {
            var request = SessionJoinRequest.builder().hash(serverHash).continuation(Mynx.getMINECRAFT().getUser().getAccessToken().replace("§MYNX§", ""));
            Mynx.getLOGGER().info(request.toString());
            var response = request.build().doRequest(Mynx.getMYNX_CLIENT());
            switch (response.getStatus()) {
                case SUCCESS -> cir.setReturnValue(null);
                case FAIL -> {
                    assert response.getFail() != null;
                    cir.setReturnValue(Component.translatable("disconnect.loginFailedInfo", Component.literal(response.getFail().getMessage() + " (FAIL)")));
                }
                case ERROR -> {
                    assert response.getError() != null;
                    cir.setReturnValue(Component.translatable("disconnect.loginFailedInfo", Component.literal(response.getError().getMessage() + " (ERROR)")));
                }
            }
        }
    }
}