package com.nyfaria.projectnublar.block;

import com.nyfaria.projectnublar.api.FossilPiece;
import com.nyfaria.projectnublar.api.Quality;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class AmberBlock extends Block {
    final ResourceLocation entityType;
    final Block base;
    public AmberBlock(Properties properties, ResourceLocation entityType, Block base) {
        super(properties);
        this.entityType = entityType;
        this.base = base;
    }


    public ResourceLocation getEntityType() {
        return entityType;
    }

    public Block getBase() {
        return base;
    }

}
