package pet.jinx.mynx.ui.screen;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import pet.jinx.mynx.ui.widget.MynxServerSelectionList;

import java.util.List;

public class MynxServersScreen extends Screen {
    private final Screen parent;

    private Button joinButton;
    private Button deleteButton;
    private Button bookmarkButton;
    private Button clearButton;

    private boolean deleteSure = false;
    private boolean clearSure = false;

    @Nullable
    private List<Component> toolTip;

    @Getter
    private final ServerStatusPinger pinger = new ServerStatusPinger();
    public MynxServerSelectionList serverSelectionList;
    @Getter
    private final List<ServerData> servers = Lists.newArrayList();
    private boolean inited;

    public MynxServersScreen(Screen parent) {
        super(Component.literal("Find Servers/Players"));
        this.parent = parent;
    }

    @Override
    protected void init() {

        if (this.inited) {
            this.serverSelectionList.setRectangle(this.width, this.height, 32, this.height - 64);
        } else {
            this.inited = true;
            this.serverSelectionList = new MynxServerSelectionList(this, this.minecraft, this.width, this.height - 64 - 32, 32, 36);
            this.serverSelectionList.updateServers(servers);
        }
        this.addWidget(this.serverSelectionList);

        GridLayout gridLayout = new GridLayout();
        GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(1);

        LinearLayout linearLayoutTop = rowHelper.addChild(new LinearLayout(308, 20, LinearLayout.Orientation.HORIZONTAL));
        Button findServersButton = this.addRenderableWidget(Button.builder(Component.literal("Find Server(s)"), button -> this.minecraft.setScreen(new MynxFindServersScreen(this))).width(100).build());
        linearLayoutTop.addChild(findServersButton);
        this.joinButton = this.addRenderableWidget(Button.builder(Component.literal("Join Server"),
                button -> {
                    var entry = this.serverSelectionList.getSelected();
                    this.join(entry.getServerData());
                }).width(100).tooltip(Tooltip.create(Component.literal("Right click to join using a random account."))).build());
        linearLayoutTop.addChild(this.joinButton);
        Button findPlayersButton = this.addRenderableWidget(Button.builder(Component.literal("Find Player(s)"), button -> this.minecraft.setScreen(new MynxFindPlayersScreen(this))).width(100).build());
        linearLayoutTop.addChild(findPlayersButton);

        rowHelper.addChild(SpacerElement.height(4));

        LinearLayout linearLayoutBottom = rowHelper.addChild(new LinearLayout(308, 20, LinearLayout.Orientation.HORIZONTAL));
        Button refreshButton = this.addRenderableWidget(Button.builder(Component.literal("Refresh"), button -> {
            this.serverSelectionList.updateServers(servers);
        }).width(60).build());
        linearLayoutBottom.addChild(refreshButton);
        this.deleteButton = this.addRenderableWidget(Button.builder(Component.literal("Delete"), button -> {
            if (!deleteSure) {
                button.setMessage(Component.literal("You sure?"));
                this.deleteSure = true;
            } else {
                servers.remove(this.serverSelectionList.getSelected().getServerData());
                this.serverSelectionList.setSelected(null);
                this.serverSelectionList.updateServers(servers);
                button.setMessage(Component.literal("Delete"));
                this.deleteSure = false;
            }
        }).width(60).build());
        linearLayoutBottom.addChild(this.deleteButton);
        this.bookmarkButton = this.addRenderableWidget(Button.builder(Component.literal("Bookmark"), button -> {

        }).width(60).build());
        linearLayoutBottom.addChild(this.bookmarkButton);
        this.clearButton = this.addRenderableWidget(Button.builder(Component.literal("Clear"), button -> {
            if (!clearSure) {
                button.setMessage(Component.literal("You sure?"));
                this.clearSure = true;
            } else {
                servers.clear();
                this.serverSelectionList.setSelected(null);
                this.serverSelectionList.updateServers(servers);
                button.setMessage(Component.literal("Clear"));
                this.clearSure = false;
            }
        }).width(60).build());
        linearLayoutBottom.addChild(this.clearButton);
        Button cancelButton = this.addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> this.minecraft.setScreen(this.parent)).width(60).build());
        linearLayoutBottom.addChild(cancelButton);

        gridLayout.arrangeElements();
        FrameLayout.centerInRectangle(gridLayout, 0, this.height - 64, this.width, 64);
        this.onSelectedChange();
    }

    @Override
    public void tick() {
        super.tick();
        this.pinger.tick();
    }

    @Override
    public void removed() {
        this.pinger.removeAll();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        this.toolTip = null;
        this.renderBackground(guiGraphics, i, j, f);
        this.serverSelectionList.render(guiGraphics, i, j, f);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, i, j, f);
        if (this.toolTip != null) {
            guiGraphics.renderComponentTooltip(this.font, this.toolTip, i, j);
        }
    }

    public void setSelected(MynxServerSelectionList.ServerEntry entry) {
        this.serverSelectionList.setSelected(entry);
        this.onSelectedChange();
    }


    public void onSelectedChange() {
        this.joinButton.active = false;
        this.deleteButton.active = false;
        this.bookmarkButton.active = false;
        this.clearButton.active = false;
        MynxServerSelectionList.ServerEntry entry = this.serverSelectionList.getSelected();
        if (entry != null) {
            this.joinButton.active = true;
            this.deleteButton.active = true;
            this.bookmarkButton.active = true;
        }
        if (!servers.isEmpty()) {
            this.clearButton.active = true;
        }
    }

    public void setToolTip(@Nullable List<Component> list) {
        this.toolTip = list;
    }

    private void join(ServerData serverData) {
        ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString(serverData.ip), serverData, false);
    }
}
