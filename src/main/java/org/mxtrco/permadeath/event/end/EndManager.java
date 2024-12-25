package org.mxtrco.permadeath.event.end;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.mxtrco.permadeath.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SplittableRandom;

public class EndManager implements Listener {

    private Main main;

    private List<Entity> enderCreepers;
    private List<Entity> enderGhasts;

    private ArrayList<Location> alreadyExploded = new ArrayList<>();

    private ArrayList<Enderman> invulnerable = new ArrayList<>();

    private SplittableRandom random;

    public EndManager(Main main) {
        this.main = main;

        main.getServer().getPluginManager().registerEvents(this, main);

        this.enderCreepers = new ArrayList<>();
        this.enderGhasts = new ArrayList<>();
        this.random = new SplittableRandom();
    }

    public boolean isInEnd(Location p) {
        return p.getWorld().getName().endsWith("_the_end");
    }

    public static String format(String s) {
        s = s.replace("#&", "#");
        // HEX_COLOR_PATTERN eliminado ya que no está definido.
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent e) {
        if (isInEnd(e.getEntity().getLocation())) {
            if (e.getEntity() instanceof TNTPrimed) {
                if (!(e.getEntity() instanceof TNTPrimed)) return;
                if (e.getEntity().getCustomName() == null) return;
                if (!e.getEntity().getCustomName().equalsIgnoreCase("dragontnt")) return;
                e.setRadius(5.0F); // Si está habilitado, un radio más pequeño

            }
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        LivingEntity entity = event.getEntity();

        if (isInEnd(entity.getLocation()) && entity instanceof EnderDragon) {
            EnderDragon dragon = (EnderDragon) entity;

            // Configurar nombre personalizado
            dragon.customName(Component.text("PERMADEATH DEMON", NamedTextColor.GOLD)
                    .decorate(TextDecoration.BOLD));

            // Configurar atributos iniciales
            double health = 200.0;
            dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            dragon.setHealth(health);

            // Crear y ejecutar EndTask si no existe
            if (main.getTask() == null) {
                Main.EndTask task = new Main.EndTask(main, dragon);
                main.setTask(task);
                task.start(); // Iniciar el EndTask
            }
        }
    }
}
