package hellfall.ritualnei.recipe;

import java.util.*;

import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;

import com.emoniph.witchery.ritual.*;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class RitualRecipes {

    private static List<RitualRecipe> recipes = null;

    public static List<RitualRecipe> generateRecipes(boolean force) {
        if (recipes == null || force) {
            recipes = new ArrayList<>();
            for (RiteRegistry.Ritual ritual : RiteRegistry.instance()
                .getSortedRituals()) {
                recipes.add(new RitualRecipe(ritual));
            }
        }

        return recipes;
    }

    public static class RitualRecipe {

        public String name;
        public float power = 0;
        public ItemStack[] items = null;
        public ItemStack[] optionalItems = null;
        public String livingSacrificeName = null;
        public CircleType[] circles;
        EnumSet<RitualTraits> traits;

        // limitations (all do not matter for vanilla witchery):
        // only one living sacrifice name displays
        // only two traits display
        // only 8 items display
        // only 3 optional items display
        public RitualRecipe(RiteRegistry.Ritual ritual) {
            name = ritual.getUnlocalizedName();
            traits = ReflectionHelper.getPrivateValue(RiteRegistry.Ritual.class, ritual, "traits");

            circles = new CircleType[3];
            Arrays.fill(circles, CircleType.NONE);
            for (Circle circle : (Circle[]) ReflectionHelper
                .getPrivateValue(RiteRegistry.Ritual.class, ritual, "circles")) {
                int numRitualGlyphs = ReflectionHelper.getPrivateValue(Circle.class, circle, "numRitualGlyphs");
                int numOtherwhereGlyphs = ReflectionHelper.getPrivateValue(Circle.class, circle, "numOtherwhereGlyphs");
                int numInfernalGlyphs = ReflectionHelper.getPrivateValue(Circle.class, circle, "numInfernalGlyphs");

                // 16: small
                // 28: medium
                // 40: large
                switch (numRitualGlyphs) {
                    case 16 -> circles[0] = CircleType.RITUAL;
                    case 28 -> circles[1] = CircleType.RITUAL;
                    case 40 -> circles[2] = CircleType.RITUAL;
                }
                switch (numOtherwhereGlyphs) {
                    case 16 -> circles[0] = CircleType.OTHERWHERE;
                    case 28 -> circles[1] = CircleType.OTHERWHERE;
                    case 40 -> circles[2] = CircleType.OTHERWHERE;
                }
                switch (numInfernalGlyphs) {
                    case 16 -> circles[0] = CircleType.INFERNAL;
                    case 28 -> circles[1] = CircleType.INFERNAL;
                    case 40 -> circles[2] = CircleType.INFERNAL;
                }
            }

            processSacrifice(ReflectionHelper.getPrivateValue(RiteRegistry.Ritual.class, ritual, "initialSacrifice"));
        }

        private void processSacrifice(Sacrifice sacrifice) {
            if (sacrifice instanceof SacrificeMultiple) {
                for (Sacrifice containedSacrifice : (Sacrifice[]) ReflectionHelper
                    .getPrivateValue(SacrificeMultiple.class, (SacrificeMultiple) sacrifice, "sacrifices")) {
                    processSacrifice(containedSacrifice);
                }
            } else if (sacrifice instanceof SacrificePower) {
                this.power = ((SacrificePower) sacrifice).powerRequired;
            } else if (sacrifice instanceof SacrificeLiving) {
                this.livingSacrificeName = (String) EntityList.classToStringMapping.get(
                    ReflectionHelper
                        .getPrivateValue(SacrificeLiving.class, (SacrificeLiving) sacrifice, "entityLivingClass"));
            } else if (sacrifice instanceof SacrificeOptionalItem) {
                this.optionalItems = ReflectionHelper
                    .getPrivateValue(SacrificeItem.class, (SacrificeItem) sacrifice, "itemstacks");
            } else if (sacrifice instanceof SacrificeItem) {
                this.items = ReflectionHelper
                    .getPrivateValue(SacrificeItem.class, (SacrificeItem) sacrifice, "itemstacks");
            }
        }

        public boolean hasCircleOf(CircleType type) {
            return circles[0] == type || circles[1] == type || circles[2] == type;
        }

        public boolean talismanRepresentsCircle(ItemStack talisman) {
            int damage = talisman.getItemDamage();
            int small = damage % 8;
            int medium = damage % 64 / 8;
            int large = damage / 64;
            // talisman empty -> no match
            // all NONE -> can accept any combination of circles
            return damage != 0 && ((circles[0].ordinal() == small && circles[1].ordinal() == medium
                && circles[2].ordinal() == large)
                || (circles[0] == CircleType.NONE && circles[1] == CircleType.NONE && circles[2] == CircleType.NONE));
        }

        public enum CircleType {
            NONE,
            RITUAL,
            OTHERWHERE,
            INFERNAL
        }
    }

}
