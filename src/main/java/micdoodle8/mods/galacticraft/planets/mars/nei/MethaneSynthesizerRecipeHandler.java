package micdoodle8.mods.galacticraft.planets.mars.nei;

import net.minecraft.util.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import codechicken.lib.gui.*;
import micdoodle8.mods.galacticraft.core.util.*;
import codechicken.nei.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import micdoodle8.mods.galacticraft.planets.mars.items.*;
import net.minecraft.item.*;
import codechicken.nei.recipe.*;
import java.util.*;
import net.minecraft.client.gui.inventory.*;
import java.awt.*;
import java.util.List;

public class MethaneSynthesizerRecipeHandler extends TemplateRecipeHandler
{
    private static final ResourceLocation synthesizerGuiTexture;
    private static final ResourceLocation synthesizerGasesTexture;
    int ticksPassed;
    int extra;
    boolean fillAtmos;
    protected FontRenderer fontRendererObj;

    public MethaneSynthesizerRecipeHandler() {
        this.extra = 0;
        this.fillAtmos = false;
        this.fontRendererObj = Minecraft.getMinecraft().fontRenderer;
    }

    public String getRecipeId() {
        return "galacticraft.synthesizer";
    }

    public int recipiesPerPage() {
        return 1;
    }

    public Set<Map.Entry<PositionedStack, PositionedStack>> getRecipes() {
        return NEIGalacticraftMarsConfig.getSynthesizerRecipes();
    }

    public void drawBackground(final int recipe) {
        final int progress = this.ticksPassed % 144;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiDraw.changeTexture(MethaneSynthesizerRecipeHandler.synthesizerGuiTexture);
        GuiDraw.drawTexturedModalRect(-2, 0, 3, 4, 168, 66);
        if (progress <= 40) {
            final int level = progress * 38 / 40;
            GuiDraw.changeTexture(MethaneSynthesizerRecipeHandler.synthesizerGasesTexture);
            GuiDraw.drawTexturedModalRect(2, 62 - level, 35, 38 - level, 16, level);
            if (this.fillAtmos) {
                final int yoffset = progress / 2;
                GuiDraw.drawTexturedModalRect(23, 44 - yoffset, 35, 26 - yoffset, 16, yoffset);
            }
        }
        else if (progress < 104) {
            final int level = (progress - 41) / 3;
            final int yoffset = 20 - level;
            GuiDraw.changeTexture(MethaneSynthesizerRecipeHandler.synthesizerGasesTexture);
            GuiDraw.drawTexturedModalRect(2, 62 - yoffset, 35, 26 - yoffset, 16, yoffset);
            if (this.fillAtmos) {
                GuiDraw.drawTexturedModalRect(23, 44 - yoffset, 35, 26 - yoffset, 16, yoffset);
            }
            GuiDraw.drawTexturedModalRect(148, 62 - level, 1, 26 - level, 16, level);
            final int powerlevel = 53 - (progress - 41) / 6;
            GuiDraw.changeTexture(MethaneSynthesizerRecipeHandler.synthesizerGuiTexture);
            GuiDraw.drawTexturedModalRect(61, 13, 176, 38, powerlevel, 7);
            GuiDraw.drawTexturedModalRect(47, 12, 208, 0, 11, 10);
        }
        if (this.fillAtmos) {
            final String gasname = GCCoreUtil.translate("gas.carbondioxide.name");
            final String text1 = " * " + GCCoreUtil.translate("gui.message.withAtmosphere0.name");
            final String text2 = " " + GCCoreUtil.lowerCaseNoun(gasname);
            final String text3 = GCCoreUtil.translate("gui.message.withAtmosphere1.name");
            this.fontRendererObj.drawString(text1, 4, 85, 4210752);
            this.fontRendererObj.drawString(text2, 18, 95, 4210752);
            this.fontRendererObj.drawString(text3, 18, 105, 4210752);
        }
    }

    public void onUpdate() {
        this.ticksPassed += 1 + this.extra;
        super.onUpdate();
    }

    public void loadTransferRects() {
    }

    public void loadCraftingRecipes(final String outputId, final Object... results) {
        if (outputId.equals(this.getRecipeId())) {
            for (final Map.Entry<PositionedStack, PositionedStack> irecipe : this.getRecipes()) {
                this.arecipes.add(new CachedSynthesizerRecipe(irecipe.getKey(), irecipe.getValue()));
            }
        }
        else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadCraftingRecipes(final ItemStack result) {
        for (final Map.Entry<PositionedStack, PositionedStack> irecipe : this.getRecipes()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getValue().item, result)) {
                this.arecipes.add(new CachedSynthesizerRecipe(irecipe.getKey(), irecipe.getValue()));
            }
        }
    }

    public void loadUsageRecipes(final ItemStack ingredient) {
        for (final Map.Entry<PositionedStack, PositionedStack> irecipe : this.getRecipes()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(ingredient, irecipe.getKey().item)) {
                this.arecipes.add(new CachedSynthesizerRecipe(irecipe.getKey(), irecipe.getValue()));
                break;
            }
        }
    }

    public ArrayList<PositionedStack> getIngredientStacks(final int recipe) {
        final PositionedStack input = this.arecipes.get(recipe).getIngredients().get(0);
        final Item inputItem = input.item.getItem();
        if (inputItem == AsteroidsItems.atmosphericValve) {
            this.fillAtmos = true;
        }
        else {
            this.fillAtmos = false;
        }
        if (this.ticksPassed % 144 > 40) {
            final ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
            if (inputItem != MarsItems.carbonFragments) {
                stacks.add(new PositionedStack((Object)new ItemStack(inputItem, 1, inputItem.getMaxDamage()), input.relx, input.rely));
            }
            else if (this.ticksPassed % 144 < 104) {
                final int number = 24 - (this.ticksPassed % 144 - 40) * 3 / 8;
                stacks.add(new PositionedStack((Object)new ItemStack(inputItem, number, 0), input.relx, input.rely));
            }
            return stacks;
        }
        return (ArrayList<PositionedStack>)this.arecipes.get(recipe).getIngredients();
    }

    public PositionedStack getResultStack(final int recipe) {
        if (this.ticksPassed % 144 < 104) {
            final PositionedStack output = this.arecipes.get(recipe).getResult();
            final Item outputItem = output.item.getItem();
            return new PositionedStack((Object)new ItemStack(outputItem, 1, outputItem.getMaxDamage()), output.relx, output.rely);
        }
        return this.arecipes.get(recipe).getResult();
    }

    public String getRecipeName() {
        return GCCoreUtil.translate("tile.marsMachine.5.name");
    }

    public String getGuiTexture() {
        return "galacticraftmars/textures/gui/methaneSynthesizer.png";
    }

    public void drawForeground(final int recipe) {
    }

    public List<String> handleTooltip(final GuiRecipe<?> gui, final List<String> currenttip, final int recipe) {
        final Point mousePos = GuiDraw.getMousePosition();
        try {
            final Class<GuiContainer> clazz = GuiContainer.class;
            final Point point = mousePos;
            point.x -= (int)clazz.getField("guiLeft").get(gui);
            final Point point2 = mousePos;
            point2.y -= (int)clazz.getField("guiTop").get(gui);
        }
        catch (Exception ee) {
            ee.printStackTrace();
        }
        if (mousePos.x < 23 && mousePos.x > 6 && mousePos.y < 78 && mousePos.y > 39) {
            currenttip.add(GCCoreUtil.translate("fluid.hydrogen"));
        }
        else if (mousePos.x < 44 && mousePos.x > 27 && mousePos.y < 60 && mousePos.y > 39) {
            currenttip.add(GCCoreUtil.translate("gas.carbondioxide.name"));
        }
        return currenttip;
    }

    static {
        synthesizerGuiTexture = new ResourceLocation("galacticraftmars", "textures/gui/methaneSynthesizer.png");
        synthesizerGasesTexture = new ResourceLocation("galacticraftasteroids", "textures/gui/gasesMethaneOxygenNitrogen.png");
    }

    public class CachedSynthesizerRecipe extends TemplateRecipeHandler.CachedRecipe
    {
        public PositionedStack input;
        public PositionedStack output;

        public PositionedStack getIngredient() {
            return this.input;
        }

        public PositionedStack getResult() {
            return this.output;
        }

        public CachedSynthesizerRecipe(final PositionedStack pstack1, final PositionedStack pstack2) {
            super();
            this.input = pstack1;
            this.output = pstack2;
        }
    }
}
