package com.skyhook.mixin;

import com.skyhook.init.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemInHandRenderer.class, priority = 900)
public abstract class SkyhookItemInHandRendererMixin {

    @Shadow
    public abstract void renderItem(LivingEntity livingEntity, ItemStack itemStack, ItemDisplayContext itemDisplayContext, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i);

    @Shadow
    protected abstract void renderPlayerArm(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, float f, float g, HumanoidArm humanoidArm);

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    void skyhook$renderArmWithItem(AbstractClientPlayer player, float tickDelta, float xRot, InteractionHand hand, float attackAnim, ItemStack stack, float handHeight, PoseStack poseStack, MultiBufferSource buffer, int light, CallbackInfo ci) {

        if (!stack.is(ModItems.GAUNTLET) || !player.isUsingItem()) {
            return;
        }

        poseStack.pushPose();

        boolean isMain = hand == InteractionHand.MAIN_HAND;
        HumanoidArm arm = isMain ? player.getMainArm() : player.getMainArm().getOpposite();

        boolean isRight = arm == HumanoidArm.RIGHT;
        int dir = isRight ? 1 : -1;

        skyhook$shake(stack, player, tickDelta, poseStack);

        poseStack.translate(0, 0.25, 1.10);
        poseStack.mulPose(Axis.XP.rotationDegrees(10));
        poseStack.mulPose(Axis.YN.rotationDegrees(dir * -10.0f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(dir * 70));

        renderPlayerArm(poseStack, buffer, light, 0, 0, arm);

        poseStack.pushPose();
        poseStack.translate(dir * 0.70F, 0.44F, -0.70F); // POSITION YOU CUNT
        poseStack.mulPose(Axis.XP.rotationDegrees(-270));     // X rot
        poseStack.mulPose(Axis.YP.rotationDegrees(0 * dir)); // Y rot
        poseStack.mulPose(Axis.ZP.rotationDegrees(-330 * dir)); // Z rot

        this.renderItem(
                player,
                stack,
                isRight ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                !isRight,
                poseStack,
                buffer,
                light
        );
        poseStack.popPose();

        poseStack.popPose();

        ci.cancel();
    }

    @Inject(method = "renderItem", at = @At("HEAD"))
    void skyhook$thirdPersonRotation(LivingEntity entity, ItemStack stack, ItemDisplayContext context, boolean leftHand, PoseStack poseStack, MultiBufferSource buffer, int light, CallbackInfo ci) {

        if (!(entity instanceof AbstractClientPlayer player)) return;
        if (!stack.is(ModItems.GAUNTLET)) return;
        if (!player.isUsingItem()) return;

        if (context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            poseStack.mulPose(Axis.XP.rotationDegrees(-270));
            poseStack.mulPose(Axis.YP.rotationDegrees(-360));
            poseStack.translate(0.18, 0.25F, 0.01F); //ill fuckin kill you MOVE DAMN IT
        }
    }

    @Unique
    void skyhook$shake(ItemStack stack, AbstractClientPlayer player, float tickDelta, PoseStack poseStack) {
        float useFactor = stack.getUseDuration(player) - (player.getUseItemRemainingTicks() - tickDelta + 1.0f);
        float m = Mth.sin((useFactor - 0.1f) * 1.3f);
        float q = Mth.sin((useFactor * .3f - 0.4f) * 1.3f);
        float influence = Mth.clamp((useFactor * .1f) - 0.1f, 0, 1);
        float o = m * influence;
        float l = q * influence;
        poseStack.translate(l * 0.003f, o * 0.001f, o * 0.001f);
    }
}