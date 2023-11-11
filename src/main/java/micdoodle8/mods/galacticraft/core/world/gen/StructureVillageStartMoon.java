package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.world.*;
import net.minecraft.world.gen.structure.*;
import java.util.*;

public class StructureVillageStartMoon extends StructureStart
{
    public StructureVillageStartMoon() {
    }

    public StructureVillageStartMoon(final World par1World, final Random par2Random, final int par3, final int par4, final int par5) {
        super(par3, par4);
        final ArrayList<StructureVillagePieceWeightMoon> var6 = StructureVillagePiecesMoon.getStructureVillageWeightedPieceList(par2Random, par5);
        final StructureComponentVillageStartPiece var7 = new StructureComponentVillageStartPiece(par1World.getWorldChunkManager(), 0, par2Random, (par3 << 4) + 2, (par4 << 4) + 2, (ArrayList)var6, par5);
        this.components.add(var7);
        var7.buildComponent(var7, this.components, par2Random);
        final ArrayList<Object> var8 = var7.field_74930_j;
        final ArrayList<Object> var9 = var7.field_74932_i;
        while (!var8.isEmpty() || !var9.isEmpty()) {
            if (var8.isEmpty()) {
                final int var10 = par2Random.nextInt(var9.size());
                final StructureComponent var11 = (StructureComponent) var9.remove(var10);
                var11.buildComponent(var7, this.components, par2Random);
            }
            else {
                final int var10 = par2Random.nextInt(var8.size());
                final StructureComponent var11 = (StructureComponent) var8.remove(var10);
                var11.buildComponent(var7, this.components, par2Random);
            }
        }
        this.updateBoundingBox();
        /*  int var10 = 0;
        for (final StructureComponent var13 : this.components) {
            if (!(var13 instanceof StructureComponentVillageRoadPiece)) {
                ++var10;
            }
        }

         */
    }

    public boolean isSizeableStructure() {
        return true;
    }
}
