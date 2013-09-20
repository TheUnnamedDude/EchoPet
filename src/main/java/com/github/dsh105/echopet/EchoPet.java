package com.github.dsh105.echopet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import com.github.dsh105.echopet.api.EchoPetAPI;
import com.github.dsh105.echopet.commands.CommandComplete;
import com.github.dsh105.echopet.config.DefaultOptions;
import com.github.dsh105.echopet.entity.pet.EntityPet;
import com.github.dsh105.echopet.entity.pet.enderdragon.EntityEnderDragonPet;
import com.github.dsh105.echopet.entity.pet.giant.EntityGiantPet;
import com.github.dsh105.echopet.listeners.*;
import com.github.dsh105.echopet.mysql.SQLRefresh;
import net.minecraft.server.v1_6_R3.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.dsh105.echopet.commands.CustomCommand;
import com.github.dsh105.echopet.commands.PetAdminCommand;
import com.github.dsh105.echopet.commands.PetCommand;
import com.github.dsh105.echopet.config.YAMLConfig;
import com.github.dsh105.echopet.config.YAMLConfigManager;
import com.github.dsh105.echopet.data.AutoSave;
import com.github.dsh105.echopet.data.PetData;
import com.github.dsh105.echopet.data.PetHandler;
import com.github.dsh105.echopet.entity.pet.bat.EntityBatPet;
import com.github.dsh105.echopet.entity.pet.blaze.EntityBlazePet;
import com.github.dsh105.echopet.entity.pet.cavespider.EntityCaveSpiderPet;
import com.github.dsh105.echopet.entity.pet.chicken.EntityChickenPet;
import com.github.dsh105.echopet.entity.pet.cow.EntityCowPet;
import com.github.dsh105.echopet.entity.pet.creeper.EntityCreeperPet;
import com.github.dsh105.echopet.entity.pet.enderman.EntityEndermanPet;
import com.github.dsh105.echopet.entity.pet.ghast.EntityGhastPet;
import com.github.dsh105.echopet.entity.pet.horse.EntityHorsePet;
import com.github.dsh105.echopet.entity.pet.irongolem.EntityIronGolemPet;
import com.github.dsh105.echopet.entity.pet.magmacube.EntityMagmaCubePet;
import com.github.dsh105.echopet.entity.pet.mushroomcow.EntityMushroomCowPet;
import com.github.dsh105.echopet.entity.pet.ocelot.EntityOcelotPet;
import com.github.dsh105.echopet.entity.pet.pig.EntityPigPet;
import com.github.dsh105.echopet.entity.pet.pigzombie.EntityPigZombiePet;
import com.github.dsh105.echopet.entity.pet.sheep.EntitySheepPet;
import com.github.dsh105.echopet.entity.pet.silverfish.EntitySilverfishPet;
import com.github.dsh105.echopet.entity.pet.skeleton.EntitySkeletonPet;
import com.github.dsh105.echopet.entity.pet.slime.EntitySlimePet;
import com.github.dsh105.echopet.entity.pet.snowman.EntitySnowmanPet;
import com.github.dsh105.echopet.entity.pet.spider.EntitySpiderPet;
import com.github.dsh105.echopet.entity.pet.squid.EntitySquidPet;
import com.github.dsh105.echopet.entity.pet.villager.EntityVillagerPet;
import com.github.dsh105.echopet.entity.pet.witch.EntityWitchPet;
import com.github.dsh105.echopet.entity.pet.wither.EntityWitherPet;
import com.github.dsh105.echopet.entity.pet.wolf.EntityWolfPet;
import com.github.dsh105.echopet.entity.pet.zombie.EntityZombiePet;
import com.github.dsh105.echopet.mysql.SQLConnection;
import com.github.dsh105.echopet.mysql.SQLPetHandler;
import com.github.dsh105.echopet.util.Lang;
import com.github.dsh105.echopet.util.ReflectionUtil;
import com.github.dsh105.echopet.util.SQLUtil;

public class EchoPet extends JavaPlugin {
	
	public static EchoPet plugin;
	
	private YAMLConfigManager configManager;
	private YAMLConfig petConfig;
	private YAMLConfig mainConfig;
	private YAMLConfig langConfig;
	public DefaultOptions DO;
	public AutoSave AS;
	public PetHandler PH;
	public SQLPetHandler SPH;
	public SQLConnection sqlCon;
	private SQLRefresh sqlRefresh;
	public String prefix = "" + ChatColor.DARK_RED + "[" + ChatColor.RED + "EchoPet" + ChatColor.DARK_RED + "] " + ChatColor.RESET;

	private EchoPetAPI api;

	public String cmdString = "pet";
	public String adminCmdString = "petadmin";
	
	// Update data
	public boolean update = false;
	public String name = "";
	public long size = 0;
	public boolean updateCheck = false;
	
	public CommandMap CM;
	
	public void onEnable() {
		plugin = this;
		// Make sure that the plugin is running under the correct version to prevent errors
		if (!(Version.getNMSPackage()).equalsIgnoreCase(ReflectionUtil.getVersionString())) {
			this.log(ChatColor.RED + "EchoPet " + ChatColor.GOLD
					+ this.getDescription().getVersion() + ChatColor.RED
					+ " is only compatible with:");
			this.log(ChatColor.RED + "    " + Version.getMinecraftVersion() + "-" + Version.getCraftBukkitVersion() + ".");
			this.log(ChatColor.RED + "Initialisation failed. Please update the plugin.");
			this.checkUpdates();
			return;
		}

		this.api = new EchoPetAPI();

		PluginManager manager = getServer().getPluginManager();
		DO = new DefaultOptions(this);
		
		configManager = new YAMLConfigManager(this);
		String[] header = { "EchoPet By DSH105", "---------------------",
				"Configuration for EchoPet 2",
				"See the EchoPet Wiki before editing this file" };
		try {
			mainConfig = configManager.getNewConfig("config.yml", header);
			DO.setDefaultValues(mainConfig);
		} catch (Exception e) {}
		
		mainConfig.reloadConfig();
		
		petConfig = configManager.getNewConfig("pets.yml");
		petConfig.reloadConfig();
		
		String[] langHeader = { "EchoPet By DSH105", "---------------------",
				"Language Configuration File"};
		try {
			langConfig = configManager.getNewConfig("language.yml", langHeader);
			try {
				for (Lang l : Lang.values()) {
					String[] desc = l.getDescription();
					langConfig.set(l.getPath(), langConfig.getString(l.getPath(), l.toString_()), desc);
				}
				langConfig.saveConfig();
			} catch (Exception e) {this.debug(e, "Failed to generate default Language File.");}
			
		} catch (Exception e) {}
		langConfig.reloadConfig();
		
		PH = new PetHandler(this);
		SPH = new SQLPetHandler();
		
		if (DO.useSql()) {
			String host = mainConfig.getString("sql.host", "localhost");
			int port = mainConfig.getInt("sql.port", 3306);
			String db = mainConfig.getString("sql.database", "EchoPet");
			String user = mainConfig.getString("sql.username", "none");
			String pass = mainConfig.getString("sql.password", "none");
			sqlCon = new SQLConnection(host, port, db, user, pass);
			Connection con = sqlCon.getConnection();
			if (con != null) {
				try {
					con.prepareStatement("CREATE TABLE IF NOT EXISTS Pets (" +
							"OwnerName varchar(255)," +
							"PetType varchar(255)," +
							"PetName varchar(255)," +
							SQLUtil.serialise(PetData.values(), false) + ", " +
							"MountPetType varchar(255), MountPetName varchar(255), " +
							SQLUtil.serialise(PetData.values(), true) +
							", PRIMARY KEY (OwnerName)" +
							");").executeUpdate();
				} catch (SQLException e) {
					this.severe(e, "Failed to create 'Pets' table in MySQL Database");
				}
				this.sqlRefresh = new SQLRefresh(getMainConfig().getInt("sql.timeout") * 20 * 60);
			}
		}

		// Register custom entities
		this.registerEntity(EntityBatPet.class, "BatPet", 65);
		this.registerEntity(EntityBlazePet.class, "BlazePet", 61);
		this.registerEntity(EntityCaveSpiderPet.class, "CaveSpiderPet", 59);
		this.registerEntity(EntityChickenPet.class, "ChickenPet", 93);
		this.registerEntity(EntityCowPet.class, "CowPet", 92);
		this.registerEntity(EntityCreeperPet.class, "CreeperPet", 50);
		this.registerEntity(EntityEnderDragonPet.class, "EnderDragonPet", 63);
		this.registerEntity(EntityEndermanPet.class, "EndermanPet", 58);
		this.registerEntity(EntityGhastPet.class, "GhastPet", 56);
		this.registerEntity(EntityGiantPet.class, "GiantPet", 53);
		this.registerEntity(EntityHorsePet.class, "HorsePet", 100);
		//this.registerEntity(EntityHumanPet.class, "HumanPet", 49);
		this.registerEntity(EntityIronGolemPet.class, "IronGolemPet", 99);
		this.registerEntity(EntityMagmaCubePet.class, "MagmaCubePet", 62);
		this.registerEntity(EntityMushroomCowPet.class, "MushroomCowPet", 96);
		this.registerEntity(EntityOcelotPet.class, "OcelotPet", 98);
		this.registerEntity(EntityPigPet.class, "PigPet", 90);
		this.registerEntity(EntityPigZombiePet.class, "PigZombiePet", 57);
		this.registerEntity(EntitySheepPet.class, "SheepPet", 91);
		this.registerEntity(EntitySilverfishPet.class, "SilverfishPet", 60);
		this.registerEntity(EntitySkeletonPet.class, "SkeletonPet", 51);
		this.registerEntity(EntitySlimePet.class, "SlimePet", 55);
		this.registerEntity(EntitySnowmanPet.class, "SnowManPet", 97);
		this.registerEntity(EntitySpiderPet.class, "SpiderPet", 52);
		this.registerEntity(EntitySquidPet.class, "SquidPet", 94);
		this.registerEntity(EntityVillagerPet.class, "VillagerPet", 120);
		this.registerEntity(EntityWitchPet.class, "WitchPet", 66);
		this.registerEntity(EntityWitherPet.class, "WitherPet", 64);
		this.registerEntity(EntityWolfPet.class, "WolfPet", 95);
		this.registerEntity(EntityZombiePet.class, "ZombiePet", 54);

		// Check whether to start AutoSave
		if (getMainConfig().getBoolean("autoSave")) {
			AS = new AutoSave(getMainConfig().getInt("autoSaveTimer"));
		}

		// Register custom commands
		// Command string based off the string defined in config.yml
		// By default, set to 'pet'
		// PetAdmin command draws from the original, with 'admin' on the end
		try {
			if (Bukkit.getServer() instanceof CraftServer) {
				final Field f = CraftServer.class.getDeclaredField("commandMap");
				f.setAccessible(true);
				CM = (CommandMap) f.get(Bukkit.getServer());
			}
		}
		catch (Exception e) {
			this.debug(e, "Failed to register Pet Command.");
		}
		
		String cmdString = DO.getCommandString();
		if (CM.getCommand(cmdString) != null) {
			this.log(ChatColor.YELLOW + "A command under the name " + ChatColor.RED + "/" + cmdString + ChatColor.YELLOW + " already exists. Pet Command temporarily registered under " + ChatColor.RED + "/ec:" + cmdString);
		}
		String adminCmdString = DO.getCommandString() + "admin";
		if (CM.getCommand(adminCmdString) != null) {
			this.log(ChatColor.YELLOW + "A command under the name " + ChatColor.RED + "/" + adminCmdString + ChatColor.YELLOW + " already exists. Pet Admin Command temporarily registered under " + ChatColor.RED + "/ec:" + adminCmdString);
		}
		CustomCommand petCmd = new CustomCommand(cmdString);
		CM.register("ec", petCmd);
		petCmd.setExecutor(new PetCommand(this, cmdString));
		petCmd.setTabCompleter(new CommandComplete());
		this.cmdString = cmdString;
		
		CustomCommand petAdminCmd = new CustomCommand(adminCmdString);
		CM.register("ec", petAdminCmd);
		petAdminCmd.setExecutor(new PetAdminCommand(this, adminCmdString));
		this.adminCmdString = adminCmdString;
		
		// Register listeners
		manager.registerEvents(new MenuListener(this), this);
		manager.registerEvents(new PetEntityListener(this), this);
		manager.registerEvents(new PetOwnerListener(this), this);
		
		
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :(
		}

		this.checkUpdates();
	}
	
	public void checkUpdates() {
		if (this.getMainConfig().getBoolean("autoUpdate", false) == true) {
			@SuppressWarnings("unused")
			Updater updater = new Updater(this, "echopet", this.getFile(), Updater.UpdateType.DEFAULT, true);
		} else {
			if (this.getMainConfig().getBoolean("checkForUpdates", true) == true) {
				Updater updater = new Updater(this, "echopet", this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
				update = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
				if (this.update) {
					name = updater.getLatestVersionString();
					size = updater.getFileSize();
					log(ChatColor.GOLD + "An update is available: " + this.name + " (" + this.size + " bytes).");
					log(ChatColor.GOLD + "Type /ecupdate to update.");
					if (updateCheck == false) {
						updateCheck = true;
					}
				}
			}
		}
	}
	
	public void onDisable() {
		PH.removeAllPets();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("ecupdate")) {
			if (sender.hasPermission("echopet.update")) {
				if (updateCheck == true) {
					@SuppressWarnings("unused")
					Updater updater = new Updater(this, "echopet", this.getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
					return true;
				}
				else {
					sender.sendMessage(this.prefix + ChatColor.GOLD + " An update is not available.");
					return true;
				}
			}
			else {
				Lang.sendTo(sender, Lang.NO_PERMISSION.toString().replace("%perm%", "echopet.update"));
				return true;
			}
		}
		return false;
	}

	public void registerEntity(Class<? extends EntityPet> clazz, String name, int id) {
		Field field_c = null;
		try {
			field_c = EntityTypes.class.getDeclaredField("c");
			Field field_e = EntityTypes.class.getDeclaredField("e");
			field_c.setAccessible(true);
			field_e.setAccessible(true);

			Map<Class, String> c = (Map) field_c.get(field_c);
			Map<Class, Integer> e = (Map) field_e.get(field_e);

			Iterator i = c.keySet().iterator();
			while (i.hasNext()) {
				Class cl = (Class) i.next();
				if (cl.getCanonicalName().equals(clazz.getCanonicalName())) {
					i.remove();
				}
			}

			Iterator i2 = e.keySet().iterator();
			while (i.hasNext()) {
				Class cl = (Class) i.next();
				if (cl.getCanonicalName().equals(clazz.getCanonicalName())) {
					i.remove();
				}
			}

			c.put(clazz, name);
			e.put(clazz, id);

		} catch (Exception e) {
			this.severe(e, "Failed to register pet entity.");
		}
	}

	public EchoPetAPI getAPI() {
		return this.api;
	}
	
	public static EchoPet getPluginInstance() {
		return plugin;
	}
	
	public void log(String msg) {
		getServer().getConsoleSender().sendMessage("[EchoPet] " + msg);
	}
	
	public void severe(Exception e, String msg) {
		log(ChatColor.RED + msg);
		e.printStackTrace();
	}
	
	public void debug(Exception e, String msg) {
		if (getConfig().getBoolean("debug")) {
			log(ChatColor.RED + msg);
			e.printStackTrace();
		}
	}
	
	/*public void debug(String msg) {
		if (getConfig().getBoolean("debug")) {
			log(ChatColor.RED + msg);
		}
	}*/
	
	public Connection getSqlCon() {
		return this.sqlCon.getConnection();
	}
	
	public YAMLConfig getPetConfig() {
		return this.petConfig;
	}
	
	public YAMLConfig getMainConfig() {
		return mainConfig;
	}
	
	public YAMLConfig getLangConfig() {
		return langConfig;
	}
}