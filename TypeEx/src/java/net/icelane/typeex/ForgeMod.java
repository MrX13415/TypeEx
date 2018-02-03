package net.icelane.typeex;

import net.icelane.typeex.proxy.CommonProxy;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ForgeMod.MODID, name = ForgeMod.MODNAME, version = ForgeMod.VERSION)
public class ForgeMod
{
    public static final String MODID = "typeex";
    public static final String MODNAME = "TypeEx";
    public static final String VERSION = "0.0.1";
    
    @Instance
    public static ForgeMod instance = new ForgeMod();

    @SidedProxy(clientSide="net.icelane.typeex.proxy.ClientProxy",
    		    serverSide="net.icelane.typeex.proxy.ServerProxy")
    public CommonProxy proxy;
    
    
    public static ForgeMod Instance() {
    	return instance;
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
                    
    }
       
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        System.out.println("DIRT BLOCK >> "+Blocks.DIRT.getUnlocalizedName());
    }
}
