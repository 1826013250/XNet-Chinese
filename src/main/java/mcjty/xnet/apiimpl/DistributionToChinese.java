package mcjty.xnet.apiimpl;

public class DistributionToChinese {
    public static String convertToChinese(String original) {
        return switch (original) {
            case "Distribute" -> "全部分发";
            case "Priority" -> "按优先级分发";
            case "Roundrobin" -> "轮询调度";
            case "North" -> "北";
            case "South" -> "南";
            case "West" -> "西";
            case "East" -> "东";
            case "Down" -> "下";
            case "Up" -> "上";
            case "Ext" -> "提取";
            case "Ins" -> "输入";
            case "Count" -> "指定数量";
            case "Single" -> "单个物品";
            case "Stack" -> "一组物品";
            case "Rnd" -> "随机";
            case "Order" -> "顺序";
            case "First" -> "第一格";
            case "Off" -> "关闭";
            case "Item" -> "物品";
            case "Fluid" -> "流体";
            case "Energy" -> "能量";
            case "Rs" -> "红石信号";
            case "Sensor" -> "侦测";
            case "Output" -> "输出";
            default -> original;
        };
    }

    public static String backToEnglish(String original) {
        return switch (original) {
            case "全部分发" -> "Distribute";
            case "按优先级分发" -> "Priority";
            case "轮询调度" -> "Roundrobin";
            case "北" -> "North";
            case "南" -> "South";
            case "西" -> "West";
            case "东" -> "Ease";
            case "下" -> "Down";
            case "上" -> "Up";
            case "提取" -> "Ext";
            case "输入" -> "Ins";
            case "指定数量" -> "Count";
            case "单个物品" -> "Single";
            case "一组物品" -> "Stack";
            case "随机" -> "Rnd";
            case "顺序" -> "Order";
            case "第一格" -> "First";
            case "关闭" -> "Off";
            case "物品" -> "Item";
            case "流体" -> "Fluid";
            case "能量" -> "Energy";
            case "红石信号" -> "Rs";
            case "侦测" -> "Sensor";
            case "输出" -> "Output";
            default -> original;
        };
    }
}
