package net.icelane.typeex.proxy;

import net.icelane.typeex.ForgeMod;
import net.icelane.typeex.ui.UIHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
	
	public void preInit(FMLPreInitializationEvent e) {

    }

    public void init(FMLInitializationEvent e) {
    	// register the UI handler to the game ...
    	NetworkRegistry.INSTANCE.registerGuiHandler(ForgeMod.Instance(), new UIHandler());
    }

}
