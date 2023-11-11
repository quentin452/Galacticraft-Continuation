package micdoodle8.mods.galacticraft.core.client.render.entities;

import micdoodle8.mods.galacticraft.core.client.model.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraft.client.renderer.entity.*;
import cpw.mods.fml.common.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.*;
import net.minecraft.entity.*;
import net.minecraft.client.entity.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.blocks.*;
import micdoodle8.mods.galacticraft.core.tile.*;
import micdoodle8.mods.galacticraft.planets.mars.blocks.*;
import micdoodle8.mods.galacticraft.api.entity.*;
import micdoodle8.mods.galacticraft.api.world.*;
import net.minecraft.util.*;
import net.minecraft.block.*;
import net.minecraft.tileentity.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraft.client.model.*;

public class RenderPlayerGC extends RenderPlayer
{
    public static ModelBiped modelThermalPadding;
    public static ModelBiped modelThermalPaddingHelmet;
    private static ResourceLocation thermalPaddingTexture0;
    private static ResourceLocation thermalPaddingTexture1;
    public static boolean flagThermalOverride;
    private static Boolean isSmartRenderLoaded;
    
    public RenderPlayerGC() {
        this.mainModel = (ModelBase)new ModelPlayerGC(0.0f);
        this.modelBipedMain = (ModelBiped)this.mainModel;
        this.modelArmorChestplate = (ModelBiped)new ModelPlayerGC(1.0f);
        this.modelArmor = (ModelBiped)new ModelPlayerGC(0.5f);
        if (GalacticraftCore.isPlanetsLoaded) {
            RenderPlayerGC.thermalPaddingTexture0 = new ResourceLocation("galacticraftasteroids", "textures/misc/thermalPadding_0.png");
            RenderPlayerGC.thermalPaddingTexture1 = new ResourceLocation("galacticraftasteroids", "textures/misc/thermalPadding_1.png");
        }
    }
    
    public static void renderModelS(final RendererLivingEntity inst, final EntityLivingBase par1EntityLivingBase, final float par2, final float par3, final float par4, final float par5, final float par6, final float par7) {
        if (inst instanceof RenderPlayer) {
            final RenderPlayer thisInst = (RenderPlayer)inst;
            if (RenderPlayerGC.isSmartRenderLoaded == null) {
                RenderPlayerGC.isSmartRenderLoaded = Loader.isModLoaded("SmartRender");
            }
            if (RenderPlayerGC.thermalPaddingTexture0 != null && !RenderPlayerGC.isSmartRenderLoaded) {
                final PlayerGearData gearData = ClientProxyCore.playerItemData.get(par1EntityLivingBase.getCommandSenderName());
                if (gearData != null && !RenderPlayerGC.flagThermalOverride) {
                    for (int i = 0; i < 4; ++i) {
                        ModelBiped modelBiped;
                        if (i == 0) {
                            modelBiped = RenderPlayerGC.modelThermalPaddingHelmet;
                        }
                        else {
                            modelBiped = RenderPlayerGC.modelThermalPadding;
                        }
                        final int padding = gearData.getThermalPadding(i);
                        if (padding == 0 && !par1EntityLivingBase.isInvisible()) {
                            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                            Minecraft.getMinecraft().renderEngine.bindTexture(RenderPlayerGC.thermalPaddingTexture1);
                            modelBiped.bipedHead.showModel = (i == 0);
                            modelBiped.bipedHeadwear.showModel = (i == 0);
                            modelBiped.bipedBody.showModel = (i == 1 || i == 2);
                            modelBiped.bipedRightArm.showModel = (i == 1);
                            modelBiped.bipedLeftArm.showModel = (i == 1);
                            modelBiped.bipedRightLeg.showModel = (i == 2 || i == 3);
                            modelBiped.bipedLeftLeg.showModel = (i == 2 || i == 3);
                            modelBiped.onGround = thisInst.mainModel.onGround;
                            modelBiped.isRiding = thisInst.mainModel.isRiding;
                            modelBiped.isChild = thisInst.mainModel.isChild;
                            if (thisInst.mainModel instanceof ModelBiped) {
                                modelBiped.heldItemLeft = ((ModelBiped)thisInst.mainModel).heldItemLeft;
                                modelBiped.heldItemRight = ((ModelBiped)thisInst.mainModel).heldItemRight;
                                modelBiped.isSneak = ((ModelBiped)thisInst.mainModel).isSneak;
                                modelBiped.aimedBow = ((ModelBiped)thisInst.mainModel).aimedBow;
                            }
                            modelBiped.setLivingAnimations(par1EntityLivingBase, par2, par3, 0.0f);
                            modelBiped.render((Entity)par1EntityLivingBase, par2, par3, par4, par5, par6, par7);
                            GL11.glDisable(2896);
                            Minecraft.getMinecraft().renderEngine.bindTexture(RenderPlayerGC.thermalPaddingTexture0);
                            GL11.glEnable(3008);
                            GL11.glEnable(3042);
                            GL11.glAlphaFunc(516, 0.0f);
                            GL11.glBlendFunc(770, 771);
                            final float time = par1EntityLivingBase.ticksExisted / 10.0f;
                            final float sTime = (float)Math.sin(time) * 0.5f + 0.5f;
                            float r = 0.2f * sTime;
                            float g = 1.0f * sTime;
                            float b = 0.2f * sTime;
                            if (par1EntityLivingBase.worldObj.provider instanceof IGalacticraftWorldProvider) {
                                final float modifier = ((IGalacticraftWorldProvider)par1EntityLivingBase.worldObj.provider).getThermalLevelModifier();
                                if (modifier > 0.0f) {
                                    b = g;
                                    g = r;
                                }
                                else if (modifier < 0.0f) {
                                    r = g;
                                    g = b;
                                }
                            }
                            GL11.glColor4f(r, g, b, 0.4f * sTime);
                            modelBiped.render((Entity)par1EntityLivingBase, par2, par3, par4, par5, par6, par7);
                            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                            GL11.glDisable(3042);
                            GL11.glEnable(3008);
                            GL11.glEnable(2896);
                        }
                    }
                }
            }
        }
    }
    
    protected void rotateCorpse(final AbstractClientPlayer par1AbstractClientPlayer, final float par2, final float par3, final float par4) {
        if (par1AbstractClientPlayer.isEntityAlive() && par1AbstractClientPlayer.isPlayerSleeping()) {
            final RotatePlayerEvent event = new RotatePlayerEvent(par1AbstractClientPlayer);
            MinecraftForge.EVENT_BUS.post((Event)event);
            if (!event.vanillaOverride) {
                super.rotateCorpse(par1AbstractClientPlayer, par2, par3, par4);
            }
            else if (event.shouldRotate == null) {
                GL11.glRotatef(par1AbstractClientPlayer.getBedOrientationInDegrees(), 0.0f, 1.0f, 0.0f);
            }
            else if (event.shouldRotate) {
                float rotation = 0.0f;
                final ChunkCoordinates pos = par1AbstractClientPlayer.playerLocation;
                if (pos != null) {
                    Block bed = par1AbstractClientPlayer.worldObj.getBlock(pos.posX, pos.posY, pos.posZ);
                    int meta = par1AbstractClientPlayer.worldObj.getBlockMetadata(pos.posX, pos.posY, pos.posZ);
                    if (bed.isBed((IBlockAccess)par1AbstractClientPlayer.worldObj, pos.posX, pos.posY, pos.posZ, (EntityLivingBase)par1AbstractClientPlayer)) {
                        if (bed == GCBlocks.fakeBlock && meta == 5) {
                            final TileEntity tile = event.entityPlayer.worldObj.getTileEntity(pos.posX, pos.posY, pos.posZ);
                            if (tile instanceof TileEntityMulti) {
                                bed = ((TileEntityMulti)tile).mainBlockPosition.getBlock((IBlockAccess)event.entityPlayer.worldObj);
                                meta = ((TileEntityMulti)tile).mainBlockPosition.getBlockMetadata((IBlockAccess)event.entityPlayer.worldObj);
                            }
                        }
                        if (bed == MarsBlocks.machine && (meta & 0xC) == 0x4) {
                            switch (meta & 0x3) {
                                case 3: {
                                    rotation = 0.0f;
                                    break;
                                }
                                case 1: {
                                    rotation = 270.0f;
                                    break;
                                }
                                case 2: {
                                    rotation = 180.0f;
                                    break;
                                }
                                case 0: {
                                    rotation = 90.0f;
                                    break;
                                }
                            }
                        }
                    }
                }
                GL11.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
            }
        }
        else {
            if (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
                final EntityPlayer player = (EntityPlayer)par1AbstractClientPlayer;
                if (player.ridingEntity instanceof ICameraZoomEntity) {
                    final Entity rocket = player.ridingEntity;
                    final float rotateOffset = ((ICameraZoomEntity)rocket).getRotateOffset();
                    if (rotateOffset > -10.0f) {
                        GL11.glTranslatef(0.0f, -rotateOffset, 0.0f);
                        final float anglePitch = rocket.prevRotationPitch;
                        final float angleYaw = rocket.prevRotationYaw;
                        GL11.glRotatef(-angleYaw, 0.0f, 1.0f, 0.0f);
                        GL11.glRotatef(anglePitch, 0.0f, 0.0f, 1.0f);
                        GL11.glTranslatef(0.0f, rotateOffset, 0.0f);
                    }
                }
            }
            super.rotateCorpse(par1AbstractClientPlayer, par2, par3, par4);
        }
        if (par1AbstractClientPlayer.isSneaking() && par1AbstractClientPlayer.worldObj.provider instanceof IZeroGDimension) {
            GL11.glTranslatef(0.0f, -0.1f, 0.0f);
        }
    }
    
    static {
        RenderPlayerGC.flagThermalOverride = false;
        RenderPlayerGC.isSmartRenderLoaded = null;
        RenderPlayerGC.modelThermalPadding = (ModelBiped)new ModelPlayerGC(0.25f);
        RenderPlayerGC.modelThermalPaddingHelmet = (ModelBiped)new ModelPlayerGC(0.9f);
    }
    
    public static class RotatePlayerEvent extends PlayerEvent
    {
        public Boolean shouldRotate;
        public boolean vanillaOverride;
        
        public RotatePlayerEvent(final AbstractClientPlayer player) {
            super((EntityPlayer)player);
            this.shouldRotate = null;
            this.vanillaOverride = false;
        }
    }
}
