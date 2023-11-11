package micdoodle8.mods.galacticraft.core.entities.player;

import net.minecraftforge.common.*;
import java.lang.ref.*;
import net.minecraft.client.entity.*;
import net.minecraft.util.*;
import java.util.*;
import micdoodle8.mods.galacticraft.api.recipe.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;

public class GCPlayerStatsClient implements IExtendedEntityProperties
{
    public static final String GC_PLAYER_PROP = "GCPlayerStatsClient";
    public WeakReference<EntityPlayerSP> player;
    public boolean usingParachute;
    public boolean lastUsingParachute;
    public boolean usingAdvancedGoggles;
    public int thermalLevel;
    public boolean thermalLevelNormalising;
    public int thirdPersonView;
    public long tick;
    public boolean oxygenSetupValid;
    AxisAlignedBB boundingBoxBefore;
    public boolean lastOnGround;
    public int pjumpticks;
    public boolean pWasOnGround;
    public double distanceSinceLastStep;
    public int lastStep;
    public boolean inFreefall;
    public boolean inFreefallLast;
    public boolean inFreefallFirstCheck;
    public double downMotionLast;
    public boolean lastRidingCameraZoomEntity;
    public int landingTicks;
    public static final int MAX_LANDINGTICKS = 15;
    public float[] landingYOffset;
    public EnumGravity gdir;
    public float gravityTurnRate;
    public float gravityTurnRatePrev;
    public float gravityTurnVecX;
    public float gravityTurnVecY;
    public float gravityTurnVecZ;
    public float gravityTurnYaw;
    public int spaceRaceInviteTeamID;
    public boolean lastZoomed;
    public int buildFlags;
    public ArrayList<ISchematicPage> unlockedSchematics;
    public FreefallHandler freefallHandler;
    
    public GCPlayerStatsClient(final EntityPlayerSP player) {
        this.thirdPersonView = 0;
        this.oxygenSetupValid = true;
        this.pjumpticks = 0;
        this.landingYOffset = new float[16];
        this.gdir = EnumGravity.down;
        this.buildFlags = -1;
        this.unlockedSchematics = new ArrayList<ISchematicPage>();
        this.freefallHandler = new FreefallHandler(this);
        this.player = new WeakReference<EntityPlayerSP>(player);
    }
    
    public void setGravity(final EnumGravity newGravity) {
        if (this.gdir == newGravity) {
            return;
        }
        final float n = 0.0f;
        this.gravityTurnRate = n;
        this.gravityTurnRatePrev = n;
        final float turnSpeed = 0.05f;
        this.gravityTurnVecX = 0.0f;
        this.gravityTurnVecY = 0.0f;
        this.gravityTurnVecZ = 0.0f;
        this.gravityTurnYaw = 0.0f;
        Label_0688: {
            switch (this.gdir.getIntValue()) {
                case 1: {
                    switch (newGravity.getIntValue()) {
                        case 2: {
                            this.gravityTurnVecX = -2.0f;
                            break;
                        }
                        case 3: {
                            this.gravityTurnVecY = -1.0f;
                            this.gravityTurnYaw = -90.0f;
                            break;
                        }
                        case 4: {
                            this.gravityTurnVecY = 1.0f;
                            this.gravityTurnYaw = 90.0f;
                            break;
                        }
                        case 5: {
                            this.gravityTurnVecX = 1.0f;
                            break;
                        }
                        case 6: {
                            this.gravityTurnVecX = -1.0f;
                            break;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (newGravity.getIntValue()) {
                        case 1: {
                            this.gravityTurnVecX = -2.0f;
                        }
                        case 3: {
                            this.gravityTurnVecY = 1.0f;
                            this.gravityTurnYaw = 90.0f;
                            break;
                        }
                        case 4: {
                            this.gravityTurnVecY = -1.0f;
                            this.gravityTurnYaw = -90.0f;
                            break;
                        }
                        case 5: {
                            this.gravityTurnVecX = -1.0f;
                            break;
                        }
                        case 6: {
                            this.gravityTurnVecX = 1.0f;
                            break;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (newGravity.getIntValue()) {
                        case 1: {
                            this.gravityTurnVecY = 1.0f;
                            this.gravityTurnYaw = 90.0f;
                            break;
                        }
                        case 2: {
                            this.gravityTurnVecY = -1.0f;
                            this.gravityTurnYaw = -90.0f;
                        }
                        case 4: {
                            this.gravityTurnVecZ = -2.0f;
                            break;
                        }
                        case 5: {
                            this.gravityTurnVecZ = -1.0f;
                            this.gravityTurnYaw = -180.0f;
                            break;
                        }
                        case 6: {
                            this.gravityTurnVecZ = 1.0f;
                            break;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (newGravity.getIntValue()) {
                        case 1: {
                            this.gravityTurnVecY = -1.0f;
                            this.gravityTurnYaw = -90.0f;
                            break;
                        }
                        case 2: {
                            this.gravityTurnVecY = 1.0f;
                            this.gravityTurnYaw = 90.0f;
                            break;
                        }
                        case 3: {
                            this.gravityTurnVecZ = -2.0f;
                        }
                        case 5: {
                            this.gravityTurnVecZ = 1.0f;
                            this.gravityTurnYaw = -180.0f;
                            break;
                        }
                        case 6: {
                            this.gravityTurnVecZ = -1.0f;
                            break;
                        }
                    }
                    break;
                }
                case 5: {
                    switch (newGravity.getIntValue()) {
                        case 1: {
                            this.gravityTurnVecX = -1.0f;
                            break;
                        }
                        case 2: {
                            this.gravityTurnVecX = 1.0f;
                            break;
                        }
                        case 3: {
                            this.gravityTurnVecZ = 1.0f;
                            this.gravityTurnYaw = 180.0f;
                            break;
                        }
                        case 4: {
                            this.gravityTurnVecZ = -1.0f;
                            this.gravityTurnYaw = 180.0f;
                        }
                        case 6: {
                            this.gravityTurnVecX = -2.0f;
                            break;
                        }
                    }
                    break;
                }
                case 6: {
                    switch (newGravity.getIntValue()) {
                        case 1: {
                            this.gravityTurnVecX = 1.0f;
                            break Label_0688;
                        }
                        case 2: {
                            this.gravityTurnVecX = -1.0f;
                            break Label_0688;
                        }
                        case 3: {
                            this.gravityTurnVecZ = -1.0f;
                            break Label_0688;
                        }
                        case 4: {
                            this.gravityTurnVecZ = 1.0f;
                            break Label_0688;
                        }
                        case 5: {
                            this.gravityTurnVecX = -2.0f;
                            break Label_0688;
                        }
                    }
                    break;
                }
            }
        }
        this.gdir = newGravity;
    }
    
    public void setParachute(final boolean tf) {
        if (!(this.usingParachute = tf)) {
            this.lastUsingParachute = false;
        }
    }
    
    public void saveNBTData(final NBTTagCompound nbt) {
    }
    
    public void loadNBTData(final NBTTagCompound nbt) {
    }
    
    public void init(final Entity entity, final World world) {
    }
    
    public static void register(final EntityPlayerSP player) {
        player.registerExtendedProperties("GCPlayerStatsClient", (IExtendedEntityProperties)new GCPlayerStatsClient(player));
    }
    
    public static GCPlayerStatsClient get(final EntityPlayerSP player) {
        return (GCPlayerStatsClient)player.getExtendedProperties("GCPlayerStatsClient");
    }
}
