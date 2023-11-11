package micdoodle8.mods.galacticraft.core.wrappers;

import net.minecraft.entity.player.*;
import net.minecraft.util.*;

public class PlayerGearData
{
    private final EntityPlayer player;
    private int mask;
    private int gear;
    private int leftTank;
    private int rightTank;
    private int[] thermalPadding;
    private ResourceLocation parachute;
    private int frequencyModule;
    
    public PlayerGearData(final EntityPlayer player) {
        this(player, -1, -1, -1, -1, -1, new int[] { -1, -1, -1, -1 });
    }
    
    public PlayerGearData(final EntityPlayer player, final int mask, final int gear, final int leftTank, final int rightTank, final int frequencyModule, final int[] thermalPadding) {
        this.player = player;
        this.mask = mask;
        this.gear = gear;
        this.leftTank = leftTank;
        this.rightTank = rightTank;
        this.frequencyModule = frequencyModule;
        this.thermalPadding = thermalPadding;
    }
    
    public int getMask() {
        return this.mask;
    }
    
    public void setMask(final int mask) {
        this.mask = mask;
    }
    
    public int getGear() {
        return this.gear;
    }
    
    public void setGear(final int gear) {
        this.gear = gear;
    }
    
    public int getLeftTank() {
        return this.leftTank;
    }
    
    public void setLeftTank(final int leftTank) {
        this.leftTank = leftTank;
    }
    
    public int getRightTank() {
        return this.rightTank;
    }
    
    public void setRightTank(final int rightTank) {
        this.rightTank = rightTank;
    }
    
    public EntityPlayer getPlayer() {
        return this.player;
    }
    
    public ResourceLocation getParachute() {
        return this.parachute;
    }
    
    public void setParachute(final ResourceLocation parachute) {
        this.parachute = parachute;
    }
    
    public int getFrequencyModule() {
        return this.frequencyModule;
    }
    
    public void setFrequencyModule(final int frequencyModule) {
        this.frequencyModule = frequencyModule;
    }
    
    public int getThermalPadding(final int slot) {
        if (slot >= 0 && slot < this.thermalPadding.length) {
            return this.thermalPadding[slot];
        }
        return -1;
    }
    
    public void setThermalPadding(final int slot, final int thermalPadding) {
        if (slot >= 0 && slot < this.thermalPadding.length) {
            this.thermalPadding[slot] = thermalPadding;
        }
    }
    
    @Override
    public int hashCode() {
        return this.player.getGameProfile().getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof PlayerGearData && ((PlayerGearData)obj).player.getGameProfile().getName().equals(this.player.getGameProfile().getName());
    }
}
