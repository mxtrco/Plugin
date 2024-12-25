package org.mxtrco.permadeath.event.entity;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class TotemPopListener implements Listener {

    private static final Random RANDOM = new Random(); // Instancia de Random reutilizable
    private static final int PROBABILIDAD_EXITO = 99;  // Constante para la probabilidad de éxito (99%)

    @EventHandler
    public void onPop(EntityResurrectEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;

            // Verificar si el jugador tiene un tótem en alguna de las manos
            ItemStack mainHandItem = player.getInventory().getItemInMainHand();
            ItemStack offHandItem = player.getInventory().getItemInOffHand();

            if (!mainHandItem.getType().equals(Material.TOTEM_OF_UNDYING) &&
                    !offHandItem.getType().equals(Material.TOTEM_OF_UNDYING)) {
                return; // Si no hay tótem en las manos, no hacer nada
            }

            // Determinar si el tótem tiene éxito
            int probabilidad = RANDOM.nextInt(100) + 1; // Genera un número entre 1 y 100
            if (probabilidad > PROBABILIDAD_EXITO) { // Si no alcanza el porcentaje de éxito
                player.sendMessage("§c¡Tu tótem ha fallado!");
                Bukkit.broadcastMessage("§c¡" + player.getName() +
                        " ha usado un tótem con probabilidad: " + probabilidad + " > " + PROBABILIDAD_EXITO);
                event.setCancelled(true); // Cancelar la resurrección
            } else {
                Bukkit.broadcastMessage("§7" + player.getName() +
                        " ha usado un tótem con probabilidad: " + probabilidad + " <= " + PROBABILIDAD_EXITO);
            }
        }
    }
}
