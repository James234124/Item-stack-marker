package com.itemstackmarker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

public class ItemStackMarkerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MarkerConfig.load();
        // If nothing shows up in game, try BEFORE_ENTITIES here instead.
        WorldRenderEvents.AFTER_ENTITIES.register(MarkerRenderer::render);
    }
}
