package mcjty.xnet.modules.cables.client;

import net.minecraft.core.Direction;

public class StringConvert {
    public static String convertToChinese(Direction facing) {
        String direction = facing.getSerializedName().substring(0, 1).toUpperCase();
        return switch (direction) {
            case "N" -> "北";
            case "S" -> "南";
            case "W" -> "西";
            case "E" -> "东";
            case "U" -> "上";
            case "D" -> "下";
            default -> "未知";
        };
    }
}