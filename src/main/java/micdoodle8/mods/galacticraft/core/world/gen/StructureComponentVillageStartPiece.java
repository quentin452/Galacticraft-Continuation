package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.world.biome.*;
import java.util.*;
import net.minecraft.nbt.*;

public class StructureComponentVillageStartPiece extends StructureComponentVillageWell
{
    public WorldChunkManager worldChunkMngr;
    public int terrainType;
    public StructureVillagePieceWeightMoon structVillagePieceWeight;
    public ArrayList<StructureVillagePieceWeightMoon> structureVillageWeightedPieceList;
    public ArrayList<Object> field_74932_i;
    public ArrayList<Object> field_74930_j;
    
    public StructureComponentVillageStartPiece() {
        this.field_74932_i = new ArrayList<Object>();
        this.field_74930_j = new ArrayList<Object>();
    }
    
    public StructureComponentVillageStartPiece(final WorldChunkManager par1WorldChunkManager, final int par2, final Random par3Random, final int par4, final int par5, final ArrayList<StructureVillagePieceWeightMoon> par6ArrayList, final int par7) {
        super(null, 0, par3Random, par4, par5);
        this.field_74932_i = new ArrayList<Object>();
        this.field_74930_j = new ArrayList<Object>();
        this.worldChunkMngr = par1WorldChunkManager;
        this.structureVillageWeightedPieceList = par6ArrayList;
        this.terrainType = par7;
        this.startPiece = this;
    }
    
    @Override
    protected void func_143012_a(final NBTTagCompound nbt) {
        super.func_143012_a(nbt);
        nbt.setInteger("TerrainType", this.terrainType);
    }
    
    @Override
    protected void func_143011_b(final NBTTagCompound nbt) {
        super.func_143011_b(nbt);
        this.terrainType = nbt.getInteger("TerrainType");
    }
    
    public WorldChunkManager getWorldChunkManager() {
        return this.worldChunkMngr;
    }
}
