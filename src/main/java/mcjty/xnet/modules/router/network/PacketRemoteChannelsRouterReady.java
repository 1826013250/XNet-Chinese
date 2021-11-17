package mcjty.xnet.modules.router.network;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.xnet.client.ControllerChannelClientInfo;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketRemoteChannelsRouterReady {

    private BlockPos pos;
    private List<ControllerChannelClientInfo> list;
    private String command;

    public PacketRemoteChannelsRouterReady() {
    }

    public PacketRemoteChannelsRouterReady(PacketBuffer buf) {
        pos = buf.readBlockPos();
        command = buf.readUtf(32767);

        int size = buf.readInt();
        if (size != -1) {
            list = new ArrayList<>(size);
            for (int i = 0 ; i < size ; i++) {
                ControllerChannelClientInfo result;
                if (buf.readBoolean()) {
                    result = new ControllerChannelClientInfo(buf);
                } else {
                    result = null;
                }
                list.add(result);
            }
        } else {
            list = null;
        }
    }

    public PacketRemoteChannelsRouterReady(BlockPos pos, String command, List<ControllerChannelClientInfo> list) {
        this.pos = pos;
        this.command = command;
        this.list = new ArrayList<>();
        this.list.addAll(list);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(command);

        if (list == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(list.size());
            for (ControllerChannelClientInfo item : list) {
                if (item == null) {
                    buf.writeBoolean(false);
                } else {
                    buf.writeBoolean(true);
                    item.writeToNBT(buf);
                }
            }
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            GenericTileEntity.executeClientCommandHelper(pos, command, list);
        });
        ctx.setPacketHandled(true);
    }
}
