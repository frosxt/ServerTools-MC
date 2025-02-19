package me.reecepbcups.utiltools;

import com.google.common.base.Strings;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.*;
import java.util.regex.Pattern;


public class Util {

	static ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

	private final static int CENTER_PX = 144; // 154 default
	public static void sendCenteredMessage(final Player player, final String message){
		player.sendMessage(centerMessage(message));
	}

	public static String centerMessage(String message){
		if(message == null || message.equals("")) {
			//player.sendMessage("");
		}
		message = ChatColor.translateAlternateColorCodes('&', message);
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;
		for (final char c : message.toCharArray()){
			if (c == '�'){
				previousCode = true;
				continue;
			} else if (previousCode == true){
				previousCode = false;
				if (c == 'l' || c == 'L'){
					isBold = true;
					continue;
				} else {
					isBold = false;
				}
			} else{
				final DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}
		final int halvedMessageSize = messagePxSize / 2;
		final int toCompensate = CENTER_PX - halvedMessageSize;
		final int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		final StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate){
			sb.append(" ");
			compensated += spaceLength;
		}                                            
		return sb + message;
	}

	//Util.color(" &8[&r" + getProgressBar(TokensBal, tokensForReward, 40, '|', "&d", "&7") + "&8]  " + "&7&o((" + TokensBal + " / " + tokensForReward + "&7&o))");
	public static String getProgressBar(final int current, final int max, final int totalBars, final char symbol, final String string, final String string2) {
		float percent = (float) current / max;
		if(percent > 1.0) { percent = (float) 1.0; }
		final int progressBars = (int) (totalBars * percent);
		return Strings.repeat("" + string + symbol, progressBars) + Strings.repeat("" + string2 + symbol, totalBars - progressBars);
	}

	// Not tested yet (2/7)
	public void fillSlots(final Inventory inv, final ItemStack item) {
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) == null || inv.getItem(i).getType().equals(Material.AIR)) {
				inv.setItem(i, item);
			}
		} 
	}

	public static boolean isVersion1_8() {
		// 1.8 uses: e.getInventory().getName() && 1.9+ uses: e.getView().getTitle()
		return Bukkit.getServer().getClass().getPackage().getName().contains("1_8");
	}

	
	public static void removeItemFromPlayer(final Player player, final ItemStack item, final int amount) {
		if(item.getAmount() == 1 && isVersion1_8()) {
			player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);
			return;
		}						
		item.setAmount(item.getAmount() - amount);
	}

	public static String argsToSingleString(final int startPoint, final String[] args) {
		final StringBuilder str = new StringBuilder();
		for (int i = startPoint; i < args.length; i++) {
			if (i+1 < args.length) {
				str.append(args[i]).append(" ");
			} else {
				str.append(args[i]);
			}
		}
		return str.toString();
	}
	
	public static boolean isPluginInstalledOnServer(final String pluginName, final String moduleName) {
		if (Bukkit.getServer().getPluginManager().getPlugin(pluginName)!= null) {
			return true;
		} else {
			Util.consoleMSG("&cYou need &n'" + pluginName + "'&c installed to use &n" + moduleName + "&c with ServerTools!");
			return false;
		}
	}


	// exp
	public static void setTotalExperience(final Player player, final int exp) {
		player.setExp(0.0F);
		player.setLevel(0);
		player.setTotalExperience(0);
		int amount = exp;
		while (amount > 0) {
			final int expToLevel = getExpAtLevel(player.getLevel());
			amount -= expToLevel;
			if (amount >= 0) {
				player.giveExp(expToLevel);
				continue;
			} 
			amount += expToLevel;
			player.giveExp(amount);
			amount = 0;
		} 
	}

	private static int getExpAtLevel(final int level) {
		if (level <= 15) {
			return 2 * level + 7;
		}
		if (level <= 30) {
			return 5 * level - 38;
		}

		return 9 * level - 158;
	}

	public static int getTotalExperience(final Player player) {
		int exp = Math.round(getExpAtLevel(player.getLevel()) * player.getExp());
		int currentLevel = player.getLevel();
		while (currentLevel > 0) {
			currentLevel--;
			exp += getExpAtLevel(currentLevel);
		} 
		if (exp < 0) {
			exp = Integer.MAX_VALUE;
		}
		return exp;
	}

	public static String formatNumber(final double number) {
		final Format decimalFormat = new DecimalFormat("###,###.##");
		return decimalFormat.format(number);
	}

	public static String locationToString(final Player p) {
		final int x;
		final int y;
		final int z;
		final Location pL = p.getLocation();

		final World w = pL.getWorld();
		x = (int) pL.getX();
		y = (int) pL.getY();
		z = (int) pL.getZ();
		final float yaw = pL.getYaw();
		final float pitch = pL.getPitch();

		return w.getName()+";"+x+";"+y+";"+z+";"+yaw+";"+pitch;
	}
	public static Location stringToLocation(final String Location) {
		final String[] loc = Location.split(";");
		return new Location(Bukkit.getWorld(loc[0]), 
				Double.parseDouble(loc[1]), 
				Double.parseDouble(loc[2])+0.1, 
				Double.parseDouble(loc[3]), 
				Float.parseFloat(loc[4]), 
				Float.parseFloat(loc[5]));
	}
	
	

	public static boolean cooldown(final Map<String, Date> cooldownHash, final Integer secondCooldown, final String playerName, final String cooldownMessage) {
		final long currentTime = new Date().getTime();

		if (cooldownHash.containsKey(playerName) && cooldownHash.get(playerName).getTime() >= currentTime) {
			final Player p = Bukkit.getServer().getPlayer(playerName);
			final long timeLeft = (cooldownHash.get(playerName).getTime() - currentTime) / 1000;

			p.sendMessage(Util.color(cooldownMessage.replace("%timeleft%", String.valueOf(timeLeft))));
			return false;

		} else {
			cooldownHash.remove(playerName); //Cooldown is over
		}

		if (!(cooldownHash.containsKey(playerName))) {
			final long mil_cooldown = secondCooldown * 1000L;
			cooldownHash.put(playerName , new Date(currentTime + mil_cooldown));
		}

		return true;				
	}

	public static Long cooldownSecondsLeft(final HashMap<String, Date> CooldownHash, final Integer SecondCooldown, final String PlayerName) {
		final long CURRENT_TIME = new Date().getTime();

		if (CooldownHash.containsKey(PlayerName) && CooldownHash.get(PlayerName).getTime() >= CURRENT_TIME) {			
			//Player p = Bukkit.getServer().getPlayer(PlayerName);
			final Long timeLeft = ((CooldownHash.get(PlayerName).getTime() - CURRENT_TIME) / 1000);
			return timeLeft;
		} 

		return 0L;
	}

	public int stringToInt(final String value) {
		return Integer.parseInt(value);
	}

	public String intToString(final int value) {
		return Integer.toString(value);
	}

	private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");
	public static String color(final String message) {
		// doesnt work bc of 1.8 backwars compatibility and having to be loaded in first
//		Matcher matcher = HEX_PATTERN.matcher(ChatColor.translateAlternateColorCodes('&', message));
//		StringBuffer buffer = new StringBuffer();
//		while (matcher.find()) {
//	        matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group(1)).toString());	        
//	    }
//		return matcher.appendTail(buffer).toString();	
		
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	public static List<String> color(final List<String> list) {
		final List<String> colored = new ArrayList<>();
		for (final String s : list) {
			colored.add(color(s));
		}
		return colored;
	}

	public static void coloredMessage(final CommandSender sender, String message) {
		if (message.contains("\n")) {
			if (message.endsWith("\n")) {message+= " ";}
			
			for (final String line : message.split("\n")) {
				sender.sendMessage(color(line));
			}			
		} else {
			sender.sendMessage(color(message));
		}
	}

	public static void coloredBroadcast(final String msg) {
		Bukkit.broadcastMessage(Util.color(msg));
	}

	public static void console(final String command) {
		Bukkit.dispatchCommand(console, command);
	}

	public static void consoleMSG(final String consoleMsg) {
		Bukkit.getServer().getConsoleSender().sendMessage(Util.color(consoleMsg));
	}

// DATE AND TIME UTILITIES
	public static final long SEC = 1000L;
	public static final long MIN = 60000L;
	public static final long HOUR = 3600000L;
	public static final long DAYS = 86400000L;
	public static String onPlaceholderRequest(final String argument) {
		long timestamp;
		try {
			timestamp = Long.parseLong(argument) * 1000L;
		} catch (final NumberFormatException e) {
			return null;
		} 
		final long current = System.currentTimeMillis();
		if (timestamp > current) {
			timestamp -= current;
		} else {
			timestamp = current - timestamp;
		} 
		return formatTime(timestamp);
	}		  
	public static String formatTime(final long timestamp) {
		final long days = timestamp / 86400000L;
		final long hours = timestamp % 86400000L / 3600000L;
		final long minutes = timestamp % 3600000L / 60000L;
		final long seconds = timestamp % 60000L / 1000L;
		final StringBuilder formatted = new StringBuilder();
		if (days > 0L) {
			formatted.append(days).append("d ");
		}
		if (hours > 0L) {
			formatted.append(hours).append("h ");
		}
		if (minutes > 0L) {
			formatted.append(minutes).append("m ");
		}
		if (seconds > 0L || formatted.length() == 0) {
			formatted.append(seconds).append("s");
		}
		return formatted.toString().trim();
	}
	
	
	
	// =================================== ITEMS =========================================
	public static ItemStack createItem(final Material mat, final int amt, final int durability, final String name) {
		final ItemStack item = new ItemStack(mat, amt);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		//meta.setLore(lore); - List<String> lore
		if (durability != 0) {
			item.setDurability((short)durability);
		}
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createItemWithLore(final Material mat, final int amt, final int durability, final String name, final List<String> lore) {
		final ItemStack item = new ItemStack(mat, amt);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		if (durability != 0) {
			item.setDurability((short)durability);
		}
		item.setItemMeta(meta);
		return item;
	}
//	public static ItemStack createHead(String owner, String name, String... lore) {
//		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
//		SkullMeta meta = (SkullMeta)item.getItemMeta();
//		meta.setOwner(owner);
//		meta.setDisplayName(name);
//		List<String> l = new ArrayList<>();
//		String[] arrayOfString;
//		int j = (arrayOfString = lore).length;
//		for (int i = 0; i < j; i++) {
//			String s = arrayOfString[i];
//			l.add(s);
//		} 
//		meta.setLore(l);
//		item.setItemMeta((ItemMeta)meta);
//		return item;
//	}

	// ================================= CONFIG ==============================
	// put this in Main.class
	//	public FileConfiguration getConfigFile(String name) {
	//		return YamlConfiguration.loadConfiguration(new File(getDataFolder(), name));
	//	}
	//
	//	public void createDirectory(String DirName) {
	//		File newDir = new File(getDataFolder(), DirName.replace("/", File.separator));
	//		if (!newDir.exists()){
	//			newDir.mkdirs();
	//		}
	//	}
	//
	//	public void createConfig(String name) {
	//		File file = new File(getDataFolder(), name);
	//
	//		if (!new File(getDataFolder(), name).exists()) {
	//
	//			saveResource(name, false);
	//		}
	//
	//		@SuppressWarnings("static-access")
	//		FileConfiguration configuration = new YamlConfiguration().loadConfiguration(file);
	//		if (!file.exists()) {
	//			try {
	//				configuration.save(file);
	//			}			
	//			catch (IOException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}
	//
	//	public void createFile(String name) {
	//		File file = new File(getDataFolder(), name);
	//
	//		if (!file.exists()) {
	//			try {
	//				file.createNewFile();
	//			} catch(Exception e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}	
	//	public void saveConfig(FileConfiguration config, String name) {
	//		try {
	//			config.save(new File(getDataFolder(), name));
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}



	// ================================== INV =================================
	public static void remove(final Inventory inv, final Material mat, final int amt) {
		int amount = 0;
		final ItemStack[] arrayOfItemStack;
		final int j = (arrayOfItemStack = inv.getContents()).length;
		for (int i = 0; i < j; i++) {
			final ItemStack item = arrayOfItemStack[i];
			if (item != null && item.getType() == mat) {
				amount += item.getAmount();
			}
		} 
		inv.remove(mat);
		if (amount > amt) {
			inv.addItem(new ItemStack(mat, amount - amt));
		}
	}

	public static boolean isInt(final String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (final NumberFormatException numberFormatException) {
			return false;
		} 
	}  

	public enum Pane {
		WHITE(0),
		ORANGE(1),
		MAGENTA(2),
		LIGHT_BLUE(3),
		YELLOW(4),
		LIME(5),
		PINK(6),
		GRAY(7),
		LIGHT_GRAY(8),
		CYAN(9),
		PURPLE(10),
		BLUE(11),
		BROWN(12),
		GREEN(13),
		RED(14),
		BLACK(15);	    
		private final int value;
		Pane(final int value) {
			this.value = value;
		}	    
		public int value() {
			return value;
		}
	}




	public static boolean isArmour(final Material m) {
		return Enchantment.PROTECTION_ENVIRONMENTAL.canEnchantItem(new ItemStack(m));
	}
	public static boolean isWeapon(final Material m) {
		return Enchantment.DAMAGE_ALL.canEnchantItem(new ItemStack(m));
	}

	public static boolean isTool(final Material m) {
		return Enchantment.DIG_SPEED.canEnchantItem(new ItemStack(m));
	}

	public static boolean isType(final Material m, final String name) {
		// where name is DIAMOND, GOLD, IRON, LEATHER, CHAIN, SWORD, _AXE, PICKAXE
		return m.toString().contains(name);
	}





//	public static String getName(EntityType e) {
//		if (e.equals(EntityType.PIG_ZOMBIE))
//			return "Zombie Pigman"; 
//		if (!e.toString().contains("_"))
//			return String.valueOf(e.toString().substring(0, 1).toUpperCase()) + e.toString().substring(1).toLowerCase(); 
//		String[] split = e.toString().split("_");
//		String name = "";
//		String[] arrayOfString1;
//		int j = (arrayOfString1 = split).length;
//		for (int i = 0; i < j; i++) {
//			String s = arrayOfString1[i];
//			name = String.valueOf(name) + s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
//		} 
//		return name.trim();
//	}

//	public static EntityType getEntity(String e) {
//		if (e.equalsIgnoreCase("Zombie Pigman"))
//			return EntityType.PIG_ZOMBIE; 
//		e = e.replaceAll(" ", "_");
//		if (!e.contains("_"))
//			return EntityType.valueOf(e.toUpperCase()); 
//		String[] split = e.toString().split(" ");
//		String name = "";
//		String[] arrayOfString1;
//		int j = (arrayOfString1 = split).length;
//		for (int i = 0; i < j; i++) {
//			String s = arrayOfString1[i];
//			name = String.valueOf(name) + s.toUpperCase() + "_";
//		} 
//		return EntityType.valueOf(name.substring(0, name.length() - 1));
//	}



}

