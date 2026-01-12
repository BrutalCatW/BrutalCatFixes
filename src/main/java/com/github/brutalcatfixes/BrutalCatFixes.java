package com.github.brutalcatfixes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = BrutalCatFixes.MODID,
        version = Tags.VERSION,
        name = "BrutalCatFixes",
        acceptedMinecraftVersions = "[1.7.10]",
        dependencies = "after:Thaumcraft")
public class BrutalCatFixes {

    public static final String MODID = "brutalcatfixes";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOG.info("BrutalCatFixes preInit - Loading performance fixes for various mods");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        LOG.info("BrutalCatFixes init");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LOG.info("BrutalCatFixes postInit - All performance fixes loaded");
    }
}
