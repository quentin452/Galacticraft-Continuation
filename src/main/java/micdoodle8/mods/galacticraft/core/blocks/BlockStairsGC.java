package micdoodle8.mods.galacticraft.core.blocks;

import net.minecraft.util.*;
import net.minecraft.block.*;
import net.minecraft.creativetab.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.texture.*;
import java.util.*;

public class BlockStairsGC extends BlockStairs
{
    private IIcon[] tinSideIcon;
    private final StairsCategoryGC category;
    
    public BlockStairsGC(final String name, final Block model, final StairsCategoryGC cat) {
        super(model, 0);
        this.category = cat;
        this.setBlockName(name);
        this.useNeighborBrightness = true;
    }
    
    public CreativeTabs getCreativeTabToDisplayOn() {
        if (this.category == StairsCategoryGC.TIN1 || this.category == StairsCategoryGC.TIN2 || this.category == StairsCategoryGC.MOON_STONE || this.category == StairsCategoryGC.MARS_COBBLESTONE || this.category == StairsCategoryGC.MOON_BRICKS || this.category == StairsCategoryGC.MARS_BRICKS) {
            return GalacticraftCore.galacticraftBlocksTab;
        }
        return null;
    }
    
    public void registerBlockIcons(final IIconRegister par1IconRegister) {
        if (this.category == StairsCategoryGC.TIN1) {
            this.blockIcon = par1IconRegister.registerIcon("galacticraftcore:deco_aluminium_4");
        }
        else if (this.category == StairsCategoryGC.TIN2) {
            this.blockIcon = par1IconRegister.registerIcon("galacticraftcore:deco_aluminium_2");
        }
        else if (this.category == StairsCategoryGC.MOON_STONE) {
            this.blockIcon = par1IconRegister.registerIcon("galacticraftmoon:bottom");
        }
        else if (this.category == StairsCategoryGC.MOON_BRICKS) {
            this.blockIcon = par1IconRegister.registerIcon("galacticraftmoon:brick");
        }
        if (GalacticraftCore.isPlanetsLoaded) {
            if (this.category == StairsCategoryGC.MARS_COBBLESTONE) {
                this.blockIcon = par1IconRegister.registerIcon("galacticraftmars:cobblestone");
            }
            else if (this.category == StairsCategoryGC.MARS_BRICKS) {
                this.blockIcon = par1IconRegister.registerIcon("galacticraftmars:brick");
            }
        }
        (this.tinSideIcon = new IIcon[2])[0] = par1IconRegister.registerIcon("galacticraftcore:deco_aluminium_1");
        this.tinSideIcon[1] = par1IconRegister.registerIcon("galacticraftcore:deco_aluminium_4");
    }
    
    public boolean isWoodCategory(final String block) {
        final String type = StairsCategoryGC.valueOf(block).type;
        return type.equals("wood");
    }
    
    public boolean isStoneCategory(final String block) {
        final String type = StairsCategoryGC.valueOf(block).type;
        return type.equals("stone");
    }
    
    public static int getWoodCategoryAmount() {
        int woodCatNo = 0;
        for (final StairsCategoryGC cat : StairsCategoryGC.values()) {
            if (cat.values.contains("wood")) {
                ++woodCatNo;
            }
        }
        return woodCatNo;
    }
    
    public static int getStoneCategoryAmount() {
        int woodCatNo = 0;
        for (final StairsCategoryGC cat : StairsCategoryGC.values()) {
            if (cat.values.contains("stone")) {
                ++woodCatNo;
            }
        }
        return woodCatNo;
    }
    
    public IIcon getIcon(final int side, final int meta) {
        if (this.category == StairsCategoryGC.TIN2) {
            if (meta == 0 || meta == 8) {
                switch (side) {
                    case 0: {
                        return this.tinSideIcon[1];
                    }
                    case 1: {
                        return this.blockIcon;
                    }
                    case 2: {
                        return this.tinSideIcon[0];
                    }
                    case 3: {
                        return this.tinSideIcon[0];
                    }
                    case 4: {
                        return this.blockIcon;
                    }
                    case 5: {
                        return this.tinSideIcon[0];
                    }
                    default: {
                        return this.blockIcon;
                    }
                }
            }
            else if (meta == 1 || meta == 9) {
                switch (side) {
                    case 0: {
                        return this.tinSideIcon[1];
                    }
                    case 1: {
                        return this.blockIcon;
                    }
                    case 2: {
                        return this.tinSideIcon[0];
                    }
                    case 3: {
                        return this.tinSideIcon[0];
                    }
                    case 4: {
                        return this.tinSideIcon[0];
                    }
                    case 5: {
                        return this.blockIcon;
                    }
                    default: {
                        return this.blockIcon;
                    }
                }
            }
            else if (meta == 2 || meta == 10) {
                switch (side) {
                    case 0: {
                        return this.tinSideIcon[1];
                    }
                    case 1: {
                        return this.blockIcon;
                    }
                    case 2: {
                        return this.blockIcon;
                    }
                    case 3: {
                        return this.tinSideIcon[0];
                    }
                    case 4: {
                        return this.tinSideIcon[0];
                    }
                    case 5: {
                        return this.tinSideIcon[0];
                    }
                    default: {
                        return this.blockIcon;
                    }
                }
            }
            else if (meta == 3 || meta == 11) {
                switch (side) {
                    case 0: {
                        return this.tinSideIcon[1];
                    }
                    case 1: {
                        return this.blockIcon;
                    }
                    case 2: {
                        return this.tinSideIcon[0];
                    }
                    case 3: {
                        return this.blockIcon;
                    }
                    case 4: {
                        return this.tinSideIcon[0];
                    }
                    case 5: {
                        return this.tinSideIcon[0];
                    }
                    default: {
                        return this.blockIcon;
                    }
                }
            }
            else if (meta == 4 || meta == 12) {
                switch (side) {
                    case 0: {
                        return this.blockIcon;
                    }
                    case 1: {
                        return this.tinSideIcon[1];
                    }
                    case 2: {
                        return this.tinSideIcon[0];
                    }
                    case 3: {
                        return this.tinSideIcon[0];
                    }
                    case 4: {
                        return this.blockIcon;
                    }
                    case 5: {
                        return this.tinSideIcon[0];
                    }
                    default: {
                        return this.blockIcon;
                    }
                }
            }
            else if (meta == 5 || meta == 13) {
                switch (side) {
                    case 0: {
                        return this.blockIcon;
                    }
                    case 1: {
                        return this.tinSideIcon[1];
                    }
                    case 2: {
                        return this.tinSideIcon[0];
                    }
                    case 3: {
                        return this.tinSideIcon[0];
                    }
                    case 4: {
                        return this.tinSideIcon[0];
                    }
                    case 5: {
                        return this.blockIcon;
                    }
                    default: {
                        return this.blockIcon;
                    }
                }
            }
            else if (meta == 6 || meta == 14) {
                switch (side) {
                    case 0: {
                        return this.blockIcon;
                    }
                    case 1: {
                        return this.tinSideIcon[1];
                    }
                    case 2: {
                        return this.blockIcon;
                    }
                    case 3: {
                        return this.tinSideIcon[0];
                    }
                    case 4: {
                        return this.tinSideIcon[0];
                    }
                    case 5: {
                        return this.tinSideIcon[0];
                    }
                    default: {
                        return this.blockIcon;
                    }
                }
            }
            else if (meta == 7 || meta == 15) {
                switch (side) {
                    case 0: {
                        return this.blockIcon;
                    }
                    case 1: {
                        return this.tinSideIcon[1];
                    }
                    case 2: {
                        return this.tinSideIcon[0];
                    }
                    case 3: {
                        return this.blockIcon;
                    }
                    case 4: {
                        return this.tinSideIcon[0];
                    }
                    case 5: {
                        return this.tinSideIcon[0];
                    }
                    default: {
                        return this.blockIcon;
                    }
                }
            }
        }
        return this.blockIcon;
    }
    
    public enum StairsCategoryGC
    {
        TIN1("stone"), 
        TIN2("stone"), 
        MOON_STONE("stone"), 
        MOON_BRICKS("stone"), 
        MARS_COBBLESTONE("stone"), 
        MARS_BRICKS("stone");
        
        private final List<String> values;
        private String type;
        
        private StairsCategoryGC(final String type) {
            this.type = type;
            this.values = Arrays.asList(type);
        }
    }
}
