/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsdev.greencrate;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 *
 * @author Greenlock28
 */
public class Cooldowns {
    private static Table<String, String, Long> cooldowns = HashBasedTable.create();
    
    /**
     * Retrieve the number of milliseconds left until a given cooldown expires.
     * <p>
     * Check for a negative value to determine if a given cooldown has expired. <br>
     * Cool-downs that have never been defined will return {@link Long#MIN_VALUE}.
     * @param player - the player.
     * @param key - cool-down to locate.
     * @return Number of milliseconds until the cool-down expires.
     */
    public static long getCooldown(String playername, String key) {
        return calculateRemainder(cooldowns.get(playername, key));
    }
    
    /**
     * Update a cool-down for the specified player.
     * @param player - the player.
     * @param key - cool-down to update.
     * @param delay - number of milliseconds until the cool-down will expire again.
     * @return The previous number of milliseconds until expiration.
     */
    public static long setCooldown(String playername, String key, long delay) {
        return calculateRemainder(
                cooldowns.put(playername, key, System.currentTimeMillis() + delay));
    }
    
    /**
     * Determine if a given cool-down has expired. If it has, refresh the cool-down. If not, do nothing.
     * @param player - the player.
     * @param key - cool-down to update. 
     * @param delay - number of milliseconds until the cool-down will expire again.
     * @return TRUE if the cool-down was expired/unset and has now been reset, FALSE otherwise.
     */
    public static boolean tryCooldown(String playername, String key, long delay) {
        if (getCooldown(playername, key) <= 0) {
            setCooldown(playername, key, delay);
            return true;
        }
        return false;
    }
    
    private static long calculateRemainder(Long expireTime) {
        return expireTime != null ? expireTime - System.currentTimeMillis() : Long.MIN_VALUE;
    }
}
