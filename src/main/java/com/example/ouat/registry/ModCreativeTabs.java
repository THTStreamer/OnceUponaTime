package com.example.ouat.registry;

import com.example.ouat.OnceUponATime;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OnceUponATime.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> OUAT_TAB =
            CREATIVE_MODE_TABS.register("ouat_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup." + OnceUponATime.MOD_ID + ".main"))
                            .icon(() -> new ItemStack(ModItems.DARK_ONE_DAGGER.get()))
                            .displayItems((params, output) -> {
                                // Artifacts & Core Items
                                output.accept(ModItems.DARK_ONE_DAGGER.get());
                                output.accept(ModItems.AUTHORS_QUILL.get());
                                output.accept(ModItems.AUTHORS_BOOK.get());
                                output.accept(ModItems.ENCHANTED_ROSE.get());
                                output.accept(ModItems.MAGIC_MIRROR.get());
                                output.accept(ModItems.STOLEN_HEART.get());
                                output.accept(ModItems.CURSED_TALISMAN.get());
                                // Ritual Ingredients
                                output.accept(ModItems.SHARD_OF_DARK_POWER.get());
                                output.accept(ModItems.ESSENCE_OF_SHADOW.get());
                                output.accept(ModItems.HEART_OF_DARKNESS.get());
                                output.accept(ModItems.SHARD_OF_LIGHT.get());
                                output.accept(ModItems.ESSENCE_OF_HOPE.get());
                                output.accept(ModItems.CRYSTAL_OF_PURITY.get());
                                output.accept(ModItems.TEAR_OF_TRUE_LOVE.get());
                                output.accept(ModItems.ESSENCE_OF_BELIEF.get());
                                output.accept(ModItems.HEART_OF_INNOCENCE.get());
                                output.accept(ModItems.INK_OF_CREATION.get());
                                output.accept(ModItems.QUILL_OF_FATE.get());
                                output.accept(ModItems.PAGE_OF_DESTINY.get());
                                // Grimoires
                                output.accept(ModItems.GRIMOIRE_LIGHT.get());
                                output.accept(ModItems.GRIMOIRE_DARK.get());
                                // Show-Accurate Enchanted Items
                                output.accept(ModItems.EXCALIBUR.get());
                                output.accept(ModItems.EXCALIBUR_STONE.get());
                                output.accept(ModItems.BLUE_FAIRY_WAND.get());
                                output.accept(ModItems.MALEFICENT_STAFF.get());
                                output.accept(ModItems.JEFFERSONS_HAT.get());
                                output.accept(ModItems.CHIPPED_CUP.get());
                                output.accept(ModItems.FLYING_BROOMSTICK.get());
                                output.accept(ModItems.ENCHANTED_COMPASS.get());
                                output.accept(ModItems.ENCHANTED_CANDLE.get());
                                // Consumable Magic Items
                                output.accept(ModItems.MAGIC_BEANS.get());
                                output.accept(ModItems.FAIRY_DUST.get());
                                output.accept(ModItems.TRUE_LOVE_POTION.get());
                                output.accept(ModItems.POISONED_APPLE.get());
                                output.accept(ModItems.DREAM_CATCHER.get());
                                output.accept(ModItems.MAGIC_GLOBE.get());
                                output.accept(ModItems.MEMORY_POTION.get());
                                output.accept(ModItems.NIGHT_ROOT.get());
                                output.accept(ModItems.SQUID_INK.get());
                            })
                            .build()
            );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
