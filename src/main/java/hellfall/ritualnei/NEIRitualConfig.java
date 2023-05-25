package hellfall.ritualnei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import hellfall.ritualnei.recipe.RitualRecipeHandler;

public class NEIRitualConfig implements IConfigureNEI {

    private static final RitualRecipeHandler RITUAL_HANDLER = new RitualRecipeHandler();

    @Override
    public void loadConfig() {
        API.registerUsageHandler(RITUAL_HANDLER);
        API.registerRecipeHandler(RITUAL_HANDLER);
    }

    @Override
    public String getName() {
        return Tags.MODNAME;
    }

    @Override
    public String getVersion() {
        return Tags.VERSION;
    }
}
