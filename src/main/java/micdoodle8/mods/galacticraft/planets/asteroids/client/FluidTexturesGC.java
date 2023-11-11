package micdoodle8.mods.galacticraft.planets.asteroids.client;

import cpw.mods.fml.relauncher.*;
import net.minecraftforge.common.*;
import net.minecraftforge.client.event.*;
import micdoodle8.mods.galacticraft.planets.asteroids.*;
import net.minecraftforge.fluids.*;
import cpw.mods.fml.common.eventhandler.*;

@SideOnly(Side.CLIENT)
public class FluidTexturesGC
{
    public static void init() {
        MinecraftForge.EVENT_BUS.register((Object)new FluidTexturesGC());
    }
    
    @SubscribeEvent
    public void onStitch(final TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 0) {
            AsteroidsModule.fluidMethaneGas.setIcons(event.map.registerIcon("galacticraftasteroids:fluids/MethaneGas"));
            AsteroidsModule.fluidAtmosphericGases.setIcons(event.map.registerIcon("galacticraftasteroids:fluids/AtmosphericGases"));
            AsteroidsModule.fluidLiquidMethane.setIcons(event.map.registerIcon("galacticraftasteroids:fluids/LiquidMethane"));
            AsteroidsModule.fluidLiquidOxygen.setIcons(event.map.registerIcon("galacticraftasteroids:fluids/LiquidOxygen"));
            AsteroidsModule.fluidOxygenGas.setIcons(event.map.registerIcon("galacticraftasteroids:fluids/OxygenGas"));
            AsteroidsModule.fluidLiquidNitrogen.setIcons(event.map.registerIcon("galacticraftasteroids:fluids/LiquidNitrogen"));
            AsteroidsModule.fluidLiquidArgon.setIcons(event.map.registerIcon("galacticraftasteroids:fluids/LiquidArgon"));
            AsteroidsModule.fluidNitrogenGas.setIcons(event.map.registerIcon("galacticraftasteroids:fluids/NitrogenGas"));
            FluidRegistry.getFluid("hydrogen").setIcons(event.map.registerIcon("galacticraftasteroids:fluids/HydrogenGas"));
            FluidRegistry.getFluid("helium").setIcons(event.map.registerIcon("galacticraftasteroids:fluids/HeliumGas"));
            FluidRegistry.getFluid("argon").setIcons(event.map.registerIcon("galacticraftasteroids:fluids/ArgonGas"));
            FluidRegistry.getFluid("carbondioxide").setIcons(event.map.registerIcon("galacticraftasteroids:fluids/CarbonDioxideGas"));
        }
    }
}
