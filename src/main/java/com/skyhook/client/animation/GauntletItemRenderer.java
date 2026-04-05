package com.skyhook.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.skyhook.SkyhookMod;
import com.skyhook.item.GauntletItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GauntletItemRenderer extends GeoItemRenderer<GauntletItem> {

    private static final ModelResourceLocation ICON_MODEL = new ModelResourceLocation(
            ResourceLocation.fromNamespaceAndPath(SkyhookMod.MOD_ID, "item/gauntlet_icon"), "standalone"
    );

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

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context,
                             PoseStack poseStack, MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {
        if (context == ItemDisplayContext.GUI) {
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(ICON_MODEL);
            poseStack.pushPose();
            model.getTransforms().getTransform(context).apply(false, poseStack);
            Minecraft.getInstance().getItemRenderer().render(stack, context, false, poseStack, buffer, packedLight, packedOverlay, model);
            poseStack.popPose();
            return;
        }
        super.renderByItem(stack, context, poseStack, buffer, packedLight, packedOverlay);
    }
}
