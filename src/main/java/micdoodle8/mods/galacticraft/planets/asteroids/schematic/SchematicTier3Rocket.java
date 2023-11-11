package micdoodle8.mods.galacticraft.planets.asteroids.schematic;

import micdoodle8.mods.galacticraft.api.recipe.*;
import micdoodle8.mods.galacticraft.planets.asteroids.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.gui.*;
import micdoodle8.mods.galacticraft.planets.asteroids.client.gui.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.inventory.*;
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.*;

public class SchematicTier3Rocket implements ISchematicPage
{
    public int getPageID() {
        return ConfigManagerAsteroids.idSchematicRocketT3;
    }
    
    public int getGuiID() {
        return 4 + "GalacticraftMars".hashCode();
    }
    
    public ItemStack getRequiredItem() {
        return new ItemStack(MarsItems.schematic, 1, 0);
    }
    
    @SideOnly(Side.CLIENT)
    public GuiScreen getResultScreen(final EntityPlayer player, final int x, final int y, final int z) {
        return (GuiScreen)new GuiSchematicTier3Rocket(player.inventory, x, y, z);
    }
    
    public Container getResultContainer(final EntityPlayer player, final int x, final int y, final int z) {
        return (Container)new ContainerSchematicTier3Rocket(player.inventory, x, y, z);
    }
    
    public int compareTo(final ISchematicPage o) {
        if (this.getPageID() > o.getPageID()) {
            return 1;
        }
        return -1;
    }
}
