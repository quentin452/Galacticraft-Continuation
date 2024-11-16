package micdoodle8.mods.galacticraft.core.dimension;

import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.core.world.gen.ChunkProviderOrbit;
import micdoodle8.mods.galacticraft.core.world.gen.WorldChunkManagerOrbit;

/***
 * Properties of a WorldProviderSpaceStation 1. Spinnable with Spin Thrusters (if you don't want spin, create your own
 * SpinManager subclass which does nothing) (note: your SkyProvider needs to rotate according to setSpinDeltaPerTick()
 * 2. Oregen from other mods is inhibited in this dimension 3. AstroMiner placement is inhibited in this dimension 4.
 * The player on arrival into this dimension (after rocket flight) will be in 1st person view
 *
 */
public abstract class WorldProviderSpaceStation extends WorldProviderSpace {

    private final SpinManager spinManager = new SpinManager(this);

    /**
     * Do not return null here, the calling code does not perform a null check!
     */
    public SpinManager getSpinManager() {
        return this.spinManager;
    }

    @Override
    public void setDimension(int var1) {
        super.setDimension(var1);
    }

    /**
     * Called only once from WorldProvider.registerWorld() so this provides a handy initialisation method
     */
    @Override
    public void registerWorldChunkManager() {
        super.registerWorldChunkManager();
        this.getSpinManager()
            .registerServerSide();
    }

    @Override
    public Class<? extends IChunkProvider> getChunkProviderClass() {
        return ChunkProviderOrbit.class;
    }

    @Override
    public Class<? extends WorldChunkManager> getWorldChunkManagerClass() {
        return WorldChunkManagerOrbit.class;
    }

    @Override
    public void updateWeather() {
        super.updateWeather();
        this.spinManager.updateSpin();
    }

    @SideOnly(Side.CLIENT)
    public abstract void setSpinDeltaPerTick(float angle);

    @SideOnly(Side.CLIENT)
    public abstract void createSkyProvider();
}
