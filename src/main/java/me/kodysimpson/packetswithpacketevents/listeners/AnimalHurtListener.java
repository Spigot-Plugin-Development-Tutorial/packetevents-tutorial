package me.kodysimpson.packetswithpacketevents.listeners;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class AnimalHurtListener implements PacketListener {

    //The Server receives a packet
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {

        //Match it to a specific packet type that we want to listen for
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY){

            //Get the cross-platform user involved in the packet
            User user = event.getUser();

            //Get the Wrapper of the packet which allows us to easily extract information from it
            var packet = new WrapperPlayClientInteractEntity(event);

            //Determine if the interaction was an attack
            if (packet.getAction() != WrapperPlayClientInteractEntity.InteractAction.ATTACK){
                return;
            }

            Player player = (Player) event.getPlayer();
            World world = player.getWorld();

            //get the entity id of the entity that was attacked
            int entityId = packet.getEntityId();

            //Run the code on the main thread since we are working with entities.
            //Important: PacketEvents Listeners are async
            Bukkit.getScheduler().runTask((Plugin) PacketEvents.getAPI().getPlugin(), () -> {

                //get the entity from the entity id
                Entity entity = null;
                for (Entity e : world.getEntities()){
                    if (e.getEntityId() == entityId){
                        entity = e;
                        break;
                    }
                }

                //see if it's an animal
                if (entity instanceof Animals){

                    user.sendMessage("You attacked an animal! Stop!");

                    //generate float health value between 1 and 20
                    float health = (float) (Math.random() * 20 + 1);

                    WrapperPlayServerUpdateHealth healthPacket = new WrapperPlayServerUpdateHealth(health, 1, 0.0f);
                    user.sendPacket(healthPacket);
                }
            });

        }

    }

    //The Server sends a packet
    @Override
    public void onPacketSend(PacketSendEvent event) {

        //Intercept any update time packets so that we can cancel them
        //This will prevent the time from updating for the player. :)

        if (event.getPacketType() == PacketType.Play.Server.TIME_UPDATE){

            System.out.println("Time Update Packet Intercepted!");

            event.setCancelled(true);
        }


    }
}