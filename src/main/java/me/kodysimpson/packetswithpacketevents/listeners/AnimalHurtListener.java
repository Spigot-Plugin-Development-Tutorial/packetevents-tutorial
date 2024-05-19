package me.kodysimpson.packetswithpacketevents.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTimeUpdate;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AnimalHurtListener implements PacketListener {

    //The Server receives a packet
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {

        //Match it to a specific packet type that we want to listen for
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {

            //Get the cross-platform user involved in the packet
            User user = event.getUser();

            //Get the Wrapper of the packet which allows us to easily extract information from it
            var packet = new WrapperPlayClientInteractEntity(event);

            //Determine if the interaction was an attack
            if (packet.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                return;
            }

            Player player = (Player) event.getPlayer();
            World world = player.getWorld();

            //get the entity id of the entity that was attacked
            int entityId = packet.getEntityId();
            //Find the entity using the entity ID.
            Entity entity = SpigotConversionUtil.getEntityById(world, entityId);
            //see if it's an animal
            if (entity instanceof Animals) {

                user.sendMessage("You attacked an animal! Stop!");

                //generate float health value between 1 and 20
                float health = (float) (Math.random() * 20 + 1);

                WrapperPlayServerUpdateHealth healthPacket = new WrapperPlayServerUpdateHealth(health, 1, 0.0f);
                user.sendPacket(healthPacket);
            }
        }

    }

    //The Server sends a packet
    @Override
    public void onPacketSend(PacketSendEvent event) {

        if (event.getPacketType() == PacketType.Play.Server.TIME_UPDATE) {

            System.out.println("Time update packet intercepted!");

            WrapperPlayServerTimeUpdate packet = new WrapperPlayServerTimeUpdate(event);
            //Modify the packet
            packet.setTimeOfDay(0L);

            //event.setCancelled(true);
        }


    }
}
