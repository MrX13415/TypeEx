package net.icelane.typeex.book;

import java.io.IOException;
import java.util.HashMap;

import io.netty.buffer.Unpooled;
import net.icelane.typeex.book.io.TextInfo;
import net.icelane.typeex.book.io.UndoInfo;
import net.icelane.typeex.util.LoremIpsum;
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
	
	private static final int pageCountLimit = 50; // pages
	private static final int pageTextLimit = 256; // characters
	
	private final EntityPlayer player;
    private final ItemStack item;
    
	private TextInfo textinfo;
	private HashMap<Integer, UndoInfo> undolist = new HashMap<>();
	
    private String title = "";
    private NBTTagList pages;
    
    private final boolean signed; 
    private boolean modified;
    
	private int pageCount = 1;
    private int page;
    
    
    public BasicBook(EntityPlayer player, ItemStack item, boolean unsigned) {
		super();
		this.player = player;
		this.item = item;
		this.signed = !unsigned;
		
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
        onPageChange(page);
    }
    
	public void Initialize() {
		textinfo = new TextInfo(fontRenderer);
		textinfo.overwrite = false;
		textinfo.wordWrap = 116;
		//TODO
	}
	
	public void onPageChange(int prevPage) {
		undolist.put(prevPage, textinfo.undoinfo());
		
		textinfo.undoinfo(getUndo(page));
		
		textinfo.text(getPageText());
		textinfo.moveCursorToEnd();
		textinfo.selected = false;
	}	

	public UndoInfo getUndo(int page) {
		UndoInfo undo = undolist.get(page);
		return undo != null ? undo : new UndoInfo();
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
    	if (bookCommand(text)) return;
    	
    	if (pages == null) return;
    	if (page < 0) return;
    	if (page > pages.tagCount()) return;

        pages.set(page, new NBTTagString(text));
        
        modified = true;
    }
    
    /** 
     * ^!>mycommand[=value]\n$<br>
     * !>lorem<br><br>
     * !>foo=bar<br><br>
     * @param text
     * @return
     */
    private boolean bookCommand(String text) {
    	if (!text.contains("!>")) return false;

    	int start = 0;
    	if (text.startsWith("!>")) start = 2;
    	if (text.contains("\n!>")) start = text.indexOf("\n!>") + 3;
    	
    	int end = text.indexOf("\n", start);
    	if (end < 0) return false;
    	
    	String input = text.substring(start, end);
    	String command = input;
    	String value = "";
    	if (input.contains("=")) {
    		String comp[] = input.split("=");
    		command = comp[0].trim();
    		if (comp.length > 1) value = comp[1].trim();
    	}
    	
    	boolean result = onBookCommand(command, value);
    	
    	try {
    		text = getPageText();
    		
    		if (text.length() >= end) {
    			String part = text.substring(start - 2, end);
        		// remove command from current page ...
    			if (part.equals("!>" + input)) {
    				text = text.substring(0, start - 2) + text.substring(end);
    				pages.set(page, new NBTTagString(text));
    				textinfo.text(getPageText());
    			}
    		}
		} catch (Exception e) { 
			System.out.println("LOOOL");
			}

    	return result;
    }
    
    public boolean onBookCommand(String command, String value) {
    	switch (command) {
		case "1234":
			fillBook("1234...", LoremIpsum._1234); break;
		case "kafka":
			fillBook("Kafka", LoremIpsum.kafka); break;
		case "lorem":
			fillBook("Lorem Ipsum", LoremIpsum.lorem); break;
		default:
			return false;
		}

		return true;
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
		if (old != page) onPageChange(old);
	}
	
	public void pageIncrement() {
		page(page+1);
	}
	
	public void pageDecrement() {
		if (page <= 0) return;
		page(page-1);
	}
	
	public boolean isLastPage() {
		return page == pageCount() - 1;
	}

	public boolean isFirstPage() {
		return page == 0;
	}
	
	private void fillBook(String title, String text) {		
		page(0);
		setPageText("\n\n" + title);
		page(1);
		
		int index = 0;
		
		while(index < text.length() && pageCount < 50) {
			if (pageCount <= page()) newPage();
			
			int endindex = index + pageTextLimit;
			if (endindex > text.length()) endindex = text.length();
			
			String chunk = text.substring(index, endindex);
			setPageText(chunk);
			
			pageIncrement();
			index += pageTextLimit;
		}		
		page(0);
		textinfo.text(getPageText());
	}
	
}
