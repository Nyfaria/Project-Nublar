package net.dumbcode.projectnublar.client;

import net.dumbcode.projectnublar.api.DinoData;
import net.dumbcode.projectnublar.api.Genes;
import net.dumbcode.projectnublar.client.renderer.layer.DinoLayer;
import net.dumbcode.projectnublar.client.screen.SequencerScreen;
import net.dumbcode.projectnublar.client.widget.AdvancedColorWidget;
import net.dumbcode.projectnublar.client.widget.GeneHolder;
import net.dumbcode.projectnublar.client.widget.GeneSlider;
import net.dumbcode.projectnublar.init.EntityInit;
import net.dumbcode.projectnublar.init.GeneInit;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class CommonClientClass {

    private static final Map<Genes.Gene,  BiFunction<SequencerScreen, DinoData, GeneHolder>> GENE_WIDGET = new HashMap<>();
    private static final Map<EntityType<?>, List<DinoLayer>> LAYER_REGISTRY = new HashMap<>();
    public static  BiFunction<SequencerScreen, DinoData,GeneHolder> getGeneWidget(Genes.Gene gene) {
        for(Genes.Gene g : GENE_WIDGET.keySet()) {
            if(g.equals(gene)) {
                return GENE_WIDGET.get(g);
            }
        }
        return null;
    }
    public static void initClient() {
        registerGeneWidgets();
        registerLayerNames();
    }

    public static void registerLayerNames(){
        LAYER_REGISTRY.put(EntityInit.TYRANNOSAURUS_REX.get(),List.of(
                new DinoLayer("back", 1),
                new DinoLayer("yellow", 2),
                new DinoLayer("belly", 2),
                new DinoLayer("brightgreen", 2),
                new DinoLayer("brown", 2),
                new DinoLayer("feet", 3),
                new DinoLayer("pattern", 3),
                new DinoLayer("mouth", -1),
                new DinoLayer("teeth", -1),
                new DinoLayer("eyes", -1),
                new DinoLayer("nostrils", -1),
                new DinoLayer("claws", -1)));
    }
    public static List<DinoLayer> getDinoLayers(EntityType<?> type){
        if(!LAYER_REGISTRY.containsKey(type)){
            return List.of(new DinoLayer("base", 0));
        }
        return LAYER_REGISTRY.get(type);
    }
    public static void registerGeneWidgets() {
        registerGeneWidget(GeneInit.AGGRESSION.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.AGGRESSION.get() , value);
        }));
        registerGeneWidget(GeneInit.DEFENSE.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.DEFENSE.get() , value);
        }));
        registerGeneWidget(GeneInit.EAT_RATE.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.EAT_RATE.get() , value);
        }));
        registerGeneWidget(GeneInit.HEALTH.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.HEALTH.get() , value);
        }));
        registerGeneWidget(GeneInit.HEALTH_REGEN.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.HEALTH_REGEN.get() , value);
        }));
        registerGeneWidget(GeneInit.HEAT_RESISTANCE.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.HEAT_RESISTANCE.get() , value);
        }));
        registerGeneWidget(GeneInit.HERD_SIZE.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.HERD_SIZE.get() , value);
        }));
        registerGeneWidget(GeneInit.PACK_SIZE.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.PACK_SIZE.get() , value);
        }));
        registerGeneWidget(GeneInit.IMMUNITY.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.IMMUNITY.get() , value);
        }));
        registerGeneWidget(GeneInit.INTELLIGENCE.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.INTELLIGENCE.get() , value);
        }));
        registerGeneWidget(GeneInit.JUMP.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.JUMP.get() , value);
        }));
        registerGeneWidget(GeneInit.NOCTURNAL.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.NOCTURNAL.get() , value);
        }));
        registerGeneWidget(GeneInit.FERTILITY.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.FERTILITY.get() , value);
        }));
        registerGeneWidget(GeneInit.SIZE.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.SIZE.get() , value);
        }));
        registerGeneWidget(GeneInit.SPEED.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.SPEED.get() , value);
        }));
        registerGeneWidget(GeneInit.STOMACH_CAPACITY.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.STOMACH_CAPACITY.get() , value);
        }));
        registerGeneWidget(GeneInit.STRENGTH.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.STRENGTH.get() , value);
        }));
        registerGeneWidget(GeneInit.TAMABILITY.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.TAMABILITY.get() , value);
        }));
        registerGeneWidget(GeneInit.UNDERWATER_CAPACITY.get() , (screen, meep)->new GeneSlider(screen.leftPos() + 235, screen.topPos() + 50, 100, 20, Component.empty(), Component.literal("%"), -100, 100, 0, true, (slider, value) -> {
            meep.setGeneValue(GeneInit.UNDERWATER_CAPACITY.get() , value);
        }));
        registerGeneWidget(GeneInit.COLOR.get() , (screen, meep)-> {
            AdvancedColorWidget widget =new AdvancedColorWidget(screen, screen.leftPos() + 235, screen.topPos() + 40, 100, 100, Component.empty(), CommonClientClass.getDinoLayers(meep.getBaseDino()).stream().filter(l->l.getBasicLayer()!=-1).map(DinoLayer::getLayerName).toList());
            return widget;
        });
    }

    private static void registerGeneWidget(Genes.Gene gene, BiFunction<SequencerScreen, DinoData,GeneHolder> widget) {
        GENE_WIDGET.put(gene, widget);
    }

}
