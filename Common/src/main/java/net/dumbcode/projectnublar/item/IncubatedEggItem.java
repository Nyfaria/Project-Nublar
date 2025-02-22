package net.dumbcode.projectnublar.item;

import net.dumbcode.projectnublar.api.DinoData;
import net.dumbcode.projectnublar.entity.Dinosaur;
import net.dumbcode.projectnublar.item.api.DNADataItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class IncubatedEggItem extends DNADataItem {
    public IncubatedEggItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide()) {
            return InteractionResult.sidedSuccess(true);
        }
        if(pContext.getHand() == InteractionHand.OFF_HAND){
            return InteractionResult.FAIL;
        }
        DinoData dinoData = DinoData.fromStack(pContext.getItemInHand());
        if (dinoData != null) {
            EntityType<?> entityType = dinoData.getBaseDino();
            Dinosaur dinosaur = (Dinosaur) entityType.spawn((ServerLevel) pContext.getLevel(), pContext.getClickedPos().above(), MobSpawnType.EVENT);
            dinosaur.setDinoData(dinoData);
            pContext.getItemInHand().shrink(1);
            return InteractionResult.CONSUME;
        }
        return super.useOn(pContext);
    }

    @Override
    public Component getName(ItemStack pStack) {
        return Component.translatable(this.getDescriptionId(), DinoData.fromStack(pStack).getFormattedType());
    }
}
