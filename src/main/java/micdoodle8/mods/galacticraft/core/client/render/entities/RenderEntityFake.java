package micdoodle8.mods.galacticraft.core.client.render.entities;

import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.entities.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;

@SideOnly(Side.CLIENT)
public class RenderEntityFake extends Render
{
    protected ResourceLocation func_110779_a(final EntityMeteor entity) {
        return null;
    }
    
    protected ResourceLocation getEntityTexture(final Entity par1Entity) {
        return null;
    }
    
    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6, final float par8, final float par9) {
    }
}
