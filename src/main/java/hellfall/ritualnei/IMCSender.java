package hellfall.ritualnei;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;

public class IMCSender {

    public static void sendIMC() {
        sendHandler();
        sendCatalyst();
    }

    private static void sendHandler() {
        NBTTagCompound aNBT = new NBTTagCompound();
        aNBT.setString("handler", "ritualnei.ritual");
        aNBT.setString("modName", Tags.MODNAME);
        aNBT.setString("modId", Tags.MODID);
        aNBT.setBoolean("modRequired", true);
        aNBT.setString("itemName", "witchery:chalkheart");
        aNBT.setInteger("handlerHeight", 132);
        aNBT.setInteger("handlerWidth", 166);
        aNBT.setInteger("maxRecipesPerPage", 2);
        aNBT.setInteger("yShift", 6);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", aNBT);
    }

    private static void sendCatalyst() {
        NBTTagCompound aNBT = new NBTTagCompound();
        aNBT.setString("handlerID", "ritualnei.ritual");
        aNBT.setString("itemName", "witchery:chalkheart");
        aNBT.setInteger("priority", 0);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerCatalystInfo", aNBT);
    }

}
