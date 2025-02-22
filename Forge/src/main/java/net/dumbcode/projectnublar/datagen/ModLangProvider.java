package net.dumbcode.projectnublar.datagen;

import com.google.common.collect.ImmutableMap;
import net.dumbcode.projectnublar.Constants;
import net.dumbcode.projectnublar.api.FossilPiece;
import net.dumbcode.projectnublar.api.FossilPieces;
import net.dumbcode.projectnublar.init.BlockInit;
import net.dumbcode.projectnublar.init.EntityInit;
import net.dumbcode.projectnublar.init.ItemInit;
import net.dumbcode.projectnublar.registration.RegistryObject;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModLangProvider extends LanguageProvider {
    protected static final Map<String, String> REPLACE_LIST = ImmutableMap.of(
            "tnt", "TNT",
            "sus", "",
            "incubator", "Incubator Base",
            "sequencer", "Sequencer Base"
    );

    public ModLangProvider(PackOutput gen) {
        super(gen, Constants.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
//        ItemInit.ITEMS.getEntries().forEach(this::itemLang);
        EntityInit.ENTITIES.getEntries().forEach(this::entityLang);
        BlockInit.BLOCKS.getEntries().forEach(this::blockLang);
        add("itemGroup." + Constants.MODID +".fossil_items", "Project Nublar: Fossil Items");
        add("itemGroup." + Constants.MODID +".fossil_ores", "Project Nublar: Fossil Ores");
        add("itemGroup." + Constants.MODID +".machines", "Project Nublar: Machines");
        add("itemGroup." + Constants.MODID +".misc", "Project Nublar: Misc");
        add("itemGroup." + Constants.MODID +".dna", "Project Nublar: DNA");
        add("itemGroup." + Constants.MODID +".egg", "Project Nublar: Eggs");
        add("item." + Constants.MODID + ".fossil", "%1$s %2$s Fossil");
        add("item." + Constants.MODID + ".incubated_egg", "Incubated %1$s Egg");
        add("quality." + Constants.MODID + ".fragmented", "Fragmented");
        add("quality." + Constants.MODID + ".poor", "Poor");
        add("quality." + Constants.MODID + ".common", "Common");
        add("quality." + Constants.MODID + ".pristine", "Pristine");
        for (FossilPiece piece : FossilPieces.getPieces()) {
            add("piece." + Constants.MODID + "." + piece.name().toLowerCase(), checkReplace(piece));
        }
        add("item." + Constants.MODID + ".amber", "%1$s Amber");
        add("dna_percentage." + Constants.MODID, "DNA Percentage: %1$s");
        add("container." + Constants.MODID + ".processor", "Fossil Processor");
        add("container." + Constants.MODID + ".sequencer", "Sequencer");
        add("gui_tab." + Constants.MODID + ".sequence", "Sequence");
        add("gui_tab." + Constants.MODID + ".edit", "Edit");
        add("gui_tab." + Constants.MODID + ".synthesis", "Synthesis");
        add("item."+Constants.MODID+".test_tube2","%s Test Tube");
        add("button."+Constants.MODID+".regular","Regular");
        add("button."+Constants.MODID+".isolated","Isolated");
        Stream.of(
                ItemInit.TEST_TUBE_ITEM,
                ItemInit.IRON_FILTER,
                ItemInit.IRON_COMPUTER_CHIP,
                ItemInit.IRON_TANK_UPGRADE,
                ItemInit.GOLD_FILTER,
                ItemInit.GOLD_COMPUTER_CHIP,
                ItemInit.GOLD_TANK_UPGRADE,
                ItemInit.DIAMOND_FILTER,
                ItemInit.DIAMOND_COMPUTER_CHIP,
                ItemInit.DIAMOND_TANK_UPGRADE,
                ItemInit.HARD_DRIVE,
                ItemInit.SSD,
                ItemInit.SYRINGE,
                ItemInit.ARTIFICIAL_EGG,
                ItemInit.CRACKED_ARTIFICIAL_EGG,
                ItemInit.UNINCUBATED_EGG,
                ItemInit.SMALL_CONTAINER_UPGRADE,
                ItemInit.LARGE_CONTAINER_UPGRADE,
                ItemInit.WARM_BULB,
                ItemInit.WARMER_BULB,
                ItemInit.HOT_BULB,
                ItemInit.IRON_PLANT_TANK,
                ItemInit.GOLD_PLANT_TANK,
                ItemInit.INCUBATOR_ARM_BASE,
                ItemInit.INCUBATOR_ARM,
                ItemInit.INCUBATOR_LID,
                ItemInit.INCUBATOR_NEST,
                ItemInit.SEQUENCER_COMPUTER,
                ItemInit.SEQUENCER_DOOR,
                ItemInit.SEQUENCER_SCREEN,
                ItemInit.WIRE_SPOOL
        ).forEach(this::itemLang);
    }

    protected void itemLang(RegistryObject<Item> entry) {
        if (!(entry.get() instanceof BlockItem) || entry.get() instanceof ItemNameBlockItem) {
            addItem(entry, checkReplace(entry));
        }
    }

    protected void blockLang(RegistryObject<Block> entry) {
        addBlock(entry, checkReplace(entry));
    }

    protected void entityLang(RegistryObject<EntityType<?>> entry) {
        addEntityType(entry, checkReplace(entry));
    }

    protected String checkReplace(RegistryObject<?> registryObject) {
        return Arrays.stream(registryObject.getId().getPath().split("_"))
                .map(this::checkReplace)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "))
                .trim();
    }
    protected String checkReplace(FossilPiece registryObject) {
        return Arrays.stream(registryObject.name().split("_"))
                .map(this::checkReplace)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "))
                .trim();
    }

    protected String checkReplace(String string) {
        return REPLACE_LIST.containsKey(string) ? REPLACE_LIST.get(string) : StringUtils.capitalize(string);
    }
}
