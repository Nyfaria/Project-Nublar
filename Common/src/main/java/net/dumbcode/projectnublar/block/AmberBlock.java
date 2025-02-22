package net.dumbcode.projectnublar.block;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
