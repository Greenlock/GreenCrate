/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsdev.greencrate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Greenlock28
 */
public class GreenCrate extends JavaPlugin {

    @Override
    public void onEnable() {
        File defaultConfig = new File(getDataFolder(), "config.yml");
        if (!defaultConfig.exists()) {
            getLogger().info("Saving default GreenCrate config file...");
            saveDefaultConfig();
        }
        reloadConfig();

        listen = new CrateListener(getConfig(), getServer());

        getServer().getPluginManager().registerEvents(listen, this);

        getLogger().info("GreenCrate has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GreenCrate has been disabled!");
    }
    
    
    
    private Random rand = new Random();
    private CrateListener listen;

    
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("crate") && sender.hasPermission("greencrate.crate") && sender instanceof Player) {
            
            Player sendingPlayer = (Player)sender;
            
            if (args.length < 1) {
                DoCommandHelp(sender, sendingPlayer);
                return true;
            }
            
            switch (args[0]) {
                case "spawn":
                    DoCommandSpawn(sender, sendingPlayer, args);
                    break;
                case "give":
                    DoCommandGive(sender, sendingPlayer, args);
                    break;
                case "random":
                    DoCommandRandom(sender, sendingPlayer, args);
                    break;
                case "giverandom":
                    DoCommandGiveRandom(sender, sendingPlayer, args);
                    break;
                case "open":
                    DoCommandOpen(sender, sendingPlayer, args);
                    break;
                case "openrandom":
                    DoCommandOpenRandom(sender, sendingPlayer, args);
                    break;
                case "reload":
                    DoCommandReload(sender, sendingPlayer, args);
                    break;
                case "help":
                    DoCommandHelp(sender, sendingPlayer);
                    break;
                case "?":
                    DoCommandHelp(sender, sendingPlayer);
                    break;
                default:
                    DoCommandHelp(sender, sendingPlayer);
                    break;
            }
        }
        return true;
    }

    
    
    public void DoCommandSpawn(CommandSender cmdSender, Player sender, String[] args) {
        
        if (!(sender.hasPermission("greencrate.crate.spawn"))) {
            sender.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage("§2[§aGreenCrate§2]§r Not enough arguments supplied.");
            return;
        }

        if (getConfig().getString("crates." + args[1] + ".item-id") == null) {
            sender.sendMessage("§2[§aGreenCrate§2]§r Specified crate does not exist.");
            return;
        }

        sender.getInventory().addItem(GetCrateItemStack(args[1]));
        
        sender.sendMessage("§2[§aGreenCrate§2]§r Spawned in crate \"" + args[1].replace("_", " ") + "\"");
    }

    public void DoCommandGive(CommandSender cmdSender, Player sender, String[] args) {
        
        Player target = getServer().getPlayer(args[1]);

        if (!(sender.hasPermission("greencrate.crate.give"))) {
            sender.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage("§2[§aGreenCrate§2]§r Not enough arguments supplied.");
            return;
        }

        if (getConfig().getString("crates." + args[2] + ".item-id") == null) {
            sender.sendMessage("§2[§aGreenCrate§2]§r Specified crate does not exist.");
            return;
        }

        target.getInventory().addItem(GetCrateItemStack(args[2]));
        
        sender.sendMessage("§2[§aGreenCrate§2]§r Gave crate \"" + args[2].replace("_", " ") + "\" to player " + target.getName());
    }

    public void DoCommandRandom(CommandSender cmdSender, Player sender, String[] args) {
        
        if (!(sender.hasPermission("greencrate.crate.random"))) {
            sender.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
            return;
        }

        int crateindex = rand.nextInt(getConfig().getConfigurationSection("crates").getKeys(false).size());
        String cratename = (String) getConfig().getConfigurationSection("crates").getKeys(false).toArray()[crateindex];

        sender.getInventory().addItem(GetCrateItemStack(cratename));
        
        sender.sendMessage("§2[§aGreenCrate§2]§r Spawned in random crate \"" + cratename.replace("_", " ") + "\"");
    }

    public void DoCommandGiveRandom(CommandSender cmdSender, Player sender, String[] args) {
        
        Player target = getServer().getPlayer(args[1]);

        if (!(sender.hasPermission("greencrate.crate.giverandom"))) {
            sender.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage("§2[§aGreenCrate§2]§r Not enough arguments supplied.");
            return;
        }

        int crateindex = rand.nextInt(getConfig().getConfigurationSection("crates").getKeys(false).size());
        String cratename = (String) getConfig().getConfigurationSection("crates").getKeys(false).toArray()[crateindex];

        sender.getInventory().addItem(GetCrateItemStack(cratename));
        
        sender.sendMessage("§2[§aGreenCrate§2]§r Gave random crate \"" + cratename.replace("_", " ") + "\" to player " + target.getName());
    }

    public void DoCommandOpen(CommandSender cmdSender, Player sender, String[] args) {
        
        if (!(sender.hasPermission("greencrate.crate.open"))) {
            sender.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
        }

        if (args.length < 2) {
            sender.sendMessage("§2[§aGreenCrate§2]§r Not enough arguments supplied.");
        }

        List<ItemStack> items = listen.GetCrateItems(args[1]);

        if (getConfig().getBoolean("crates." + args[1] + ".gui.enabled")) {
            Inventory inv = sender.getServer().createInventory(null, getConfig().getInt("crates." + args[1] + ".gui.chest-rows") * 9, getConfig().getString("crates." + args[1] + ".gui.label"));

            for (int i = 0; i < items.size(); i++) {
                inv.setItem(i, (ItemStack) items.toArray()[i]);
            }

            sender.openInventory(inv);
        } else {
            for (ItemStack i : items) {
                sender.getInventory().addItem(i);
            }

            sender.updateInventory();
        }
    }

    public void DoCommandOpenRandom(CommandSender cmdSender, Player sender, String[] args) {
        
        if (!(sender.hasPermission("greencrate.crate.openrandom"))) {
            sender.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
            return;
        }

        int crateindex = rand.nextInt(getConfig().getConfigurationSection("crates").getKeys(false).size());
        String cratename = (String) getConfig().getConfigurationSection("crates").getKeys(false).toArray()[crateindex];

        List<ItemStack> items = listen.GetCrateItems(cratename);

        if (getConfig().getBoolean("crates." + cratename + ".gui.enabled")) {
            Inventory inv = sender.getServer().createInventory(null, getConfig().getInt("crates." + cratename + ".gui.chest-rows") * 9, getConfig().getString("crates." + cratename + ".gui.label"));

            for (int i = 0; i < items.size(); i++) {
                inv.setItem(i, (ItemStack) items.toArray()[i]);
            }

            sender.openInventory(inv);
        } else {
            for (ItemStack i : items) {
                sender.getInventory().addItem(i);
            }

            sender.updateInventory();
        }
    }

    public void DoCommandReload(CommandSender cmdSender, Player sender, String[] args) {
        
        if (!(sender.hasPermission("greencrate.crate.reload"))) {
            sender.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
        }

        reloadConfig();
        HandlerList.unregisterAll(this);
        getServer().getPluginManager().registerEvents(listen, this);

        sender.sendMessage("§2[§aGreenCrate§2]§r Config reloaded!");
    }

    public void DoCommandHelp(CommandSender cmdSender, Player sender) {
        
        sender.sendMessage("§2[§aGreenCrate§2]§r Commands:");
        
        sender.sendMessage("§r§" + GetPermissionColor(cmdSender, "greencrate.crate.spawn") + "/crate spawn <cratename>§r  --  Spawns in a new crate item.");
        sender.sendMessage("§r§" + GetPermissionColor(cmdSender, "greencrate.crate.give") + "/crate give <player> <cratename>§r  --  Gives the specified player a given crate.");
        sender.sendMessage("§r§" + GetPermissionColor(cmdSender, "greencrate.crate.random") + "/crate random§r  --  Spawns in a random crate.");
        sender.sendMessage("§r§" + GetPermissionColor(cmdSender, "greencrate.crate.giverandom") + "/crate giverandom <player>§r  --  Gives the specified player a random crate.");
        sender.sendMessage("§r§" + GetPermissionColor(cmdSender, "greencrate.crate.open") + "/crate open <cratename>§r  --  Opens a given crate.");
        sender.sendMessage("§r§" + GetPermissionColor(cmdSender, "greencrate.crate.openrandom") + "/crate openrandom§r  --  Opens a random crate.");
        sender.sendMessage("§r§" + GetPermissionColor(cmdSender, "greencrate.crate.reload") + "/crate reload§r  --  Reloads the GreenCrate config.yml file.");
    }

    
    
    public ItemStack GetCrateItemStack(String cratename) {
        ItemStack crate = new ItemStack(getConfig().getInt("crates." + cratename + ".item-id"), 1, (short) getConfig().getInt("crates." + cratename + ".item-data"));
        ItemMeta cratemeta = crate.getItemMeta();

        if (getConfig().getBoolean("crates." + cratename + ".enable-crate-number")) {
            cratemeta.setDisplayName(getConfig().getString("crates." + cratename + ".display-name").replace("&", "§") + " #" + Integer.toString(rand.nextInt(1000)));
        } else {
            cratemeta.setDisplayName(getConfig().getString("crates." + cratename + ".display-name").replace("&", "§"));
        }

        if (getConfig().getBoolean("crates." + cratename + ".enable-lore-name")) {
        ArrayList<String> lore = new ArrayList();
        lore.add(cratename.replace("_", " "));
        cratemeta.setLore(lore);
        }
        
        crate.setItemMeta(cratemeta);

        return crate;
    }

    public boolean HasCrateRequirements(String permnode, boolean isPlayer) {
        return true;
    }
    
    public String GetPermissionColor(CommandSender sender, String perm) {
        if (sender.hasPermission(perm))
            return "6";
        else
            return "8";
    }
}
