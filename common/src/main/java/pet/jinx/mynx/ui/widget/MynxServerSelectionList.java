package pet.jinx.mynx.ui.widget;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import pet.jinx.mynx.ui.screen.MynxServersScreen;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class MynxServerSelectionList extends ObjectSelectionList<MynxServerSelectionList.ServerEntry> {
    static final Logger LOGGER = LogUtils.getLogger();
    static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(25, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build());
    private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
    static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/server_selection.png");
    static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
    static final Component CANT_RESOLVE_TEXT = Component.translatable("multiplayer.status.cannot_resolve").withStyle(style -> style.withColor(-65536));
    static final Component CANT_CONNECT_TEXT = Component.translatable("multiplayer.status.cannot_connect").withStyle(style -> style.withColor(-65536));
    static final Component INCOMPATIBLE_STATUS = Component.translatable("multiplayer.status.incompatible");
    static final Component NO_CONNECTION_STATUS = Component.translatable("multiplayer.status.no_connection");
    static final Component PINGING_STATUS = Component.translatable("multiplayer.status.pinging");
    static final Component ONLINE_STATUS = Component.translatable("multiplayer.status.online");
    private final MynxServersScreen screen;
    private final List<ServerEntry> servers = Lists.newArrayList();

    public MynxServerSelectionList(MynxServersScreen joinMultiplayerScreen, Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
        this.screen = joinMultiplayerScreen;
    }

    private void refreshEntries() {
        this.clearEntries();
        this.servers.forEach(this::addEntry);
    }

    @Override
    public void setSelected(@Nullable MynxServerSelectionList.ServerEntry entry) {
        super.setSelected(entry);
        this.screen.onSelectedChange();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        ServerEntry entry = this.getSelected();
        return entry != null && entry.keyPressed(i, j, k) || super.keyPressed(i, j, k);
    }

    public void updateServers(List<ServerData> servers) {
        this.servers.clear();
        for (ServerData server : servers) {
            this.servers.add(new ServerEntry(this.screen, server));
        }
        this.refreshEntries();
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 30;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }

    @Environment(value=EnvType.CLIENT)
    public class ServerEntry extends Entry<ServerEntry> implements AutoCloseable {
        private final MynxServersScreen screen;
        private final Minecraft minecraft;
        private final ServerData serverData;
        private final FaviconTexture icon;
        private byte[] lastIconBytes;
        private long lastClickTime;

        protected ServerEntry(MynxServersScreen joinMultiplayerScreen, ServerData serverData) {
            this.screen = joinMultiplayerScreen;
            this.serverData = serverData;
            this.minecraft = Minecraft.getInstance();
            this.icon = FaviconTexture.forServer(this.minecraft.getTextureManager(), serverData.ip);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            List<Component> list2;
            Component component2;
            int s;
            if (!this.serverData.pinged) {
                this.serverData.pinged = true;
                this.serverData.ping = -2L;
                this.serverData.motd = CommonComponents.EMPTY;
                this.serverData.status = CommonComponents.EMPTY;
                THREAD_POOL.submit(() -> {
                    try {
                        this.screen.getPinger().pingServer(this.serverData, () -> {});
                    } catch (UnknownHostException unknownHostException) {
                        this.serverData.ping = -1L;
                        this.serverData.motd = CANT_RESOLVE_TEXT;
                    } catch (Exception exception) {
                        this.serverData.ping = -1L;
                        this.serverData.motd = CANT_CONNECT_TEXT;
                    }
                });
            }
            boolean bl2 = this.isIncompatible();
            guiGraphics.drawString(this.minecraft.font, this.serverData.name, k + 32 + 3, j + 1, 0xFFFFFF, false);
            List<FormattedCharSequence> list = this.minecraft.font.split(this.serverData.motd, l - 32 - 2);
            for (int p = 0; p < Math.min(list.size(), 2); ++p) {
                guiGraphics.drawString(this.minecraft.font, list.get(p), k + 32 + 3, j + 12 + this.minecraft.font.lineHeight * p, 0x808080, false);
            }
            Component component = bl2 ? this.serverData.version.copy().withStyle(ChatFormatting.RED) : this.serverData.status;
            int q = this.minecraft.font.width(component);
            guiGraphics.drawString(this.minecraft.font, component, k + l - q - 15 - 2, j + 1, 0x808080, false);
            int r = 0;
            if (bl2) {
                s = 5;
                component2 = INCOMPATIBLE_STATUS;
                list2 = this.serverData.playerList;
            } else if (this.pingCompleted()) {
                s = this.serverData.ping < 0L ? 5 : (this.serverData.ping < 150L ? 0 : (this.serverData.ping < 300L ? 1 : (this.serverData.ping < 600L ? 2 : (this.serverData.ping < 1000L ? 3 : 4))));
                if (this.serverData.ping < 0L) {
                    component2 = NO_CONNECTION_STATUS;
                    list2 = Collections.emptyList();
                } else {
                    component2 = Component.translatable("multiplayer.status.ping", this.serverData.ping);
                    list2 = this.serverData.playerList;
                }
            } else {
                r = 1;
                s = (int)(Util.getMillis() / 100L + (i * 2L) & 7L);
                if (s > 4) {
                    s = 8 - s;
                }
                component2 = PINGING_STATUS;
                list2 = Collections.emptyList();
            }
            guiGraphics.blit(GUI_ICONS_LOCATION, k + l - 15, j, r * 10, 176 + s * 8, 10, 8, 256, 256);
            byte[] bs = this.serverData.getIconBytes();
            if (!Arrays.equals(bs, this.lastIconBytes)) {
                if (this.uploadServerIcon(bs)) {
                    this.lastIconBytes = bs;
                } else {
                    this.serverData.setIconBytes(null);
                }
            }
            this.drawIcon(guiGraphics, k, j, this.icon.textureLocation());
            int t = n - k;
            int u = o - j;
            if (t >= l - 15 && t <= l - 5 && u >= 0 && u <= 8) {
                this.screen.setToolTip(Collections.singletonList(component2));
            } else if (t >= l - q - 15 - 2 && t <= l - 15 - 2 && u >= 0 && u <= 8) {
                this.screen.setToolTip(list2);
            }
            if (this.minecraft.options.touchscreen().get() || bl) {
                guiGraphics.fill(k, j, k + 32, j + 32, -1601138544);
                int v = n - k;
                int w = o - j;
                if (this.canJoin()) {
                    if (v < 32 && v > 16) {
                        guiGraphics.blit(ICON_OVERLAY_LOCATION, k, j, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        guiGraphics.blit(ICON_OVERLAY_LOCATION, k, j, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (i > 0) {
                    if (v < 16 && w < 16) {
                        guiGraphics.blit(ICON_OVERLAY_LOCATION, k, j, 96.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        guiGraphics.blit(ICON_OVERLAY_LOCATION, k, j, 96.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (i < this.screen.getServers().size() - 1) {
                    if (v < 16 && w > 16) {
                        guiGraphics.blit(ICON_OVERLAY_LOCATION, k, j, 64.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        guiGraphics.blit(ICON_OVERLAY_LOCATION, k, j, 64.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
            }
        }

        private boolean pingCompleted() {
            return this.serverData.pinged && this.serverData.ping != -2L;
        }

        private boolean isIncompatible() {
            return this.serverData.protocol != SharedConstants.getCurrentVersion().getProtocolVersion();
        }

        protected void drawIcon(GuiGraphics guiGraphics, int i, int j, ResourceLocation resourceLocation) {
            RenderSystem.enableBlend();
            guiGraphics.blit(resourceLocation, i, j, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
        }

        private boolean canJoin() {
            return true;
        }

        private boolean uploadServerIcon(byte @Nullable [] bs) {
            if (bs == null) {
                this.icon.clear();
            } else {
                try {
                    this.icon.upload(NativeImage.read(bs));
                } catch (Throwable throwable) {
                    LOGGER.error("Invalid icon for server {} ({})", this.serverData.name, this.serverData.ip, throwable);
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean keyPressed(int i, int j, int k) {
            if (Screen.hasShiftDown()) {
                MynxServerSelectionList serverSelectionList = this.screen.serverSelectionList;
                int l = serverSelectionList.children().indexOf(this);
                if (l == -1) {
                    return true;
                }
                if (i == 264 && l < this.screen.getServers().size() - 1 || i == 265 && l > 0) {
                    this.swap(l, i == 264 ? l + 1 : l - 1);
                    return true;
                }
            }
            return super.keyPressed(i, j, k);
        }

        private void swap(int i, int j) {
            //this.screen.getServers().swap(i, j);
            this.screen.serverSelectionList.updateServers(this.screen.getServers());
            ServerEntry entry = this.screen.serverSelectionList.children().get(j);
            this.screen.serverSelectionList.setSelected(entry);
            MynxServerSelectionList.this.ensureVisible(entry);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            double f = d - (double) MynxServerSelectionList.this.getRowLeft();
            double g = e - (double) MynxServerSelectionList.this.getRowTop(MynxServerSelectionList.this.children().indexOf(this));
            if (f <= 32.0) {
                if (f < 32.0 && f > 16.0 && this.canJoin()) {
                    this.screen.setSelected(this);
                    //this.screen.joinSelectedServer(); TODO
                    return true;
                }
                int j = this.screen.serverSelectionList.children().indexOf(this);
                if (f < 16.0 && g < 16.0 && j > 0) {
                    this.swap(j, j - 1);
                    return true;
                }
                if (f < 16.0 && g > 16.0 && j < this.screen.getServers().size() - 1) {
                    this.swap(j, j + 1);
                    return true;
                }
            }
            this.screen.setSelected(this);
            if (Util.getMillis() - this.lastClickTime < 250L) {
                // this.screen.joinSelectedServer(); TODO
            }
            this.lastClickTime = Util.getMillis();
            return true;
        }

        public ServerData getServerData() {
            return this.serverData;
        }

        @Override
        public @NotNull Component getNarration() {
            MutableComponent mutableComponent = Component.empty();
            mutableComponent.append(Component.translatable("narrator.select", this.serverData.name));
            mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
            if (this.isIncompatible()) {
                mutableComponent.append(INCOMPATIBLE_STATUS);
                mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
                mutableComponent.append(Component.translatable("multiplayer.status.version.narration", this.serverData.version));
                mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
                mutableComponent.append(Component.translatable("multiplayer.status.motd.narration", this.serverData.motd));
            } else if (this.serverData.ping < 0L) {
                mutableComponent.append(NO_CONNECTION_STATUS);
            } else if (!this.pingCompleted()) {
                mutableComponent.append(PINGING_STATUS);
            } else {
                mutableComponent.append(ONLINE_STATUS);
                mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
                mutableComponent.append(Component.translatable("multiplayer.status.ping.narration", this.serverData.ping));
                mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
                mutableComponent.append(Component.translatable("multiplayer.status.motd.narration", this.serverData.motd));
                if (this.serverData.players != null) {
                    mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
                    mutableComponent.append(Component.translatable("multiplayer.status.player_count.narration", this.serverData.players.online(), this.serverData.players.max()));
                    mutableComponent.append(CommonComponents.NARRATION_SEPARATOR);
                    mutableComponent.append(ComponentUtils.formatList(this.serverData.playerList, Component.literal(", ")));
                }
            }
            return mutableComponent;
        }

        @Override
        public void close() {
            this.icon.close();
        }
    }
}
