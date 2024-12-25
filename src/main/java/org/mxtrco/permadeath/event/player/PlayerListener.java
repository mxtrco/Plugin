package org.mxtrco.permadeath.event.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.mxtrco.permadeath.Main;
import org.mxtrco.permadeath.event.player.SoundManager;
import org.mxtrco.permadeath.event.player.StormManager;

public class PlayerListener implements Listener {
    // En lugar de crear una nueva instancia, obtener la referencia del Main
    private final StormManager stormManager;

    public PlayerListener() {
        // Obtener la instancia existente del StormManager desde Main
        this.stormManager = Main.getInstance().getStormManager(); // Necesitarás añadir este getter en Main
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        // Reproduce un sonido a todos los jugadores
        SoundManager.playDeathSounds();

        // Retraso en ticks (20 ticks = 1 segundo)
        int delayInTicks = 100; // 10 segundos de retraso
        SoundManager.playDelayedSound(delayInTicks);

        // Avisar a todos los jugadores que se inició/extiende una tormenta
        Bukkit.broadcastMessage("§c¡Una tormenta se ha desatado debido a la muerte de " + player.getName() + "!");

        // Extender el tiempo de la tormenta usando extendStorm
        stormManager.onPlayerDeath(player.getWorld());

        // Cambiar el modo de juego a espectador inmediatamente
        player.setGameMode(GameMode.SPECTATOR);

        // Ejecutar la desconexión y el baneo con un retraso de 10 segundos
        Bukkit.getScheduler().runTaskLater(
                Main.getInstance(),
                () -> {
                    String playerIp = player.getAddress().getAddress().getHostAddress();
                    Bukkit.getBanList(org.bukkit.BanList.Type.IP)
                            .addBan(playerIp, "§cHas sido Permabaneado.", null, null);

                    player.kickPlayer("§cHas sido Permabaneado.");
                },
                delayInTicks + 50
        );
    }

    @EventHandler
    public void onPlayerTrySleep(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();

        if (stormManager.isStormActive()) {
            event.setCancelled(true);
            SoundManager.playExplosionSound(player);
            player.sendMessage("§cNo puedes dormir durante el death train.");
        }
    }
}