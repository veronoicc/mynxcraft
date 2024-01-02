package pet.jinx.mynx.ui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MynxFindServersScreen extends Screen {
    private final Screen parent;
    protected MynxFindServersScreen(Screen parent) {
        super(Component.literal("MYNX - Find Servers"));
        this.parent = parent;
    }
}
