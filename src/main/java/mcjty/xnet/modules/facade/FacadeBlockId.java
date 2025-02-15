package mcjty.xnet.modules.facade;

import mcjty.lib.varia.Tools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class FacadeBlockId {
    private final String registryName;

    public FacadeBlockId(BlockState mimicBlock) {
        Block block = mimicBlock.getBlock();
        this.registryName = Tools.getId(block).toString();
    }

    public BlockState getBlockState() {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName)).defaultBlockState();
    }

    @Override
    public String toString() {
        return registryName;
    }
}
