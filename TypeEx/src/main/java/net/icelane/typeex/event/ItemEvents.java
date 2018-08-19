package net.icelane.typeex.event;

import net.icelane.typeex.net.minecraft.client.gui.TXGuiScreenBook;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemEvents {
	
	private static ItemEvents eventHandler = new ItemEvents();

	public static ItemEvents getListener(){
		return eventHandler;
	}
	
	@SubscribeEvent
	public void onItemUse(PlayerInteractEvent.RightClickItem event)
	{
		EntityPlayer player = event.getEntityPlayer();
	    ItemStack book = event.getItemStack();
	    
	    player.sendMessage(new TextComponentString("YEAH!"));
	    
    	TXGuiScreenBook bookui = new TXGuiScreenBook(player, book, true);
    	Minecraft.getMinecraft().displayGuiScreen(bookui);
	    event.setCanceled(true);
	}
	
}
