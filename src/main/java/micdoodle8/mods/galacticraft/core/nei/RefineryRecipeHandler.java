package micdoodle8.mods.galacticraft.core.nei;

import codechicken.nei.recipe.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import codechicken.lib.gui.*;
import net.minecraft.item.*;
import codechicken.nei.*;
import micdoodle8.mods.galacticraft.core.items.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.*;
import java.util.*;

public class RefineryRecipeHandler extends TemplateRecipeHandler
{
    private static final ResourceLocation refineryGuiTexture;
    int ticksPassed;

    public String getRecipeId() {
        return "galacticraft.refinery";
    }

    public int recipiesPerPage() {
        return 2;
    }

    public Set<Map.Entry<PositionedStack, PositionedStack>> getRecipes() {
        return NEIGalacticraftConfig.getRefineryRecipes();
    }

    public void drawBackground(final int recipe) {
        final int progress = this.ticksPassed % 144;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiDraw.changeTexture(RefineryRecipeHandler.refineryGuiTexture);
        GuiDraw.drawTexturedModalRect(-2, 0, 3, 4, 168, 64);
        if (progress < 104 && progress > 40) {
            GuiDraw.drawTexturedModalRect(2, 42, 176, 6, 16, 20);
        }
        else if (progress < 124) {
            GuiDraw.drawTexturedModalRect(148, 42, 192, 6, 16, 20);
        }
        GuiDraw.drawTexturedModalRect(21, 21, 0, 186, progress, 20);
    }

    public void onUpdate() {
        this.ticksPassed += 2;
        super.onUpdate();
    }

    public void loadTransferRects() {
    }

    public void loadCraftingRecipes(final String outputId, final Object... results) {
        if (outputId.equals(this.getRecipeId())) {
            for (final Map.Entry<PositionedStack, PositionedStack> irecipe : this.getRecipes()) {
                this.arecipes.add(new CachedRefineryRecipe(irecipe.getKey(), irecipe.getValue()));
            }
        }
        else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadCraftingRecipes(final ItemStack result) {
        for (final Map.Entry<PositionedStack, PositionedStack> irecipe : this.getRecipes()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getValue().item, result)) {
                this.arecipes.add(new CachedRefineryRecipe(irecipe.getKey(), irecipe.getValue()));
            }
        }
    }

    public void loadUsageRecipes(final ItemStack ingredient) {
        for (final Map.Entry<PositionedStack, PositionedStack> irecipe : this.getRecipes()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(ingredient, irecipe.getKey().item)) {
                this.arecipes.add(new CachedRefineryRecipe(irecipe.getKey(), irecipe.getValue()));
                break;
            }
        }
    }

    public ArrayList<PositionedStack> getIngredientStacks(final int recipe) {
        if (this.ticksPassed % 144 > 20) {
            final ArrayList<PositionedStack> stacks = new ArrayList<>();
            stacks.add(new PositionedStack(new ItemStack(GCItems.oilCanister, 1, GCItems.oilCanister.getMaxDamage()), this.arecipes.get(recipe).getIngredients().get(0).relx, this.arecipes.get(recipe).getIngredients().get(0).rely));
            return stacks;
        }
        return (ArrayList<PositionedStack>)this.arecipes.get(recipe).getIngredients();
    }

    public PositionedStack getResultStack(final int recipe) {
        if (this.ticksPassed % 144 < 124) {
            return new PositionedStack(new ItemStack(GCItems.oilCanister, 1, GCItems.oilCanister.getMaxDamage()), this.arecipes.get(recipe).getResult().relx, this.arecipes.get(recipe).getResult().rely);
        }
        return this.arecipes.get(recipe).getResult();
    }

    public String getRecipeName() {
        return GCCoreUtil.translate("tile.refinery.name");
    }

    public String getGuiTexture() {
        return GalacticraftCore.TEXTURE_PREFIX + "textures/gui/refinery.png";
    }

    public void drawForeground(final int recipe) {
    }

    static {
        refineryGuiTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/refinery.png");
    }

    public class CachedRefineryRecipe extends TemplateRecipeHandler.CachedRecipe
    {
        public PositionedStack input;
        public PositionedStack output;

        public PositionedStack getIngredient() {
            return this.input;
        }

        public PositionedStack getResult() {
            return this.output;
        }

        public CachedRefineryRecipe(final PositionedStack pstack1, final PositionedStack pstack2) {
            super();
            this.input = pstack1;
            this.output = pstack2;
        }
    }
}
