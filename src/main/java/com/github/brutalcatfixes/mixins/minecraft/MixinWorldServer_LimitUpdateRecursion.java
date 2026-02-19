package com.github.brutalcatfixes.mixins.minecraft;

import net.minecraft.block.Block;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.brutalcatfixes.config.BrutalCatFixesConfig;

@Mixin(WorldServer.class)
public class MixinWorldServer_LimitUpdateRecursion {

    @Unique
    private int brutalcatfixes$currentBlockUpdateRecursiveCalls = 0;

    @Inject(method = "scheduleBlockUpdateWithPriority", at = @At("HEAD"), cancellable = true)
    private void brutalcatfixes$incrementBlockUpdateRecursionCounter(int x, int y, int z, Block block, int tickDelay,
            int priority, CallbackInfo ci) {
        if (brutalcatfixes$currentBlockUpdateRecursiveCalls >= BrutalCatFixesConfig.limitRecursiveBlockUpdateDepth) {
            ci.cancel();
            return;
        }
        brutalcatfixes$currentBlockUpdateRecursiveCalls++;
    }

    @Inject(method = "scheduleBlockUpdateWithPriority", at = @At("RETURN"))
    private void brutalcatfixes$decrementBlockUpdateRecursionCounter(int x, int y, int z, Block block, int tickDelay,
            int priority, CallbackInfo ci) {
        brutalcatfixes$currentBlockUpdateRecursiveCalls = Math
                .max(0, brutalcatfixes$currentBlockUpdateRecursiveCalls - 1);
    }
}
