package micdoodle8.mods.galacticraft.core.util;

import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import cpw.mods.fml.relauncher.*;

public class CreativeTabGC extends CreativeTabs
{
    private final Item itemForTab;
    private final int metaForTab;
    
    public CreativeTabGC(final int par1, final String par2Str, final Item itemForTab, final int metaForTab) {
        super(par1, par2Str);
        this.itemForTab = itemForTab;
        this.metaForTab = metaForTab;
    }
    
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return this.itemForTab;
    }
    
    @SideOnly(Side.CLIENT)
    public int func_151243_f() {
        return this.metaForTab;
    }
}
