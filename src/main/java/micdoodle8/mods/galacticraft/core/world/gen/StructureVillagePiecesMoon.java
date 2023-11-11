package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.util.*;
import java.util.*;
import net.minecraft.world.gen.structure.*;

public class StructureVillagePiecesMoon
{
    public static ArrayList<StructureVillagePieceWeightMoon> getStructureVillageWeightedPieceList(final Random par0Random, final int par1) {
        final ArrayList<StructureVillagePieceWeightMoon> var2 = new ArrayList<StructureVillagePieceWeightMoon>();
        var2.add(new StructureVillagePieceWeightMoon((Class<? extends StructureComponentVillage>)StructureComponentVillageWoodHut.class, 5, MathHelper.getRandomIntegerInRange(par0Random, 2 + par1, 5 + par1 * 3)));
        var2.add(new StructureVillagePieceWeightMoon((Class<? extends StructureComponentVillage>)StructureComponentVillageField.class, 5, MathHelper.getRandomIntegerInRange(par0Random, 3 + par1, 5 + par1)));
        var2.add(new StructureVillagePieceWeightMoon((Class<? extends StructureComponentVillage>)StructureComponentVillageField2.class, 5, MathHelper.getRandomIntegerInRange(par0Random, 3 + par1, 5 + par1)));
        var2.add(new StructureVillagePieceWeightMoon((Class<? extends StructureComponentVillage>)StructureComponentVillageHouse.class, 5, MathHelper.getRandomIntegerInRange(par0Random, 3 + par1, 4 + par1 * 2)));
        final Iterator<StructureVillagePieceWeightMoon> var3 = var2.iterator();
        while (var3.hasNext()) {
            if (var3.next().villagePiecesLimit == 0) {
                var3.remove();
            }
        }
        return var2;
    }
    
    private static int func_75079_a(final List<StructureVillagePieceWeightMoon> par0List) {
        boolean var1 = false;
        int var2 = 0;
        for (final StructureVillagePieceWeightMoon var4 : par0List) {
            if (var4.villagePiecesLimit > 0 && var4.villagePiecesSpawned < var4.villagePiecesLimit) {
                var1 = true;
            }
            var2 += var4.villagePieceWeight;
        }
        return var1 ? var2 : -1;
    }
    
    private static StructureComponentVillage func_75083_a(final StructureComponentVillageStartPiece par0ComponentVillageStartPiece, final StructureVillagePieceWeightMoon par1StructureVillagePieceWeight, final List<StructureComponent> par2List, final Random par3Random, final int par4, final int par5, final int par6, final int par7, final int par8) {
        final Class<?> var9 = par1StructureVillagePieceWeight.villagePieceClass;
        Object var10 = null;
        if (var9 == StructureComponentVillageWoodHut.class) {
            var10 = StructureComponentVillageWoodHut.func_74908_a(par0ComponentVillageStartPiece, (List)par2List, par3Random, par4, par5, par6, par7, par8);
        }
        else if (var9 == StructureComponentVillageField.class) {
            var10 = StructureComponentVillageField.func_74900_a(par0ComponentVillageStartPiece, (List)par2List, par3Random, par4, par5, par6, par7, par8);
        }
        else if (var9 == StructureComponentVillageField2.class) {
            var10 = StructureComponentVillageField2.func_74900_a(par0ComponentVillageStartPiece, (List)par2List, par3Random, par4, par5, par6, par7, par8);
        }
        else if (var9 == StructureComponentVillageHouse.class) {
            var10 = StructureComponentVillageHouse.func_74921_a(par0ComponentVillageStartPiece, (List)par2List, par3Random, par4, par5, par6, par7, par8);
        }
        return (StructureComponentVillage)var10;
    }
    
    private static StructureComponentVillage getNextVillageComponent(final StructureComponentVillageStartPiece par0ComponentVillageStartPiece, final List<StructureComponent> par1List, final Random par2Random, final int par3, final int par4, final int par5, final int par6, final int par7) {
        final int var8 = func_75079_a(par0ComponentVillageStartPiece.structureVillageWeightedPieceList);
        if (var8 <= 0) {
            return null;
        }
        int var9 = 0;
        while (var9 < 5) {
            ++var9;
            int var10 = par2Random.nextInt(var8);
            for (final StructureVillagePieceWeightMoon var12 : par0ComponentVillageStartPiece.structureVillageWeightedPieceList) {
                var10 -= var12.villagePieceWeight;
                if (var10 < 0) {
                    if (!var12.canSpawnMoreVillagePiecesOfType(par7)) {
                        break;
                    }
                    if (var12 == par0ComponentVillageStartPiece.structVillagePieceWeight && par0ComponentVillageStartPiece.structureVillageWeightedPieceList.size() > 1) {
                        break;
                    }
                    final StructureComponentVillage var13 = func_75083_a(par0ComponentVillageStartPiece, var12, par1List, par2Random, par3, par4, par5, par6, par7);
                    if (var13 != null) {
                        final StructureVillagePieceWeightMoon structureVillagePieceWeightMoon = var12;
                        ++structureVillagePieceWeightMoon.villagePiecesSpawned;
                        par0ComponentVillageStartPiece.structVillagePieceWeight = var12;
                        if (!var12.canSpawnMoreVillagePieces()) {
                            par0ComponentVillageStartPiece.structureVillageWeightedPieceList.remove(var12);
                        }
                        return var13;
                    }
                    continue;
                }
            }
        }
        final StructureBoundingBox var14 = StructureComponentVillageTorch.func_74904_a(par0ComponentVillageStartPiece, (List)par1List, par2Random, par3, par4, par5, par6);
        if (var14 != null) {
            return (StructureComponentVillage)new StructureComponentVillageTorch(par0ComponentVillageStartPiece, par7, par2Random, var14, par6);
        }
        return null;
    }
    
    private static StructureComponent getNextVillageStructureComponent(final StructureComponentVillageStartPiece par0ComponentVillageStartPiece, final List<StructureComponent> par1List, final Random par2Random, final int par3, final int par4, final int par5, final int par6, final int par7) {
        if (par7 > 50) {
            return null;
        }
        if (Math.abs(par3 - par0ComponentVillageStartPiece.getBoundingBox().minX) > 112 || Math.abs(par5 - par0ComponentVillageStartPiece.getBoundingBox().minZ) > 112) {
            return null;
        }
        final StructureComponentVillage var8 = getNextVillageComponent(par0ComponentVillageStartPiece, par1List, par2Random, par3, par4, par5, par6, par7 + 1);
        if (var8 != null) {
            par1List.add((StructureComponent)var8);
            par0ComponentVillageStartPiece.field_74932_i.add(var8);
            return (StructureComponent)var8;
        }
        return null;
    }
    
    private static StructureComponent getNextComponentVillagePath(final StructureComponentVillageStartPiece par0ComponentVillageStartPiece, final List<StructureComponent> par1List, final Random par2Random, final int par3, final int par4, final int par5, final int par6, final int par7) {
        if (par7 > 3 + par0ComponentVillageStartPiece.terrainType) {
            return null;
        }
        if (Math.abs(par3 - par0ComponentVillageStartPiece.getBoundingBox().minX) > 112 || Math.abs(par5 - par0ComponentVillageStartPiece.getBoundingBox().minZ) > 112) {
            return null;
        }
        final StructureBoundingBox var8 = StructureComponentVillagePathGen.func_74933_a(par0ComponentVillageStartPiece, (List)par1List, par2Random, par3, par4, par5, par6);
        if (var8 != null && var8.minY > 10) {
            final StructureComponentVillagePathGen var9 = new StructureComponentVillagePathGen(par0ComponentVillageStartPiece, par7, par2Random, var8, par6);
            par1List.add((StructureComponent)var9);
            par0ComponentVillageStartPiece.field_74930_j.add(var9);
            return (StructureComponent)var9;
        }
        return null;
    }
    
    static StructureComponent getNextStructureComponent(final StructureComponentVillageStartPiece par0ComponentVillageStartPiece, final List<StructureComponent> par1List, final Random par2Random, final int par3, final int par4, final int par5, final int par6, final int par7) {
        return getNextVillageStructureComponent(par0ComponentVillageStartPiece, par1List, par2Random, par3, par4, par5, par6, par7);
    }
    
    static StructureComponent getNextStructureComponentVillagePath(final StructureComponentVillageStartPiece par0ComponentVillageStartPiece, final List<StructureComponent> par1List, final Random par2Random, final int par3, final int par4, final int par5, final int par6, final int par7) {
        return getNextComponentVillagePath(par0ComponentVillageStartPiece, par1List, par2Random, par3, par4, par5, par6, par7);
    }
}
