package pet.jinx.mynx.ui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MynxFindPlayersScreen extends Screen {
    private final Screen parent;
    protected MynxFindPlayersScreen(Screen parent) {
        super(Component.literal("MYNX - Find Players"));
        this.parent = parent;
    }
}
