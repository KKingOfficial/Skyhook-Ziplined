package com.skyhook.client.animation;

import com.skyhook.SkyhookMod;
import com.skyhook.item.GauntletItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.model.GeoModel;

public class GauntletItemRenderer extends GeoItemRenderer<GauntletItem> {

    public GauntletItemRenderer() {
        super(new GeoModel<>() {
            @Override
            public ResourceLocation getModelResource(GauntletItem animatable) {
                return ResourceLocation.fromNamespaceAndPath(SkyhookMod.MOD_ID, "geo/gauntlet.geo.json");
            }

            @Override
            public ResourceLocation getTextureResource(GauntletItem animatable) {
                return ResourceLocation.fromNamespaceAndPath(SkyhookMod.MOD_ID, "textures/item/gauntlet.png");
            }

            @Override
            public ResourceLocation getAnimationResource(GauntletItem animatable) {
                return ResourceLocation.fromNamespaceAndPath(SkyhookMod.MOD_ID, "animations/gauntlet.animation.json");
            }
        });
    }
}
