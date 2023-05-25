package hellfall.ritualnei;

import codechicken.nei.drawable.DrawableBuilder;
import codechicken.nei.recipe.GuiRecipeTab;
import codechicken.nei.recipe.HandlerInfo;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class CommonProxy {

    public void init(FMLInitializationEvent event) {
        IMCSender.sendIMC();
    }

    public void postInit(FMLPostInitializationEvent event) {
        // NEI currently assumes all handler images are 256x256, which is wrong. this one is 16x16
        // therefore, here is some stupid shit
        try {
            ReflectionHelper.setPrivateValue(
                HandlerInfo.class,
                GuiRecipeTab.handlerAdderFromIMC.get("ritualnei.ritual"),
                new DrawableBuilder("witchery:textures/blocks/heartGlyph.png", 1, 1, 15, 15).setTextureSize(16, 16)
                    .build(),
                "image");
        } catch (Throwable t) {
            // ignore, non-GTNH NEI will error here
            // if this is GTNH NEI and it errors, it will fall back to golden chalk item
        }
    }
}
