package micdoodle8.mods.galacticraft.core.client.sounds;

import net.minecraft.client.entity.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.util.*;
import net.minecraft.client.audio.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;

public class SoundUpdaterRocket extends MovingSound
{
    private final EntityPlayerSP thePlayer;
    private final EntityAutoRocket theRocket;
    private boolean soundStopped;
    private boolean ignition;
    
    public SoundUpdaterRocket(final EntityPlayerSP par1EntityPlayerSP, final EntityAutoRocket par2Entity) {
        super(new ResourceLocation(GalacticraftCore.TEXTURE_PREFIX + "shuttle.shuttle"));
        this.ignition = false;
        this.theRocket = par2Entity;
        this.thePlayer = par1EntityPlayerSP;
        this.field_147666_i = ISound.AttenuationType.NONE;
        this.volume = 1.0E-5f;
        this.field_147663_c = 0.0f;
        this.repeat = true;
        this.field_147665_h = 0;
        this.updateSoundLocation((Entity)par2Entity);
    }
    
    public void update() {
        if (!this.theRocket.isDead) {
            if (this.theRocket.launchPhase == EntitySpaceshipBase.EnumLaunchPhase.IGNITED.ordinal()) {
                if (!this.ignition) {
                    this.field_147663_c = 0.0f;
                    this.ignition = true;
                }
                if (this.theRocket.timeUntilLaunch < this.theRocket.getPreLaunchWait()) {
                    if (this.field_147663_c < 1.0f) {
                        this.field_147663_c += 0.0025f;
                    }
                    if (this.field_147663_c > 1.0f) {
                        this.field_147663_c = 1.0f;
                    }
                }
            }
            else {
                this.field_147663_c = 1.0f;
            }
            if (this.theRocket.launchPhase == EntitySpaceshipBase.EnumLaunchPhase.IGNITED.ordinal() || this.theRocket.getLaunched()) {
                if (this.theRocket.posY > 1000.0) {
                    this.volume = 0.0f;
                    if (!this.theRocket.landing) {
                        this.donePlaying = true;
                    }
                }
                else if (this.theRocket.posY > 200.0) {
                    this.volume = (1200.0f - (float)this.theRocket.posY) * 0.001f;
                }
                else {
                    this.volume = 1.0f;
                }
            }
            this.updateSoundLocation((Entity)this.theRocket);
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
