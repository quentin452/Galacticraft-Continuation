package micdoodle8.mods.galacticraft.planets.mars.client.render.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import micdoodle8.mods.galacticraft.planets.mars.MarsModule;
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars;

public class ItemRendererMachine implements IItemRenderer {

    private static final ResourceLocation chamberTexture0 = new ResourceLocation(
        MarsModule.ASSET_PREFIX,
        "textures/model/chamber_dark.png");

    private final IModelCustom model;

    public ItemRendererMachine(IModelCustom model) {
        this.model = model;
    }

    private void renderCryogenicChamber(ItemRenderType type) {
        GL11.glPushMatrix();

        this.transform(type);

        FMLClientHandler.instance()
            .getClient().renderEngine.bindTexture(ItemRendererMachine.chamberTexture0);
        this.model.renderPart("Main_Cylinder");

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0.1F, 0.6F, 0.5F, 0.4F);

        this.model.renderPart("Shield_Torus");

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    public void transform(ItemRenderType type) {
        if (type == ItemRenderType.EQUIPPED) {
            GL11.glRotatef(70, 1.0F, 0, 0);
            GL11.glRotatef(-10, 0.0F, 1, 0);
            GL11.glRotatef(50, 0.0F, 1, 1);
            GL11.glScalef(3.8F, 4.1F, 3.8F);
            GL11.glTranslatef(0.25F, 1.2F, 0F);
        }

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(0.0F, -0.9F, 0.0F);
            GL11.glRotatef(0, 0, 0, 1);
            GL11.glRotatef(45, 0, 1, 0);
            GL11.glRotatef(90, 1, 0, 0);
            GL11.glTranslatef(5.5F, 7.0F, -8.5F);
            GL11.glScalef(6.2F, 8.2F, 6.2F);
        }

        GL11.glScalef(-0.4F, -0.4F, 0.4F);

        if (type == ItemRenderType.INVENTORY || type == ItemRenderType.ENTITY) {
            if (type == ItemRenderType.INVENTORY) {
                GL11.glTranslatef(0, -1.9F, 0);
                GL11.glScalef(0.7F, 0.6F, 0.7F);
                GL11.glRotatef(225F, 0F, 1F, 0F);
            } else {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glTranslatef(0, -3.9F, 0);
                GL11.glDisable(GL11.GL_BLEND);
            }

            GL11.glScalef(1.3F, 1.3F, 1.3F);
        }
    }

    /**
     * IItemRenderer implementation *
     */
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (item.getItemDamage() < BlockMachineMars.CRYOGENIC_CHAMBER_METADATA
            || item.getItemDamage() >= BlockMachineMars.LAUNCH_CONTROLLER_METADATA) {
            return false;
        }

        boolean result;
        switch (type) {
            case ENTITY:
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
            case INVENTORY:
                result = true;
                break;
            default:
                result = false;
                break;
        }

        return result;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (item.getItemDamage() >= BlockMachineMars.CRYOGENIC_CHAMBER_METADATA
            && item.getItemDamage() < BlockMachineMars.LAUNCH_CONTROLLER_METADATA) {
            switch (type) {
                case EQUIPPED:
                    this.renderCryogenicChamber(type);
                    break;
                case EQUIPPED_FIRST_PERSON:
                    this.renderCryogenicChamber(type);
                    break;
                case INVENTORY:
                    this.renderCryogenicChamber(type);
                    break;
                case ENTITY:
                    this.renderCryogenicChamber(type);
                    break;
                default:
                    break;
            }
        }
    }
}
