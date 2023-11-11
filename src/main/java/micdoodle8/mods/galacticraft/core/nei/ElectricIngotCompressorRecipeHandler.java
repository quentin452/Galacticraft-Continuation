package micdoodle8.mods.galacticraft.core.nei;

import codechicken.nei.recipe.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import codechicken.lib.gui.*;
import codechicken.nei.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.*;
import java.util.*;

public class ElectricIngotCompressorRecipeHandler extends TemplateRecipeHandler
{
    private static final ResourceLocation ingotCompressorTexture;
    public static int ticksPassed;
    private int alternate;

    public ElectricIngotCompressorRecipeHandler() {
        this.alternate = 0;
    }

    public String getRecipeId() {
        return "galacticraft.electricingotcompressor";
    }

    public int recipiesPerPage() {
        return 1;
    }

    public Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getRecipes() {
        final HashMap<ArrayList<PositionedStack>, PositionedStack> recipes = new HashMap<ArrayList<PositionedStack>, PositionedStack>();
        for (final Map.Entry<HashMap<Integer, PositionedStack>, PositionedStack> stack : NEIGalacticraftConfig.getIngotCompressorRecipes()) {
            final ArrayList<PositionedStack> inputStacks = new ArrayList<PositionedStack>();
            for (final Map.Entry<Integer, PositionedStack> input : stack.getKey().entrySet()) {
                final PositionedStack inputStack = input.getValue().copy();
                for (final ItemStack inputItemStack : inputStack.items) {
                    inputItemStack.stackSize = 2;
                }
                inputStacks.add(inputStack);
            }
            recipes.put(inputStacks, stack.getValue());
        }
        return recipes.entrySet();
    }

    public void drawBackground(final int recipe) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiDraw.changeTexture(ElectricIngotCompressorRecipeHandler.ingotCompressorTexture);
        GuiDraw.drawTexturedModalRect(20, 25, 18, 17, 137, 54);
        if (ElectricIngotCompressorRecipeHandler.ticksPassed % 70 > 26) {
            GuiDraw.drawTexturedModalRect(103, 38, 176, 0, 17, 13);
        }
        GuiDraw.drawTexturedModalRect(79, 46, 176, 13, Math.min(ElectricIngotCompressorRecipeHandler.ticksPassed % 70, 53), 17);
    }

    public void onUpdate() {
        ElectricIngotCompressorRecipeHandler.ticksPassed += 1 + this.alternate;
        this.alternate = 1 - this.alternate;
        super.onUpdate();
    }

    public void loadTransferRects() {
    }

    public void loadCraftingRecipes(final String outputId, final Object... results) {
        if (outputId.equals(this.getRecipeId())) {
            for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
                this.arecipes.add(new ElectricCompressorRecipe(irecipe.getKey(), irecipe.getValue()));
            }
        }
        else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadCraftingRecipes(final ItemStack result) {
        for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getValue().item, result)) {
                this.arecipes.add(new ElectricCompressorRecipe(irecipe.getKey(), irecipe.getValue()));
            }
        }
    }

    public void loadUsageRecipes(final ItemStack ingredient) {
        for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
            for (final PositionedStack pstack : irecipe.getKey()) {
                if (pstack.contains(ingredient)) {
                    this.arecipes.add(new ElectricCompressorRecipe(irecipe.getKey(), irecipe.getValue()));
                    break;
                }
            }
        }
    }

    public ArrayList<PositionedStack> getIngredientStacks(final int recipe) {
        return (ArrayList<PositionedStack>)this.arecipes.get(recipe).getIngredients();
    }

    public PositionedStack getResultStack(final int recipe) {
        if (ElectricIngotCompressorRecipeHandler.ticksPassed % 70 >= 53) {
            return this.arecipes.get(recipe).getResult();
        }
        return null;
    }

    public String getRecipeName() {
        return GCCoreUtil.translate("tile.machine2.4.name");
    }

    public String getGuiTexture() {
        return GalacticraftCore.TEXTURE_PREFIX + "textures/gui/electric_IngotCompressor.png";
    }

    public void drawForeground(final int recipe) {
    }

    static {
        ingotCompressorTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/electric_IngotCompressor.png");
    }

    public class ElectricCompressorRecipe extends TemplateRecipeHandler.CachedRecipe
    {
        public ArrayList<PositionedStack> input;
        public PositionedStack output;

        public ArrayList<PositionedStack> getIngredients() {
            return (ArrayList<PositionedStack>)this.getCycledIngredients(ElectricIngotCompressorRecipeHandler.this.cycleticks / 20, (List)this.input);
        }

        public PositionedStack getResult() {
            return this.output;
        }

        public ElectricCompressorRecipe(final ArrayList<PositionedStack> pstack1, final PositionedStack pstack2) {
            super();
            final ArrayList<PositionedStack> ingred = new ArrayList<PositionedStack>();
            for (final PositionedStack stack : pstack1) {
                final PositionedStack stack2 = stack.copy();
                ingred.add(stack2);
            }
            this.input = ingred;
            pstack2.rely -= 8;
            this.output = pstack2;
        }

        public ElectricCompressorRecipe(final ElectricIngotCompressorRecipeHandler this$0, final Map.Entry<ArrayList<PositionedStack>, PositionedStack> recipe) {
            this(new ArrayList<PositionedStack>(recipe.getKey()), recipe.getValue().copy());
        }

        public PositionedStack getOtherStack() {
            if (ElectricIngotCompressorRecipeHandler.ticksPassed % 70 >= 53) {
                final PositionedStack copy;
                final PositionedStack outputCopy = copy = this.output.copy();
                copy.rely += 18;
                return outputCopy;
            }
            return null;
        }
    }
}
