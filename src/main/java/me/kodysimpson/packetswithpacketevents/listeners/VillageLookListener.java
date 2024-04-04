package me.kodysimpson.packetswithpacketevents.listeners;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityHeadLook;
import org.bukkit.Location;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class VillageLookListener implements Listener {

    @EventHandler
    public void onPlayerMovement(PlayerMoveEvent e) {

        //when the player moves within 5 blocks of a villager, make the villager look at the player
        e.getPlayer().getNearbyEntities(5, 5, 5).stream()
                .filter(entity -> entity instanceof Villager)
                .forEach(entity -> {
                    float yaw = calculateYawToFacePlayer(entity.getLocation(), e.getPlayer().getLocation());
                    WrapperPlayServerEntityHeadLook packet = new WrapperPlayServerEntityHeadLook(entity.getEntityId(), yaw);

                    //send it to this player only so the villager looks at them specifically
                    PacketEvents.getAPI().getPlayerManager().sendPacket(e.getPlayer(), packet);
                });

    }

    public float calculateYawToFacePlayer(Location entityLocation, Location playerLocation) {
        // Calculate the difference in position
        double deltaX = playerLocation.getX() - entityLocation.getX();
        double deltaZ = playerLocation.getZ() - entityLocation.getZ();

        // Calculate the yaw in radians, then convert to degrees
        double yawRadians = Math.atan2(deltaZ, deltaX);
        double yawDegrees = Math.toDegrees(yawRadians);

        // Adjust the yaw to match Minecraft's coordinate system
        double adjustedYaw = yawDegrees - 90; // Subtract 90 degrees to align with Minecraft's axis

        // Normalize the yaw to a value between -180 and 180
        adjustedYaw = normalizeYaw(adjustedYaw);

        return (float) adjustedYaw;
    }

    public double normalizeYaw(double yaw) {
        yaw = (yaw % 360);
        if (yaw < -180) {
            yaw += 360;
        } else if (yaw > 180) {
            yaw -= 360;
        }
        return yaw;
    }


}