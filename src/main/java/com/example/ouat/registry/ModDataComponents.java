package com.example.ouat.registry;

import com.example.ouat.OnceUponATime;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.UUID;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(
                    Registries.DATA_COMPONENT_TYPE,
                    OnceUponATime.MOD_ID
            );

    public record RitualIngredientData(String ingredientId, String magicType) {
        public static final Codec<RitualIngredientData> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        Codec.STRING.fieldOf("ingredient_id").forGetter(RitualIngredientData::ingredientId),
                        Codec.STRING.fieldOf("magic_type").forGetter(RitualIngredientData::magicType)
                ).apply(inst, RitualIngredientData::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, RitualIngredientData> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, RitualIngredientData::ingredientId,
                        ByteBufCodecs.STRING_UTF8, RitualIngredientData::magicType,
                        RitualIngredientData::new
                );
    }

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RitualIngredientData>> RITUAL_INGREDIENT =
            COMPONENTS.registerComponentType("ritual_ingredient",
                    builder -> builder
                            .persistent(RitualIngredientData.CODEC)
                            .networkSynchronized(RitualIngredientData.STREAM_CODEC)
            );

    public record DarkOneDaggerData(UUID boundOwner, UUID instanceId, boolean isAuthentic) {
        public static final Codec<DarkOneDaggerData> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        ResourceLocation.CODEC.xmap(
                                rl -> UUID.fromString(rl.toString()),
                                uuid -> ResourceLocation.parse(uuid.toString())
                        ).fieldOf("bound_owner").forGetter(DarkOneDaggerData::boundOwner),
                        ResourceLocation.CODEC.xmap(
                                rl -> UUID.fromString(rl.toString()),
                                uuid -> ResourceLocation.parse(uuid.toString())
                        ).fieldOf("instance_id").forGetter(DarkOneDaggerData::instanceId),
                        Codec.BOOL.fieldOf("is_authentic").forGetter(DarkOneDaggerData::isAuthentic)
                ).apply(inst, DarkOneDaggerData::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, DarkOneDaggerData> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, e -> e.boundOwner().toString(),
                        ByteBufCodecs.STRING_UTF8, e -> e.instanceId().toString(),
                        ByteBufCodecs.BOOL, DarkOneDaggerData::isAuthentic,
                        (s1, s2, b) -> new DarkOneDaggerData(UUID.fromString(s1), UUID.fromString(s2), b)
                );
    }

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DarkOneDaggerData>> DARK_ONE_DAGGER =
            COMPONENTS.registerComponentType("dark_one_dagger",
                    builder -> builder
                            .persistent(DarkOneDaggerData.CODEC)
                            .networkSynchronized(DarkOneDaggerData.STREAM_CODEC)
            );

    public record GrimoireData(String school, List<String> containedSpells, int level) {
        public static final Codec<GrimoireData> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        Codec.STRING.fieldOf("school").forGetter(GrimoireData::school),
                        Codec.STRING.listOf().fieldOf("contained_spells").forGetter(GrimoireData::containedSpells),
                        Codec.INT.fieldOf("level").forGetter(GrimoireData::level)
                ).apply(inst, GrimoireData::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, GrimoireData> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, GrimoireData::school,
                        ByteBufCodecs.collection(java.util.ArrayList::new, ByteBufCodecs.STRING_UTF8), GrimoireData::containedSpells,
                        ByteBufCodecs.VAR_INT, GrimoireData::level,
                        GrimoireData::new
                );
    }

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GrimoireData>> GRIMOIRE_DATA =
            COMPONENTS.registerComponentType("grimoire_data",
                    builder -> builder
                            .persistent(GrimoireData.CODEC)
                            .networkSynchronized(GrimoireData.STREAM_CODEC)
            );

    public record MagicalBoundData(UUID ownerUuid, String bindingType) {
        public static final Codec<MagicalBoundData> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        ResourceLocation.CODEC.xmap(
                                rl -> UUID.fromString(rl.toString()),
                                uuid -> ResourceLocation.parse(uuid.toString())
                        ).fieldOf("owner_uuid").forGetter(MagicalBoundData::ownerUuid),
                        Codec.STRING.fieldOf("binding_type").forGetter(MagicalBoundData::bindingType)
                ).apply(inst, MagicalBoundData::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, MagicalBoundData> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, e -> e.ownerUuid().toString(),
                        ByteBufCodecs.STRING_UTF8, MagicalBoundData::bindingType,
                        (s, t) -> new MagicalBoundData(UUID.fromString(s), t)
                );
    }

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MagicalBoundData>> MAGICAL_BOUND =
            COMPONENTS.registerComponentType("magical_bound",
                    builder -> builder
                            .persistent(MagicalBoundData.CODEC)
                            .networkSynchronized(MagicalBoundData.STREAM_CODEC)
            );

    public record MagicMirrorData(UUID trappedEntity) {
        public static final Codec<MagicMirrorData> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        ResourceLocation.CODEC.xmap(
                                rl -> UUID.fromString(rl.toString()),
                                uuid -> ResourceLocation.parse(uuid.toString())
                        ).optionalFieldOf("trapped_entity", null).forGetter(MagicMirrorData::trappedEntity)
                ).apply(inst, MagicMirrorData::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, MagicMirrorData> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8,
                        e -> e.trappedEntity() != null ? e.trappedEntity().toString() : "",
                        s -> new MagicMirrorData(s.isEmpty() ? null : UUID.fromString(s))
                );
    }

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MagicMirrorData>> MIRROR_DATA =
            COMPONENTS.registerComponentType("mirror_data",
                    builder -> builder
                            .persistent(MagicMirrorData.CODEC)
                            .networkSynchronized(MagicMirrorData.STREAM_CODEC)
            );

    public record StolenHeartData(String victimName, UUID victimUUID) {
        public static final Codec<StolenHeartData> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        Codec.STRING.fieldOf("victim_name").forGetter(StolenHeartData::victimName),
                        ResourceLocation.CODEC.xmap(
                                rl -> UUID.fromString(rl.toString()),
                                uuid -> ResourceLocation.parse(uuid.toString())
                        ).optionalFieldOf("victim_uuid", null).forGetter(StolenHeartData::victimUUID)
                ).apply(inst, StolenHeartData::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, StolenHeartData> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, StolenHeartData::victimName,
                        ByteBufCodecs.STRING_UTF8, e -> e.victimUUID() != null ? e.victimUUID().toString() : "",
                        (s, uuid) -> new StolenHeartData(s, uuid.isEmpty() ? null : UUID.fromString(uuid))
                );
    }

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<StolenHeartData>> STOLEN_HEART =
            COMPONENTS.registerComponentType("stolen_heart",
                    builder -> builder
                            .persistent(StolenHeartData.CODEC)
                            .networkSynchronized(StolenHeartData.STREAM_CODEC)
            );

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}
