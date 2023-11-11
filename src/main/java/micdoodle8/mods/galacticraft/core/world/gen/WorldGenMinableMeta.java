package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.world.gen.feature.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import java.util.*;
import net.minecraft.util.*;

public class WorldGenMinableMeta extends WorldGenMinable
{
    private final Block minableBlockId;
    private final int numberOfBlocks;
    private final int metadata;
    private boolean usingMetadata;
    private final Block fillerID;
    private final int fillerMetadata;
    
    public WorldGenMinableMeta(final Block par1, final int par2, final int par3, final boolean par4, final Block id, final int meta) {
        super(par1, par3, par2, id);
        this.usingMetadata = false;
        this.minableBlockId = par1;
        this.numberOfBlocks = par2;
        this.metadata = par3;
        this.usingMetadata = par4;
        this.fillerID = id;
        this.fillerMetadata = meta;
    }
    
    public boolean generate(final World par1World, final Random par2Random, final int px, final int py, final int pz) {
        final float f = par2Random.nextFloat() * 3.1415927f;
        final float sinf = MathHelper.sin(f) * this.numberOfBlocks / 8.0f;
        final float cosf = MathHelper.cos(f) * this.numberOfBlocks / 8.0f;
        final float x1 = px + 8 + sinf;
        final float x2 = -2.0f * sinf;
        final float z1 = pz + 8 + cosf;
        final float z2 = -2.0f * cosf;
        final float y1 = (float)(py + par2Random.nextInt(3) - 2);
        final float y2 = py + par2Random.nextInt(3) - 2 - y1;
        for (int l = 0; l <= this.numberOfBlocks; ++l) {
            final float progress = l / (float)this.numberOfBlocks;
            final float cx = x1 + x2 * progress;
            final float cy = y1 + y2 * progress;
            final float cz = z1 + z2 * progress;
            final float size = ((MathHelper.sin(3.1415927f * progress) + 1.0f) * par2Random.nextFloat() * this.numberOfBlocks / 16.0f + 1.0f) / 2.0f;
            final int xMin = MathHelper.floor_float(cx - size);
            final int yMin = MathHelper.floor_float(cy - size);
            final int zMin = MathHelper.floor_float(cz - size);
            final int xMax = MathHelper.floor_float(cx + size);
            final int yMax = MathHelper.floor_float(cy + size);
            final int zMax = MathHelper.floor_float(cz + size);
            for (int ix = xMin; ix <= xMax; ++ix) {
                float dx = (ix + 0.5f - cx) / size;
                dx *= dx;
                if (dx < 1.0f) {
                    for (int iy = yMin; iy <= yMax; ++iy) {
                        float dy = (iy + 0.5f - cy) / size;
                        dy *= dy;
                        if (dx + dy < 1.0f) {
                            for (int iz = zMin; iz <= zMax; ++iz) {
                                float dz = (iz + 0.5f - cz) / size;
                                dz *= dz;
                                if (dx + dy + dz < 1.0f && par1World.getBlock(ix, iy, iz) == this.fillerID && par1World.getBlockMetadata(ix, iy, iz) == this.fillerMetadata) {
                                    if (!this.usingMetadata) {
                                        par1World.setBlock(ix, iy, iz, this.minableBlockId, 0, 3);
                                    }
                                    else {
                                        par1World.setBlock(ix, iy, iz, this.minableBlockId, this.metadata, 3);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
