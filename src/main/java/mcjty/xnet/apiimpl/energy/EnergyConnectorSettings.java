package mcjty.xnet.apiimpl.energy;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.rftoolsbase.api.xnet.gui.IEditorGui;
import mcjty.rftoolsbase.api.xnet.gui.IndicatorIcon;
import mcjty.rftoolsbase.api.xnet.helper.AbstractConnectorSettings;
import mcjty.xnet.XNet;
import mcjty.xnet.apiimpl.EnumStringTranslators;
import mcjty.xnet.setup.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class EnergyConnectorSettings extends AbstractConnectorSettings {

    public static final ResourceLocation iconGuiElements = new ResourceLocation(XNet.MODID, "textures/gui/guielements.png");

    public static final String TAG_MODE = "mode";
    public static final String TAG_RATE = "rate";
    public static final String TAG_MINMAX = "minmax";
    public static final String TAG_PRIORITY = "priority";

    public enum EnergyMode {
        INS,
        EXT
    }

    private EnergyMode energyMode = EnergyMode.INS;

    @Nullable private Integer priority = 0;
    @Nullable private Integer rate = null;
    @Nullable private Integer minmax = null;

    public EnergyConnectorSettings(@Nonnull Direction side) {
        super(side);
    }

    public EnergyMode getEnergyMode() {
        return energyMode;
    }

    @Nullable
    @Override
    public IndicatorIcon getIndicatorIcon() {
        return switch (energyMode) {
            case INS -> new IndicatorIcon(iconGuiElements, 0, 70, 13, 10);
            case EXT -> new IndicatorIcon(iconGuiElements, 13, 70, 13, 10);
        };
    }

    @Override
    @Nullable
    public String getIndicator() {
        return null;
    }

    @Override
    public void createGui(IEditorGui gui) {
        advanced = gui.isAdvanced();
        sideGui(gui);
        colorsGui(gui);
        redstoneGui(gui);
        gui.nl()
                .choices(TAG_MODE, "输入/提取模式", energyMode, EnergyMode.values())
                .nl()

                .label("权").integer(TAG_PRIORITY, "输入的优先级", priority, 30).nl()

                .label("速率")
                .integer(TAG_RATE,
                        (energyMode == EnergyMode.EXT ? "最大能量提取速率" : "最大能量输入速率") +
                        "|(最大 " + (advanced ? Config.maxRfRateAdvanced.get() : Config.maxRfRateNormal.get()) + ")", rate, 40)
                .shift(10)
                .label(energyMode == EnergyMode.EXT ? "最小" : "最大")
                .integer(TAG_MINMAX, energyMode == EnergyMode.EXT ? "当能量小于设定值时|禁用能量提取" : "当能量大于设定值时|禁用能量输入", minmax, 50);
    }

    private static final Set<String> INSERT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS, TAG_COLOR+"0", TAG_COLOR+"1", TAG_COLOR+"2", TAG_COLOR+"3", TAG_RATE, TAG_MINMAX, TAG_PRIORITY);
    private static final Set<String> EXTRACT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS, TAG_COLOR+"0", TAG_COLOR+"1", TAG_COLOR+"2", TAG_COLOR+"3", TAG_RATE, TAG_MINMAX, TAG_PRIORITY);

    @Override
    public boolean isEnabled(String tag) {
        if (energyMode == EnergyMode.INS) {
            if (tag.equals(TAG_FACING)) {
                return advanced;
            }
            return INSERT_TAGS.contains(tag);
        } else {
            if (tag.equals(TAG_FACING)) {
                return false;           // We cannot extract from different sides
            }
            return EXTRACT_TAGS.contains(tag);
        }
    }

    @Nonnull
    public Integer getPriority() {
        return priority == null ? 0 : priority;
    }

    @Nullable
    public Integer getRate() {
        return rate;
    }

    @Nullable
    public Integer getMinmax() {
        return minmax;
    }

    @Override
    public void update(Map<String, Object> data) {
        super.update(data);
        energyMode = EnergyMode.valueOf(((String)data.get(TAG_MODE)).toUpperCase());
        rate = (Integer) data.get(TAG_RATE);
        minmax = (Integer) data.get(TAG_MINMAX);
        priority = (Integer) data.get(TAG_PRIORITY);
    }

    @Override
    public JsonObject writeToJson() {
        JsonObject object = new JsonObject();
        super.writeToJsonInternal(object);
        setEnumSafe(object, "energymode", energyMode);
        setIntegerSafe(object, "priority", priority);
        setIntegerSafe(object, "rate", rate);
        setIntegerSafe(object, "minmax", minmax);
        if (rate != null && rate > Config.maxRfRateNormal.get()) {
            object.add("advancedneeded", new JsonPrimitive(true));
        }
        return object;
    }

    @Override
    public void readFromJson(JsonObject object) {
        super.readFromJsonInternal(object);
        energyMode = getEnumSafe(object, "energymode", EnumStringTranslators::getEnergyMode);
        priority = getIntegerSafe(object, "priority");
        rate = getIntegerSafe(object, "rate");
        minmax = getIntegerSafe(object, "minmax");
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        energyMode = EnergyMode.values()[tag.getByte("itemMode")];
        if (tag.contains("priority")) {
            priority = tag.getInt("priority");
        } else {
            priority = null;
        }
        if (tag.contains("rate")) {
            rate = tag.getInt("rate");
        } else {
            rate = null;
        }
        if (tag.contains("minmax")) {
            minmax = tag.getInt("minmax");
        } else {
            minmax = null;
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.putByte("itemMode", (byte) energyMode.ordinal());
        if (priority != null) {
            tag.putInt("priority", priority);
        }
        if (rate != null) {
            tag.putInt("rate", rate);
        }
        if (minmax != null) {
            tag.putInt("minmax", minmax);
        }
    }
}
