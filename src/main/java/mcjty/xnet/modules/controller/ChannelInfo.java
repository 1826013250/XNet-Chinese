package mcjty.xnet.modules.controller;

import mcjty.lib.varia.OrientationTools;
import mcjty.rftoolsbase.api.xnet.channels.IChannelSettings;
import mcjty.rftoolsbase.api.xnet.channels.IChannelType;
import mcjty.rftoolsbase.api.xnet.keys.ConsumerId;
import mcjty.rftoolsbase.api.xnet.keys.SidedConsumer;
import mcjty.xnet.XNet;
import mcjty.xnet.client.ConnectorInfo;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ChannelInfo {

    public static final int MAX_CHANNELS = 8;

    private final IChannelType type;
    private final IChannelSettings channelSettings;
    private String channelName;
    private boolean enabled = true;

    private final Map<SidedConsumer, ConnectorInfo> connectors = new HashMap<>();

    public ChannelInfo(IChannelType type) {
        this.type = type;
        channelSettings = type.createChannel();
    }

    @Nonnull
    public String getChannelName() {
        return channelName == null ? "" : channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public IChannelType getType() {
        return type;
    }

    public IChannelSettings getChannelSettings() {
        return channelSettings;
    }

    public Map<SidedConsumer, ConnectorInfo> getConnectors() {
        return connectors;
    }

    public ConnectorInfo createConnector(SidedConsumer id, boolean advanced) {
        ConnectorInfo info = new ConnectorInfo(type, id, advanced);
        connectors.put(id, info);
        return info;
    }

    public void writeToNBT(CompoundTag tag) {
        channelSettings.writeToNBT(tag);
        tag.putBoolean("enabled", enabled);
        if (channelName != null && !channelName.isEmpty()) {
            tag.putString("name", channelName);
        }
        ListTag conlist = new ListTag();
        for (Map.Entry<SidedConsumer, ConnectorInfo> entry : connectors.entrySet()) {
            CompoundTag tc = new CompoundTag();
            ConnectorInfo connectorInfo = entry.getValue();
            connectorInfo.writeToNBT(tc);
            tc.putInt("consumerId", entry.getKey().consumerId().id());
            tc.putInt("side", entry.getKey().side().ordinal());
            tc.putString("type", connectorInfo.getType().getID());
            tc.putBoolean("advanced", connectorInfo.isAdvanced());
            conlist.add(tc);
        }
        tag.put("connectors", conlist);
    }

    public void readFromNBT(CompoundTag tag) {
        channelSettings.readFromNBT(tag);
        enabled = tag.getBoolean("enabled");
        if (tag.contains("name")) {
            channelName = tag.getString("name");
        } else {
            channelName = null;
        }
        ListTag conlist = tag.getList("connectors", Tag.TAG_COMPOUND);
        for (int i = 0 ; i < conlist.size() ; i++) {
            CompoundTag tc = conlist.getCompound(i);
            String id = tc.getString("type");
            IChannelType type = XNet.xNetApi.findType(id);
            if (type == null) {
                XNet.setup.getLogger().warn("不支持的类型 “" + id + "”!");
                continue;
            }
            if (!getType().equals(type)) {
                XNet.setup.getLogger().warn("尝试为连接器加载一个不匹配的类型“" + type + "”!");
                continue;
            }
            ConsumerId consumerId = new ConsumerId(tc.getInt("consumerId"));
            Direction side = OrientationTools.DIRECTION_VALUES[tc.getInt("side")];
            SidedConsumer key = new SidedConsumer(consumerId, side);
            boolean advanced = tc.getBoolean("advanced");
            ConnectorInfo connectorInfo = new ConnectorInfo(type, key, advanced);
            connectorInfo.readFromNBT(tc);
            connectors.put(key, connectorInfo);
        }
    }
}
