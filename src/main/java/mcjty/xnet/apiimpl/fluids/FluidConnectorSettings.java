package mcjty.xnet.apiimpl.fluids;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lib.varia.FluidTools;
import mcjty.lib.varia.JSonTools;
import mcjty.rftoolsbase.api.xnet.gui.IEditorGui;
import mcjty.rftoolsbase.api.xnet.gui.IndicatorIcon;
import mcjty.rftoolsbase.api.xnet.helper.AbstractConnectorSettings;
import mcjty.xnet.XNet;
import mcjty.xnet.apiimpl.EnumStringTranslators;
import mcjty.xnet.setup.Config;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class FluidConnectorSettings extends AbstractConnectorSettings {

    public static final ResourceLocation iconGuiElements = new ResourceLocation(XNet.MODID, "textures/gui/guielements.png");

    public static final String TAG_MODE = "mode";
    public static final String TAG_RATE = "rate";
    public static final String TAG_MINMAX = "minmax";
    public static final String TAG_PRIORITY = "priority";
    public static final String TAG_FILTER = "flt";
    public static final String TAG_SPEED = "speed";


    public enum FluidMode {
        INS,
        EXT
    }

    private FluidMode fluidMode = FluidMode.INS;

    @Nullable private Integer priority = 0;
    @Nullable private Integer rate = null;
    @Nullable private Integer minmax = null;
    private int speed = 2;

    private ItemStack filter = ItemStack.EMPTY;

    public FluidConnectorSettings(@Nonnull Direction side) {
        super(side);
    }

    public FluidMode getFluidMode() {
        return fluidMode;
    }

    public int getSpeed() {
        return speed;
    }

    @Nonnull
    public Integer getPriority() {
        return priority == null ? 0 : priority;
    }

    @Nonnull
    public Integer getRate() {
        return rate == null ? Config.maxFluidRateNormal.get() : rate;
    }

    @Nullable
    public Integer getMinmax() {
        return minmax;
    }

    @Nullable
    @Override
    public IndicatorIcon getIndicatorIcon() {
        return switch (fluidMode) {
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
        String[] speeds;
        int maxrate;
        if (advanced) {
            speeds = new String[] { "10", "20", "60", "100", "200" };
            maxrate = Config.maxFluidRateAdvanced.get();
        } else {
            speeds = new String[] { "20", "60", "100", "200" };
            maxrate = Config.maxFluidRateNormal.get();
        }

        sideGui(gui);
        colorsGui(gui);
        redstoneGui(gui);
        gui.nl()
                .choices(TAG_MODE, "输入/提取模式", fluidMode, FluidMode.values())
                .choices(TAG_SPEED, "每次操作所需的游戏刻", Integer.toString(speed * 10), speeds)
                .nl()

                .label("权").integer(TAG_PRIORITY, "输入优先级", priority, 36).nl()

                .label("速率")
                .integer(TAG_RATE, fluidMode == FluidMode.EXT ? "流体提取速率|(最大 " + maxrate + "mb)" : "流体输入速率|(最大 " + maxrate + "mb)", rate, 36, maxrate)
                .shift(10)
                .label(fluidMode == FluidMode.EXT ? "最小" : "最大")
                .integer(TAG_MINMAX, fluidMode == FluidMode.EXT ? "保持目标容器内液体的最小值" : "当目标容器内液体超过设定值时|禁用流体输入", minmax, 36)
                .nl()
                .label("过滤器")
                .ghostSlot(TAG_FILTER, filter);
    }

    private static final Set<String> INSERT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS, TAG_COLOR+"0", TAG_COLOR+"1", TAG_COLOR+"2", TAG_COLOR+"3", TAG_RATE, TAG_MINMAX, TAG_PRIORITY, TAG_FILTER);
    private static final Set<String> EXTRACT_TAGS = ImmutableSet.of(TAG_MODE, TAG_RS, TAG_COLOR+"0", TAG_COLOR+"1", TAG_COLOR+"2", TAG_COLOR+"3", TAG_RATE, TAG_MINMAX, TAG_PRIORITY, TAG_FILTER, TAG_SPEED);

    @Override
    public boolean isEnabled(String tag) {
        if (fluidMode == FluidMode.INS) {
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

    @Nullable
    public FluidStack getMatcher() {
        // @todo optimize/cache this?
        if (!filter.isEmpty()) {
            return FluidTools.convertBucketToFluid(filter);
        } else {
            return null;
        }
    }


    @Override
    public void update(Map<String, Object> data) {
        super.update(data);
        fluidMode = FluidMode.valueOf(((String)data.get(TAG_MODE)).toUpperCase());
        rate = (Integer) data.get(TAG_RATE);
        minmax = (Integer) data.get(TAG_MINMAX);
        priority = (Integer) data.get(TAG_PRIORITY);
        speed = Integer.parseInt((String) data.get(TAG_SPEED)) / 10;
        if (speed == 0) {
            speed = 2;
        }
        filter = (ItemStack) data.get(TAG_FILTER);
        if (filter == null) {
            filter = ItemStack.EMPTY;
        }
    }

    @Override
    public JsonObject writeToJson() {
        JsonObject object = new JsonObject();
        super.writeToJsonInternal(object);
        setEnumSafe(object, "fluidmode", fluidMode);
        setIntegerSafe(object, "priority", priority);
        setIntegerSafe(object, "rate", rate);
        setIntegerSafe(object, "minmax", minmax);
        setIntegerSafe(object, "speed", speed);
        if (!filter.isEmpty()) {
            object.add("filter", JSonTools.itemStackToJson(filter));
        }
        if (rate != null && rate > Config.maxFluidRateNormal.get()) {
            object.add("advancedneeded", new JsonPrimitive(true));
        }
        if (speed == 1) {
            object.add("advancedneeded", new JsonPrimitive(true));
        }
        return object;
    }

    @Override
    public void readFromJson(JsonObject object) {
        super.readFromJsonInternal(object);
        fluidMode = getEnumSafe(object, "fluidmode", EnumStringTranslators::getFluidMode);
        priority = getIntegerSafe(object, "priority");
        rate = getIntegerSafe(object, "rate");
        minmax = getIntegerSafe(object, "minmax");
        speed = getIntegerNotNull(object, "speed");
        if (object.has("filter")) {
            filter = JSonTools.jsonToItemStack(object.get("filter").getAsJsonObject());
        } else {
            filter = ItemStack.EMPTY;
        }
    }


    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        fluidMode = FluidMode.values()[tag.getByte("fluidMode")];
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
        speed = tag.getInt("speed");
        if (speed == 0) {
            speed = 2;
        }
        if (tag.contains("filter")) {
            CompoundTag itemTag = tag.getCompound("filter");
            filter = ItemStack.of(itemTag);
        } else {
            filter = ItemStack.EMPTY;
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.putByte("fluidMode", (byte) fluidMode.ordinal());
        if (priority != null) {
            tag.putInt("priority", priority);
        }
        if (rate != null) {
            tag.putInt("rate", rate);
        }
        if (minmax != null) {
            tag.putInt("minmax", minmax);
        }
        tag.putInt("speed", speed);
        if (!filter.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            filter.save(itemTag);
            tag.put("filter", itemTag);
        }
    }
}
