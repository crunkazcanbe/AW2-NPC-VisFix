package com.dogpound.aw2visfix;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

/**
 * AW2 NPC Vis Fix — coremod loading plugin.
 *
 * Registers the render mixin config through MixinBooter's IEarlyMixinLoader. MixinBooter
 * (verified against 10.7) gathers configs ONLY from coremods that implement this interface
 * via getMixinConfigs() — it does NOT read the 'MixinConfigs' jar-manifest attribute — and
 * it does so at the correct early phase ("Gathering present mods"), so the config is
 * registered before AW2's RenderNpcBase is classloaded; the @Inject then binds lazily at
 * that classload (first NPC render). This is the same mechanism the other DogPound mixin
 * coremods use on the Cleanroom pack (where MixinBooter is bundled into the loader, so the
 * IEarlyMixinLoader interface is always resolvable during coremod discovery).
 *
 * Pure coremod: no @Mod in this jar (FMLCorePluginContainsFMLMod=false).
 */
@IFMLLoadingPlugin.Name("AW2NpcVisFix Core")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class AW2VisFixCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    private static final String CONFIG = "mixins.aw2visfix.json";

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList(CONFIG);
    }

    @Override public String[] getASMTransformerClass() { return new String[0]; }
    @Override public String getModContainerClass() { return null; }
    @Override public String getSetupClass() { return null; }
    @Override public void injectData(Map<String, Object> data) { }
    @Override public String getAccessTransformerClass() { return null; }
}
