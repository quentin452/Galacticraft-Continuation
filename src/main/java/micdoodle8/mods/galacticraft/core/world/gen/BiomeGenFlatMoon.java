package micdoodle8.mods.galacticraft.core.world.gen;

import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraftforge.common.*;

public class BiomeGenFlatMoon extends BiomeGenBaseMoon
{
    public BiomeGenFlatMoon(final int par1) {
        super(par1);
        this.setBiomeName("moonFlat");
        this.setColor(11111111);
        this.setHeight(new BiomeGenBase.Height(1.5f, 0.4f));
        if (!ConfigManagerCore.disableBiomeTypeRegistrations) {
            BiomeDictionary.registerBiomeType((BiomeGenBase)this, new BiomeDictionary.Type[] { BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.DEAD });
        }
    }
}
