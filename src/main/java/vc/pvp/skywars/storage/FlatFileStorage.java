package vc.pvp.skywars.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import vc.pvp.skywars.SkyWars;
import vc.pvp.skywars.config.PluginConfig;
import vc.pvp.skywars.player.GamePlayer;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

public class FlatFileStorage extends DataStorage {

    @Override
    public void loadPlayer(@Nonnull GamePlayer player) {
        try {
            File dataDirectory = SkyWars.get().getDataFolder();
            File playerDataDirectory = new File(dataDirectory, "player_data");

            if (!playerDataDirectory.exists() && !playerDataDirectory.mkdirs()) {
                System.out.println("Failed to load player " + player + ": Could not create player_data directory.");
                return;
            }

            File playerFile = new File(playerDataDirectory, player + ".yml");
            if (!playerFile.exists() && !playerFile.createNewFile()) {
                System.out.println("Failed to load player " + player + ": Could not create player file.");
                return;
            }

            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerFile);

            if (!PluginConfig.useEconomy() || SkyWars.getEconomy() == null) {
                player.setScore(fileConfiguration.getInt("score", 0));
            }

        } catch (IOException ioException) {
            System.out.println("Failed to load player " + player + ": " + ioException.getMessage());
        }
    }

    @Override
    public void savePlayer(@Nonnull GamePlayer player) {
        try {
            File dataDirectory = SkyWars.get().getDataFolder();
            File playerDataDirectory = new File(dataDirectory, "player_data");

            if (!playerDataDirectory.exists() && !playerDataDirectory.mkdirs()) {
                System.out.println("Failed to save player " + player + ": Could not create player_data directory.");
                return;
            }

            File playerFile = new File(playerDataDirectory, player + ".yml");
            if (!playerFile.exists() && !playerFile.createNewFile()) {
                System.out.println("Failed to save player " + player + ": Could not create player file.");
                return;
            }

            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(playerFile);
            fileConfiguration.set("score", player.getScore());
            fileConfiguration.save(playerFile);

        } catch (IOException ioException) {
            System.out.println("Failed to save player " + player + ": " + ioException.getMessage());
        }
    }

}