/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dsdev.greencrate;

import org.bukkit.inventory.Inventory;

/**
 *
 * @author Greenlock28
 */
public class OpenCrateInstance {
    public String PlayerName = null;
    public String CrateName = null;
    public Inventory InvHandle = null;
    
    public OpenCrateInstance(String p, String c, Inventory i)
    {
        PlayerName = p;
        CrateName = c;
        InvHandle = i;
    }
}
