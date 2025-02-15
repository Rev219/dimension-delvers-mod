package com.dimensiondelvers.dimensiondelvers.item.socket;

import com.dimensiondelvers.dimensiondelvers.init.ModDataComponentType;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemData;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemShape;
import com.dimensiondelvers.dimensiondelvers.modifier.ModifierInstance;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.Optional;

// Vanilla Equivalent ItemEnchantments
public record GearSocket(
        RunegemShape shape,
        Optional<ModifierInstance> modifier,
        Optional<ItemStack> runegem
) {

    // Further define runeGemShape
    // Needs to have a modifier and a Runegem
    // should eventually also have the roll of the modifier (and a tier?)
    public static Codec<GearSocket> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RunegemShape.CODEC.fieldOf("shape").forGetter(GearSocket::shape),
            ModifierInstance.CODEC.optionalFieldOf("modifier", null).forGetter(GearSocket::modifier),
            ItemStack.CODEC.optionalFieldOf("runegem").forGetter(GearSocket::runegem)
    ).apply(inst, GearSocket::new));

    public boolean isEmpty() {
        return runegem.isEmpty() || runegem.get().isEmpty();
    }

    public boolean canBeApplied(RunegemData runegemData) {
        return isEmpty() && this.shape().equals(runegemData.shape());
    }

    public GearSocket applyRunegem(ItemStack stack, Level level) {
        RunegemData runegemData = stack.get(ModDataComponentType.RUNEGEM_DATA);
        if (runegemData == null) {
            return new GearSocket(this.shape(), Optional.empty(), Optional.empty());
        }
        Optional<Holder<Enchantment>> modifier = runegemData.getRandomModifier(level);
        return new GearSocket(this.shape(), modifier, Optional.of(stack));
    }
}