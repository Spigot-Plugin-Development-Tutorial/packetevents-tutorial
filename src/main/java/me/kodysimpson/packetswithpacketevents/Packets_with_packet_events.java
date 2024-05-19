package me.kodysimpson.packetswithpacketevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.kodysimpson.packetswithpacketevents.listeners.AnimalHurtListener;
import me.kodysimpson.packetswithpacketevents.listeners.VillageLookListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Packets_with_packet_events extends JavaPlugin {

    //JavaDocs: https://packetevents.github.io/javadocs/

    private static Packets_with_packet_events INSTANCE;

    @Override
    public void onLoad() {
        INSTANCE = this;
        //we are using spigot
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        //configure the settings
        //reEncodeByDefault = allow modification of packets in your listeners by default.
        PacketEvents.getAPI().getSettings().reEncodeByDefault(true)
                .checkForUpdates(true)
                .bStats(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        // register your packet listeners!
        PacketEvents.getAPI().getEventManager().registerListener(new AnimalHurtListener(),
                PacketListenerPriority.NORMAL);
        PacketEvents.getAPI().init();

        getServer().getPluginManager().registerEvents(new VillageLookListener(), this);
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    public static Packets_with_packet_events getInstance() {
        return INSTANCE;
    }
}
