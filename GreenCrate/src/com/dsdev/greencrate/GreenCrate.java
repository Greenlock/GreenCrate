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
        if (cmd.getName().equalsIgnoreCase("crate") && sender.hasPermission("greencrate.crate")) {
            if (args.length < 1) {
                if (sender instanceof Player) {
                    Player target = (Player) sender;

                    target.sendMessage("§2[§aGreenCrate§2]§r Commands:");
                    target.sendMessage("§r§6/crate spawn <cratename>§r  --  Spawns in a new crate item.");
                    target.sendMessage("§r§6/crate give <player> <cratename>§r  --  Gives the specified player a given crate.");
                    target.sendMessage("§r§6/crate random§r  --  Spawns in a random crate.");
                    target.sendMessage("§r§6/crate giverandom <player>§r  --  Gives the specified player a random crate.");
                    target.sendMessage("§r§6/crate open <cratename>§r  --  Opens a given crate.");
                    target.sendMessage("§r§6/crate openrandom§r  --  Opens a random crate.");
                    target.sendMessage("§r§6/crate reload§r  --  Reloads the GreenCrate config.yml file.");
                    return true;
                } else {
                    return false;
                }
            }


            //--  /crate spawn <name>
            if (args[0].equals("spawn")) {
                if (!(sender instanceof Player)) {
                    return true;
                }

                Player target = (Player) sender;

                if (!(sender.hasPermission("greencrate.crate.spawn"))) {
                    target.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
                }

                if (args.length < 2) {
                    target.sendMessage("§2[§aGreenCrate§2]§r Not enough arguments supplied.");
                }

                if (getConfig().getString("crates." + args[1] + ".item-id") == null) {
                    target.sendMessage("§2[§aGreenCrate§2]§r Specified crate does not exist.");
                    return true;
                }

                int randresult = rand.nextInt(1000);

                ItemStack crate = new ItemStack(getConfig().getInt("crates." + args[1] + ".item-id"), 1, (short) getConfig().getInt("crates." + args[1] + ".item-data"));
                ItemMeta cratemeta = crate.getItemMeta();

                if (!getConfig().getBoolean("crates." + args[1] + ".enable-crate-number")) {
                    cratemeta.setDisplayName(getConfig().getString("crates." + args[1] + ".display-name").replace("&", "§"));
                } else {
                    cratemeta.setDisplayName(getConfig().getString("crates." + args[1] + ".display-name").replace("&", "§") + " #" + Integer.toString(randresult));
                }

                ArrayList<String> lore = new ArrayList();
                lore.add(args[1].replace("_", " "));
                cratemeta.setLore(lore);

                crate.setItemMeta(cratemeta);

                target.getInventory().addItem(crate);
                target.sendMessage("§2[§aGreenCrate§2]§r Spawned in crate \"" + args[1].replace("_", " ") + "\"");
            } //--  /crate give <player> <name>
            else if (args[0].equals("give")) {
                Player target = getServer().getPlayer(args[1]);

                Player tsender = null;
                if (sender instanceof Player) {
                    tsender = (Player) sender;
                }

                if (!(tsender.hasPermission("greencrate.crate.give"))) {
                    target.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
                }

                if (args.length < 3) {
                    if (tsender != null) {
                        tsender.sendMessage("§2[§aGreenCrate§2]§r Not enough arguments supplied.");
                    }
                    return true;
                }

                if (getConfig().getString("crates." + args[2] + ".item-id") == null) {
                    if (tsender != null) {
                        tsender.sendMessage("§2[§aGreenCrate§2]§r Specified crate does not exist.");
                    }
                    return true;
                }

                int randresult = rand.nextInt(1000);

                ItemStack crate = new ItemStack(getConfig().getInt("crates." + args[2] + ".item-id"), 1, (short) getConfig().getInt("crates." + args[2] + ".item-data"));
                ItemMeta cratemeta = crate.getItemMeta();

                if (!getConfig().getBoolean("crates." + args[2] + ".enable-crate-number")) {
                    cratemeta.setDisplayName(getConfig().getString("crates." + args[2] + ".display-name").replace("&", "§"));
                } else {
                    cratemeta.setDisplayName(getConfig().getString("crates." + args[2] + ".display-name").replace("&", "§") + " #" + Integer.toString(randresult));
                }

                ArrayList<String> lore = new ArrayList<String>();
                lore.add(args[2].replace("_", " "));
                cratemeta.setLore(lore);

                crate.setItemMeta(cratemeta);

                target.getInventory().addItem(crate);
                tsender.sendMessage("§2[§aGreenCrate§2]§r Gave crate \"" + args[2].replace("_", " ") + "\" to player " + target.getName());
            } //--  /crate random
            else if (args[0].equals("random")) {
                Player target = (Player) sender;

                if (!(sender.hasPermission("greencrate.crate.random"))) {
                    target.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
                }

                int crateindex = rand.nextInt(getConfig().getConfigurationSection("crates").getKeys(false).size());
                String cratename = (String) getConfig().getConfigurationSection("crates").getKeys(false).toArray()[crateindex];

                int randresult = rand.nextInt(1000);

                ItemStack crate = new ItemStack(getConfig().getInt("crates." + cratename + ".item-id"), 1, (short) getConfig().getInt("crates." + cratename + ".item-data"));
                ItemMeta cratemeta = crate.getItemMeta();

                if (!getConfig().getBoolean("crates." + cratename + ".enable-crate-number")) {
                    cratemeta.setDisplayName(getConfig().getString("crates." + cratename + ".display-name").replace("&", "§"));
                } else {
                    cratemeta.setDisplayName(getConfig().getString("crates." + cratename + ".display-name").replace("&", "§") + " #" + Integer.toString(randresult));
                }

                ArrayList<String> lore = new ArrayList<String>();
                lore.add(cratename.replace("_", " "));
                cratemeta.setLore(lore);

                crate.setItemMeta(cratemeta);

                target.getInventory().addItem(crate);
                target.sendMessage("§2[§aGreenCrate§2]§r Gave random crate \"" + cratename.replace("_", " ") + "\"");
            } //--  /crate giverandom <player>
            else if (args[0].equals("giverandom")) {
                Player target = getServer().getPlayer(args[1]);

                Player tsender = null;
                if (sender instanceof Player) {
                    tsender = (Player) sender;
                }

                if (!(tsender.hasPermission("greencrate.crate.giverandom"))) {
                    target.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
                }

                if (args.length < 2) {
                    if (tsender != null) {
                        tsender.sendMessage("§2[§aGreenCrate§2]§r Not enough arguments supplied.");
                    }
                    return true;
                }

                int crateindex = rand.nextInt(getConfig().getConfigurationSection("crates").getKeys(false).size());
                String cratename = (String) getConfig().getConfigurationSection("crates").getKeys(false).toArray()[crateindex];

                int randresult = rand.nextInt(1000);

                ItemStack crate = new ItemStack(getConfig().getInt("crates." + cratename + ".item-id"), 1, (short) getConfig().getInt("crates." + cratename + ".item-data"));
                ItemMeta cratemeta = crate.getItemMeta();

                if (!getConfig().getBoolean("crates." + cratename + ".enable-crate-number")) {
                    cratemeta.setDisplayName(getConfig().getString("crates." + cratename + ".display-name").replace("&", "§"));
                } else {
                    cratemeta.setDisplayName(getConfig().getString("crates." + cratename + ".display-name").replace("&", "§") + " #" + Integer.toString(randresult));
                }

                ArrayList<String> lore = new ArrayList();
                lore.add(cratename.replace("_", " "));
                cratemeta.setLore(lore);

                crate.setItemMeta(cratemeta);

                target.getInventory().addItem(crate);
                tsender.sendMessage("§2[§aGreenCrate§2]§r Gave random crate \"" + cratename.replace("_", " ") + "\" to player " + target.getName());
            } //--  /crate open <cratename>
            else if (args[0].equals("open")) {
                if (!(sender instanceof Player)) {
                    return false;
                }

                Player target = (Player) sender;

                if (!(sender.hasPermission("greencrate.crate.open"))) {
                    target.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
                }

                if (args.length < 2) {
                    target.sendMessage("§2[§aGreenCrate§2]§r Not enough arguments supplied.");
                }

                List<ItemStack> items = listen.GetCrateItems(args[1]);

                if (getConfig().getBoolean("crates." + args[1] + ".gui.enabled")) {
                    Inventory inv = target.getServer().createInventory(null, getConfig().getInt("crates." + args[1] + ".gui.chest-rows") * 9, getConfig().getString("crates." + args[1] + ".gui.label"));

                    for (int i = 0; i < items.size(); i++) {
                        inv.setItem(i, (ItemStack) items.toArray()[i]);
                    }

                    target.openInventory(inv);
                } else {
                    for (ItemStack i : items) {
                        target.getInventory().addItem(i);
                    }

                    target.updateInventory();
                }
            } //--  /crate openrandom
            else if (args[0].equals("openrandom")) {
                if (!(sender instanceof Player)) {
                    return false;
                }

                Player target = (Player) sender;

                if (!(sender.hasPermission("greencrate.crate.openrandom"))) {
                    target.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
                }

                int crateindex = rand.nextInt(getConfig().getConfigurationSection("crates").getKeys(false).size());
                String cratename = (String) getConfig().getConfigurationSection("crates").getKeys(false).toArray()[crateindex];

                List<ItemStack> items = listen.GetCrateItems(cratename);

                if (getConfig().getBoolean("crates." + cratename + ".gui.enabled")) {
                    Inventory inv = target.getServer().createInventory(null, getConfig().getInt("crates." + cratename + ".gui.chest-rows") * 9, getConfig().getString("crates." + cratename + ".gui.label"));

                    for (int i = 0; i < items.size(); i++) {
                        inv.setItem(i, (ItemStack) items.toArray()[i]);
                    }

                    target.openInventory(inv);
                } else {
                    for (ItemStack i : items) {
                        target.getInventory().addItem(i);
                    }

                    target.updateInventory();
                }
            } //--  /crate reload
            else if (args[0].equals("reload")) {
                if (!(sender.hasPermission("greencrate.crate.reload"))) {
                    Player target = (Player) sender;
                    target.sendMessage("§2[§aGreenCrate§2]§r You do not have permission for this command.");
                }

                reloadConfig();
                HandlerList.unregisterAll(this);
                getServer().getPluginManager().registerEvents(new CrateListener(getConfig(), getServer()), this);

                Player target = (Player) sender;
                target.sendMessage("§2[§aGreenCrate§2]§r Config reloaded!");
            } //--  /crate
            else {
                if (!(sender instanceof Player)) {
                    return true;
                }

                Player target = (Player) sender;

                target.sendMessage("§2[§aGreenCrate§2]§r Commands:");
                target.sendMessage("§r§6/crate spawn <cratename>§r  --  Spawns in a new crate item.");
                target.sendMessage("§r§6/crate give <player> <cratename>§r  --  Gives the specified player a given crate.");
                target.sendMessage("§r§6/crate random§r  --  Spawns in a random crate.");
                target.sendMessage("§r§6/crate giverandom <player>§r  --  Gives the specified player a random crate.");
                target.sendMessage("§r§6/crate open <cratename>§r  --  Opens a given crate.");
                target.sendMessage("§r§6/crate openrandom§r  --  Opens a random crate.");
                target.sendMessage("§r§6/crate reload§r  --  Reloads the GreenCrate config.yml file.");
            }
        }
        return true;
    }

    public ItemStack GetCrateItemStack(String cratename) {
        ItemStack crate = new ItemStack(getConfig().getInt("crates." + cratename + ".item-id"), 1, (short) getConfig().getInt("crates." + cratename + ".item-data"));
        ItemMeta cratemeta = crate.getItemMeta();

        if (!getConfig().getBoolean("crates." + cratename + ".enable-crate-number")) {
            cratemeta.setDisplayName(getConfig().getString("crates." + cratename + ".display-name").replace("&", "§"));
        } else {
            cratemeta.setDisplayName(getConfig().getString("crates." + cratename + ".display-name").replace("&", "§") + " #" + Integer.toString(rand.nextInt(1000)));
        }

        ArrayList<String> lore = new ArrayList();
        lore.add(cratename.replace("_", " "));
        cratemeta.setLore(lore);

        crate.setItemMeta(cratemeta);
        
        return crate;
    }

    public boolean HasCrateRequirements(String permnode, boolean isPlayer) {
        return true;
    }
}
