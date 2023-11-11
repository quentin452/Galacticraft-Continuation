package micdoodle8.mods.galacticraft.core.schematic;

import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.core.client.gui.container.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.core.inventory.*;

public class SchematicMoonBuggy extends SchematicPage
{
    public int getPageID() {
        return ConfigManagerCore.idSchematicMoonBuggy;
    }
    
    public int getGuiID() {
        return 1;
    }
    
    public ItemStack getRequiredItem() {
        return new ItemStack(GCItems.schematic, 1, 0);
    }
    
    @SideOnly(Side.CLIENT)
    public GuiScreen getResultScreen(final EntityPlayer player, final int x, final int y, final int z) {
        return (GuiScreen)new GuiSchematicBuggy(player.inventory);
    }
    
    public Container getResultContainer(final EntityPlayer player, final int x, final int y, final int z) {
        return (Container)new ContainerBuggyBench(player.inventory, x, y, z);
    }
}
