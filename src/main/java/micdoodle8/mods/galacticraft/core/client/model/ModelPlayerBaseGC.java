package micdoodle8.mods.galacticraft.core.client.model;

import net.minecraftforge.client.model.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import java.lang.reflect.*;
import api.player.model.*;
import net.smart.render.playerapi.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import net.minecraft.item.*;
import java.util.*;
import cpw.mods.fml.client.*;
import org.lwjgl.opengl.*;
import cpw.mods.fml.common.*;

public class ModelPlayerBaseGC extends ModelPlayerBase
{
    public ModelRenderer[] parachute;
    public ModelRenderer[] parachuteStrings;
    public ModelRenderer[][] tubes;
    public ModelRenderer[] greenOxygenTanks;
    public ModelRenderer[] orangeOxygenTanks;
    public ModelRenderer[] redOxygenTanks;
    public ModelRenderer oxygenMask;
    private boolean usingParachute;
    protected static IModelCustom frequencyModule;
    public static AbstractClientPlayer playerRendering;
    protected static PlayerGearData currentGearData;
    public static final ResourceLocation oxygenMaskTexture;
    public static final ResourceLocation playerTexture;
    public static final ResourceLocation frequencyModuleTexture;
    public static boolean isSmartMovingLoaded;
    private static Class modelRotationGCSmartMoving;
    private static Constructor modelRotationGCSmartMovingInit;

    public ModelPlayerBaseGC(final ModelPlayerAPI modelPlayerAPI) {
        super(modelPlayerAPI);
        this.parachute = new ModelRenderer[3];
        this.parachuteStrings = new ModelRenderer[4];
        this.tubes = new ModelRenderer[2][7];
        this.greenOxygenTanks = new ModelRenderer[2];
        this.orangeOxygenTanks = new ModelRenderer[2];
        this.redOxygenTanks = new ModelRenderer[2];
    }

    private ModelRenderer createModelRenderer(final ModelPlayer player, final int texOffsetX, final int texOffsetY, final int type) {
        if (ModelPlayerBaseGC.isSmartMovingLoaded) {
            try {
                switch (type) {
                    case 0:
                    case 9: {
                        return (ModelRenderer) ModelPlayerBaseGC.modelRotationGCSmartMovingInit.newInstance(player, texOffsetX, texOffsetY, SmartRender.getPlayerBase(this.modelPlayer).getHead(), type);
                    }
                    default: {
                        return (ModelRenderer) ModelPlayerBaseGC.modelRotationGCSmartMovingInit.newInstance(player, texOffsetX, texOffsetY, SmartRender.getPlayerBase(this.modelPlayer).getBody(), type);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ModelRenderer((ModelBase)player, texOffsetX, texOffsetY);
    }

    private void init() {
        final float var1 = 0.0f;
        final Render render = RenderManager.instance.getEntityClassRenderObject((Class)EntityClientPlayerMP.class);
        final ModelBiped modelBipedMain = ((RenderPlayer)render).modelBipedMain;
        if (this.modelPlayer.equals(modelBipedMain)) {
            (this.oxygenMask = this.createModelRenderer(this.modelPlayer, 0, 0, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, 1.0f);
            this.oxygenMask.setRotationPoint(0.0f, 0.0f, 0.0f);
            (this.parachute[0] = this.createModelRenderer(this.modelPlayer, 0, 0, 1).setTextureSize(512, 256)).addBox(-20.0f, -45.0f, -20.0f, 10, 2, 40, var1);
            this.parachute[0].setRotationPoint(15.0f, 4.0f, 0.0f);
            this.parachute[0].rotateAngleZ = 0.5235988f;
            (this.parachute[1] = this.createModelRenderer(this.modelPlayer, 0, 42, 1).setTextureSize(512, 256)).addBox(-20.0f, -45.0f, -20.0f, 40, 2, 40, var1);
            this.parachute[1].setRotationPoint(0.0f, 0.0f, 0.0f);
            (this.parachute[2] = this.createModelRenderer(this.modelPlayer, 0, 0, 1).setTextureSize(512, 256)).addBox(-20.0f, -45.0f, -20.0f, 10, 2, 40, var1);
            this.parachute[2].setRotationPoint(11.0f, -11.0f, 0.0f);
            this.parachute[2].rotateAngleZ = -0.5235988f;
            (this.parachuteStrings[0] = this.createModelRenderer(this.modelPlayer, 100, 0, 1).setTextureSize(512, 256)).addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, var1);
            this.parachuteStrings[0].rotateAngleZ = 2.7052603f;
            this.parachuteStrings[0].rotateAngleX = 0.40142572f;
            this.parachuteStrings[0].setRotationPoint(-9.0f, -7.0f, 2.0f);
            (this.parachuteStrings[1] = this.createModelRenderer(this.modelPlayer, 100, 0, 1).setTextureSize(512, 256)).addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, var1);
            this.parachuteStrings[1].rotateAngleZ = 2.7052603f;
            this.parachuteStrings[1].rotateAngleX = -0.40142572f;
            this.parachuteStrings[1].setRotationPoint(-9.0f, -7.0f, 2.0f);
            (this.parachuteStrings[2] = this.createModelRenderer(this.modelPlayer, 100, 0, 1).setTextureSize(512, 256)).addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, var1);
            this.parachuteStrings[2].rotateAngleZ = -2.7052603f;
            this.parachuteStrings[2].rotateAngleX = 0.40142572f;
            this.parachuteStrings[2].setRotationPoint(9.0f, -7.0f, 2.0f);
            (this.parachuteStrings[3] = this.createModelRenderer(this.modelPlayer, 100, 0, 1).setTextureSize(512, 256)).addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, var1);
            this.parachuteStrings[3].rotateAngleZ = -2.7052603f;
            this.parachuteStrings[3].rotateAngleX = -0.40142572f;
            this.parachuteStrings[3].setRotationPoint(9.0f, -7.0f, 2.0f);
            (this.tubes[0][0] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[0][0].setRotationPoint(2.0f, 3.0f, 5.8f);
            this.tubes[0][0].setTextureSize(128, 64);
            this.tubes[0][0].mirror = true;
            (this.tubes[0][1] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[0][1].setRotationPoint(2.0f, 2.0f, 6.8f);
            this.tubes[0][1].setTextureSize(128, 64);
            this.tubes[0][1].mirror = true;
            (this.tubes[0][2] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[0][2].setRotationPoint(2.0f, 1.0f, 6.8f);
            this.tubes[0][2].setTextureSize(128, 64);
            this.tubes[0][2].mirror = true;
            (this.tubes[0][3] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[0][3].setRotationPoint(2.0f, 0.0f, 6.8f);
            this.tubes[0][3].setTextureSize(128, 64);
            this.tubes[0][3].mirror = true;
            (this.tubes[0][4] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[0][4].setRotationPoint(2.0f, -1.0f, 6.8f);
            this.tubes[0][4].setTextureSize(128, 64);
            this.tubes[0][4].mirror = true;
            (this.tubes[0][5] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[0][5].setRotationPoint(2.0f, -2.0f, 5.8f);
            this.tubes[0][5].setTextureSize(128, 64);
            this.tubes[0][5].mirror = true;
            (this.tubes[0][6] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[0][6].setRotationPoint(2.0f, -3.0f, 4.8f);
            this.tubes[0][6].setTextureSize(128, 64);
            this.tubes[0][6].mirror = true;
            (this.tubes[1][0] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[1][0].setRotationPoint(-2.0f, 3.0f, 5.8f);
            this.tubes[1][0].setTextureSize(128, 64);
            this.tubes[1][0].mirror = true;
            (this.tubes[1][1] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[1][1].setRotationPoint(-2.0f, 2.0f, 6.8f);
            this.tubes[1][1].setTextureSize(128, 64);
            this.tubes[1][1].mirror = true;
            (this.tubes[1][2] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[1][2].setRotationPoint(-2.0f, 1.0f, 6.8f);
            this.tubes[1][2].setTextureSize(128, 64);
            this.tubes[1][2].mirror = true;
            (this.tubes[1][3] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[1][3].setRotationPoint(-2.0f, 0.0f, 6.8f);
            this.tubes[1][3].setTextureSize(128, 64);
            this.tubes[1][3].mirror = true;
            (this.tubes[1][4] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[1][4].setRotationPoint(-2.0f, -1.0f, 6.8f);
            this.tubes[1][4].setTextureSize(128, 64);
            this.tubes[1][4].mirror = true;
            (this.tubes[1][5] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[1][5].setRotationPoint(-2.0f, -2.0f, 5.8f);
            this.tubes[1][5].setTextureSize(128, 64);
            this.tubes[1][5].mirror = true;
            (this.tubes[1][6] = this.createModelRenderer(this.modelPlayer, 0, 0, 2)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
            this.tubes[1][6].setRotationPoint(-2.0f, -3.0f, 4.8f);
            this.tubes[1][6].setTextureSize(128, 64);
            this.tubes[1][6].mirror = true;
            (this.greenOxygenTanks[0] = this.createModelRenderer(this.modelPlayer, 4, 0, 3)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, var1);
            this.greenOxygenTanks[0].setRotationPoint(2.0f, 2.0f, 3.8f);
            this.greenOxygenTanks[0].mirror = true;
            (this.greenOxygenTanks[1] = this.createModelRenderer(this.modelPlayer, 4, 0, 4)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, var1);
            this.greenOxygenTanks[1].setRotationPoint(-2.0f, 2.0f, 3.8f);
            this.greenOxygenTanks[1].mirror = true;
            (this.orangeOxygenTanks[0] = this.createModelRenderer(this.modelPlayer, 16, 0, 5)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, var1);
            this.orangeOxygenTanks[0].setRotationPoint(2.0f, 2.0f, 3.8f);
            this.orangeOxygenTanks[0].mirror = true;
            (this.orangeOxygenTanks[1] = this.createModelRenderer(this.modelPlayer, 16, 0, 6)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, var1);
            this.orangeOxygenTanks[1].setRotationPoint(-2.0f, 2.0f, 3.8f);
            this.orangeOxygenTanks[1].mirror = true;
            (this.redOxygenTanks[0] = this.createModelRenderer(this.modelPlayer, 28, 0, 7)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, var1);
            this.redOxygenTanks[0].setRotationPoint(2.0f, 2.0f, 3.8f);
            this.redOxygenTanks[0].mirror = true;
            (this.redOxygenTanks[1] = this.createModelRenderer(this.modelPlayer, 28, 0, 8)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, var1);
            this.redOxygenTanks[1].setRotationPoint(-2.0f, 2.0f, 3.8f);
            this.redOxygenTanks[1].mirror = true;
        }
    }

    public void beforeRender(final Entity var1, final float var2, final float var3, final float var4, final float var5, final float var6, final float var7) {
        if (!(var1 instanceof EntityPlayer)) {
            return;
        }
        this.usingParachute = false;
        final EntityPlayer player = (EntityPlayer)var1;
        final PlayerGearData gearData = ClientProxyCore.playerItemData.get(player.getCommandSenderName());
        if (gearData != null) {
            this.usingParachute = (gearData.getParachute() != null);
        }
        ModelPlayerBaseGC.playerRendering = (AbstractClientPlayer)var1;
        ModelPlayerBaseGC.currentGearData = ClientProxyCore.playerItemData.get(ModelPlayerBaseGC.playerRendering.getCommandSenderName());
        if (ModelPlayerBaseGC.currentGearData == null) {
            final String id = player.getGameProfile().getName();
            if (!ClientProxyCore.gearDataRequests.contains(id)) {
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_GEAR_DATA, new Object[] { id }));
                ClientProxyCore.gearDataRequests.add(id);
            }
        }
        super.beforeRender(var1, var2, var3, var4, var5, var6, var7);
        if (this.oxygenMask == null) {
            this.init();
        }
    }

    public void afterSetRotationAngles(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6, final Entity par7Entity) {
        super.afterSetRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
        if (!(par7Entity instanceof EntityPlayer)) {
            return;
        }
        final EntityPlayer player = (EntityPlayer)par7Entity;
        final ItemStack currentItemStack = player.inventory.getCurrentItem();
        if (!par7Entity.onGround && par7Entity.worldObj.provider instanceof IGalacticraftWorldProvider && par7Entity.ridingEntity == null && (currentItemStack == null || !(currentItemStack.getItem() instanceof IHoldableItem))) {
            final float speedModifier = 0.2324f;
            final float angularSwingArm = MathHelper.cos(par1 * (speedModifier / 2.0f));
            final float rightMod = (this.modelPlayer.heldItemRight != 0) ? 1.0f : 2.0f;
            final ModelRenderer bipedRightArm = this.modelPlayer.bipedRightArm;
            bipedRightArm.rotateAngleX -= MathHelper.cos(par1 * 0.6662f + 3.1415927f) * rightMod * par2 * 0.5f;
            final ModelRenderer bipedLeftArm = this.modelPlayer.bipedLeftArm;
            bipedLeftArm.rotateAngleX -= MathHelper.cos(par1 * 0.6662f) * 2.0f * par2 * 0.5f;
            final ModelRenderer bipedRightArm2 = this.modelPlayer.bipedRightArm;
            bipedRightArm2.rotateAngleX += -angularSwingArm * 4.0f * par2 * 0.5f;
            final ModelRenderer bipedLeftArm2 = this.modelPlayer.bipedLeftArm;
            bipedLeftArm2.rotateAngleX += angularSwingArm * 4.0f * par2 * 0.5f;
            final ModelRenderer bipedLeftLeg = this.modelPlayer.bipedLeftLeg;
            bipedLeftLeg.rotateAngleX -= MathHelper.cos(par1 * 0.6662f + 3.1415927f) * 1.4f * par2;
            final ModelRenderer bipedLeftLeg2 = this.modelPlayer.bipedLeftLeg;
            bipedLeftLeg2.rotateAngleX += MathHelper.cos(par1 * 0.1162f * 2.0f + 3.1415927f) * 1.4f * par2;
            final ModelRenderer bipedRightLeg = this.modelPlayer.bipedRightLeg;
            bipedRightLeg.rotateAngleX -= MathHelper.cos(par1 * 0.6662f) * 1.4f * par2;
            final ModelRenderer bipedRightLeg2 = this.modelPlayer.bipedRightLeg;
            bipedRightLeg2.rotateAngleX += MathHelper.cos(par1 * 0.1162f * 2.0f) * 1.4f * par2;
        }
        if (this.usingParachute) {
            final ModelRenderer bipedLeftArm3 = this.modelPlayer.bipedLeftArm;
            bipedLeftArm3.rotateAngleX += 3.1415927f;
            final ModelRenderer bipedLeftArm4 = this.modelPlayer.bipedLeftArm;
            bipedLeftArm4.rotateAngleZ += 0.31415927f;
            final ModelRenderer bipedRightArm3 = this.modelPlayer.bipedRightArm;
            bipedRightArm3.rotateAngleX += 3.1415927f;
            final ModelRenderer bipedRightArm4 = this.modelPlayer.bipedRightArm;
            bipedRightArm4.rotateAngleZ -= 0.31415927f;
        }
        if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof IHoldableItem) {
            final IHoldableItem holdableItem = (IHoldableItem)player.inventory.getCurrentItem().getItem();
            if (holdableItem.shouldHoldLeftHandUp(player)) {
                this.modelPlayer.bipedLeftArm.rotateAngleX = 0.0f;
                this.modelPlayer.bipedLeftArm.rotateAngleZ = 0.0f;
                final ModelRenderer bipedLeftArm5 = this.modelPlayer.bipedLeftArm;
                bipedLeftArm5.rotateAngleX += (float)3.441592741012573;
                final ModelRenderer bipedLeftArm6 = this.modelPlayer.bipedLeftArm;
                bipedLeftArm6.rotateAngleZ += 0.31415927f;
            }
            if (holdableItem.shouldHoldRightHandUp(player)) {
                this.modelPlayer.bipedRightArm.rotateAngleX = 0.0f;
                this.modelPlayer.bipedRightArm.rotateAngleZ = 0.0f;
                final ModelRenderer bipedRightArm5 = this.modelPlayer.bipedRightArm;
                bipedRightArm5.rotateAngleX += (float)3.441592741012573;
                final ModelRenderer bipedRightArm6 = this.modelPlayer.bipedRightArm;
                bipedRightArm6.rotateAngleZ -= 0.31415927f;
            }
            if (player.onGround && holdableItem.shouldCrouch(player)) {
                this.modelPlayer.bipedBody.rotateAngleX = 0.35f;
                this.modelPlayer.bipedRightLeg.rotationPointZ = 4.0f;
                this.modelPlayer.bipedLeftLeg.rotationPointZ = 4.0f;
            }
        }
        final List<?> l = (List<?>)player.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)player, AxisAlignedBB.getBoundingBox(player.posX - 20.0, 0.0, player.posZ - 20.0, player.posX + 20.0, 200.0, player.posZ + 20.0));
        for (int i = 0; i < l.size(); ++i) {
            final Entity e = (Entity)l.get(i);
            if (e instanceof EntityTieredRocket) {
                final EntityTieredRocket ship = (EntityTieredRocket)e;
                if (ship.riddenByEntity != null && !ship.riddenByEntity.equals((Object)player) && (ship.getLaunched() || ship.timeUntilLaunch < 390)) {
                    final ModelRenderer bipedRightArm7 = this.modelPlayer.bipedRightArm;
                    bipedRightArm7.rotateAngleZ -= 0.3926991f + MathHelper.sin(par3 * 0.9f) * 0.2f;
                    this.modelPlayer.bipedRightArm.rotateAngleX = 3.1415927f;
                    break;
                }
            }
        }
    }

    public void afterRender(final Entity var1, final float var2, final float var3, final float var4, final float var5, final float var6, final float var7) {
        super.afterRender(var1, var2, var3, var4, var5, var6, var7);
        if (ModelPlayerBaseGC.isSmartMovingLoaded) {
            return;
        }
        if (!(var1 instanceof EntityPlayer)) {
            return;
        }
        if (this.oxygenMask == null) {
            return;
        }
        final EntityPlayer player = (EntityPlayer)var1;
        final PlayerGearData gearData = ClientProxyCore.playerItemData.get(player.getCommandSenderName());
        if (var1 instanceof AbstractClientPlayer && gearData != null) {
            this.usingParachute = (gearData.getParachute() != null);
            final boolean wearingMask = gearData.getMask() > -1;
            final boolean wearingGear = gearData.getGear() > -1;
            final boolean wearingLeftTankGreen = gearData.getLeftTank() == 0;
            final boolean wearingLeftTankOrange = gearData.getLeftTank() == 1;
            final boolean wearingLeftTankRed = gearData.getLeftTank() == 2;
            final boolean wearingRightTankGreen = gearData.getRightTank() == 0;
            final boolean wearingRightTankOrange = gearData.getRightTank() == 1;
            final boolean wearingRightTankRed = gearData.getRightTank() == 2;
            if (wearingMask) {
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerBaseGC.oxygenMaskTexture);
                GL11.glPushMatrix();
                GL11.glScalef(1.05f, 1.05f, 1.05f);
                this.oxygenMask.rotateAngleY = this.modelPlayer.bipedHead.rotateAngleY;
                this.oxygenMask.rotateAngleX = this.modelPlayer.bipedHead.rotateAngleX;
                this.oxygenMask.render(var7);
                GL11.glScalef(1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            }
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerBaseGC.playerTexture);
            if (wearingGear) {
                for (int i = 0; i < 7; ++i) {
                    for (int k = 0; k < 2; ++k) {
                        this.tubes[k][i].render(var7);
                    }
                }
            }
            if (wearingLeftTankRed) {
                this.redOxygenTanks[0].render(var7);
            }
            if (wearingLeftTankOrange) {
                this.orangeOxygenTanks[0].render(var7);
            }
            if (wearingLeftTankGreen) {
                this.greenOxygenTanks[0].render(var7);
            }
            if (wearingRightTankRed) {
                this.redOxygenTanks[1].render(var7);
            }
            if (wearingRightTankOrange) {
                this.orangeOxygenTanks[1].render(var7);
            }
            if (wearingRightTankGreen) {
                this.greenOxygenTanks[1].render(var7);
            }
            if (this.usingParachute) {
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(gearData.getParachute());
                this.parachute[0].render(var7);
                this.parachute[1].render(var7);
                this.parachute[2].render(var7);
                this.parachuteStrings[0].render(var7);
                this.parachuteStrings[1].render(var7);
                this.parachuteStrings[2].render(var7);
                this.parachuteStrings[3].render(var7);
            }
        }
    }

    static {
        oxygenMaskTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/oxygen.png");
        playerTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/player.png");
        frequencyModuleTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/frequencyModule.png");
        ModelPlayerBaseGC.isSmartMovingLoaded = Loader.isModLoaded("SmartRender");
        if (ModelPlayerBaseGC.isSmartMovingLoaded) {
            try {
                ModelPlayerBaseGC.modelRotationGCSmartMoving = Class.forName("micdoodle8.mods.galacticraft.core.client.model.ModelRotationRendererGC");
                ModelPlayerBaseGC.modelRotationGCSmartMovingInit = ModelPlayerBaseGC.modelRotationGCSmartMoving.getConstructor(ModelBase.class, Integer.TYPE, Integer.TYPE, ModelRenderer.class, Integer.TYPE);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
