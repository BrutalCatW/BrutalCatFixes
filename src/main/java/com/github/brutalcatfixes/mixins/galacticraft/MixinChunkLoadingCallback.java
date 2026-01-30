package com.github.brutalcatfixes.mixins.galacticraft;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.world.ChunkLoadingCallback;

/**
 * Mixin to fix GC-SaveThread blocking disk IO by making save() async-safe.
 *
 * Solution 3 from GALACTICRAFT_SAVETHREAD_FIX.md: - Clone chunkLoaderList data to avoid blocking main thread - Allow
 * save() to work asynchronously without race conditions
 */
@Mixin(value = ChunkLoadingCallback.class, remap = false)
public class MixinChunkLoadingCallback {

    @Shadow
    @Final
    private static HashMap<String, HashMap<Integer, HashSet<ChunkCoordinates>>> chunkLoaderList;

    /**
     * @author BrutalCatFixes
     * @reason Make save() async-safe by cloning data before disk IO
     */
    @Overwrite
    public static void save(WorldServer world) {
        // Clone the data to make this method async-safe
        final HashMap<String, HashMap<Integer, HashSet<ChunkCoordinates>>> dataToSave;
        synchronized (chunkLoaderList) {
            // Deep copy to prevent concurrent modification
            dataToSave = new HashMap<>();
            for (Entry<String, HashMap<Integer, HashSet<ChunkCoordinates>>> playerEntry : chunkLoaderList.entrySet()) {
                HashMap<Integer, HashSet<ChunkCoordinates>> dimensionMapCopy = new HashMap<>();
                for (Entry<Integer, HashSet<ChunkCoordinates>> dimEntry : playerEntry.getValue().entrySet()) {
                    dimensionMapCopy.put(dimEntry.getKey(), new HashSet<>(dimEntry.getValue()));
                }
                dataToSave.put(playerEntry.getKey(), dimensionMapCopy);
            }
        }

        final File saveDir = getSaveDir();

        if (saveDir != null) {
            final File saveFile = new File(saveDir, "chunkloaders.dat");

            if (!saveFile.exists()) {
                try {
                    if (!saveFile.createNewFile()) {
                        GCLog.severe("Could not create chunk loader data file: " + saveFile.getAbsolutePath());
                    }
                } catch (final IOException e) {
                    GCLog.severe("Could not create chunk loader data file: " + saveFile.getAbsolutePath());
                    e.printStackTrace();
                }
            }

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(saveFile);
            } catch (final FileNotFoundException e) {
                e.printStackTrace();
            }
            if (fos != null) {
                final DataOutputStream dataStream = new DataOutputStream(fos);
                try {
                    // Use cloned data instead of original chunkLoaderList
                    dataStream.writeInt(dataToSave.size());

                    for (final Entry<String, HashMap<Integer, HashSet<ChunkCoordinates>>> playerEntry : dataToSave
                            .entrySet()) {
                        dataStream.writeUTF(playerEntry.getKey());
                        dataStream.writeInt(playerEntry.getValue().size());

                        for (final Entry<Integer, HashSet<ChunkCoordinates>> dimensionEntry : playerEntry.getValue()
                                .entrySet()) {
                            dataStream.writeInt(dimensionEntry.getKey());
                            dataStream.writeInt(dimensionEntry.getValue().size());

                            for (final ChunkCoordinates coords : dimensionEntry.getValue()) {
                                dataStream.writeInt(coords.posX);
                                dataStream.writeInt(coords.posY);
                                dataStream.writeInt(coords.posZ);
                            }
                        }
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                try {
                    dataStream.close();
                    fos.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Shadow
    private static File getSaveDir() {
        if (DimensionManager.getWorld(0) != null) {
            final File saveDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "galacticraft");

            if (!saveDir.exists() && !saveDir.mkdirs()) {
                GCLog.severe("Could not create chunk loader save data folder: " + saveDir.getAbsolutePath());
            }

            return saveDir;
        }
        return null;
    }
}
