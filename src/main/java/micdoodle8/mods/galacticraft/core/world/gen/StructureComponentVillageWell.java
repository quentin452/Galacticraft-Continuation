package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.nbt.*;
import net.minecraft.world.gen.structure.*;
import java.util.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import net.minecraft.init.*;
import net.minecraft.block.*;

public class StructureComponentVillageWell extends StructureComponentVillage
{
    private int averageGroundLevel;
    
    public StructureComponentVillageWell() {
        this.averageGroundLevel = -1;
    }
    
    public StructureComponentVillageWell(final StructureComponentVillageStartPiece par1ComponentVillageStartPiece, final int par2, final Random par3Random, final int par4, final int par5) {
        super(par1ComponentVillageStartPiece, par2);
        this.averageGroundLevel = -1;
        switch (this.coordBaseMode = par3Random.nextInt(4)) {
            case 0:
            case 2: {
                this.boundingBox = new StructureBoundingBox(par4, 64, par5, par4 + 6 - 1, 78, par5 + 6 - 1);
                break;
            }
            default: {
                this.boundingBox = new StructureBoundingBox(par4, 64, par5, par4 + 6 - 1, 78, par5 + 6 - 1);
                break;
            }
        }
    }
    
    protected void func_143012_a(final NBTTagCompound nbt) {
        super.func_143012_a(nbt);
        nbt.setInteger("AvgGroundLevel", this.averageGroundLevel);
    }
    
    protected void func_143011_b(final NBTTagCompound nbt) {
        super.func_143011_b(nbt);
        this.averageGroundLevel = nbt.getInteger("AvgGroundLevel");
    }
    
    public void buildComponent(final StructureComponent par1StructureComponent, final List par2List, final Random par3Random) {
        StructureVillagePiecesMoon.getNextStructureComponentVillagePath((StructureComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.minX - 1, this.boundingBox.maxY - 4, this.boundingBox.minZ + 1, 1, this.getComponentType());
        StructureVillagePiecesMoon.getNextStructureComponentVillagePath((StructureComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.maxX + 1, this.boundingBox.maxY - 4, this.boundingBox.minZ + 1, 3, this.getComponentType());
        StructureVillagePiecesMoon.getNextStructureComponentVillagePath((StructureComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.minX + 1, this.boundingBox.maxY - 4, this.boundingBox.minZ - 1, 2, this.getComponentType());
        StructureVillagePiecesMoon.getNextStructureComponentVillagePath((StructureComponentVillageStartPiece)par1StructureComponent, par2List, par3Random, this.boundingBox.minX + 1, this.boundingBox.maxY - 4, this.boundingBox.maxZ + 1, 0, this.getComponentType());
    }
    
    public boolean addComponentParts(final World par1World, final Random par2Random, final StructureBoundingBox par3StructureBoundingBox) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);
            if (this.averageGroundLevel < 0) {
                return true;
            }
            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 3, 0);
        }
        this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 1, 0, 1, 4, 12, 4, GCBlocks.basicBlock, 4, (Block)Blocks.flowing_water, 0, false);
        this.placeBlockAtCurrentPosition(par1World, Blocks.air, 0, 2, 12, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.air, 0, 3, 12, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.air, 0, 2, 12, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.air, 0, 3, 12, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.fence, 0, 1, 13, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.fence, 0, 1, 14, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.fence, 0, 4, 13, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.fence, 0, 4, 14, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.fence, 0, 1, 13, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.fence, 0, 1, 14, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.fence, 0, 4, 13, 4, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.fence, 0, 4, 14, 4, par3StructureBoundingBox);
        this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 1, 15, 1, 4, 15, 4, GCBlocks.basicBlock, 4, GCBlocks.basicBlock, 4, false);
        for (int var4 = 0; var4 <= 5; ++var4) {
            for (int var5 = 0; var5 <= 5; ++var5) {
                if (var5 == 0 || var5 == 5 || var4 == 0 || var4 == 5) {
                    this.placeBlockAtCurrentPosition(par1World, Blocks.planks, 1, var5, 11, var4, par3StructureBoundingBox);
                    this.clearCurrentPositionBlocksUpwards(par1World, var5, 12, var4, par3StructureBoundingBox);
                }
            }
        }
        return true;
    }
    
    protected void fillWithBlocksAndMetadata(final World par1World, final StructureBoundingBox par2StructureBoundingBox, final int par3, final int par4, final int par5, final int par6, final int par7, final int par8, final Block par9, final Block par10, final boolean par11) {
        final Block var12 = this.getBiomeSpecificBlock(par9, 0);
        final int var13 = this.getBiomeSpecificBlockMetadata(par9, 0);
        final Block var14 = this.getBiomeSpecificBlock(par10, 0);
        final int var15 = this.getBiomeSpecificBlockMetadata(par10, 0);
        super.fillWithMetadataBlocks(par1World, par2StructureBoundingBox, par3, par4, par5, par6, par7, par8, var12, var13, var14, var15, par11);
    }
}
