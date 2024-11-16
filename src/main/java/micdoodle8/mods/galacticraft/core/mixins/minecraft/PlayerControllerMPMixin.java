package micdoodle8.mods.galacticraft.core.mixins.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import micdoodle8.mods.galacticraft.core.entities.player.GCEntityClientPlayerMP;

@Mixin(PlayerControllerMP.class)
public abstract class PlayerControllerMPMixin {

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    @Final
    private NetHandlerPlayClient netClientHandler;

    /**
     * @author micdoodle8
     * @author SinTh0r4s
     * @author glowredman
     * @reason enable custom Galacticraft dimension behaviour
     */
    @Overwrite
    public EntityClientPlayerMP func_147493_a(World world, StatFileWriter stats) {
        return new GCEntityClientPlayerMP(this.mc, world, this.mc.getSession(), this.netClientHandler, stats);
    }
}
