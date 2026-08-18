package com.example.ouat.menu;

import com.example.ouat.registry.ModItems;
import com.example.ouat.registry.ModMenus;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class PotionBrewerMenu extends AbstractContainerMenu {
    public static final int INPUT_START = 0;
    public static final int INPUT_COUNT = 9;
    public static final int OUTPUT_SLOT = 9;

    private final ContainerLevelAccess access;
    private final Player player;
    private final SimpleContainer inputContainer = new SimpleContainer(9);

    // Current recipe result
    private ItemStack result = ItemStack.EMPTY;
    private boolean needsUpdate = true;

    public PotionBrewerMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.create(playerInventory.player.level(), playerInventory.player.blockPosition()));
    }

    public PotionBrewerMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenus.POTION_BREWER.get(), containerId);
        this.access = access;
        this.player = playerInventory.player;

        // 9 input slots (3x3 grid)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int slotIndex = col + row * 3;
                this.addSlot(new Slot(this.inputContainer, slotIndex,
                        25 + col * 28, 17 + row * 28) {
                    @Override
                    public void setChanged() {
                        super.setChanged();
                        onInputChanged();
                    }
                });
            }
        }

        // Output slot
        this.addSlot(new OutputSlot(this, OUTPUT_SLOT, 143, 53));

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 115 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 173));
        }
    }

    public void onInputChanged() {
        needsUpdate = true;
    }

    public ItemStack getResult() {
        if (needsUpdate) {
            result = checkRecipe();
            needsUpdate = false;
        }
        return result;
    }

    public boolean canBrew() {
        return !getResult().isEmpty();
    }

    public void brew() {
        if (!canBrew()) return;

        // Consume ingredients
        for (int i = INPUT_START; i < INPUT_START + INPUT_COUNT; i++) {
            ItemStack slotStack = this.getSlot(i).getItem();
            if (!slotStack.isEmpty()) {
                slotStack.shrink(1);
            }
        }

        // Give result to player
        ItemStack resultStack = getResult().copy();
        if (!player.getInventory().add(resultStack)) {
            player.drop(resultStack, false);
        }

        needsUpdate = true;
    }

    private ItemStack checkRecipe() {
        List<String> ingredients = new ArrayList<>();
        for (int i = INPUT_START; i < INPUT_START + INPUT_COUNT; i++) {
            ItemStack slotStack = this.getSlot(i).getItem();
            if (!slotStack.isEmpty()) {
                String id = getItemId(slotStack);
                if (id != null) {
                    ingredients.add(id);
                }
            }
        }

        ingredients.sort(String::compareTo);
        String key = String.join(",", ingredients);

        return switch (key) {
            // Dark Curse: heart_of_darkness + shard_of_dark_power + essence_of_shadow + stolen_heart + night_root + fairy_dust
            case "essence_of_shadow,fairy_dust,heart_of_darkness,night_root,shard_of_dark_power,stolen_heart" ->
                    new ItemStack(ModItems.DARK_CURSE.get());
            // Sleeping Curse: poisoned_apple + enchanted_rose
            case "enchanted_rose,poisoned_apple" ->
                    new ItemStack(ModItems.POISONED_APPLE.get());
            // True Love Potion: tear_of_true_love + fairy_dust + enchanted_candle
            case "enchanted_candle,fairy_dust,tear_of_true_love" ->
                    new ItemStack(ModItems.TRUE_LOVE_POTION.get());
            // Memory Restoration: memory_potion + essence_of_belief
            case "essence_of_belief,memory_potion" ->
                    new ItemStack(ModItems.MEMORY_POTION.get());
            // Curse of Empty Heart: stolen_heart + heart_of_darkness
            case "heart_of_darkness,stolen_heart" ->
                    new ItemStack(ModItems.STOLEN_HEART.get());
            // Dreamcatcher Enhanced: dream_catcher + night_root + squid_ink
            case "dream_catcher,night_root,squid_ink" ->
                    new ItemStack(ModItems.DREAM_CATCHER.get());
            // Dark Potion: shard_of_dark_power + essence_of_shadow + ink_of_creation
            case "essence_of_shadow,ink_of_creation,shard_of_dark_power" ->
                    new ItemStack(ModItems.GRIMOIRE_DARK.get());
            // Light Potion: shard_of_light + essence_of_hope + crystal_of_purity
            case "crystal_of_purity,essence_of_hope,shard_of_light" ->
                    new ItemStack(ModItems.GRIMOIRE_LIGHT.get());
            // Heart Protection: heart_of_innocence + tear_of_true_love
            case "heart_of_innocence,tear_of_true_love" ->
                    new ItemStack(ModItems.ENCHANTED_CANDLE.get());
            // Night Root Extract: night_root + night_root + fairy_dust
            case "fairy_dust,night_root,night_root" ->
                    new ItemStack(ModItems.NIGHT_ROOT.get());
            // Squid Ink Brew: squid_ink + squid_ink + essence_of_shadow
            case "essence_of_shadow,squid_ink,squid_ink" ->
                    new ItemStack(ModItems.SQUID_INK.get());
            default -> ItemStack.EMPTY;
        };
    }

    private String getItemId(ItemStack stack) {
        if (stack.is(ModItems.SHARD_OF_DARK_POWER.get())) return "shard_of_dark_power";
        if (stack.is(ModItems.ESSENCE_OF_SHADOW.get())) return "essence_of_shadow";
        if (stack.is(ModItems.HEART_OF_DARKNESS.get())) return "heart_of_darkness";
        if (stack.is(ModItems.SHARD_OF_LIGHT.get())) return "shard_of_light";
        if (stack.is(ModItems.ESSENCE_OF_HOPE.get())) return "essence_of_hope";
        if (stack.is(ModItems.CRYSTAL_OF_PURITY.get())) return "crystal_of_purity";
        if (stack.is(ModItems.TEAR_OF_TRUE_LOVE.get())) return "tear_of_true_love";
        if (stack.is(ModItems.ESSENCE_OF_BELIEF.get())) return "essence_of_belief";
        if (stack.is(ModItems.HEART_OF_INNOCENCE.get())) return "heart_of_innocence";
        if (stack.is(ModItems.INK_OF_CREATION.get())) return "ink_of_creation";
        if (stack.is(ModItems.QUILL_OF_FATE.get())) return "quill_of_fate";
        if (stack.is(ModItems.PAGE_OF_DESTINY.get())) return "page_of_destiny";
        if (stack.is(ModItems.STOLEN_HEART.get())) return "stolen_heart";
        if (stack.is(ModItems.ENCHANTED_ROSE.get())) return "enchanted_rose";
        if (stack.is(ModItems.ENCHANTED_CANDLE.get())) return "enchanted_candle";
        if (stack.is(ModItems.POISONED_APPLE.get())) return "poisoned_apple";
        if (stack.is(ModItems.FAIRY_DUST.get())) return "fairy_dust";
        if (stack.is(ModItems.TRUE_LOVE_POTION.get())) return "true_love_potion";
        if (stack.is(ModItems.DREAM_CATCHER.get())) return "dream_catcher";
        if (stack.is(ModItems.NIGHT_ROOT.get())) return "night_root";
        if (stack.is(ModItems.SQUID_INK.get())) return "squid_ink";
        if (stack.is(ModItems.MEMORY_POTION.get())) return "memory_potion";
        if (stack.is(ModItems.CURSED_TALISMAN.get())) return "cursed_talisman";
        if (stack.is(ModItems.MAGIC_BEANS.get())) return "magic_beans";
        if (stack.is(ModItems.EXCALIBUR_STONE.get())) return "excalibur_stone";
        return null;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // Return ingredients to player
        for (int i = 0; i < INPUT_COUNT; i++) {
            ItemStack stack = this.inputContainer.getItem(i);
            if (!stack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(stack);
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            int inputEnd = INPUT_START + INPUT_COUNT;

            if (index == OUTPUT_SLOT) {
                if (!this.moveItemStackTo(slotStack, inputEnd, inputEnd + 36, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= INPUT_START && index < inputEnd) {
                if (!this.moveItemStackTo(slotStack, inputEnd, inputEnd + 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= inputEnd) {
                if (!this.moveItemStackTo(slotStack, INPUT_START, inputEnd, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public static class OutputSlot extends Slot {
        private final PotionBrewerMenu menu;

        public OutputSlot(PotionBrewerMenu menu, int index, int x, int y) {
            super(new SimpleContainer(1), 0, x, y);
            this.menu = menu;
        }

        @Override
        public ItemStack getItem() {
            return menu.getResult();
        }

        @Override
        public void setByPlayer(ItemStack stack) {
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public ItemStack remove(int amount) {
            return getItem().copy();
        }

        @Override
        public boolean mayPickup(Player player) {
            return menu.canBrew();
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            menu.brew();
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }
    }
}
