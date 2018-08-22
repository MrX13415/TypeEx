package net.icelane.typeex.book;

import java.io.IOException;

import io.netty.buffer.Unpooled;
import net.icelane.typeex.book.io.TextInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
abstract class BasicBook extends GuiScreen {
	
	private static final int pageCountLimit = 50;
	
	private final EntityPlayer player;
    private final ItemStack item;
    
	private TextInfo textinfo;
	
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

        if (pages == null && isUnsigned())
        {
        	pages = new NBTTagList();
        	pages.appendTag(new NBTTagString(""));
            pageCount = 1;
        }
	}
    
    public void initGui()
    {
        Initialize();
        onPageChange();
    }
    
	public void Initialize() {
		textinfo = new TextInfo(fontRenderer);
		textinfo.overwrite = false;
		textinfo.wordWrap = 116;
		//TODO
	}
	
	public void onPageChange() {
		textinfo.text(getPageText());
		textinfo.moveCursorToEnd();
	}	
    
    public TextInfo textinfo() {
		return textinfo;
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
        if (pages == null || pages.tagCount() >= pageCountLimit) return false;
        
        pages.appendTag(new NBTTagString(""));
        pageCount++;
        
        modified = true;
        return true;
    }
    
    protected void sendBookToServer(boolean publish) throws IOException
    {
        if (isSigned() || !isModified()) return;
        if (getPages() == null) return;
        
        // remove empty pages from the end ...
        while (getPages().tagCount() > 1)
        {
            String s = getPages().getStringTagAt(getPages().tagCount() - 1);
            if (!s.isEmpty()) break;
            getPages().removeTag(getPages().tagCount() - 1);
        }

        if (getItem().hasTagCompound())
        {
            NBTTagCompound nbttagcompound = getItem().getTagCompound();
            nbttagcompound.setTag("pages", getPages());
        }
        else
        {
            getItem().setTagInfo("pages", getPages());
        }

        String s1 = "MC|BEdit";

        if (publish)
        {
            s1 = "MC|BSign";
            getItem().setTagInfo("author", new NBTTagString(getPlayer().getName()));
            getItem().setTagInfo("title", new NBTTagString(title().trim()));
        }

        PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
        packetbuffer.writeItemStack(getItem());
        this.mc.getConnection().sendPacket(new CPacketCustomPayload(s1, packetbuffer));

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
	
	public boolean setModified() {
		return modified = true;
	}

	public boolean isSigned() {
		return signed;
	}
	
	public boolean isUnsigned() {
		return !signed;
	}
	
	public String title() {
		return title;
	}

	public void title(String title) {
		this.title = title;
	}

	public int pageCount() {
		return pageCount;
	}

	public int page() {
		return page;
	}
	
	public void page(int index) {
		int old = page;
		page = index;
		if (old != page) onPageChange();
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
