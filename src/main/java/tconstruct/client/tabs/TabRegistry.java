package tconstruct.client.tabs;

import net.minecraft.client.*;
import net.minecraftforge.client.event.*;
import net.minecraft.client.gui.inventory.*;
import cpw.mods.fml.relauncher.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.gui.*;
import java.util.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.client.*;

public class TabRegistry
{
    private static ArrayList<AbstractTab> tabList;
    private static Minecraft mc;
    private static boolean initWithPotion;
    
    public static void registerTab(final AbstractTab tab) {
        TabRegistry.tabList.add(tab);
    }
    
    public static ArrayList<AbstractTab> getTabList() {
        return TabRegistry.tabList;
    }
    
    public static void addTabsToInventory(final GuiContainer gui) {
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void guiPostInit(final GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiInventory) {
            int guiLeft = (event.gui.width - 176) / 2;
            final int guiTop = (event.gui.height - 166) / 2;
            guiLeft += getPotionOffset();
            updateTabValues(guiLeft, guiTop, InventoryTabVanilla.class);
            addTabsToList(event.gui.buttonList);
        }
    }
    
    public static void openInventoryGui() {
        TabRegistry.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0DPacketCloseWindow(TabRegistry.mc.thePlayer.openContainer.windowId));
        final GuiInventory inventory = new GuiInventory((EntityPlayer)TabRegistry.mc.thePlayer);
        TabRegistry.mc.displayGuiScreen((GuiScreen)inventory);
    }
    
    public static void updateTabValues(final int cornerX, final int cornerY, final Class<?> selectedButton) {
        int count = 2;
        for (int i = 0; i < TabRegistry.tabList.size(); ++i) {
            final AbstractTab t = TabRegistry.tabList.get(i);
            if (t.shouldAddToList()) {
                t.id = count;
                t.xPosition = cornerX + (count - 2) * 28;
                t.yPosition = cornerY - 28;
                t.enabled = !t.getClass().equals(selectedButton);
                t.potionOffsetLast = getPotionOffsetNEI();
                ++count;
            }
        }
    }
    
    public static void addTabsToList(final List buttonList) {
        for (final AbstractTab tab : TabRegistry.tabList) {
            if (tab.shouldAddToList()) {
                buttonList.add(tab);
            }
        }
    }
    
    public static int getPotionOffset() {
        if (!TabRegistry.mc.thePlayer.getActivePotionEffects().isEmpty()) {
            TabRegistry.initWithPotion = true;
            return 60 + getPotionOffsetNEI();
        }
        TabRegistry.initWithPotion = false;
        return 0;
    }
    
    public static int getPotionOffsetNEI() {
        if (TabRegistry.initWithPotion && Loader.isModLoaded("NotEnoughItems")) {
            try {
                final Class<?> c = Class.forName("codechicken.nei.NEIClientConfig");
                final Object hidden = c.getMethod("isHidden", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
                final Object enabled = c.getMethod("isEnabled", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
                if (hidden instanceof Boolean && enabled instanceof Boolean) {
                    if ((boolean)hidden || !(boolean)enabled) {
                        return 0;
                    }
                    return -60;
                }
            }
            catch (Exception ex) {}
        }
        return 0;
    }
    
    static {
        TabRegistry.tabList = new ArrayList<AbstractTab>();
        TabRegistry.mc = FMLClientHandler.instance().getClient();
    }
}
