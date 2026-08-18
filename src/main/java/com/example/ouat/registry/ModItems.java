package com.example.ouat.registry;

import com.example.ouat.OnceUponATime;
import com.example.ouat.items.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.UUID;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(OnceUponATime.MOD_ID);

    public static final DeferredItem<Item> SHARD_OF_DARK_POWER = ITEMS.registerItem(
            "shard_of_dark_power", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> ESSENCE_OF_SHADOW = ITEMS.registerItem(
            "essence_of_shadow", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> HEART_OF_DARKNESS = ITEMS.registerItem(
            "heart_of_darkness", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> DARK_ONE_DAGGER = ITEMS.registerItem(
            "dark_one_dagger", DarkOneDaggerItem::new,
            new Item.Properties().stacksTo(1).durability(1000).rarity(Rarity.EPIC)
    );

    public static final DeferredItem<Item> GRIMOIRE_LIGHT = ITEMS.registerItem(
            "grimoire_light", props -> new GrimoireItem(props, "Light", 15),
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> GRIMOIRE_DARK = ITEMS.registerItem(
            "grimoire_dark", props -> new GrimoireItem(props, "Dark", 15),
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> AUTHORS_QUILL = ITEMS.registerItem(
            "authors_quill", AuthorsQuillItem::new,
            new Item.Properties().stacksTo(1).durability(500).rarity(Rarity.EPIC)
    );

    public static final DeferredItem<Item> AUTHORS_BOOK = ITEMS.registerItem(
            "authors_book", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)
    );

    public static final DeferredItem<Item> ENCHANTED_ROSE = ITEMS.registerItem(
            "enchanted_rose", EnchantedRoseItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> MAGIC_MIRROR = ITEMS.registerItem(
            "magic_mirror", MagicMirrorItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> CURSED_TALISMAN = ITEMS.registerItem(
            "cursed_talisman", CursedTalismanItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> SHARD_OF_LIGHT = ITEMS.registerItem(
            "shard_of_light", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> ESSENCE_OF_HOPE = ITEMS.registerItem(
            "essence_of_hope", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> CRYSTAL_OF_PURITY = ITEMS.registerItem(
            "crystal_of_purity", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> TEAR_OF_TRUE_LOVE = ITEMS.registerItem(
            "tear_of_true_love", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> ESSENCE_OF_BELIEF = ITEMS.registerItem(
            "essence_of_belief", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> HEART_OF_INNOCENCE = ITEMS.registerItem(
            "heart_of_innocence", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> INK_OF_CREATION = ITEMS.registerItem(
            "ink_of_creation", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> QUILL_OF_FATE = ITEMS.registerItem(
            "quill_of_fate", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> PAGE_OF_DESTINY = ITEMS.registerItem(
            "page_of_destiny", Item::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> STOLEN_HEART = ITEMS.registerItem(
            "stolen_heart", StolenHeartItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)
    );

    // Show-Accurate Enchanted Items
    public static final DeferredItem<Item> EXCALIBUR = ITEMS.registerItem(
            "excalibur", ExcaliburItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(2000)
    );

    public static final DeferredItem<Item> JEFFERSONS_HAT = ITEMS.registerItem(
            "jeffersons_hat", JeffersonHatItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> BLUE_FAIRY_WAND = ITEMS.registerItem(
            "blue_fairy_wand", BlueFairyWandItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)
    );

    public static final DeferredItem<Item> MALEFICENT_STAFF = ITEMS.registerItem(
            "maleficent_staff", MaleficentStaffItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(1500)
    );

    public static final DeferredItem<Item> CHIPPED_CUP = ITEMS.registerItem(
            "chipped_cup", ChippedCupItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> MAGIC_BEANS = ITEMS.registerItem(
            "magic_beans", MagicBeanItem::new,
            new Item.Properties().stacksTo(16).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> FAIRY_DUST = ITEMS.registerItem(
            "fairy_dust", FairyDustItem::new,
            new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> TRUE_LOVE_POTION = ITEMS.registerItem(
            "true_love_potion", TrueLovePotionItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)
    );

    public static final DeferredItem<Item> DREAM_CATCHER = ITEMS.registerItem(
            "dream_catcher", DreamCatcherItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> ENCHANTED_COMPASS = ITEMS.registerItem(
            "enchanted_compass", EnchantedCompassItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> ENCHANTED_CANDLE = ITEMS.registerItem(
            "enchanted_candle", EnchantedCandleItem::new,
            new Item.Properties().stacksTo(3).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> POISONED_APPLE = ITEMS.registerItem(
            "poisoned_apple", PoisonedAppleItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> FLYING_BROOMSTICK = ITEMS.registerItem(
            "flying_broomstick", FlyingBroomstickItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> EXCALIBUR_STONE = ITEMS.registerItem(
            "excalibur_stone", ExcaliburStoneItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)
    );

    public static final DeferredItem<Item> MAGIC_GLOBE = ITEMS.registerItem(
            "magic_globe", MagicGlobeItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    public static final DeferredItem<Item> MEMORY_POTION = ITEMS.registerItem(
            "memory_potion", MemoryPotionItem::new,
            new Item.Properties().stacksTo(3).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> NIGHT_ROOT = ITEMS.registerItem(
            "night_root", NightRootItem::new,
            new Item.Properties().stacksTo(3).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> SQUID_INK = ITEMS.registerItem(
            "squid_ink", SquidInkItem::new,
            new Item.Properties().stacksTo(3).rarity(Rarity.UNCOMMON)
    );

    public static final DeferredItem<Item> POTION_BREWER = ITEMS.registerItem(
            "potion_brewer", PotionBrewerItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)
    );

    public static final DeferredItem<Item> DARK_CURSE = ITEMS.registerItem(
            "dark_curse", DarkCurseItem::new,
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)
    );

    public static final DeferredItem<BlockItem> RITUAL_ALTAR = ITEMS.registerSimpleBlockItem(
            "ritual_altar", ModBlocks.RITUAL_ALTAR,
            new Item.Properties().rarity(Rarity.EPIC)
    );

    public static void addDefaultComponents(ModifyDefaultComponentsEvent event) {
        event.modify(SHARD_OF_DARK_POWER.get(), b -> b.set(ModDataComponents.RITUAL_INGREDIENT.get(),
                new ModDataComponents.RitualIngredientData("shard_of_dark_power", "dark")));
        event.modify(ESSENCE_OF_SHADOW.get(), b -> b.set(ModDataComponents.RITUAL_INGREDIENT.get(),
                new ModDataComponents.RitualIngredientData("essence_of_shadow", "dark")));
        event.modify(HEART_OF_DARKNESS.get(), b -> b.set(ModDataComponents.RITUAL_INGREDIENT.get(),
                new ModDataComponents.RitualIngredientData("heart_of_darkness", "dark")));
        event.modify(DARK_ONE_DAGGER.get(), b -> b.set(ModDataComponents.DARK_ONE_DAGGER.get(),
                new ModDataComponents.DarkOneDaggerData(UUID.randomUUID(), UUID.randomUUID(), true)));
        event.modify(GRIMOIRE_LIGHT.get(), b -> b.set(ModDataComponents.GRIMOIRE_DATA.get(),
                new ModDataComponents.GrimoireData("light", List.of(), 1)));
        event.modify(GRIMOIRE_DARK.get(), b -> b.set(ModDataComponents.GRIMOIRE_DATA.get(),
                new ModDataComponents.GrimoireData("dark", List.of(), 1)));
        event.modify(SHARD_OF_LIGHT.get(), b -> b.set(ModDataComponents.RITUAL_INGREDIENT.get(),
                new ModDataComponents.RitualIngredientData("shard_of_light", "light")));
        event.modify(ESSENCE_OF_HOPE.get(), b -> b.set(ModDataComponents.RITUAL_INGREDIENT.get(),
                new ModDataComponents.RitualIngredientData("essence_of_hope", "light")));
        event.modify(CRYSTAL_OF_PURITY.get(), b -> b.set(ModDataComponents.RITUAL_INGREDIENT.get(),
                new ModDataComponents.RitualIngredientData("crystal_of_purity", "light")));
        event.modify(TEAR_OF_TRUE_LOVE.get(), b -> b.set(ModDataComponents.RITUAL_INGREDIENT.get(),
                new ModDataComponents.RitualIngredientData("tear_of_true_love", "light")));
        event.modify(ESSENCE_OF_BELIEF.get(), b -> b.set(ModDataComponents.RITUAL_INGREDIENT.get(),
                new ModDataComponents.RitualIngredientData("essence_of_belief", "light")));
        event.modify(HEART_OF_INNOCENCE.get(), b -> b.set(ModDataComponents.RITUAL_INGREDIENT.get(),
                new ModDataComponents.RitualIngredientData("heart_of_innocence", "light")));
        event.modify(INK_OF_CREATION.get(), b -> b.set(ModDataComponents.RITUAL_INGREDIENT.get(),
                new ModDataComponents.RitualIngredientData("ink_of_creation", "dark")));
        event.modify(QUILL_OF_FATE.get(), b -> b.set(ModDataComponents.RITUAL_INGREDIENT.get(),
                new ModDataComponents.RitualIngredientData("quill_of_fate", "dark")));
        event.modify(PAGE_OF_DESTINY.get(), b -> b.set(ModDataComponents.RITUAL_INGREDIENT.get(),
                new ModDataComponents.RitualIngredientData("page_of_destiny", "dark")));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
