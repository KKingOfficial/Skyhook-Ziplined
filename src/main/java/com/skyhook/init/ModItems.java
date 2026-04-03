package com.skyhook.init;

import com.skyhook.SkyhookMod;
import com.skyhook.item.GauntletItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(SkyhookMod.MOD_ID);

    public static final DeferredItem<Item> GAUNTLET =
            ITEMS.registerItem("gauntlet", props -> new GauntletItem());
}
