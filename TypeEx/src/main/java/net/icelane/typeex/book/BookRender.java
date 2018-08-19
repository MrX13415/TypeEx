package net.icelane.typeex.book;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

abstract class BookRender extends BasicBook {

	public BookRender(EntityPlayer player, ItemStack item, boolean signed) {
		super(player, item, signed);
	}

	private static final ResourceLocation BOOK_GUI_TEXTURES = new ResourceLocation("textures/gui/book.png");
	
    /** Update ticks since the gui was opened */
    private int updateCount;
    
    /** Determines if the signing screen is open */
    private boolean bookGettingSigned;

}
