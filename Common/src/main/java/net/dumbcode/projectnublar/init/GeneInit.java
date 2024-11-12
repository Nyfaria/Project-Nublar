package net.dumbcode.projectnublar.init;

import net.dumbcode.projectnublar.Constants;
import net.dumbcode.projectnublar.api.Genes;
import net.dumbcode.projectnublar.registration.RegistrationProvider;
import net.dumbcode.projectnublar.registration.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GeneInit {

    public static ResourceKey<Registry<Genes.Gene>> GENE_KEY = ResourceKey.createRegistryKey(Constants.modLoc("gene"));
    public static RegistrationProvider<Genes.Gene> GENES = RegistrationProvider.get(GENE_KEY, Constants.MODID);
    public static Supplier<Registry<Genes.Gene>> REG = GENES.registryBuilder().build();

    public static RegistryObject<Genes.Gene> AGGRESSION = register("aggression");
    public static RegistryObject<Genes.Gene> DEFENSE = register("defense");
    public static RegistryObject<Genes.Gene> EAT_RATE = register("eat_rate");
    public static RegistryObject<Genes.Gene> HEALTH = register("health");
    public static RegistryObject<Genes.Gene> HEALTH_REGEN = register("health_regen");
    public static RegistryObject<Genes.Gene> HEAT_RESISTANCE = register("heat_resistance");
    public static RegistryObject<Genes.Gene> HERD_SIZE = register("herd_size");
    public static RegistryObject<Genes.Gene> PACK_SIZE = register("pack_size");
    public static RegistryObject<Genes.Gene> IMMUNITY = register("immunity");
    public static RegistryObject<Genes.Gene> INTELLIGENCE = register("intelligence");
    public static RegistryObject<Genes.Gene> JUMP = register("jump");
    public static RegistryObject<Genes.Gene> NOCTURNAL = register("nocturnal");
    public static RegistryObject<Genes.Gene> FERTILITY = register("fertility");
    public static RegistryObject<Genes.Gene> SIZE = register("size");
    public static RegistryObject<Genes.Gene> SPEED = register("speed");
    public static RegistryObject<Genes.Gene> STOMACH_CAPACITY = register("stomach_capacity");
    public static RegistryObject<Genes.Gene> STRENGTH = register("strength");
    public static RegistryObject<Genes.Gene> TAMABILITY = register("tamability");
    public static RegistryObject<Genes.Gene> UNDERWATER_CAPACITY = register("underwater_capacity");
    public static RegistryObject<Genes.Gene> COLOR = register("color", 0);
    public static RegistryObject<Genes.Gene> GENDER = register("gender");


    public static RegistryObject<Genes.Gene> register(String name) {
        return GENES.register(name, () -> new Genes.Gene(name));
    }

    public static RegistryObject<Genes.Gene> register(String name, double requirement) {
        return GENES.register(name, () -> new Genes.Gene(name, requirement));
    }

    public static void loadClass() {

    }
}
