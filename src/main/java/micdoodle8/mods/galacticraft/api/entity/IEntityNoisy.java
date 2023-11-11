package micdoodle8.mods.galacticraft.api.entity;

import net.minecraft.server.gui.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.audio.*;

public interface IEntityNoisy
{
    @SideOnly(Side.CLIENT)
    IUpdatePlayerListBox getSoundUpdater();
    
    @SideOnly(Side.CLIENT)
    ISound setSoundUpdater(final EntityPlayerSP p0);
}
