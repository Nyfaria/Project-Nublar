package com.nyfaria.projectnublar.api.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.nyfaria.projectnublar.api.NublarMath;
import com.nyfaria.projectnublar.block.AmberBlock;
import com.nyfaria.projectnublar.init.ItemInit;
import com.nyfaria.projectnublar.init.LootFunctionInit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class AmberItemFunction extends LootItemConditionalFunction {

    public AmberItemFunction(LootItemCondition[] $$0) {
        super($$0);
    }

    public static Builder<?> amberItem() {
        return simpleBuilder((conditions) -> {
            return new AmberItemFunction(conditions);
        });
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext lootContext) {
        AmberBlock block = (AmberBlock) ((BlockItem) itemStack.getItem()).getBlock();
        ResourceLocation dino = block.getEntityType();
        ItemStack toolStack = lootContext.getParamOrNull(LootContextParams.TOOL);
        if (toolStack != null) {
            int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, toolStack);
            boolean hasSilkTouch = EnchantmentHelper.hasSilkTouch(toolStack);
            if (!hasSilkTouch) {
                itemStack = new ItemStack(ItemInit.AMBER_ITEM.get());
                itemStack.getOrCreateTag().putDouble("dna_percentage", NublarMath.round(Math.pow(lootContext.getRandom().nextDouble(), 0.8d),2));
                itemStack.getOrCreateTag().putString("dino", dino.toString());
            }
        }
        return itemStack;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootFunctionInit.AMBER_FUNCTION.get();
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<AmberItemFunction> {
        public Serializer() {
        }

        public void serialize(JsonObject $$0, AmberItemFunction $$1, JsonSerializationContext $$2) {
            super.serialize($$0, $$1, $$2);
        }

        public AmberItemFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
            return new AmberItemFunction(conditions);
        }
    }
}
