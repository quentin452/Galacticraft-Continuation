package micdoodle8.mods.galacticraft.core.client.model;

import net.minecraft.client.model.*;
import micdoodle8.mods.galacticraft.core.*;
import net.minecraftforge.client.model.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.client.entity.*;
import cpw.mods.fml.client.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.entity.*;
import micdoodle8.mods.galacticraft.api.world.*;
import micdoodle8.mods.galacticraft.api.item.*;
import net.minecraft.util.*;
import micdoodle8.mods.galacticraft.api.prefab.entity.*;
import net.minecraft.item.*;
import java.util.*;
import cpw.mods.fml.common.*;

public class ModelPlayerGC extends ModelBiped
{
    public static final ResourceLocation oxygenMaskTexture;
    public static final ResourceLocation playerTexture;
    public static final ResourceLocation frequencyModuleTexture;
    public ModelRenderer[] parachute;
    public ModelRenderer[] parachuteStrings;
    public ModelRenderer[][] tubes;
    public ModelRenderer[] greenOxygenTanks;
    public ModelRenderer[] orangeOxygenTanks;
    public ModelRenderer[] redOxygenTanks;
    public ModelRenderer oxygenMask;
    private boolean usingParachute;
    private IModelCustom frequencyModule;
    private static boolean crossbowModLoaded;
    
    public ModelPlayerGC(final float var1) {
        super(var1);
        this.parachute = new ModelRenderer[3];
        this.parachuteStrings = new ModelRenderer[4];
        this.tubes = new ModelRenderer[2][7];
        this.greenOxygenTanks = new ModelRenderer[2];
        this.orangeOxygenTanks = new ModelRenderer[2];
        this.redOxygenTanks = new ModelRenderer[2];
        (this.oxygenMask = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, 1.0f);
        this.oxygenMask.setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.parachute[0] = new ModelRenderer((ModelBase)this, 0, 0).setTextureSize(512, 256)).addBox(-20.0f, -45.0f, -20.0f, 10, 2, 40, var1);
        this.parachute[0].setRotationPoint(15.0f, 4.0f, 0.0f);
        (this.parachute[1] = new ModelRenderer((ModelBase)this, 0, 42).setTextureSize(512, 256)).addBox(-20.0f, -45.0f, -20.0f, 40, 2, 40, var1);
        this.parachute[1].setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.parachute[2] = new ModelRenderer((ModelBase)this, 0, 0).setTextureSize(512, 256)).addBox(-20.0f, -45.0f, -20.0f, 10, 2, 40, var1);
        this.parachute[2].setRotationPoint(11.0f, -11.0f, 0.0f);
        (this.parachuteStrings[0] = new ModelRenderer((ModelBase)this, 100, 0).setTextureSize(512, 256)).addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, var1);
        this.parachuteStrings[0].setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.parachuteStrings[1] = new ModelRenderer((ModelBase)this, 100, 0).setTextureSize(512, 256)).addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, var1);
        this.parachuteStrings[1].setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.parachuteStrings[2] = new ModelRenderer((ModelBase)this, 100, 0).setTextureSize(512, 256)).addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, var1);
        this.parachuteStrings[2].setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.parachuteStrings[3] = new ModelRenderer((ModelBase)this, 100, 0).setTextureSize(512, 256)).addBox(-0.5f, 0.0f, -0.5f, 1, 40, 1, var1);
        this.parachuteStrings[3].setRotationPoint(0.0f, 0.0f, 0.0f);
        (this.tubes[0][0] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[0][0].setRotationPoint(2.0f, 3.0f, 5.8f);
        this.tubes[0][0].setTextureSize(128, 64);
        this.tubes[0][0].mirror = true;
        (this.tubes[0][1] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[0][1].setRotationPoint(2.0f, 2.0f, 6.8f);
        this.tubes[0][1].setTextureSize(128, 64);
        this.tubes[0][1].mirror = true;
        (this.tubes[0][2] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[0][2].setRotationPoint(2.0f, 1.0f, 6.8f);
        this.tubes[0][2].setTextureSize(128, 64);
        this.tubes[0][2].mirror = true;
        (this.tubes[0][3] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[0][3].setRotationPoint(2.0f, 0.0f, 6.8f);
        this.tubes[0][3].setTextureSize(128, 64);
        this.tubes[0][3].mirror = true;
        (this.tubes[0][4] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[0][4].setRotationPoint(2.0f, -1.0f, 6.8f);
        this.tubes[0][4].setTextureSize(128, 64);
        this.tubes[0][4].mirror = true;
        (this.tubes[0][5] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[0][5].setRotationPoint(2.0f, -2.0f, 5.8f);
        this.tubes[0][5].setTextureSize(128, 64);
        this.tubes[0][5].mirror = true;
        (this.tubes[0][6] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[0][6].setRotationPoint(2.0f, -3.0f, 4.8f);
        this.tubes[0][6].setTextureSize(128, 64);
        this.tubes[0][6].mirror = true;
        (this.tubes[1][0] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[1][0].setRotationPoint(-2.0f, 3.0f, 5.8f);
        this.tubes[1][0].setTextureSize(128, 64);
        this.tubes[1][0].mirror = true;
        (this.tubes[1][1] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[1][1].setRotationPoint(-2.0f, 2.0f, 6.8f);
        this.tubes[1][1].setTextureSize(128, 64);
        this.tubes[1][1].mirror = true;
        (this.tubes[1][2] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[1][2].setRotationPoint(-2.0f, 1.0f, 6.8f);
        this.tubes[1][2].setTextureSize(128, 64);
        this.tubes[1][2].mirror = true;
        (this.tubes[1][3] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[1][3].setRotationPoint(-2.0f, 0.0f, 6.8f);
        this.tubes[1][3].setTextureSize(128, 64);
        this.tubes[1][3].mirror = true;
        (this.tubes[1][4] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[1][4].setRotationPoint(-2.0f, -1.0f, 6.8f);
        this.tubes[1][4].setTextureSize(128, 64);
        this.tubes[1][4].mirror = true;
        (this.tubes[1][5] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[1][5].setRotationPoint(-2.0f, -2.0f, 5.8f);
        this.tubes[1][5].setTextureSize(128, 64);
        this.tubes[1][5].mirror = true;
        (this.tubes[1][6] = new ModelRenderer((ModelBase)this, 0, 0)).addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, var1);
        this.tubes[1][6].setRotationPoint(-2.0f, -3.0f, 4.8f);
        this.tubes[1][6].setTextureSize(128, 64);
        this.tubes[1][6].mirror = true;
        (this.greenOxygenTanks[0] = new ModelRenderer((ModelBase)this, 4, 0)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, var1);
        this.greenOxygenTanks[0].setRotationPoint(2.0f, 2.0f, 3.8f);
        this.greenOxygenTanks[0].mirror = true;
        (this.greenOxygenTanks[1] = new ModelRenderer((ModelBase)this, 4, 0)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, var1);
        this.greenOxygenTanks[1].setRotationPoint(-2.0f, 2.0f, 3.8f);
        this.greenOxygenTanks[1].mirror = true;
        (this.orangeOxygenTanks[0] = new ModelRenderer((ModelBase)this, 16, 0)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, var1);
        this.orangeOxygenTanks[0].setRotationPoint(2.0f, 2.0f, 3.8f);
        this.orangeOxygenTanks[0].mirror = true;
        (this.orangeOxygenTanks[1] = new ModelRenderer((ModelBase)this, 16, 0)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, var1);
        this.orangeOxygenTanks[1].setRotationPoint(-2.0f, 2.0f, 3.8f);
        this.orangeOxygenTanks[1].mirror = true;
        (this.redOxygenTanks[0] = new ModelRenderer((ModelBase)this, 28, 0)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, var1);
        this.redOxygenTanks[0].setRotationPoint(2.0f, 2.0f, 3.8f);
        this.redOxygenTanks[0].mirror = true;
        (this.redOxygenTanks[1] = new ModelRenderer((ModelBase)this, 28, 0)).addBox(-1.5f, 0.0f, -1.5f, 3, 7, 3, var1);
        this.redOxygenTanks[1].setRotationPoint(-2.0f, 2.0f, 3.8f);
        this.redOxygenTanks[1].mirror = true;
        this.frequencyModule = AdvancedModelLoader.loadModel(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "models/frequencyModule.obj"));
    }
    
    public void render(final Entity var1, final float var2, final float var3, final float var4, final float var5, final float var6, final float var7) {
        final Class<?> entityClass = EntityClientPlayerMP.class;
        final Render render = RenderManager.instance.getEntityClassRenderObject((Class)entityClass);
        final ModelBiped modelBipedMain = ((RenderPlayer)render).modelBipedMain;
        this.usingParachute = false;
        boolean wearingMask = false;
        boolean wearingGear = false;
        boolean wearingLeftTankGreen = false;
        boolean wearingLeftTankOrange = false;
        boolean wearingLeftTankRed = false;
        boolean wearingRightTankGreen = false;
        boolean wearingRightTankOrange = false;
        boolean wearingRightTankRed = false;
        boolean wearingFrequencyModule = false;
        final EntityPlayer player = (EntityPlayer)var1;
        final PlayerGearData gearData = ClientProxyCore.playerItemData.get(player.getCommandSenderName());
        if (gearData != null) {
            this.usingParachute = (gearData.getParachute() != null);
            wearingMask = (gearData.getMask() > -1);
            wearingGear = (gearData.getGear() > -1);
            wearingLeftTankGreen = (gearData.getLeftTank() == 0);
            wearingLeftTankOrange = (gearData.getLeftTank() == 1);
            wearingLeftTankRed = (gearData.getLeftTank() == 2);
            wearingRightTankGreen = (gearData.getRightTank() == 0);
            wearingRightTankOrange = (gearData.getRightTank() == 1);
            wearingRightTankRed = (gearData.getRightTank() == 2);
            wearingFrequencyModule = (gearData.getFrequencyModule() > -1);
        }
        else {
            final String id = player.getGameProfile().getName();
            if (!ClientProxyCore.gearDataRequests.contains(id)) {
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_GEAR_DATA, new Object[] { id }));
                ClientProxyCore.gearDataRequests.add(id);
            }
        }
        this.setRotationAngles(var2, var3, var4, var5, var6, var7, var1);
        if (var1 instanceof AbstractClientPlayer && this.equals(modelBipedMain)) {
            if (gearData != null) {
                if (wearingMask) {
                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.oxygenMaskTexture);
                    GL11.glPushMatrix();
                    GL11.glScalef(1.05f, 1.05f, 1.05f);
                    this.oxygenMask.rotateAngleY = this.bipedHead.rotateAngleY;
                    this.oxygenMask.rotateAngleX = this.bipedHead.rotateAngleX;
                    this.oxygenMask.render(var7);
                    GL11.glScalef(1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                }
                if (wearingFrequencyModule) {
                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.frequencyModuleTexture);
                    GL11.glPushMatrix();
                    GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
                    GL11.glRotatef((float)(this.bipedHeadwear.rotateAngleY * -57.29577951308232), 0.0f, 1.0f, 0.0f);
                    GL11.glRotatef((float)(this.bipedHeadwear.rotateAngleX * 57.29577951308232), 1.0f, 0.0f, 0.0f);
                    GL11.glScalef(0.3f, 0.3f, 0.3f);
                    GL11.glTranslatef(-1.1f, 1.2f, 0.0f);
                    this.frequencyModule.renderPart("Main");
                    GL11.glTranslatef(0.0f, 1.2f, 0.0f);
                    GL11.glRotatef((float)(Math.sin(var1.ticksExisted * 0.05) * 50.0), 1.0f, 0.0f, 0.0f);
                    GL11.glRotatef((float)(Math.cos(var1.ticksExisted * 0.1) * 50.0), 0.0f, 1.0f, 0.0f);
                    GL11.glTranslatef(0.0f, -1.2f, 0.0f);
                    this.frequencyModule.renderPart("Radar");
                    GL11.glPopMatrix();
                }
                FMLClientHandler.instance().getClient().renderEngine.bindTexture(ModelPlayerGC.playerTexture);
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
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(((AbstractClientPlayer)player).getLocationSkin());
        }
        super.render(var1, var2, var3, var4, var5, var6, var7);
    }
    
    public void setRotationAngles(final float par1, final float par2, final float par3, final float par4, final float par5, final float par6, final Entity par7Entity) {
        super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
        final EntityPlayer player = (EntityPlayer)par7Entity;
        final ItemStack currentItemStack = player.inventory.getCurrentItem();
        if (!par7Entity.onGround && par7Entity.worldObj.provider instanceof IGalacticraftWorldProvider && par7Entity.ridingEntity == null && (currentItemStack == null || !(currentItemStack.getItem() instanceof IHoldableItem))) {
            final float speedModifier = 0.2324f;
            final float angularSwingArm = MathHelper.cos(par1 * (speedModifier / 2.0f));
            final float rightMod = (this.heldItemRight != 0) ? 1.0f : 2.0f;
            final ModelRenderer bipedRightArm = this.bipedRightArm;
            bipedRightArm.rotateAngleX -= MathHelper.cos(par1 * 0.6662f + 3.1415927f) * rightMod * par2 * 0.5f;
            final ModelRenderer bipedLeftArm = this.bipedLeftArm;
            bipedLeftArm.rotateAngleX -= MathHelper.cos(par1 * 0.6662f) * 2.0f * par2 * 0.5f;
            final ModelRenderer bipedRightArm2 = this.bipedRightArm;
            bipedRightArm2.rotateAngleX += -angularSwingArm * 4.0f * par2 * 0.5f;
            final ModelRenderer bipedLeftArm2 = this.bipedLeftArm;
            bipedLeftArm2.rotateAngleX += angularSwingArm * 4.0f * par2 * 0.5f;
            final ModelRenderer bipedLeftLeg = this.bipedLeftLeg;
            bipedLeftLeg.rotateAngleX -= MathHelper.cos(par1 * 0.6662f + 3.1415927f) * 1.4f * par2;
            final ModelRenderer bipedLeftLeg2 = this.bipedLeftLeg;
            bipedLeftLeg2.rotateAngleX += MathHelper.cos(par1 * 0.1162f * 2.0f + 3.1415927f) * 1.4f * par2;
            final ModelRenderer bipedRightLeg = this.bipedRightLeg;
            bipedRightLeg.rotateAngleX -= MathHelper.cos(par1 * 0.6662f) * 1.4f * par2;
            final ModelRenderer bipedRightLeg2 = this.bipedRightLeg;
            bipedRightLeg2.rotateAngleX += MathHelper.cos(par1 * 0.1162f * 2.0f) * 1.4f * par2;
        }
        if (this.usingParachute) {
            this.parachute[0].rotateAngleZ = 0.5235988f;
            this.parachute[2].rotateAngleZ = -0.5235988f;
            this.parachuteStrings[0].rotateAngleZ = 2.7052603f;
            this.parachuteStrings[0].rotateAngleX = 0.40142572f;
            this.parachuteStrings[0].setRotationPoint(-9.0f, -7.0f, 2.0f);
            this.parachuteStrings[1].rotateAngleZ = 2.7052603f;
            this.parachuteStrings[1].rotateAngleX = -0.40142572f;
            this.parachuteStrings[1].setRotationPoint(-9.0f, -7.0f, 2.0f);
            this.parachuteStrings[2].rotateAngleZ = -2.7052603f;
            this.parachuteStrings[2].rotateAngleX = 0.40142572f;
            this.parachuteStrings[2].setRotationPoint(9.0f, -7.0f, 2.0f);
            this.parachuteStrings[3].rotateAngleZ = -2.7052603f;
            this.parachuteStrings[3].rotateAngleX = -0.40142572f;
            this.parachuteStrings[3].setRotationPoint(9.0f, -7.0f, 2.0f);
            final ModelRenderer bipedLeftArm3 = this.bipedLeftArm;
            bipedLeftArm3.rotateAngleX += 3.1415927f;
            final ModelRenderer bipedLeftArm4 = this.bipedLeftArm;
            bipedLeftArm4.rotateAngleZ += 0.31415927f;
            final ModelRenderer bipedRightArm3 = this.bipedRightArm;
            bipedRightArm3.rotateAngleX += 3.1415927f;
            final ModelRenderer bipedRightArm4 = this.bipedRightArm;
            bipedRightArm4.rotateAngleZ -= 0.31415927f;
        }
        if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof IHoldableItem) {
            final IHoldableItem holdableItem = (IHoldableItem)player.inventory.getCurrentItem().getItem();
            if (holdableItem.shouldHoldLeftHandUp(player)) {
                this.bipedLeftArm.rotateAngleX = 0.0f;
                this.bipedLeftArm.rotateAngleZ = 0.0f;
                final ModelRenderer bipedLeftArm5 = this.bipedLeftArm;
                bipedLeftArm5.rotateAngleX += (float)3.441592741012573;
                final ModelRenderer bipedLeftArm6 = this.bipedLeftArm;
                bipedLeftArm6.rotateAngleZ += 0.31415927f;
            }
            if (holdableItem.shouldHoldRightHandUp(player)) {
                this.bipedRightArm.rotateAngleX = 0.0f;
                this.bipedRightArm.rotateAngleZ = 0.0f;
                final ModelRenderer bipedRightArm5 = this.bipedRightArm;
                bipedRightArm5.rotateAngleX += (float)3.441592741012573;
                final ModelRenderer bipedRightArm6 = this.bipedRightArm;
                bipedRightArm6.rotateAngleZ -= 0.31415927f;
            }
            if (player.onGround && holdableItem.shouldCrouch(player)) {
                this.bipedBody.rotateAngleX = 0.5f;
                this.bipedRightLeg.rotationPointZ = 4.0f;
                this.bipedLeftLeg.rotationPointZ = 4.0f;
                this.bipedRightLeg.rotationPointY = 9.0f;
                this.bipedLeftLeg.rotationPointY = 9.0f;
                this.bipedHead.rotationPointY = 1.0f;
                this.bipedHeadwear.rotationPointY = 1.0f;
            }
        }
        this.greenOxygenTanks[0].rotateAngleX = this.bipedBody.rotateAngleX;
        this.greenOxygenTanks[0].rotateAngleY = this.bipedBody.rotateAngleY;
        this.greenOxygenTanks[0].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.greenOxygenTanks[1].rotateAngleX = this.bipedBody.rotateAngleX;
        this.greenOxygenTanks[1].rotateAngleY = this.bipedBody.rotateAngleY;
        this.greenOxygenTanks[1].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.orangeOxygenTanks[0].rotateAngleX = this.bipedBody.rotateAngleX;
        this.orangeOxygenTanks[0].rotateAngleY = this.bipedBody.rotateAngleY;
        this.orangeOxygenTanks[0].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.orangeOxygenTanks[1].rotateAngleX = this.bipedBody.rotateAngleX;
        this.orangeOxygenTanks[1].rotateAngleY = this.bipedBody.rotateAngleY;
        this.orangeOxygenTanks[1].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.redOxygenTanks[0].rotateAngleX = this.bipedBody.rotateAngleX;
        this.redOxygenTanks[0].rotateAngleY = this.bipedBody.rotateAngleY;
        this.redOxygenTanks[0].rotateAngleZ = this.bipedBody.rotateAngleZ;
        this.redOxygenTanks[1].rotateAngleX = this.bipedBody.rotateAngleX;
        this.redOxygenTanks[1].rotateAngleY = this.bipedBody.rotateAngleY;
        this.redOxygenTanks[1].rotateAngleZ = this.bipedBody.rotateAngleZ;
        final List<?> l = (List<?>)player.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)player, AxisAlignedBB.getBoundingBox(player.posX - 20.0, 0.0, player.posZ - 20.0, player.posX + 20.0, 200.0, player.posZ + 20.0));
        for (int i = 0; i < l.size(); ++i) {
            final Entity e = (Entity)l.get(i);
            if (e instanceof EntityTieredRocket) {
                final EntityTieredRocket ship = (EntityTieredRocket)e;
                if (ship.riddenByEntity != null && !ship.riddenByEntity.equals((Object)player) && (ship.getLaunched() || ship.timeUntilLaunch < 390)) {
                    final ModelRenderer bipedRightArm7 = this.bipedRightArm;
                    bipedRightArm7.rotateAngleZ -= 0.3926991f + MathHelper.sin(par3 * 0.9f) * 0.2f;
                    this.bipedRightArm.rotateAngleX = 3.1415927f;
                    break;
                }
            }
        }
    }
    
    static {
        oxygenMaskTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/oxygen.png");
        playerTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/player.png");
        frequencyModuleTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/model/frequencyModule.png");
        ModelPlayerGC.crossbowModLoaded = false;
        ModelPlayerGC.crossbowModLoaded = Loader.isModLoaded("CrossbowMod2");
    }
}
