package micdoodle8.mods.galacticraft.core.dimension;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.biome.*;
import micdoodle8.mods.galacticraft.core.world.gen.*;
import cpw.mods.fml.relauncher.*;

public abstract class WorldProviderSpaceStation extends WorldProviderSpace
{
    private SpinManager spinManager;
    
    public WorldProviderSpaceStation() {
        this.spinManager = new SpinManager(this);
    }
    
    public SpinManager getSpinManager() {
        return this.spinManager;
    }
    
    public void setDimension(final int var1) {
        super.setDimension(var1);
    }
    
    public void registerWorldChunkManager() {
        super.registerWorldChunkManager();
        this.getSpinManager().registerServerSide();
    }
    
    public Class<? extends IChunkProvider> getChunkProviderClass() {
        return (Class<? extends IChunkProvider>)ChunkProviderOrbit.class;
    }
    
    public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
        return (Class<? extends WorldChunkManager>)WorldChunkManagerOrbit.class;
    }
    
    public void updateWeather() {
        super.updateWeather();
        this.spinManager.updateSpin();
    }
    
    @SideOnly(Side.CLIENT)
    public abstract void setSpinDeltaPerTick(final float p0);
    
    @SideOnly(Side.CLIENT)
    public abstract void createSkyProvider();
}
