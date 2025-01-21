package net.dumbcode.projectnublar.init;

import net.dumbcode.projectnublar.Constants;
import net.dumbcode.projectnublar.api.FossilCollection;
import net.dumbcode.projectnublar.block.ElectricFencePostBlock;
import net.dumbcode.projectnublar.block.ElectricFenceBlock;
import net.dumbcode.projectnublar.block.EggPrinterBlock;
import net.dumbcode.projectnublar.block.GeneratorBlock;
import net.dumbcode.projectnublar.block.IncubatorBlock;
import net.dumbcode.projectnublar.block.SequencerBlock;
import net.dumbcode.projectnublar.block.api.EnumConnectionType;
import net.dumbcode.projectnublar.block.entity.BlockEntityElectricFence;
import net.dumbcode.projectnublar.block.entity.BlockEntityElectricFencePole;
import net.dumbcode.projectnublar.block.entity.EggPrinterBlockEntity;
import net.dumbcode.projectnublar.block.entity.GeneratorBlockEntity;
import net.dumbcode.projectnublar.block.entity.IncubatorBlockEntity;
import net.dumbcode.projectnublar.block.entity.ProcessorBlockEntity;
import net.dumbcode.projectnublar.block.entity.SequencerBlockEntity;
import net.dumbcode.projectnublar.block.ProcessorBlock;
import net.dumbcode.projectnublar.item.GeoMultiBlockItem;
import net.dumbcode.projectnublar.registration.RegistrationProvider;
import net.dumbcode.projectnublar.registration.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockInit {
    public static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(Registries.BLOCK, Constants.MODID);
    public static final RegistrationProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistrationProvider.get(Registries.BLOCK_ENTITY_TYPE, Constants.MODID);

    public static FossilCollection FOSSIL = FossilCollection.create("tyrannosaurus_rex");
    public static RegistryObject<Block> PROCESSOR = registerBlock("processor", () -> new ProcessorBlock(BlockBehaviour.Properties.of().noOcclusion(),3,2, 2), block->()-> new GeoMultiBlockItem(block.get(),ItemInit.getItemProperties(),3,2, 2));
    public static RegistryObject<Block> SEQUENCER = registerBlock("sequencer", () -> new SequencerBlock(BlockBehaviour.Properties.of().noOcclusion(),2,2, 2), block->()-> new GeoMultiBlockItem(block.get(),ItemInit.getItemProperties(),2,2, 2));
    public static RegistryObject<Block> EGG_PRINTER = registerBlock("egg_printer", () -> new EggPrinterBlock(BlockBehaviour.Properties.of().noOcclusion(),1,2, 1), block->()-> new GeoMultiBlockItem(block.get(),ItemInit.getItemProperties(),1,2, 1));
    public static RegistryObject<Block> INCUBATOR = registerBlock("incubator", () -> new IncubatorBlock(BlockBehaviour.Properties.of().noOcclusion(),2,2, 1), block->()-> new GeoMultiBlockItem(block.get(),ItemInit.getItemProperties(),2,2, 1));
    public static RegistryObject<Block> ELECTRIC_FENCE = registerBlock("electric_fence", () -> new ElectricFenceBlock(BlockBehaviour.Properties.of().noLootTable().noOcclusion()));
    public static RegistryObject<Block> LOW_SECURITY_ELECTRIC_FENCE_POST = registerBlock("low_security_electric_fence_post", () -> new ElectricFencePostBlock(BlockBehaviour.Properties.of().noOcclusion(), EnumConnectionType.LOW_SECURITY));
    public static RegistryObject<Block> HIGH_SECURITY_ELECTRIC_FENCE_POST = registerBlock("high_security_electric_fence_post", () -> new ElectricFencePostBlock(BlockBehaviour.Properties.of().noOcclusion(), EnumConnectionType.HIGH_SECURITY));
    public static RegistryObject<Block> COAL_GENERATOR = registerBlock("coal_generator", ()-> new GeneratorBlock(BlockBehaviour.Properties.of(),256,16,0));
    public static RegistryObject<Block> CREATIVE_GENERATOR = registerBlock("creative_generator", ()-> new GeneratorBlock(BlockBehaviour.Properties.of(),99999,99999,0));


    public static RegistryObject<BlockEntityType<ProcessorBlockEntity>> PROCESSOR_BLOCK_ENTITY = BLOCK_ENTITIES.register("processor", () -> BlockEntityType.Builder.of(ProcessorBlockEntity::new, PROCESSOR.get()).build(null));
    public static RegistryObject<BlockEntityType<SequencerBlockEntity>> SEQUENCER_BLOCK_ENTITY = BLOCK_ENTITIES.register("sequencer", () -> BlockEntityType.Builder.of(SequencerBlockEntity::new, SEQUENCER.get()).build(null));
    public static RegistryObject<BlockEntityType<EggPrinterBlockEntity>> EGG_PRINTER_BLOCK_ENTITY = BLOCK_ENTITIES.register("egg_printer", () -> BlockEntityType.Builder.of(EggPrinterBlockEntity::new, EGG_PRINTER.get()).build(null));
    public static RegistryObject<BlockEntityType<IncubatorBlockEntity>> INCUBATOR_BLOCK_ENTITY = BLOCK_ENTITIES.register("incubator", () -> BlockEntityType.Builder.of(IncubatorBlockEntity::new, INCUBATOR.get()).build(null));
    public static RegistryObject<BlockEntityType<BlockEntityElectricFence>> ELECTRIC_FENCE_BLOCK_ENTITY = BLOCK_ENTITIES.register("electric_fence", () -> BlockEntityType.Builder.of(BlockEntityElectricFence::new, BlockInit.ELECTRIC_FENCE.get()).build(null));
    public static RegistryObject<BlockEntityType<BlockEntityElectricFencePole>> ELECTRIC_FENCE_POST_BLOCK_ENTITY = BLOCK_ENTITIES.register("electric_fence_pole", () -> BlockEntityType.Builder.of(BlockEntityElectricFencePole::new, BlockInit.LOW_SECURITY_ELECTRIC_FENCE_POST.get(),BlockInit.HIGH_SECURITY_ELECTRIC_FENCE_POST.get()).build(null));
    public static RegistryObject<BlockEntityType<GeneratorBlockEntity>> GENERATOR = BLOCK_ENTITIES.register("coal_generator", ()-> BlockEntityType.Builder.of(GeneratorBlockEntity::new, COAL_GENERATOR.get(), CREATIVE_GENERATOR.get()).build(null));
    public static void loadClass() {}

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name, block, b -> () -> new BlockItem(b.get(), ItemInit.getItemProperties()));
    }

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Function<RegistryObject<T>, Supplier<? extends BlockItem>> item) {
        var reg = BLOCKS.register(name, block);
        ItemInit.ITEMS.register(name, () -> item.apply(reg).get());
        return reg;
    }
}