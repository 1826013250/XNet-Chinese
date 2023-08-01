package mcjty.xnet.modules.various.items;

import mcjty.lib.varia.ComponentFactory;
import mcjty.rftoolsbase.api.xnet.keys.ConsumerId;
import mcjty.xnet.XNet;
import mcjty.xnet.modules.cables.CableColor;
import mcjty.xnet.modules.cables.CableModule;
import mcjty.xnet.modules.cables.blocks.ConnectorBlock;
import mcjty.xnet.modules.cables.blocks.ConnectorTileEntity;
import mcjty.xnet.modules.cables.blocks.GenericCableBlock;
import mcjty.xnet.multiblock.WorldBlob;
import mcjty.xnet.multiblock.XNetBlobData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ConnectorUpgradeItem extends Item {

    public ConnectorUpgradeItem() {
        super(new Properties()
                .tab(XNet.setup.getTab())
        );
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(ComponentFactory.literal(ChatFormatting.BLUE + "对着正常的连接器潜行并右键"));
        tooltip.add(ComponentFactory.literal(ChatFormatting.BLUE + "将其升级为高级连接器"));
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(@Nonnull Level worldIn, @Nonnull Player playerIn, @Nonnull InteractionHand hand) {
        return super.use(worldIn, playerIn, hand);
    }

    @Override
    @Nonnull
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        Player player = context.getPlayer();
        Block block = state.getBlock();

        if (block == CableModule.CONNECTOR.get()) {
            if (!world.isClientSide) {
                BlockEntity te = world.getBlockEntity(pos);
                if (te instanceof ConnectorTileEntity) {
                    CompoundTag tag = te.saveWithoutMetadata();
                    CableColor color = world.getBlockState(pos).getValue(GenericCableBlock.COLOR);

                    XNetBlobData blobData = XNetBlobData.get(world);
                    WorldBlob worldBlob = blobData.getWorldBlob(world);
                    ConsumerId consumer = worldBlob.getConsumerAt(pos);
                    ((ConnectorBlock)block).unlinkBlock(world, pos);
                    world.setBlockAndUpdate(pos, CableModule.ADVANCED_CONNECTOR.get().defaultBlockState().setValue(GenericCableBlock.COLOR, color));
                    BlockState blockState = world.getBlockState(pos);
                    ((ConnectorBlock)blockState.getBlock()).createCableSegment(world, pos, consumer);

                    blockState = ((ConnectorBlock) block).calculateState(world, pos, blockState);
                    world.setBlock(pos, blockState, Block.UPDATE_ALL);
                    player.getInventory().removeItem(player.getInventory().selected, 1);
                    player.containerMenu.broadcastChanges();
                    player.displayClientMessage(ComponentFactory.literal(ChatFormatting.GREEN + "连接器已升级"), false);
                }
            }
            return InteractionResult.SUCCESS;
        } else if (block == CableModule.ADVANCED_CONNECTOR.get()) {
            if (!world.isClientSide) {
                player.displayClientMessage(ComponentFactory.literal(ChatFormatting.YELLOW + "这个连接器已经是高级的了!"), false);
            }
            return InteractionResult.SUCCESS;
        } else {
            if (!world.isClientSide) {
                player.displayClientMessage(ComponentFactory.literal(ChatFormatting.RED + "对着连接器使用来升级!"), false);
            }
            return InteractionResult.SUCCESS;
        }
    }

}
