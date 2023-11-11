package micdoodle8.mods.galacticraft.planets.mars.world.gen;

import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraftforge.common.*;

public class BiomeGenFlatMars extends BiomeGenBaseMars
{
    public BiomeGenFlatMars(final int par1) {
        super(par1);
        this.setBiomeName("marsFlat");
        this.setColor(16711680);
        this.setHeight(new BiomeGenBase.Height(2.5f, 0.4f));
        if (!ConfigManagerCore.disableBiomeTypeRegistrations) {
            BiomeDictionary.registerBiomeType((BiomeGenBase)this, new BiomeDictionary.Type[] { BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.DEAD, BiomeDictionary.Type.SANDY });
        }
    }
}
