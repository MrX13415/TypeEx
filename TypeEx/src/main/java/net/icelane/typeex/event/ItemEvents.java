package net.icelane.typeex.event;

import net.icelane.typeex.book.BookUI;
import net.icelane.typeex.net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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
	    
	    //quick and dirty first try
	    if (book.getItem() != Items.WRITABLE_BOOK) return;
	    
	    //player.sendMessage(new TextComponentString("YEAH!"));
	    
	    //GuiScreenBook bookui = new GuiScreenBook(player, book, true);
    	BookUI bookui = new BookUI(player, book, true);
    	Minecraft.getMinecraft().displayGuiScreen(bookui);
    	
	    event.setCanceled(true);
	}
	
}
