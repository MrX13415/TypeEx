package net.icelane.typeex.book;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
abstract class BasicBook extends GuiScreen {
	
	private static final int pageCountLimit = 50;
	
	private final EntityPlayer player;
    private final ItemStack item;
    
    private String title = "";
    private NBTTagList pages;
    
    private final boolean signed; 
    private boolean modified;
    
	private int pageCount = 1;
    private int page;
    
    
    public BasicBook(EntityPlayer player, ItemStack item, boolean signed) {
		super();
		this.player = player;
		this.item = item;
		this.signed = signed;
		
		if (item.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = item.getTagCompound();
            pages = nbttagcompound.getTagList("pages", 8).copy();
            pageCount = pages.tagCount();

            if (pageCount < 1)
            {
            	pages.appendTag(new NBTTagString("")); // Forge: fix MC-1685
                pageCount = 1;
            }
        }

        if (pages == null && !isSigned())
        {
        	pages = new NBTTagList();
        	pages.appendTag(new NBTTagString(""));
            pageCount = 1;
        }
	}
    
    public NBTTagList getPages() {
		return pages;
	}

    public String getPageText()
    {
    	if (pages == null) return "";
    	if (page < 0) return "";
    	if (page > pages.tagCount()) return "";

    	return pages.getStringTagAt(page);
    }

    public void setPageText(String text)
    {
    	if (pages == null) return;
    	if (page < 0) return;
    	if (page > pages.tagCount()) return;

        pages.set(page, new NBTTagString(text));
        
        modified = true;
    }
    
    public boolean newPage()
    {
        if (pages == null && pages.tagCount() >= pageCountLimit) return false;
        
        pages.appendTag(new NBTTagString(""));
        pageCount++;
        
        modified = true;
        return true;
    }
    
	
    public EntityPlayer getPlayer() {
		return player;
	}

	public ItemStack getItem() {
		return item;
	}

	public boolean isModified() {
		return modified;
	}

	public boolean isSigned() {
		return signed;
	}
	
	public String getTitle() {
		return title;
	}

	public int pageCount() {
		return pageCount;
	}

	public int page() {
		return page;
	}
	
	public int page(int index) {
		return page = index;
	}
	
	public void pageIncrement() {
		page++;
	}
	
	public void pageDecrement() {
		--page;
	}
	
	public boolean isLastPage() {
		return page == pageCount() - 1;
	}

	public boolean isFirstPage() {
		return page == 0;
	}
}
