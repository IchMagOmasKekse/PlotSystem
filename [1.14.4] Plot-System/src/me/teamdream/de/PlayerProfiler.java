package me.teamdream.de;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerProfiler {
	
	public static int howManyPlotsHas(Player p) {
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(new File("plugins/TeamDream/Profiling/"+p.getUniqueId()+".yml"));
		return cfg.getInt("plots claimed");
	}
	
	public static void addBoughtPlot(Player p, int amount) {
		File file = new File("plugins/TeamDream/Profiling/"+p.getUniqueId()+".yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		
		cfg.set("plots claimed", cfg.getInt("plots claimed")+amount);
		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
