package com.example.ouat.registry;

import com.example.ouat.OnceUponATime;
import com.example.ouat.menu.PotionBrewerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, OnceUponATime.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<PotionBrewerMenu>> POTION_BREWER =
            MENUS.register("potion_brewer", () ->
                    IMenuTypeExtension.create((containerId, playerInventory, data) ->
                            new PotionBrewerMenu(containerId, playerInventory)));

    public static void register(IEventBus modBus) {
        MENUS.register(modBus);
    }
}
