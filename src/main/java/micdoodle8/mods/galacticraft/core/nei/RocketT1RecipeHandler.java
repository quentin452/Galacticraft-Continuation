package micdoodle8.mods.galacticraft.core.nei;

import codechicken.nei.recipe.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import codechicken.lib.gui.*;
import net.minecraft.item.*;
import codechicken.nei.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.*;
import java.util.*;

public class RocketT1RecipeHandler extends TemplateRecipeHandler
{
    private static final ResourceLocation rocketGuiTexture;

    public String getRecipeId() {
        return "galacticraft.rocketT1";
    }

    public int recipiesPerPage() {
        return 1;
    }

    public Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getRecipes() {
        final HashMap<ArrayList<PositionedStack>, PositionedStack> recipes = new HashMap<ArrayList<PositionedStack>, PositionedStack>();
        for (final Map.Entry<HashMap<Integer, PositionedStack>, PositionedStack> stack : NEIGalacticraftConfig.getRocketBenchRecipes()) {
            final ArrayList<PositionedStack> inputStacks = new ArrayList<PositionedStack>();
            for (final Map.Entry<Integer, PositionedStack> input : stack.getKey().entrySet()) {
                inputStacks.add(input.getValue());
            }
            recipes.put(inputStacks, stack.getValue());
        }
        return recipes.entrySet();
    }

    public void drawBackground(final int recipe) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiDraw.changeTexture(RocketT1RecipeHandler.rocketGuiTexture);
        GuiDraw.drawTexturedModalRect(0, 0, 3, 4, 168, 130);
    }

    public void loadTransferRects() {
    }

    public void loadCraftingRecipes(final String outputId, final Object... results) {
        if (outputId.equals(this.getRecipeId())) {
            for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
                this.arecipes.add(new CachedRocketRecipe(irecipe.getKey(), irecipe.getValue()));
            }
        }
        else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadCraftingRecipes(final ItemStack result) {
        for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getValue().item, result)) {
                this.arecipes.add(new CachedRocketRecipe(irecipe.getKey(), irecipe.getValue()));
            }
        }
    }

    public void loadUsageRecipes(final ItemStack ingredient) {
        for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
            for (final PositionedStack pstack : irecipe.getKey()) {
                if (NEIServerUtils.areStacksSameTypeCrafting(ingredient, pstack.item)) {
                    this.arecipes.add(new CachedRocketRecipe(irecipe.getKey(), irecipe.getValue()));
                    break;
                }
            }
        }
    }

    public String getRecipeName() {
        return GCCoreUtil.translate("tile.rocketWorkbench.name");
    }

    public String getGuiTexture() {
        return GalacticraftCore.TEXTURE_PREFIX + "textures/gui/rocketbench.png";
    }

    public void drawForeground(final int recipe) {
    }

    static {
        rocketGuiTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/rocketbench.png");
    }

    public class CachedRocketRecipe extends TemplateRecipeHandler.CachedRecipe
    {
        public ArrayList<PositionedStack> input;
        public PositionedStack output;

        public ArrayList<PositionedStack> getIngredients() {
            return this.input;
        }

        public PositionedStack getResult() {
            return this.output;
        }

        public CachedRocketRecipe(final ArrayList<PositionedStack> pstack1, final PositionedStack pstack2) {
            super();
            this.input = pstack1;
            this.output = pstack2;
        }

        public CachedRocketRecipe(final RocketT1RecipeHandler this$0, final Map.Entry<ArrayList<PositionedStack>, PositionedStack> recipe) {
            this(recipe.getKey(), recipe.getValue());
        }
    }
}
