package micdoodle8.mods.galacticraft.planets.mars.nei;

import codechicken.nei.recipe.*;
import net.minecraft.util.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import codechicken.lib.gui.*;
import micdoodle8.mods.galacticraft.core.util.*;
import codechicken.nei.*;
import micdoodle8.mods.galacticraft.planets.asteroids.items.*;
import net.minecraft.item.*;
import micdoodle8.mods.galacticraft.core.items.*;
import java.util.*;

public class GasLiquefierRecipeHandler extends TemplateRecipeHandler
{
    private static final ResourceLocation liquefierGuiTexture;
    private static final ResourceLocation liquefierGasesTexture;
    int ticksPassed;
    int extra;
    int inputGas;
    int outputGas;
    boolean fillAtmos;
    protected FontRenderer fontRendererObj;

    public GasLiquefierRecipeHandler() {
        this.extra = 0;
        this.inputGas = 0;
        this.outputGas = 0;
        this.fillAtmos = false;
        this.fontRendererObj = Minecraft.getMinecraft().fontRenderer;
    }

    public String getRecipeId() {
        return "galacticraft.liquefier";
    }

    public int recipiesPerPage() {
        return 1;
    }

    public Set<Map.Entry<PositionedStack, PositionedStack>> getRecipes() {
        return NEIGalacticraftMarsConfig.getLiquefierRecipes();
    }

    public void drawBackground(final int recipe) {
        final int progress = this.ticksPassed % 144;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiDraw.changeTexture(GasLiquefierRecipeHandler.liquefierGuiTexture);
        GuiDraw.drawTexturedModalRect(-2, 0, 3, 4, 168, 64);
        if (progress <= 40) {
            if (this.fillAtmos) {
                final int yoffset = progress / 3;
                GuiDraw.changeTexture(GasLiquefierRecipeHandler.liquefierGasesTexture);
                GuiDraw.drawTexturedModalRect(2, 62 - yoffset, 1 + this.inputGas * 17, 26 - yoffset, 16, yoffset);
            }
            else if (this.inputGas > 0) {
                GuiDraw.changeTexture(GasLiquefierRecipeHandler.liquefierGasesTexture);
                GuiDraw.drawTexturedModalRect(2, 52, 1 + this.inputGas * 17, 16, 16, 10);
            }
        }
        else if (progress < 104) {
            final int level = (progress - 41) / 3;
            int yoffset2 = 20 - level;
            if (this.fillAtmos) {
                yoffset2 = 13 + level / 3;
            }
            GuiDraw.changeTexture(GasLiquefierRecipeHandler.liquefierGasesTexture);
            GuiDraw.drawTexturedModalRect(2, 62 - yoffset2, 1 + this.inputGas * 17, 26 - yoffset2, 16, yoffset2);
            if (this.outputGas == 0) {
                GuiDraw.changeTexture(GasLiquefierRecipeHandler.liquefierGuiTexture);
                GuiDraw.drawTexturedModalRect(127, 62 - level, 192, 26 - level, 16, level);
            }
            else {
                GuiDraw.drawTexturedModalRect(127 + ((this.outputGas == 3) ? 21 : 0), 62 - level, 1 + this.outputGas * 17, 26 - level, 16, level);
                GuiDraw.changeTexture(GasLiquefierRecipeHandler.liquefierGuiTexture);
            }
            final int powerlevel = 53 - (progress - 41) / 6;
            GuiDraw.drawTexturedModalRect(37, 13, 176, 38, powerlevel, 7);
            GuiDraw.drawTexturedModalRect(23, 12, 208, 0, 11, 10);
        }
        if (this.fillAtmos) {
            final String gasname = (this.outputGas == 3) ? GCCoreUtil.translate("gas.oxygen.name") : GCCoreUtil.translate("gas.nitrogen.name");
            final String text1 = " * " + GCCoreUtil.translate("gui.message.withAtmosphere0.name");
            final String text2 = GCCoreUtil.lowerCaseNoun(gasname) + " " + GCCoreUtil.translate("gui.message.withAtmosphere1.name");
            this.fontRendererObj.drawString(text1, 4, 83, 4210752);
            this.fontRendererObj.drawString(text2, 4, 93, 4210752);
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
                this.arecipes.add(new CachedLiquefierRecipe(irecipe.getKey(), irecipe.getValue()));
            }
        }
        else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadCraftingRecipes(final ItemStack result) {
        for (final Map.Entry<PositionedStack, PositionedStack> irecipe : this.getRecipes()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getValue().item, result)) {
                this.arecipes.add(new CachedLiquefierRecipe(irecipe.getKey(), irecipe.getValue()));
            }
        }
    }

    public void loadUsageRecipes(final ItemStack ingredient) {
        for (final Map.Entry<PositionedStack, PositionedStack> irecipe : this.getRecipes()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(ingredient, irecipe.getKey().item)) {
                this.arecipes.add(new CachedLiquefierRecipe(irecipe.getKey(), irecipe.getValue()));
                break;
            }
        }
    }

    public ArrayList<PositionedStack> getIngredientStacks(final int recipe) {
        final PositionedStack input = this.arecipes.get(recipe).getIngredients().get(0);
        final Item inputItem = input.item.getItem();
        this.inputGas = 2;
        this.fillAtmos = false;
        if (inputItem == AsteroidsItems.methaneCanister) {
            this.inputGas = 0;
        }
        else if (inputItem == AsteroidsItems.canisterLOX) {
            this.inputGas = 1;
        }
        else if (inputItem == AsteroidsItems.atmosphericValve) {
            this.fillAtmos = true;
        }
        if (this.ticksPassed % 144 > 40) {
            final ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
            stacks.add(new PositionedStack((Object)new ItemStack(inputItem, 1, inputItem.getMaxDamage()), input.relx, input.rely));
            return stacks;
        }
        return (ArrayList<PositionedStack>)this.arecipes.get(recipe).getIngredients();
    }

    public PositionedStack getResultStack(final int recipe) {
        final PositionedStack output = this.arecipes.get(recipe).getResult();
        final Item outputItem = output.item.getItem();
        if (outputItem == GCItems.fuelCanister) {
            this.outputGas = 0;
        }
        else if (outputItem == AsteroidsItems.canisterLOX) {
            this.outputGas = 3;
        }
        else {
            this.outputGas = 4;
        }
        if (this.ticksPassed % 144 < 104) {
            return new PositionedStack((Object)new ItemStack(outputItem, 1, outputItem.getMaxDamage()), output.relx, output.rely);
        }
        return this.arecipes.get(recipe).getResult();
    }

    public String getRecipeName() {
        return GCCoreUtil.translate("tile.marsMachine.4.name");
    }

    public String getGuiTexture() {
        return "galacticraftmarstextures/gui/gasLiquefier.png";
    }

    public void drawForeground(final int recipe) {
    }

    static {
        liquefierGuiTexture = new ResourceLocation("galacticraftmars", "textures/gui/gasLiquefier.png");
        liquefierGasesTexture = new ResourceLocation("galacticraftasteroids", "textures/gui/gasesMethaneOxygenNitrogen.png");
    }

    public class CachedLiquefierRecipe extends TemplateRecipeHandler.CachedRecipe
    {
        public PositionedStack input;
        public PositionedStack output;

        public PositionedStack getIngredient() {
            return this.input;
        }

        public PositionedStack getResult() {
            return this.output;
        }

        public CachedLiquefierRecipe(final PositionedStack pstack1, final PositionedStack pstack2) {
            super();
            this.input = pstack1;
            this.output = pstack2;
        }
    }
}
