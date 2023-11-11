package micdoodle8.mods.galacticraft.core.nei;

import net.minecraft.util.*;
import codechicken.nei.recipe.*;
import org.lwjgl.opengl.*;
import codechicken.lib.gui.*;
import net.minecraft.init.*;
import net.minecraft.block.*;
import codechicken.nei.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import micdoodle8.mods.galacticraft.core.util.*;
import micdoodle8.mods.galacticraft.core.*;
import java.util.*;

public class IngotCompressorRecipeHandler extends TemplateRecipeHandler
{
    private static final ResourceLocation ingotCompressorTexture;
    private static int ticksPassed;
    public static ArrayList<FurnaceRecipeHandler.FuelPair> afuels;
    public static TreeSet<Integer> efuels;

    public String getRecipeId() {
        return "galacticraft.ingotcompressor";
    }

    public int recipiesPerPage() {
        return 1;
    }

    public Set<Map.Entry<ArrayList<PositionedStack>, PositionedStack>> getRecipes() {
        final HashMap<ArrayList<PositionedStack>, PositionedStack> recipes = new HashMap<ArrayList<PositionedStack>, PositionedStack>();
        for (final Map.Entry<HashMap<Integer, PositionedStack>, PositionedStack> stack : NEIGalacticraftConfig.getIngotCompressorRecipes()) {
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
        GuiDraw.changeTexture(IngotCompressorRecipeHandler.ingotCompressorTexture);
        GuiDraw.drawTexturedModalRect(20, 25, 18, 17, 137, 78);
        if (IngotCompressorRecipeHandler.ticksPassed % 70 > 26) {
            GuiDraw.drawTexturedModalRect(103, 36, 176, 0, 17, 13);
        }
        GuiDraw.drawTexturedModalRect(79, 44, 176, 13, Math.min(IngotCompressorRecipeHandler.ticksPassed % 70, 53), 17);
        final int yOffset = (int)Math.floor(IngotCompressorRecipeHandler.ticksPassed % 48 * 0.2916666666666667);
        GuiDraw.drawTexturedModalRect(83, 35 + yOffset, 176, 30 + yOffset, 14, 14 - yOffset);
    }

    public void onUpdate() {
        ++IngotCompressorRecipeHandler.ticksPassed;
        super.onUpdate();
    }

    public void loadTransferRects() {
    }

    public void loadCraftingRecipes(final String outputId, final Object... results) {
        if (outputId.equals(this.getRecipeId())) {
            for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
                this.arecipes.add(new CompressorRecipe(irecipe.getKey(), irecipe.getValue()));
            }
        }
        else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    public void loadCraftingRecipes(final ItemStack result) {
        for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getValue().item, result)) {
                this.arecipes.add(new CompressorRecipe(irecipe.getKey(), irecipe.getValue()));
            }
        }
    }

    public void loadUsageRecipes(final ItemStack ingredient) {
        for (final Map.Entry<ArrayList<PositionedStack>, PositionedStack> irecipe : this.getRecipes()) {
            for (final PositionedStack pstack : irecipe.getKey()) {
                if (pstack.contains(ingredient)) {
                    this.arecipes.add(new CompressorRecipe(irecipe.getKey(), irecipe.getValue()));
                    break;
                }
            }
        }
    }

    public TemplateRecipeHandler newInstance() {
        if (IngotCompressorRecipeHandler.afuels == null) {
            findFuels();
        }
        return super.newInstance();
    }

    public ArrayList<PositionedStack> getIngredientStacks(final int recipe) {
        return (ArrayList<PositionedStack>)this.arecipes.get(recipe).getIngredients();
    }

    public PositionedStack getResultStack(final int recipe) {
        if (IngotCompressorRecipeHandler.ticksPassed % 70 >= 53) {
            return this.arecipes.get(recipe).getResult();
        }
        return null;
    }

    private static void removeFuels() {
        (IngotCompressorRecipeHandler.efuels = new TreeSet<Integer>()).add(Block.getIdFromBlock((Block)Blocks.brown_mushroom));
        IngotCompressorRecipeHandler.efuels.add(Block.getIdFromBlock(Blocks.brown_mushroom_block));
        IngotCompressorRecipeHandler.efuels.add(Block.getIdFromBlock(Blocks.wall_sign));
        IngotCompressorRecipeHandler.efuels.add(Block.getIdFromBlock(Blocks.standing_sign));
        IngotCompressorRecipeHandler.efuels.add(Block.getIdFromBlock(Blocks.wooden_door));
        IngotCompressorRecipeHandler.efuels.add(Block.getIdFromBlock(Blocks.trapped_chest));
    }

    private static void findFuels() {
        IngotCompressorRecipeHandler.afuels = new ArrayList<FurnaceRecipeHandler.FuelPair>();
        for (final ItemStack item : ItemList.items) {
            if (!IngotCompressorRecipeHandler.efuels.contains(Item.getIdFromItem(item.getItem()))) {
                final int burnTime = TileEntityFurnace.getItemBurnTime(item);
                if (burnTime <= 0) {
                    continue;
                }
                final FurnaceRecipeHandler.FuelPair fuelPair = new FurnaceRecipeHandler.FuelPair(item.copy(), burnTime);
                fuelPair.stack.relx = 57;
                fuelPair.stack.rely = 83;
                IngotCompressorRecipeHandler.afuels.add(fuelPair);
            }
        }
    }

    public String getRecipeName() {
        return GCCoreUtil.translate("tile.machine.3.name");
    }

    public String getGuiTexture() {
        return GalacticraftCore.TEXTURE_PREFIX + "textures/gui/ingotCompressor.png";
    }

    public void drawForeground(final int recipe) {
    }

    static {
        ingotCompressorTexture = new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/ingotCompressor.png");
        removeFuels();
    }

    public class CompressorRecipe extends TemplateRecipeHandler.CachedRecipe
    {
        public ArrayList<PositionedStack> input;
        public PositionedStack output;

        public ArrayList<PositionedStack> getIngredients() {
            return (ArrayList<PositionedStack>)this.getCycledIngredients(IngotCompressorRecipeHandler.this.cycleticks / 20, (List)this.input);
        }

        public PositionedStack getResult() {
            return this.output;
        }

        public CompressorRecipe(final ArrayList<PositionedStack> pstack1, final PositionedStack pstack2) {
            super();
            this.input = pstack1;
            this.output = pstack2;
        }

        public CompressorRecipe(final IngotCompressorRecipeHandler this$0, final Map.Entry<ArrayList<PositionedStack>, PositionedStack> recipe) {
            this(new ArrayList<PositionedStack>(recipe.getKey()), recipe.getValue().copy());
        }

        public List<PositionedStack> getOtherStacks() {
            final ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
            final PositionedStack stack = this.getOtherStack();
            if (stack != null) {
                stacks.add(stack);
            }
            return stacks;
        }

        public PositionedStack getOtherStack() {
            return IngotCompressorRecipeHandler.afuels.get(IngotCompressorRecipeHandler.ticksPassed / 48 % IngotCompressorRecipeHandler.afuels.size()).stack;
        }
    }
}
