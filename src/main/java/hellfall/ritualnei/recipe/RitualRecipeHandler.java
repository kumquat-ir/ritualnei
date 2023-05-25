package hellfall.ritualnei.recipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import com.emoniph.witchery.Witchery;
import com.emoniph.witchery.ritual.RitualTraits;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import hellfall.ritualnei.recipe.RitualRecipes.RitualRecipe;
import hellfall.ritualnei.recipe.RitualRecipes.RitualRecipe.CircleType;

public class RitualRecipeHandler extends TemplateRecipeHandler {

    private static final ResourceLocation CIRCLE_WHITE_SMALL = new ResourceLocation(
        "witchery:textures/gui/circle_white_small.png");
    private static final ResourceLocation CIRCLE_WHITE_MEDIUM = new ResourceLocation(
        "witchery:textures/gui/circle_white_medium.png");
    private static final ResourceLocation CIRCLE_WHITE_LARGE = new ResourceLocation(
        "witchery:textures/gui/circle_white_large.png");
    private static final ResourceLocation CIRCLE_RED_SMALL = new ResourceLocation(
        "witchery:textures/gui/circle_red_small.png");
    private static final ResourceLocation CIRCLE_RED_MEDIUM = new ResourceLocation(
        "witchery:textures/gui/circle_red_medium.png");
    private static final ResourceLocation CIRCLE_RED_LARGE = new ResourceLocation(
        "witchery:textures/gui/circle_red_large.png");
    private static final ResourceLocation CIRCLE_BLUE_SMALL = new ResourceLocation(
        "witchery:textures/gui/circle_blue_small.png");
    private static final ResourceLocation CIRCLE_BLUE_MEDIUM = new ResourceLocation(
        "witchery:textures/gui/circle_blue_medium.png");
    private static final ResourceLocation CIRCLE_BLUE_LARGE = new ResourceLocation(
        "witchery:textures/gui/circle_blue_large.png");
    private static final ResourceLocation SLOT = new ResourceLocation("textures/gui/container/stats_icons.png");

    public class CachedRitualRecipe extends CachedRecipe {

        public RitualRecipe recipe;

        public CachedRitualRecipe(RitualRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public PositionedStack getResult() {
            return null;
        }

        @Override
        public List<PositionedStack> getIngredients() {
            List<PositionedStack> ingredients = new ArrayList<>();

            int offset = 0;
            if (recipe.items != null) {
                for (ItemStack stack : recipe.items) {
                    ingredients.add(new PositionedStack(stack, 20 * offset + 3, 19));
                    offset++;
                }
                offset = 0;
            }
            if (recipe.optionalItems != null) {
                for (ItemStack stack : recipe.optionalItems) {
                    ingredients.add(new PositionedStack(stack, 20 * offset + 3, 48));
                    offset++;
                }
            }

            return ingredients;
        }
    }

    @Override
    public void loadCraftingRecipes(String inputId, Object... ingredients) {
        if (inputId.equals("ritualnei.allrituals")) {
            for (RitualRecipe recipe : RitualRecipes.generateRecipes(false)) {
                arecipes.add(new CachedRitualRecipe(recipe));
            }
        } else {
            super.loadCraftingRecipes(inputId, ingredients);
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (RitualRecipe recipe : RitualRecipes.generateRecipes(false)) {
            // check if the item can represent some type of circle:
            // chalk matches any rituals with any circles drawn with it (golden chalk matches all)
            // non-empty talismans match any rituals that take exactly the circles contained within
            // rituals that take any circles match only golden chalk but every non-empty talisman
            if (NEIClientUtils.areStacksSameType(ingredient, new ItemStack(Witchery.Items.CHALK_GOLDEN))
                || NEIClientUtils.areStacksSameType(ingredient, new ItemStack(Witchery.Items.CHALK_RITUAL))
                    && recipe.hasCircleOf(CircleType.RITUAL)
                || NEIClientUtils.areStacksSameType(ingredient, new ItemStack(Witchery.Items.CHALK_OTHERWHERE))
                    && recipe.hasCircleOf(CircleType.OTHERWHERE)
                || NEIClientUtils.areStacksSameType(ingredient, new ItemStack(Witchery.Items.CHALK_INFERNAL))
                    && recipe.hasCircleOf(CircleType.INFERNAL)
                || (ingredient != null && ingredient.getItem() == Witchery.Items.CIRCLE_TALISMAN)
                    && recipe.talismanRepresentsCircle(ingredient)) {
                arecipes.add(new CachedRitualRecipe(recipe));
                continue;
            }

            for (ItemStack stack : recipe.items) {
                if (NEIClientUtils.areStacksSameType(ingredient, stack)) {
                    arecipes.add(new CachedRitualRecipe(recipe));
                    break;
                }
            }
        }
    }

    @Override
    public void drawBackground(int recipeIndex) {
        super.drawBackground(recipeIndex);
        CachedRitualRecipe recipe = (CachedRitualRecipe) arecipes.get(recipeIndex);

        if (recipe.recipe.items != null) {
            for (int i = 0; i < recipe.recipe.items.length; i++) {
                drawSlot(20 * i + 2, 18);
            }
        }
        if (recipe.recipe.optionalItems != null) {
            for (int i = 0; i < recipe.recipe.optionalItems.length; i++) {
                drawSlot(20 * i + 2, 47);
            }
        }
    }

    @Override
    public void drawExtras(int recipeIndex) {
        CachedRitualRecipe recipe = (CachedRitualRecipe) arecipes.get(recipeIndex);
        String ritualName = StatCollector.translateToLocal(recipe.recipe.name);
        // the lang values here are for witchery's books, so they contain descriptions in them. strip those out.
        String realRitualName = ritualName.substring(0, ritualName.indexOf("{r"));

        drawCircles(recipe.recipe.circles);
        GuiDraw.drawStringC(realRitualName, 166 / 2, 0, 0xFF000000, false);
        if (recipe.recipe.items != null) {
            GuiDraw.drawString(StatCollector.translateToLocal("ritualnei.requireditems"), 2, 9, 0xFF000000, false);
        }
        if (recipe.recipe.optionalItems != null) {
            GuiDraw.drawString(StatCollector.translateToLocal("ritualnei.optionalitems"), 2, 38, 0xFF000000, false);
        }
        if (recipe.recipe.power > 0) {
            GuiDraw.drawString(
                StatCollector.translateToLocalFormatted("ritualnei.power", (int) recipe.recipe.power),
                2,
                67,
                0xFF000000,
                false);
        }
        if (!recipe.recipe.traits.isEmpty()) {
            GuiDraw.drawString(StatCollector.translateToLocal("ritualnei.conditions"), 2, 78, 0xFF000000, false);
            int lines = 0;
            for (RitualTraits trait : recipe.recipe.traits) {
                if (lines > 1) break;
                GuiDraw.drawString(
                    StatCollector.translateToLocal(
                        "ritualnei.conditions." + trait.name()
                            .toLowerCase()),
                    2,
                    88 + 10 * lines,
                    0xFF000000,
                    false);
                lines++;
            }
        }
        if (recipe.recipe.livingSacrificeName != null) {
            GuiDraw.drawString(StatCollector.translateToLocal("ritualnei.sacrifice"), 2, 109, 0xFF000000, false);
            GuiDraw.drawString(
                StatCollector.translateToLocal("entity." + recipe.recipe.livingSacrificeName + ".name"),
                2,
                119,
                0xFF000000,
                false);
        }
    }

    private void drawSlot(int x, int y) {
        GuiDraw.changeTexture(SLOT);
        // because its not 256x256. why is this assumed everywhere
        Gui.func_146110_a(x, y, 0, 0, 18, 18, 128, 128);
    }

    private void drawCircles(CircleType[] circles) {
        if (circles[0] == CircleType.NONE && circles[1] == CircleType.NONE && circles[2] == CircleType.NONE) {
            // the right half of each type of circle, each a different size
            GuiDraw.changeTexture(CIRCLE_WHITE_SMALL);
            GuiDraw.drawTexturedModalRect(80 + 39, 43, 39, 0, 45, 84);
            GuiDraw.changeTexture(CIRCLE_BLUE_MEDIUM);
            GuiDraw.drawTexturedModalRect(80 + 39, 43, 39, 0, 45, 84);
            GuiDraw.changeTexture(CIRCLE_RED_LARGE);
            GuiDraw.drawTexturedModalRect(80 + 39, 43, 39, 0, 45, 84);
            GuiDraw.drawStringC(StatCollector.translateToLocal("ritualnei.anycircles"), 100, 81, 0xFF000000, false);
            return;
        }
        switch (circles[0]) {
            case RITUAL -> drawCircle(CIRCLE_WHITE_SMALL);
            case INFERNAL -> drawCircle(CIRCLE_RED_SMALL);
            case OTHERWHERE -> drawCircle(CIRCLE_BLUE_SMALL);
        }
        switch (circles[1]) {
            case RITUAL -> drawCircle(CIRCLE_WHITE_MEDIUM);
            case INFERNAL -> drawCircle(CIRCLE_RED_MEDIUM);
            case OTHERWHERE -> drawCircle(CIRCLE_BLUE_MEDIUM);
        }
        switch (circles[2]) {
            case RITUAL -> drawCircle(CIRCLE_WHITE_LARGE);
            case INFERNAL -> drawCircle(CIRCLE_RED_LARGE);
            case OTHERWHERE -> drawCircle(CIRCLE_BLUE_LARGE);
        }
    }

    private void drawCircle(ResourceLocation circle) {
        GuiDraw.changeTexture(circle);
        GuiDraw.drawTexturedModalRect(80, 43, 0, 0, 84, 84);
    }

    @Override
    public String getGuiTexture() {
        return "nei:textures/gui/recipebg.png";
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("ritualnei.title");
    }

    @Override
    public String getOverlayIdentifier() {
        return "ritualnei.ritual";
    }

    @Override
    public String getHandlerId() {
        return "ritualnei.ritual";
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(103, 66, 38, 38), "ritualnei.allrituals"));
    }

    @Override
    public int recipiesPerPage() {
        // yes, this is different than the value provided by IMC.
        // GTNH NEI prioritizes IMC, and 2 is the correct value for it
        // base NEI doesnt know about the IMC, and 1 is the correct value for it
        return 1;
    }
}
