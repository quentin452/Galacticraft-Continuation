package micdoodle8.mods.galacticraft.api.recipe;

import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.gui.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.inventory.*;

public interface ISchematicPage extends Comparable<ISchematicPage>
{
    int getPageID();
    
    int getGuiID();
    
    ItemStack getRequiredItem();
    
    @SideOnly(Side.CLIENT)
    GuiScreen getResultScreen(final EntityPlayer p0, final int p1, final int p2, final int p3);
    
    Container getResultContainer(final EntityPlayer p0, final int p1, final int p2, final int p3);
}
