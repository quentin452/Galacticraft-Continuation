package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.creativetab.*;
import cpw.mods.fml.relauncher.*;

public class BlockOreGC extends Block
{
    public BlockOreGC(final String name) {
        super(Material.rock);
        this.setBlockName(name);
        this.setBlockTextureName(GalacticraftCore.TEXTURE_PREFIX + name);
        this.setHardness(2.0f);
    }
    
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return GalacticraftCore.galacticraftBlocksTab;
    }
}
