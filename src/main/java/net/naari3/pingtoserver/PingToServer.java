package net.naari3.pingtoserver;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedInEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.naari3.pingtoserver.pinger.Pinger;
import net.naari3.pingtoserver.pinger.TcpPinger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("pingtoserver")
public class PingToServer {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private PingToServerState state;
    private Pinger pinger;

    public PingToServer() {
        this.state = new PingToServerState();

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        // Register ourselves for server and other game events we are interested in
        forgeBus.register(this);

        forgeBus.addListener(this::onWorldLoad);
        forgeBus.addListener(this::onWorldUnload);

        forgeBus.addListener(this::onRenderGameOverlayEvent);

        forgeBus.addListener(this::onLoggedInEvent);
        forgeBus.addListener(this::onLoggedOutEvent);

        forgeBus.addListener(this::onTickEvent);
    }

    private void onWorldLoad(WorldEvent.Load event) {
        LOGGER.info("Load World {}", event);
    }

    private void onWorldUnload(WorldEvent.Unload event) {
        LOGGER.info("Unload World {}", event);
        if (this.state != null && this.state.isStarted()) {
            this.state.stop();
        }
    }

    private void onRenderGameOverlayEvent(RenderGameOverlayEvent event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.DEBUG) return;

        if (this.state.isStarted() && this.pinger != null) {
            float x = 150;
            float y = 100;
            int color = 0xffffffff;
            Minecraft.getInstance().fontRenderer.drawStringWithShadow(this.pinger.getContent(), x, y, color);
        }
    }

    private void onLoggedInEvent(LoggedInEvent event) {
        LOGGER.info("onLoggedInEvent");
        LOGGER.info(event.getResult());
        if (event.getResult() == Event.Result.DEFAULT) {
            ServerData server = Minecraft.getInstance().getCurrentServerData();
            if (server != null) {
                LOGGER.info(server.serverIP);
                try {
                    this.pinger = new TcpPinger(server.serverIP, 5000);
                    this.state.start();
                } catch (URISyntaxException err) {
                    LOGGER.warn("Can't parse");
                }
            }
            LOGGER.info("LOGGED IN");
        }
    }

    private void onLoggedOutEvent(LoggedOutEvent event) {
        this.state.stop();
        this.pinger = null;
    }

    private int tickCounter = 0;

    private void onTickEvent(ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;

        tickCounter++;
        if (tickCounter > 20) {
            if (this.state.isStarted() && this.pinger != null) {
                this.pinger.pingAsync();
            }
            tickCounter = 0;
        }
    }
}
