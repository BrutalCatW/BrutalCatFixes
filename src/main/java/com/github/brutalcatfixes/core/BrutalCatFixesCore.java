package com.github.brutalcatfixes.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.brutalcatfixes.config.BrutalCatFixesConfig;
import com.github.brutalcatfixes.mixins.Mixins;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class BrutalCatFixesCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.brutalcatfixes.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        try {
            ConfigurationManager.registerConfig(BrutalCatFixesConfig.class);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
        return IMixins.getEarlyMixins(Mixins.class, loadedCoreMods);
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
