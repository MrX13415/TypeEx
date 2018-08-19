package net.icelane.typeex.book;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

abstract class BasicBook extends GuiScreen {
	
    final EntityPlayer player;
    final ItemStack item;
    
    String title = "";
    NBTTagList pages;
    
    final boolean signed; 
    boolean modified;
    
    int pageCount = 1;
    int page;
    
    
	BasicBook(EntityPlayer player, ItemStack item, boolean signed) {
		super();
		this.player = player;
		this.item = item;
		this.signed = signed;
	}
    
    String getPageText()
    {
    	if (pages == null) return "";
    	if (page < 0) return "";
    	if (page > pages.tagCount()) return "";

    	return pages.getStringTagAt(page);
    }

    void setPageText(String text)
    {
    	if (pages == null) return;
    	if (page < 0) return;
    	if (page > pages.tagCount()) return;

        pages.set(page, new NBTTagString(text));
        modified = true;
    }
	
}
