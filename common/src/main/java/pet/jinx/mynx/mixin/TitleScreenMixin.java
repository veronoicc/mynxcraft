package pet.jinx.mynx.mixin;


import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pet.jinx.mynx.ui.screen.MynxServersScreen;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"))
    public void init(CallbackInfo ci) {
        int j = 24;
        int i = this.height / 4 + 48;

        this.addRenderableWidget(Button.builder(Component.literal("Purrlock Servers/Players"), button -> this.minecraft.setScreen(new MynxServersScreen(this))).bounds(this.width / 2 - 124, i + j, 20, 20).build());
        //this.addRenderableWidget(Button.builder().bounds(this.width / 2 + 104, i + j, 20, 20)., Component.literal("Purrlock Streamsniper"), button -> this.minecraft.setScreen(new PurrcraftServersScreen(this))));
    }
}
