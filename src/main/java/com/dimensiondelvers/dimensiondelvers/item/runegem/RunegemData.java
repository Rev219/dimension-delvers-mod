package com.dimensiondelvers.dimensiondelvers.item.runegem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record RunegemData(RuneGemShape shape, TagKey<Enchantment> tag, RuneGemTier tier) {
    public static Codec<RunegemData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RuneGemShape.CODEC.fieldOf("shape").forGetter(RunegemData::shape),
            TagKey.codec(Registries.ENCHANTMENT).fieldOf("tag").forGetter(RunegemData::tag),
            RuneGemTier.CODEC.fieldOf("tier").forGetter(RunegemData::tier)
    ).apply(inst, RunegemData::new));

    public Optional<Holder<Enchantment>> getRandomModifier(Level level) {
        return level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(tag)
                .flatMap(holders -> holders.getRandomElement(level.random));
    }
}
