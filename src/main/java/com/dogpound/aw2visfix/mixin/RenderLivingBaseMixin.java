package com.dogpound.aw2visfix.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

/**
 * AW2 NPC Vis Fix (vanilla-target variant).
 *
 * Mixes into vanilla RenderLivingBase#doRender (SRG func_76986_a) — a class that is ALWAYS
 * loaded, so the mixin config never fails the "target not found" check that AW2's own
 * RenderNpcBase hits during MixinBooter's early gather phase. Every action is gated on
 * (entity instanceof NpcBase) so ONLY Ancient Warfare 2 NPCs are touched; vanilla and other
 * mobs are untouched.
 */
@Mixin(net.minecraft.client.renderer.entity.RenderLivingBase.class)
public abstract class RenderLivingBaseMixin {

    private static final String TAG = "[AW2VisFix]";
    private static final HashMap<String, Long> AW2VISFIX_LASTLOG = new HashMap<String, Long>();
    private static final long AW2VISFIX_THROTTLE_MS = 3000L;
    private static final ThreadLocal<Boolean> AW2VISFIX_CLEARED =
            new ThreadLocal<Boolean>() { @Override protected Boolean initialValue() { return Boolean.FALSE; } };

    @Inject(method = "doRender", at = @At("HEAD"))
    private void aw2visfix$head(EntityLivingBase entity, double x, double y, double z,
                               float yaw, float partialTicks, CallbackInfo ci) {
        AW2VISFIX_CLEARED.set(Boolean.FALSE);
        if (!(entity instanceof NpcBase)) return;          // AW2 NPCs only
        NpcBase npc = (NpcBase) entity;
        boolean invisible = npc.isInvisible();
        try {
            String cls = npc.getClass().getName();
            long now = System.currentTimeMillis();
            Long last = AW2VISFIX_LASTLOG.get(cls);
            if (last == null || (now - last) >= AW2VISFIX_THROTTLE_MS) {
                AW2VISFIX_LASTLOG.put(cls, now);
                ResourceLocation tex = null; String texStr = "<none>"; boolean texResolves = false;
                try {
                    tex = npc.getTexture();
                    if (tex != null) {
                        texStr = tex.toString();
                        Minecraft mc = Minecraft.getMinecraft();
                        if (mc != null) {
                            TextureManager tm = mc.getTextureManager();
                            if (tm != null) { ITextureObject o = tm.getTexture(tex); texResolves = (o != null); }
                        }
                    }
                } catch (Throwable t) { texStr = "<error:" + t + ">"; }
                System.out.println(TAG + " doRender RAN for " + cls + " | isInvisible=" + invisible
                        + " | texture=" + texStr + " | textureResolves=" + texResolves
                        + (invisible ? " | -> clearing invisible flag for this pass" : ""));
            }
        } catch (Throwable ignored) {}
        try {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
        } catch (Throwable ignored) {}
        if (invisible) {
            try { npc.setInvisible(false); AW2VISFIX_CLEARED.set(Boolean.TRUE); }
            catch (Throwable ignored) { AW2VISFIX_CLEARED.set(Boolean.FALSE); }
        }
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    private void aw2visfix$ret(EntityLivingBase entity, double x, double y, double z,
                              float yaw, float partialTicks, CallbackInfo ci) {
        if (entity instanceof NpcBase && Boolean.TRUE.equals(AW2VISFIX_CLEARED.get())) {
            try { ((NpcBase) entity).setInvisible(true); } catch (Throwable ignored) {}
        }
        AW2VISFIX_CLEARED.set(Boolean.FALSE);
    }
}
