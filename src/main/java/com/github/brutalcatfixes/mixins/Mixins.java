package com.github.brutalcatfixes.mixins;

import com.github.brutalcatfixes.config.BrutalCatFixesConfig;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixins implements IMixins {

    // spotless:off
    FIX_THAUMCRAFT_UTILS(new MixinBuilder("Optimize Thaumcraft Utils.getFirstUncoveredBlockHeight with O(1) heightmap lookup")
            .addCommonMixins("thaumcraft.MixinUtils")
            .setApplyIf(() -> BrutalCatFixesConfig.fixThaumcraftUtils)
            .setPhase(Phase.EARLY)),

    FIX_GALACTICRAFT_SAVE(new MixinBuilder("Make Galacticraft ChunkLoadingCallback.save() async-safe")
            .addCommonMixins("galacticraft.MixinChunkLoadingCallback")
            .setApplyIf(() -> BrutalCatFixesConfig.fixGalacticraftSave)
            .setPhase(Phase.EARLY)),

    LIMIT_RECURSIVE_BLOCK_UPDATE_DEPTH(new MixinBuilder("Limit the number of recursive cascading block updates during world generation to prevent stack overflow crashes")
            .addCommonMixins("minecraft.MixinWorldServer_LimitUpdateRecursion")
            .setApplyIf(() -> BrutalCatFixesConfig.limitRecursiveBlockUpdateDepth >= 0)
            .setPhase(Phase.EARLY)),
    // spotless:on
    ;

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
