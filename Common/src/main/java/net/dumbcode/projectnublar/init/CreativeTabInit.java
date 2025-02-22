package net.dumbcode.projectnublar.init;

import net.dumbcode.projectnublar.Constants;
import net.dumbcode.projectnublar.api.DNAData;
import net.dumbcode.projectnublar.api.DinoData;
import net.dumbcode.projectnublar.api.FossilCollection;
import net.dumbcode.projectnublar.api.FossilPieces;
import net.dumbcode.projectnublar.api.Quality;
import net.dumbcode.projectnublar.config.FossilsConfig;
import net.dumbcode.projectnublar.registration.RegistrationProvider;
import net.dumbcode.projectnublar.registration.RegistryObject;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class CreativeTabInit {
    public static final RegistrationProvider<CreativeModeTab> CREATIVE_MODE_TABS = RegistrationProvider.get(Registries.CREATIVE_MODE_TAB, Constants.MODID);
    public static final RegistryObject<CreativeModeTab> FOSSIL_ITEMS_TAB = CREATIVE_MODE_TABS.register(Constants.MODID + "_fossil_items", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup." + Constants.MODID + ".fossil_items"))
            .icon(() -> {
                ItemStack stack = new ItemStack(ItemInit.FOSSIL_ITEM.get());
                DNAData dnaData = new DNAData();
                dnaData.setEntityType(EntityInit.TYRANNOSAURUS_REX.get());
                dnaData.setFossilPiece(FossilPieces.REX_SKULL);
                dnaData.setQuality(Quality.PRISTINE);
                stack.getOrCreateTag().put("DNAData", dnaData.saveToNBT(new CompoundTag()));
                return stack;
            })
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        EntityInit.ENTITIES.getEntries().forEach((entityRegistryObject) -> {
                            FossilPieces.getPieces().forEach((fossilPiece) -> {
                                FossilsConfig.Fossil fossil = FossilsConfig.getFossils().get(entityRegistryObject.getId().toString());
                                if (FossilsConfig.getSet(fossil.getPieces().get()).pieces.get().contains(fossilPiece.name()) || fossil.getSpecial_pieces().get().contains(fossilPiece.name())) {
                                    for (Quality value : Quality.values()) {
                                        if (value == Quality.NONE) continue;
                                        ItemStack stack = new ItemStack(ItemInit.FOSSIL_ITEM.get());
                                        DNAData dnaData = new DNAData();
                                        dnaData.setEntityType(entityRegistryObject.get());
                                        dnaData.setFossilPiece(fossilPiece);
                                        dnaData.setQuality(value);
                                        stack.getOrCreateTag().put("DNAData", dnaData.saveToNBT(new CompoundTag()));
                                        output.accept(stack);
                                    }
                                }
                            });
                            ItemStack amberStack = new ItemStack(ItemInit.AMBER_ITEM.get());
                            DNAData dnaData = new DNAData();
                            dnaData.setEntityType(entityRegistryObject.get());
                            dnaData.setDnaPercentage(0.8);
                            amberStack.getOrCreateTag().put("DNAData", dnaData.saveToNBT(new CompoundTag()));
                            output.accept(amberStack);
                        });
                    })
            .build());
    public static final RegistryObject<CreativeModeTab> FOSSIL_ORES_TAB = CREATIVE_MODE_TABS.register(Constants.MODID + "_fossil_ores", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup." + Constants.MODID + ".fossil_ores"))
            .icon(() -> {
                Block block = FossilCollection.COLLECTIONS.get("projectnublar:tyrannosaurus_rex").fossilblocks().get(Blocks.STONE).get(Quality.PRISTINE).get(FossilPieces.getPieceByName("rex_skull")).get();
                ItemStack stack = new ItemStack(block);
                return stack;
            })
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        FossilCollection.COLLECTIONS.forEach((entity, fossilCollection) -> {
                            fossilCollection.fossilblocks().forEach((block, qualityMap) -> {
                                qualityMap.forEach((quality, stoneMap) -> {
                                    stoneMap.forEach((piece, blockRegistryObject) -> {
                                        FossilsConfig.Fossil fossil = FossilsConfig.getFossils().get(entity);
                                        if (FossilsConfig.getSet(fossil.getPieces().get()).pieces.get().contains(piece.name()) || fossil.getSpecial_pieces().get().contains(piece.name())) {
                                            ItemStack stack = new ItemStack(blockRegistryObject.get());
                                            stack.getOrCreateTag().putString("quality", quality.getName());
                                            output.accept(stack);
                                        }
                                    });
                                });
                            });
                            fossilCollection.amberBlocks().forEach((block, blockRegistryObject) -> output.accept(blockRegistryObject.get()));
                        });
                    })
            .build());

    public static final RegistryObject<CreativeModeTab> MACHINES_TAB = CREATIVE_MODE_TABS.register(Constants.MODID + "_machines", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup." + Constants.MODID + ".machines"))
            .icon(() -> new ItemStack(BlockInit.PROCESSOR.get()))
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        output.accept(BlockInit.PROCESSOR.get());
                        output.accept(BlockInit.SEQUENCER.get());
                        output.accept(ItemInit.SEQUENCER_COMPUTER.get());
                        output.accept(ItemInit.SEQUENCER_DOOR.get());
                        output.accept(ItemInit.SEQUENCER_SCREEN.get());
                        output.accept(BlockInit.EGG_PRINTER.get());
                        output.accept(BlockInit.INCUBATOR.get());
                        output.accept(ItemInit.INCUBATOR_ARM.get());
                        output.accept(ItemInit.INCUBATOR_ARM_BASE.get());
                        output.accept(ItemInit.INCUBATOR_LID.get());
                        output.accept(ItemInit.INCUBATOR_NEST.get());
                        output.accept(BlockInit.COAL_GENERATOR.get());
                        output.accept(BlockInit.CREATIVE_GENERATOR.get());
                    })
            .build());
    public static final RegistryObject<CreativeModeTab> MISC_TAB = CREATIVE_MODE_TABS.register(Constants.MODID + "_misc", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup." + Constants.MODID + ".misc"))
            .icon(() -> new ItemStack(ItemInit.IRON_FILTER.get()))
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        output.accept(ItemInit.IRON_FILTER.get());
                        output.accept(ItemInit.GOLD_FILTER.get());
                        output.accept(ItemInit.DIAMOND_FILTER.get());
                        output.accept(ItemInit.IRON_TANK_UPGRADE.get());
                        output.accept(ItemInit.GOLD_TANK_UPGRADE.get());
                        output.accept(ItemInit.DIAMOND_TANK_UPGRADE.get());
                        output.accept(ItemInit.IRON_COMPUTER_CHIP.get());
                        output.accept(ItemInit.GOLD_COMPUTER_CHIP.get());
                        output.accept(ItemInit.DIAMOND_COMPUTER_CHIP.get());
                        output.accept(ItemInit.HARD_DRIVE.get());
                        output.accept(ItemInit.SSD.get());
                        output.accept(ItemInit.SMALL_CONTAINER_UPGRADE.get());
                        output.accept(ItemInit.LARGE_CONTAINER_UPGRADE.get());
                        output.accept(ItemInit.WARM_BULB.get());
                        output.accept(ItemInit.WARMER_BULB.get());
                        output.accept(ItemInit.HOT_BULB.get());
                        output.accept(ItemInit.IRON_PLANT_TANK.get());
                        output.accept(ItemInit.GOLD_PLANT_TANK.get());
                        output.accept(BlockInit.HIGH_SECURITY_ELECTRIC_FENCE_POST.get());
                        output.accept(BlockInit.LOW_SECURITY_ELECTRIC_FENCE_POST.get());
                        output.accept(ItemInit.WIRE_SPOOL.get());

                    })
            .build());

    public static final RegistryObject<CreativeModeTab> DNA_TAB = CREATIVE_MODE_TABS.register(Constants.MODID + "_dna", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup." + Constants.MODID + ".dna"))
            .icon(() -> new ItemStack(ItemInit.TEST_TUBE_ITEM.get()))
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        output.accept(ItemInit.TEST_TUBE_ITEM.get());
                        for (RegistryObject<EntityType<?>> entry : EntityInit.ENTITIES.getEntries()) {
                            ItemStack stack = new ItemStack(ItemInit.TEST_TUBE_ITEM.get());
                            DNAData dnaData = new DNAData();
                            dnaData.setEntityType(entry.get());
                            dnaData.setDnaPercentage(0.5);
                            stack.getOrCreateTag().put("DNAData", dnaData.saveToNBT(new CompoundTag()));
                            output.accept(stack);
                        }
                    })
            .build());

    public static final RegistryObject<CreativeModeTab> SYRINGE_TAB = CREATIVE_MODE_TABS.register(Constants.MODID + "_syringe", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup." + Constants.MODID + ".syringe"))
            .icon(() -> new ItemStack(ItemInit.SYRINGE.get()))
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        output.accept(ItemInit.SYRINGE.get());
                        for (ResourceLocation entry : BuiltInRegistries.ENTITY_TYPE.keySet()) {
                            if (entry.getPath().contains("parrot")) {
                                for (Parrot.Variant variant : Parrot.Variant.values()) {
                                    ItemStack stack = new ItemStack(ItemInit.SYRINGE.get());
                                    DNAData dnaData = new DNAData();
                                    dnaData.setEntityType(BuiltInRegistries.ENTITY_TYPE.get(entry));
                                    dnaData.setVariant(variant.getSerializedName());
                                    dnaData.setDnaPercentage(1.0);
                                    stack.getOrCreateTag().put("DNAData", dnaData.saveToNBT(new CompoundTag()));
                                    output.accept(stack);
                                }
                            } else if (entry.getPath().contains("cat")) {
                                BuiltInRegistries.CAT_VARIANT.keySet().forEach((catVariant) -> {
                                    ItemStack stack = new ItemStack(ItemInit.SYRINGE.get());
                                    DNAData dnaData = new DNAData();
                                    dnaData.setEntityType(BuiltInRegistries.ENTITY_TYPE.get(entry));
                                    dnaData.setVariant(catVariant.toString());
                                    dnaData.setDnaPercentage(0.5);
                                    stack.getOrCreateTag().put("DNAData", dnaData.saveToNBT(new CompoundTag()));
                                    output.accept(stack);
                                });
                            } else {
                                if(BuiltInRegistries.ENTITY_TYPE.get(entry).create(Minecraft.getInstance().level) instanceof LivingEntity) {
                                    ItemStack stack = new ItemStack(ItemInit.SYRINGE.get());
                                    DNAData dnaData = new DNAData();
                                    dnaData.setEntityType(BuiltInRegistries.ENTITY_TYPE.get(entry));
                                    dnaData.setDnaPercentage(0.5);
                                    stack.getOrCreateTag().put("DNAData", dnaData.saveToNBT(new CompoundTag()));
                                    output.accept(stack);
                                }
                            }
                        }
                    })
            .build());
    public static final RegistryObject<CreativeModeTab> EGG_TAB = CREATIVE_MODE_TABS.register(Constants.MODID + "_egg", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup." + Constants.MODID + ".egg"))
            .icon(() -> new ItemStack(ItemInit.ARTIFICIAL_EGG.get()))
            .displayItems(
                    (itemDisplayParameters, output) -> {
                        output.accept(ItemInit.ARTIFICIAL_EGG.get());
                        ItemStack stack = new ItemStack(ItemInit.INCUBATED_EGG.get());
                        ItemStack stack2 = new ItemStack(ItemInit.UNINCUBATED_EGG.get());
                        DinoData dnaData = new DinoData();
                        dnaData.setBaseDino(EntityInit.TYRANNOSAURUS_REX.get());
                        dnaData.setBasePercentage(100);
                        dnaData.toStack(stack);
                        dnaData.copy().toStack(stack2);
                        output.accept(stack);
                        output.accept(stack2);
                    })
            .build());

    public static void loadClass() {
    }
}
