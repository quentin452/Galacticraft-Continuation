package micdoodle8.mods.galacticraft.planets.asteroids.client.sounds;

import net.minecraft.client.audio.*;
import net.minecraft.client.entity.*;
import micdoodle8.mods.galacticraft.planets.asteroids.entities.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;

public class SoundUpdaterMiner extends MovingSound
{
    private final EntityPlayerSP thePlayer;
    private final EntityAstroMiner theRocket;
    private boolean soundStopped;
    private float targetVolume;
    private float targetPitch;
    
    public SoundUpdaterMiner(final EntityPlayerSP par1EntityPlayerSP, final EntityAstroMiner par2Entity) {
        super(new ResourceLocation(GalacticraftCore.TEXTURE_PREFIX + "entity.astrominer"));
        this.theRocket = par2Entity;
        this.thePlayer = par1EntityPlayerSP;
        this.volume = 1.0E-5f;
        this.targetVolume = 0.6f;
        this.targetPitch = 1.0f;
        this.field_147663_c = 1.0f;
        this.repeat = true;
        this.field_147665_h = 0;
        this.updateSoundLocation(par2Entity);
    }
    
    public void update() {
        if (!this.theRocket.isDead) {
            if (this.theRocket.AIstate == 1 || this.theRocket.AIstate == 5) {
                this.targetVolume = 0.6f;
                this.targetPitch = 0.1f;
            }
            else {
                this.targetVolume = 1.0f;
                this.targetPitch = 1.0f;
            }
            if (this.volume < this.targetVolume) {
                this.volume += 0.1f;
                if (this.volume > this.targetVolume) {
                    this.volume = this.targetVolume;
                }
            }
            else if (this.volume > this.targetVolume) {
                this.volume -= 0.1f;
                if (this.volume < this.targetVolume) {
                    this.volume = this.targetVolume;
                }
            }
            if (this.field_147663_c < this.targetPitch) {
                this.field_147663_c += 0.05f;
                if (this.field_147663_c > this.targetPitch) {
                    this.field_147663_c = this.targetPitch;
                }
            }
            else if (this.field_147663_c > this.targetPitch) {
                this.field_147663_c -= 0.05f;
                if (this.field_147663_c < this.targetPitch) {
                    this.field_147663_c = this.targetPitch;
                }
            }
            this.updateSoundLocation(this.theRocket);
        }
        else {
            this.donePlaying = true;
        }
    }
    
    public void stopRocketSound() {
        this.donePlaying = true;
        this.soundStopped = true;
    }
    
    public void updateSoundLocation(final Entity e) {
        this.xPosF = (float)e.posX;
        this.yPosF = (float)e.posY;
        this.zPosF = (float)e.posZ;
    }
}
