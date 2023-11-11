package micdoodle8.mods.galacticraft.core.dimension;

import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.entity.*;
import java.util.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraft.util.*;

public class TeleportTypeSpaceStation implements ITeleportType
{
    public boolean useParachute() {
        return false;
    }
    
    public Vector3 getPlayerSpawnLocation(final WorldServer world, final EntityPlayerMP player) {
        return new Vector3(0.5, 65.0, 0.5);
    }
    
    public Vector3 getEntitySpawnLocation(final WorldServer world, final Entity player) {
        return new Vector3(0.5, 65.0, 0.5);
    }
    
    public Vector3 getParaChestSpawnLocation(final WorldServer world, final EntityPlayerMP player, final Random rand) {
        return null;
    }
    
    public void onSpaceDimensionChanged(final World newWorld, final EntityPlayerMP player, final boolean ridingAutoRocket) {
        if (ConfigManagerCore.spaceStationsRequirePermission && !newWorld.isRemote) {
            player.addChatMessage((IChatComponent)new ChatComponentText(EnumColor.YELLOW + GCCoreUtil.translate("gui.spacestation.typeCommand") + " " + EnumColor.AQUA + "/ssinvite " + GCCoreUtil.translate("gui.spacestation.playername") + " " + EnumColor.YELLOW + GCCoreUtil.translate("gui.spacestation.toAllowEntry")));
        }
    }
    
    public void setupAdventureSpawn(final EntityPlayerMP player) {
    }
}
