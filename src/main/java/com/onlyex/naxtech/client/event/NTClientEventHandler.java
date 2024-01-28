package com.onlyex.naxtech.client.event;

import com.onlyex.naxtech.client.renderer.texture.NTTextures;
import com.onlyex.naxtech.client.shader.CosmicShaderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class NTClientEventHandler {

    public static FloatBuffer cosmicUVs = BufferUtils.createFloatBuffer(4 * 10);

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            cosmicUVs = BufferUtils.createFloatBuffer(4 * NTTextures.COSMIC.length);
            TextureAtlasSprite icon;
            for (TextureAtlasSprite cosmicIcon : NTTextures.COSMIC) {
                icon = cosmicIcon;
                cosmicUVs.put(icon.getMinU());
                cosmicUVs.put(icon.getMinV());
                cosmicUVs.put(icon.getMaxU());
                cosmicUVs.put(icon.getMaxV());
            }
            cosmicUVs.flip();
        }
    }

    @SubscribeEvent
    public void onDrawScreenPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
        CosmicShaderHelper.inventoryRender = true;
    }

    @SubscribeEvent
    public void drawScreenPost(GuiScreenEvent.DrawScreenEvent.Post event) {
        CosmicShaderHelper.inventoryRender = false;
    }
}
