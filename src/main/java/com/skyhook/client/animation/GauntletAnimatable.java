package com.skyhook.client.animation;

import com.skyhook.item.GauntletItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.function.Consumer;


@OnlyIn(Dist.CLIENT)
public final class GauntletAnimatable {

    private static final RawAnimation ANIM_ACTIVATE = RawAnimation.begin().thenPlay("animation.gauntlet.activate");
    private static final RawAnimation ANIM_SPIN     = RawAnimation.begin().thenLoop("animation.gauntlet.spin");
    private static final RawAnimation ANIM_RELEASE  = RawAnimation.begin().thenPlay("animation.gauntlet.release");
    private static final RawAnimation ANIM_IDLE     = RawAnimation.begin().thenLoop("animation.gauntlet.idle");

    private GauntletAnimatable() {}


    public static void registerRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GauntletItemRenderer renderer;

            @Override
            public GauntletItemRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new GauntletItemRenderer();
                return this.renderer;
            }
        });
    }


    public static PlayState handleAnimation(AnimationState<GauntletItem> state) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return PlayState.STOP;

        ItemStack held = player.getUseItem();
        boolean isUsing = player.isUsingItem() && held.getItem() instanceof GauntletItem;

        if (isUsing) {
            int ticksUsed = GauntletItem.MAX_USE_DURATION - player.getUseItemRemainingTicks();
            if (ticksUsed < GauntletItem.HOLD_THRESHOLD_TICKS) {
                return state.setAndContinue(ANIM_ACTIVATE);
            } else {
                return state.setAndContinue(ANIM_SPIN);
            }
        }

        AnimationController<?> ctrl = state.getController();
        String current = ctrl.getCurrentAnimation() != null
                ? ctrl.getCurrentAnimation().animation().name() : "";

        if (current.equals("animation.gauntlet.activate") || current.equals("animation.gauntlet.spin")) {
            return state.setAndContinue(ANIM_RELEASE);
        }

        if (current.equals("animation.gauntlet.release") && !ctrl.hasAnimationFinished()) {
            return PlayState.CONTINUE;
        }

        return state.setAndContinue(ANIM_IDLE);
    }
}
