package com.example.ouat.items;

import com.example.ouat.OnceUponATime;
import com.example.ouat.dimensions.DimensionManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;

public class JeffersonHatItem extends Item {
    @SuppressWarnings("unchecked")
    private static final net.minecraft.resources.ResourceKey<Level>[] DIMENSIONS = new net.minecraft.resources.ResourceKey[]{
            DimensionManager.STORYBROOKE,
            DimensionManager.ENCHANTED_FOREST,
            DimensionManager.UNDERWORLD,
            DimensionManager.NEVERLAND,
            DimensionManager.WONDERLAND
    };
    private static final String[] DIMENSION_NAMES = {
            "Storybrooke", "The Enchanted Forest", "The Underworld", "Neverland", "Wonderland"
    };

    public JeffersonHatItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // Smoke vortex at departure
            if (serverPlayer.level() instanceof ServerLevel serverLevel) {
                spawnSmokeVortex(serverLevel, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ());
            }

            // Pick a random dimension
            int index = new Random().nextInt(DIMENSIONS.length);
            net.minecraft.resources.ResourceKey<Level> targetDim = DIMENSIONS[index];
            String dimensionName = DIMENSION_NAMES[index];

            ServerLevel targetLevel = serverPlayer.server.getLevel(targetDim);
            if (targetLevel == null) {
                serverPlayer.sendSystemMessage(Component.literal("§c" + dimensionName + " could not be reached. The hat shudders..."));
                return InteractionResultHolder.fail(stack);
            }

            // Teleport to the dimension
            serverPlayer.teleportTo(targetLevel,
                    serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                    serverPlayer.getYRot(), serverPlayer.getXRot());

            // Smoke vortex at arrival
            if (serverPlayer.level() instanceof ServerLevel arrivalLevel) {
                spawnSmokeVortex(arrivalLevel, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ());
            }

            serverPlayer.playSound(SoundEvents.ENDERMAN_TELEPORT, 2.0F, 0.5F);
            serverPlayer.sendSystemMessage(Component.literal("§5§lYou spin Jefferson's hat and step through a portal..."));
            serverPlayer.sendSystemMessage(Component.literal("§d§lWelcome to " + dimensionName + "!"));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private void spawnSmokeVortex(ServerLevel level, double x, double y, double z) {
        for (int i = 0; i < 60; i++) {
            double angle = (i / 60.0) * Math.PI * 2;
            double radius = 1.5 - (i / 60.0) * 0.5;
            double px = x + Math.cos(angle) * radius;
            double pz = z + Math.sin(angle) * radius;
            double py = y + 0.1 + (i / 60.0) * 2.0;
            level.sendParticles(ParticleTypes.SMOKE, px, py, pz, 3, 0.05, 0.05, 0.05, 0.02);
            level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, px, py, pz, 2, 0.05, 0.05, 0.05, 0.01);
        }
        for (int i = 0; i < 30; i++) {
            double angle = (i / 30.0) * Math.PI * 2;
            double radius = 0.5;
            level.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    x + Math.cos(angle) * radius, y + 1.0, z + Math.sin(angle) * radius,
                    5, 0.05, 0.05, 0.05, 0.02);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§5§lJefferson's Hat").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal("§7A hat that opens portals between realms"));
        tooltip.add(Component.literal("§7Right-click to travel to a random realm:"));
        tooltip.add(Component.literal("§8Storybrooke, Enchanted Forest, Underworld, Neverland, Wonderland"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
