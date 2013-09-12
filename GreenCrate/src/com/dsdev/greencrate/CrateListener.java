/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsdev.greencrate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Greenlock28
 */
public class CrateListener implements Listener {

    public FileConfiguration config = null;
    public Random rand = new Random();

    public CrateListener(FileConfiguration conf, Server s) {
        config = conf;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        for (String cratename : config.getConfigurationSection("crates").getKeys(false)) {
            if (config.getBoolean("crates." + cratename + ".mob-drop.enabled")) {
                if (EntityType.fromName(config.getString("crates." + cratename + ".mob-drop.mob")) == event.getEntityType()) {
                    if (rand.nextInt(100) < config.getInt("crates." + cratename + ".mob-drop.chance")) {
                        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), GetCrateItemStack(cratename));
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerUse(PlayerInteractEvent event) {
        for (String cratename : config.getConfigurationSection("crates").getKeys(false)) {
            if (event.getPlayer().getItemInHand().getTypeId() == config.getInt("crates." + cratename + ".item-id")) {
                if (event.getPlayer().getItemInHand().getDurability() == config.getInt("crates." + cratename + ".item-data")) {
                    if (event.getPlayer().getItemInHand().getItemMeta().hasLore() && event.getPlayer().getItemInHand().getItemMeta().getLore().get(0).equals(cratename)) {
                        GiveCrate(cratename, event.getPlayer(), event);
                    }
                }
            }
        }
    }

    @SuppressWarnings({"deprecation"})
    public void GiveCrate(String cratename, Player p, PlayerInteractEvent event) {
        if (config.getBoolean("crates." + cratename + ".cancel-event")) {
            event.setCancelled(true);
        }

        if (config.getBoolean("crates." + cratename + ".confiscate")) {
            if (p.getItemInHand().getAmount() == 1) {
                p.getInventory().removeItem(p.getItemInHand());
            } else {
                p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
            }
            p.updateInventory();
        }

        List<ItemStack> items = GetCrateItems(cratename);

        if (config.getBoolean("crates." + cratename + ".gui.enabled")) {
            Inventory inv = event.getPlayer().getServer().createInventory(null, config.getInt("crates." + cratename + ".gui.chest-rows") * 9, config.getString("crates." + cratename + ".gui.label"));

            for (int i = 0; i < items.size(); i++) {
                inv.setItem(i, (ItemStack) items.toArray()[i]);
            }

            p.openInventory(inv);
        } else {
            for (ItemStack i : items) {
                p.getInventory().addItem(i);
            }

            p.updateInventory();
        }

        if (config.getBoolean("crates." + cratename + ".notify-used")) {
            p.sendMessage(config.getString("crates." + cratename + ".notify-msg")
                    .replace("<displayname>", config.getString("crates." + cratename + ".display-name") + "&r")
                    .replace("<cratename>", cratename)
                    .replace("&", "§"));
        }
    }

    public List<ItemStack> GetCrateItems(String cratename) {
        List<ItemStack> ret = new ArrayList();

        for (String itemkey : config.getConfigurationSection("crates." + cratename + ".contents").getKeys(false)) {
            ItemStack item = new ItemStack(Material.POISONOUS_POTATO, 1);
            for (String itemvalue : config.getConfigurationSection("crates." + cratename + ".contents." + itemkey).getKeys(false)) {
                switch (itemvalue) {
                    case "ID":
                        item.setTypeId(config.getInt("crates." + cratename + ".contents." + itemkey + ".ID"));
                        break;
                    case "DATA":
                        item.setDurability((short) config.getInt("crates." + cratename + ".contents." + itemkey + ".DATA"));
                        break;
                    case "AMOUNT":
                        item.setAmount(config.getInt("crates." + cratename + ".contents." + itemkey + ".AMOUNT"));
                        break;
                    case "ENCHANTMENTS":
                        //if (!config.getStringList("crates." + cratename + ".contents." + itemkey + ".ENCHANTMENTS").get(0).equals("none"))
                        for (String en : config.getStringList("crates." + cratename + ".contents." + itemkey + ".ENCHANTMENTS")) {
                            try {
                                item.addEnchantment(Enchantment.getByName(en.split("-")[0]), Integer.parseInt(en.split("-")[1]));
                            } catch (Exception ex) {
                            }
                        }
                        break;
                    case "DISPLAYNAME": {
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(config.getString("crates." + cratename + ".contents." + itemkey + ".DISPLAYNAME").replace("&", "§"));
                        item.setItemMeta(meta);
                        break;
                    }
                    case "LORE": {
                        ItemMeta meta = item.getItemMeta();
                        List<String> metalore = new ArrayList();
                        for (String lore : config.getStringList("crates." + cratename + ".contents." + itemkey + ".LORE")) {
                            metalore.add(lore.replace("&", "§"));
                        }
                        meta.setLore(metalore);
                        item.setItemMeta(meta);
                        break;
                    }
                }
            }
            ret.add(item);
        }

        return ret;
    }

    public ItemStack GetCrateItemStack(String cratename) {
        int randresult = rand.nextInt(1000);

        ItemStack crate = new ItemStack(config.getInt("crates." + cratename + ".item-id"), 1, (short) config.getInt("crates." + cratename + ".item-data"));
        ItemMeta cratemeta = crate.getItemMeta();

        if (!config.getBoolean("crates." + cratename + ".disable-crate-number")) {
            cratemeta.setDisplayName(config.getString("crates." + cratename + ".display-name").replace("&", "§") + " #" + Integer.toString(randresult));
        } else {
            cratemeta.setDisplayName(config.getString("crates." + cratename + ".display-name").replace("&", "§"));
        }

        ArrayList<String> lore = new ArrayList();
        lore.add(cratename.replace("_", " "));
        cratemeta.setLore(lore);

        crate.setItemMeta(cratemeta);

        return crate;
    }
}