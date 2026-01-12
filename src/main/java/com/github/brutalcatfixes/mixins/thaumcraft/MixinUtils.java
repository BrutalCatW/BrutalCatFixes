package com.github.brutalcatfixes.mixins.thaumcraft;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import thaumcraft.common.lib.utils.Utils;

/**
 * Optimizes Thaumcraft's Utils.getFirstUncoveredBlockHeight() which causes severe performance issues during world
 * generation.
 *
 * Original implementation has two critical problems: 1. BUG in loop condition: "|| var3 > 250" causes the loop to
 * continue when var3 > 250, potentially causing infinite loops or out-of-bounds access (Y > 255) 2. INEFFICIENCY: Loop
 * from Y=10 to Y=250 calls world.isAirBlock() up to 240 times per position - BiomeGenTaint.decorateSpecial() calls this
 * 8 times per chunk - Result: 8 × 240 = 1920 isAirBlock() calls per chunk
 *
 * Fix: Replace with world.getHeightValue(x, z) which uses pre-computed heightmap - O(1) instead of O(240) - No loop, no
 * repeated block access - Significantly improves TPS during Deep Dark generation
 */
@Mixin(value = Utils.class, remap = false)
public class MixinUtils {

    /**
     * @author BrutalCatFixes
     * @reason Replace inefficient loop (240 iterations) with O(1) heightmap lookup. Original code has a bug in loop
     *         condition (|| var3 > 250) and causes severe performance issues during world generation.
     */
    @Overwrite
    public static int getFirstUncoveredBlockHeight(World world, int x, int z) {
        // Use heightmap for O(1) lookup instead of O(240) loop
        int height = world.getHeightValue(x, z);

        // Debug logging (disabled by default)
        // BrutalCatFixes.LOG.debug("getFirstUncoveredBlockHeight({}, {}) = {}", x, z, height);

        return height;
    }
}
