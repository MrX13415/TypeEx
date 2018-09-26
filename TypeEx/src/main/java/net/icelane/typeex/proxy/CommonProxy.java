package net.icelane.typeex.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent e) {

    }

    public void init(FMLInitializationEvent e) {
    	// register the UI handler to the game ...
    	//NetworkRegistry.INSTANCE.registerGuiHandler(ForgeMod.Instance(), new UIHandler());
    }

}
