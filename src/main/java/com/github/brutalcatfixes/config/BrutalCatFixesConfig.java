package com.github.brutalcatfixes.config;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = "brutalcatfixes", category = "fixes")
@Config.RequiresMcRestart
public class BrutalCatFixesConfig {

    @Config.Comment("Enable Thaumcraft Utils optimization (replaces inefficient O(240) loop with O(1) heightmap lookup)")
    @Config.DefaultBoolean(true)
    public static boolean fixThaumcraftUtils = true;

    @Config.Comment("Enable Galacticraft async-safe chunk loader save fix")
    @Config.DefaultBoolean(false)
    public static boolean fixGalacticraftSave;

    @Config.Comment("Limit the number of recursive cascading block updates during world generation to prevent stack overflow crashes, set to -1 to disable the limit.")
    @Config.RangeInt(min = -1)
    @Config.DefaultInt(256)
    public static int limitRecursiveBlockUpdateDepth;
}
