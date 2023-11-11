package micdoodle8.mods.galacticraft.core.schematic;

import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.core.client.gui.container.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.inventory.*;

public class SchematicRocketT1 extends SchematicPage
{
    public int getPageID() {
        return ConfigManagerCore.idSchematicRocketT1;
    }
    
    public int getGuiID() {
        return 0;
    }
    
    public ItemStack getRequiredItem() {
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    public GuiScreen getResultScreen(final EntityPlayer player, final int x, final int y, final int z) {
        return (GuiScreen)new GuiSchematicTier1Rocket(player.inventory, x, y, z);
    }
    
    public Container getResultContainer(final EntityPlayer player, final int x, final int y, final int z) {
        return (Container)new ContainerSchematicTier1Rocket(player.inventory, x, y, z);
    }
}
