package net.dumbcode.projectnublar.client.widget;

import net.dumbcode.projectnublar.api.DinoData;
import net.dumbcode.projectnublar.api.Genes;

public interface GeneHolder {
    void setGene(Genes.Gene gene, DinoData data);
}
