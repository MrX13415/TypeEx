package net.icelane.typeex.event;

import net.icelane.typeex.book.BookUI;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemEvents {
	
	private static ItemEvents eventHandler = new ItemEvents();

	public static ItemEvents getEventHandler(){
		return eventHandler;
	}
	
	@SubscribeEvent
	public void onItemUse(PlayerInteractEvent.RightClickItem event)
	{
		EntityPlayer player = event.getEntityPlayer();
	    ItemStack book = event.getItemStack();
	    
	    //player.sendMessage(new TextComponentString("YEAH!"));
	    
    	BookUI bookui = new BookUI(player, book, false);
    	Minecraft.getMinecraft().displayGuiScreen(bookui);
    	
	    event.setCanceled(true);
	}
	
}
