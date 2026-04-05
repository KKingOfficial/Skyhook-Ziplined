package com.skyhook.item;

import com.skyhook.client.animation.GauntletAnimatable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class GauntletItem extends Item implements GeoItem {

    public static final int HOLD_THRESHOLD_TICKS = 10;
    public static final int MAX_USE_DURATION = 72000;

    private static final RawAnimation ANIM_ACTIVATE = RawAnimation.begin().thenPlay("animation.gauntlet.activate");
    private static final RawAnimation ANIM_SPIN     = RawAnimation.begin().thenLoop("animation.gauntlet.spin");
    private static final RawAnimation ANIM_RELEASE  = RawAnimation.begin().thenPlay("animation.gauntlet.release");
    private static final RawAnimation ANIM_IDLE     = RawAnimation.begin().thenLoop("animation.gauntlet.idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GauntletItem() {
        super(new Item.Properties().stacksTo(1));
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }


    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            GauntletAnimatable.registerRenderer(consumer);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "gauntlet_controller", 2, this::animationHandler));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private PlayState animationHandler(AnimationState<GauntletItem> state) {
        return GauntletAnimatable.handleAnimation(state);
    }


    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        if (level instanceof ServerLevel serverLevel) {
            GeoItem.getOrAssignId(stack, serverLevel);
        }
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.PLAYERS, 1.0f, 1.4f);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (!(livingEntity instanceof Player player)) return;
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), SoundSource.PLAYERS, 0.7f, 0.8f);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return MAX_USE_DURATION;
    }
}
