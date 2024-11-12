package net.dumbcode.projectnublar.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.dumbcode.projectnublar.CommonClass;
import net.dumbcode.projectnublar.init.GeneInit;
import net.dumbcode.projectnublar.registration.RegistryObject;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.DyeColor;

import java.util.List;

public class Genes {
    public static Codec<Gene> CODEC = Codec.STRING.xmap(Genes::byName, Gene::name);
    public static Multimap<Gene, Pair<EntityType<?>, Double>> GENE_STORAGE = HashMultimap.create();

    public static void addToGene(Gene gene, EntityType<?> type, double value) {
        GENE_STORAGE.put(gene, Pair.of(type, value));
    }

    public static Gene byName(String name) {
        for (RegistryObject<Gene> gene : GeneInit.GENES.getEntries()) {
            if (gene.get().name().equals(name)) {
                return gene.get();
            }
        }
        return null;
    }

    public record Gene(String name, double requirement) {

        public Gene(String name) {
            this(name, 1);
        }

        public Component getTooltip(Double value) {

            return Component.literal(CommonClass.checkReplace(name())).append(Component.literal(": ")).append(Component.literal(String.valueOf(value.intValue())).withStyle(value > 0 ? ChatFormatting.GREEN : ChatFormatting.RED).append(Component.literal("%")));
        }

        public Component getTooltip() {
            return Component.literal(CommonClass.checkReplace(name()));
        }


        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Gene gene) {
                return gene.name().equals(name());
            }
            return false;
        }
    }
}
