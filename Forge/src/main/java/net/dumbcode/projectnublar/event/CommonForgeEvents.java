package net.dumbcode.projectnublar.event;

import net.dumbcode.projectnublar.block.api.BlockConnectableBase;
import net.dumbcode.projectnublar.block.api.ConnectableBlockEntity;
import net.dumbcode.projectnublar.block.api.Connection;
import net.dumbcode.projectnublar.data.GeneDataReloadListener;
import net.dumbcode.projectnublar.init.ItemInit;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {
    @SubscribeEvent
    public static void onReloadListeners(AddReloadListenerEvent event){
        event.addListener(new GeneDataReloadListener());
    }


    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        Level world = event.getLevel();
        Direction side = event.getFace();
        if (side != null && !event.getItemStack().isEmpty() && event.getItemStack().getItem() == ItemInit.WIRE_SPOOL.get()) {
            BlockEntity tile = world.getBlockEntity(event.getPos().relative(side));
            if (tile instanceof ConnectableBlockEntity) {
                ConnectableBlockEntity cb = (ConnectableBlockEntity) tile;
                if (side.getAxis() == Direction.Axis.Y) {
                    double yRef = side == Direction.DOWN ? Double.MIN_VALUE : Double.MAX_VALUE;
                    Connection ref = null;
                    for (Connection connection : cb.getConnections()) {
                        if (connection.isBroken()) {
                            double[] in = connection.getIn();
                            double yin = (in[4] + in[5]) / 2D;
                            if (side == Direction.DOWN == yin > yRef) {
                                yRef = yin;
                                ref = connection;
                            }
                        }
                    }
                    if (ref != null) {
                        ref.setBroken(false);
                        event.setCanceled(true);
                        BlockConnectableBase.placeEffect(event.getEntity(), event.getHand(), event.getLevel(), event.getPos());
                    }
                } else {
                    for (Connection connection : cb.getConnections()) {
                        if (connection.isBroken()) {
                            connection.setBroken(false);
                            event.setCanceled(true);
                            BlockConnectableBase.placeEffect(event.getEntity(), event.getHand(), event.getLevel(), event.getPos());
                            break;
                        }
                    }
                }
            }
        }
    }

}
