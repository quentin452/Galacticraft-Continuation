package micdoodle8.mods.galacticraft.core.client.gui.screen;

import tconstruct.client.tabs.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.client.*;
import net.minecraft.client.entity.*;

public class InventoryTabGalacticraft extends AbstractTab
{
    public InventoryTabGalacticraft() {
        super(0, 0, 0, new ItemStack(GCItems.oxMask));
    }
    
    public void onTabClicked() {
        GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_OPEN_EXTENDED_INVENTORY, new Object[0]));
        ClientProxyCore.playerClientHandler.onBuild(0, (EntityPlayerSP)FMLClientHandler.instance().getClientPlayerEntity());
    }
    
    public boolean shouldAddToList() {
        return true;
    }
}
