package mcjty.xnet.varia;

import mcjty.lib.container.GenericBlock;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.container.GenericItemBlock;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.varia.Logging;
import mcjty.xnet.XNet;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Constructor;

public abstract class GenericXNetBlock<T extends GenericTileEntity, C extends Container> extends GenericBlock {

    private final Class<? extends C> containerClass;

    public GenericXNetBlock(Material material,
                            Class<? extends T> tileEntityClass,
                            Class<? extends C> containerClass,
                            String name, boolean isContainer) {
        this(material, tileEntityClass, containerClass, GenericItemBlock.class, name, isContainer);
    }

    public GenericXNetBlock(Material material,
                            Class<? extends T> tileEntityClass,
                            Class<? extends C> containerClass,
                            Class<? extends ItemBlock> itemBlockClass,
                            String name, boolean isContainer) {
        super(XNet.instance, material, tileEntityClass, isContainer);
        this.containerClass = containerClass;
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(XNet.tabXNet);
        GameRegistry.registerBlock(this, itemBlockClass, name);
        GameRegistry.registerTileEntityWithAlternatives(tileEntityClass, XNet.MODID + "_" + name, name);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @SideOnly(Side.CLIENT)
    public Class<? extends GenericGuiContainer> getGuiClass() {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createClientGui(EntityPlayer entityPlayer, TileEntity tileEntity) {
        T inventory = (T) tileEntity;
        C container;
        GenericGuiContainer gui;
        try {
            Constructor<? extends C> constructor = containerClass.getConstructor(EntityPlayer.class, IInventory.class);
            container = constructor.newInstance(entityPlayer, inventory instanceof IInventory ? inventory : null);
            Constructor<? extends GenericGuiContainer> guiConstructor = getGuiClass().getConstructor(tileEntityClass, containerClass);
            gui = guiConstructor.newInstance(inventory, container);
            return gui;
        } catch (Exception e) {
            Logging.logError("Severe exception during creation of gui!");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Container createServerContainer(EntityPlayer entityPlayer, TileEntity tileEntity) {
        T inventory = (T) tileEntity;
        C container;
        try {
            Constructor<? extends C> constructor = containerClass.getConstructor(EntityPlayer.class, IInventory.class);
            container = constructor.newInstance(entityPlayer, inventory instanceof IInventory ? inventory : null);
            return container;
        } catch (Exception e) {
            Logging.logError("Severe exception during creation of gui!");
            throw new RuntimeException(e);
        }
    }

//    @Override
//    protected WrenchUsage getWrenchUsage(BlockPos pos, EntityPlayer player, ItemStack itemStack, WrenchUsage wrenchUsed, Item item) {
//        WrenchUsage usage = super.getWrenchUsage(pos, player, itemStack, wrenchUsed, item);
//        if (item instanceof IToolHammer && usage == WrenchUsage.DISABLED) {
//            // It is still possible it is a smart wrench.
//            if (item instanceof SmartWrench) {
//                SmartWrench smartWrench = (SmartWrench) item;
//                SmartWrenchMode mode = smartWrench.getMode(itemStack);
//                if (mode.equals(SmartWrenchMode.MODE_SELECT)) {
//                    if (player.isSneaking()) {
//                        usage = WrenchUsage.SNEAK_SELECT;
//                    } else {
//                        usage = WrenchUsage.SELECT;
//                    }
//                }
//            }
//        }
//        return usage;
//    }
//
    @Override
    protected boolean checkAccess(World world, EntityPlayer player, TileEntity te) {
        if (te instanceof GenericTileEntity) {
//            GenericTileEntity genericTileEntity = (GenericTileEntity) te;
//            if ((!SecurityTools.isAdmin(player)) && (!player.getPersistentID().equals(genericTileEntity.getOwnerUUID()))) {
//                int securityChannel = genericTileEntity.getSecurityChannel();
//                if (securityChannel != -1) {
//                    SecurityChannels securityChannels = SecurityChannels.getChannels(world);
//                    SecurityChannels.SecurityChannel channel = securityChannels.getChannel(securityChannel);
//                    boolean playerListed = channel.getPlayers().contains(player.getDisplayName());
//                    if (channel.isWhitelist() != playerListed) {
//                        Logging.message(player, EnumChatFormatting.RED + "You have no permission to use this block!");
//                        return true;
//                    }
//                }
//            }
        }
        return false;
    }


}
