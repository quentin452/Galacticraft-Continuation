package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.world.biome.*;
import net.minecraft.world.gen.structure.*;
import cpw.mods.fml.common.*;
import java.util.*;

public class MapGenVillageMoon extends MapGenStructure
{
    public static List<BiomeGenBase> villageSpawnBiomes;
    private final int terrainType;
    private static boolean initialized;
    
    public static void initiateStructures() throws Throwable {
        if (!MapGenVillageMoon.initialized) {
            MapGenStructureIO.registerStructure((Class)StructureVillageStartMoon.class, "MoonVillage");
            MapGenStructureIO.func_143031_a((Class)StructureComponentVillageField.class, "MoonField1");
            MapGenStructureIO.func_143031_a((Class)StructureComponentVillageField2.class, "MoonField2");
            MapGenStructureIO.func_143031_a((Class)StructureComponentVillageHouse.class, "MoonHouse");
            MapGenStructureIO.func_143031_a((Class)StructureComponentVillageRoadPiece.class, "MoonRoadPiece");
            MapGenStructureIO.func_143031_a((Class)StructureComponentVillagePathGen.class, "MoonPath");
            MapGenStructureIO.func_143031_a((Class)StructureComponentVillageTorch.class, "MoonTorch");
            MapGenStructureIO.func_143031_a((Class)StructureComponentVillageStartPiece.class, "MoonWell");
            MapGenStructureIO.func_143031_a((Class)StructureComponentVillageWoodHut.class, "MoonWoodHut");
        }
        MapGenVillageMoon.initialized = true;
    }
    
    public MapGenVillageMoon() {
        this.terrainType = 0;
    }
    
    protected boolean canSpawnStructureAtCoords(int i, int j) {
        final byte numChunks = 32;
        final byte offsetChunks = 8;
        final int oldi = i;
        final int oldj = j;
        if (i < 0) {
            i -= 31;
        }
        if (j < 0) {
            j -= 31;
        }
        int randX = i / 32;
        int randZ = j / 32;
        final Random var7 = this.worldObj.setRandomSeed(i, j, 10387312);
        randX *= 32;
        randZ *= 32;
        randX += var7.nextInt(24);
        randZ += var7.nextInt(24);
        return oldi == randX && oldj == randZ;
    }
    
    protected StructureStart getStructureStart(final int par1, final int par2) {
        FMLLog.info("Generating Moon Village at x" + par1 * 16 + " z" + par2 * 16, new Object[0]);
        return new StructureVillageStartMoon(this.worldObj, this.rand, par1, par2, this.terrainType);
    }
    
    public String func_143025_a() {
        return "MoonVillage";
    }
    
    static {
        MapGenVillageMoon.villageSpawnBiomes = Arrays.asList(BiomeGenBaseMoon.moonFlat);
        try {
            initiateStructures();
        }
        catch (Throwable t) {}
    }
}
