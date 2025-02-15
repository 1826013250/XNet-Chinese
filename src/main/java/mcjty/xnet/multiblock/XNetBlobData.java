package mcjty.xnet.multiblock;

import mcjty.lib.varia.LevelTools;
import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class XNetBlobData extends AbstractWorldData<XNetBlobData> {

    private static final String NAME = "XNetBlobData";

    private final Map<ResourceKey<Level>, WorldBlob> worldBlobMap = new HashMap<>();

    public XNetBlobData() {
    }

    public XNetBlobData(CompoundTag tag) {
        if (tag.contains("worlds")) {
            ListTag worlds = (ListTag) tag.get("worlds");
            for (net.minecraft.nbt.Tag world : worlds) {
                CompoundTag tc = (CompoundTag) world;
                ResourceKey<Level> dim = LevelTools.getId(tc.getString("dimtype"));
                WorldBlob blob = new WorldBlob(dim);
                blob.readFromNBT(tc);
                worldBlobMap.put(dim, blob);
            }
        }
    }

    @Nonnull
    public static XNetBlobData get(Level world) {
        return getData(world, XNetBlobData::new, XNetBlobData::new, NAME);
    }

    public WorldBlob getWorldBlob(Level world) {
        return getWorldBlob(world.dimension());
    }

    public WorldBlob getWorldBlob(ResourceKey<Level> type) {
        if (!worldBlobMap.containsKey(type)) {
            worldBlobMap.put(type, new WorldBlob(type));
        }
        return worldBlobMap.get(type);
    }


    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        ListTag list = new ListTag();
        for (var entry : worldBlobMap.entrySet()) {
            WorldBlob blob = entry.getValue();
            CompoundTag tc = new CompoundTag();
            tc.putString("dimtype", blob.getDimensionType().location().toString());
            blob.writeToNBT(tc);
            list.add(tc);
        }
        compound.put("worlds", list);

        return compound;
    }
}
