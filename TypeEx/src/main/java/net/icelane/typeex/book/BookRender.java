package net.icelane.typeex.book;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;

import net.icelane.typeex.book.io.TextInfo.TextChunk;
import net.icelane.typeex.book.ui.NextPageButton;
import net.icelane.typeex.util.Color;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class BookRender extends BasicBook {

    public static final ResourceLocation BOOK_GUI_TEXTURES = new ResourceLocation("textures/gui/book.png");
    
    public static final int DEFAULT_COLOR_TEXT = 0xFF000000;
    public static final int DEFAULT_COLOR_CURSOR = 0xFF000000;
    public static final int DEFAULT_COLOR_CURSORBLINK = 0x00FFFFFF;
    public static final int DEFAULT_COLOR_SELECTION = 0xFF0000FF;
    public static final int DEFAULT_COLOR_SELECTION_ALTERNATIV = 0xFFBFA086; //hue = 0.075f; saturation = 0.3f; brightness = 0.75f;
    
    
    // Colors: ARGB
    private int Color_Text = DEFAULT_COLOR_TEXT;
    private int Color_Cursor = DEFAULT_COLOR_CURSOR;
	private int Color_CursorBlink = DEFAULT_COLOR_CURSORBLINK;
    private int Color_Selection = DEFAULT_COLOR_SELECTION;

    public int cursorWidth_Normal = 1;
    private int cursorWidth_Override = 5;

    private boolean rainbowText;
    private boolean rainbowSelection;
    private boolean rainbowCoursor;
    
    private float rainbowSaturation = 1.0f;
    private float rainbowBrightness = 1.0f;
    private float rainbowHue        = 0.0f;
	private boolean rainbowupdate;
	
    /** Update ticks since the GUI was opened */
    private int updateTicks;
    
    /** Determines if the signing screen is open */
    private boolean signing;
    
    private List<ITextComponent> cachedComponents;
    private int cachedPage = -1;
    
    private GuiButton buttonDone;
    private GuiButton buttonSign;
    private GuiButton buttonFinalize;
    private GuiButton buttonCancel;
	
    private NextPageButton buttonNextPage;
    private NextPageButton buttonPreviousPage;

    
	public BookRender(EntityPlayer player, ItemStack item, boolean unsigned) {
		super(player, item, unsigned);
	}

	@Override
	public void onPageChange(int prevPage) {
		super.onPageChange(prevPage);
		if(isSigning()) textinfo().text(title());
	}	
	
	@Override
    public boolean onBookCommand(String command) {
    	switch (command) {
		case "rainbowcursor":
			rainbowCoursor = !rainbowCoursor; break;
		case "rainbowselection":
			rainbowSelection = !rainbowSelection; break;
		case "rainbowtext":
			rainbowText = !rainbowText; break;
		case "selectioncolor":
			rainbowSelection = false;
			switch (getColor_Selection()) {
			case DEFAULT_COLOR_SELECTION:
				setColor_Selection(DEFAULT_COLOR_SELECTION_ALTERNATIV); break;
			case DEFAULT_COLOR_SELECTION_ALTERNATIV:
				setColor_Selection(DEFAULT_COLOR_SELECTION); break;
			}
			break;
		default:
			return super.onBookCommand(command);
		}

    	return true;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
    	super.initGui();
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);

        int x = (width - 192) / 2;
        buttonNextPage     = (NextPageButton) addButton(new NextPageButton(1, x + 120, 156, true));
        buttonPreviousPage = (NextPageButton) addButton(new NextPageButton(2, x + 38, 156, false));
        
        if (isSigned()) {
        	buttonDone = addButton(new GuiButton(0, width / 2 - 100, 196, 200, 20, I18n.format("gui.done")));
        	
        } else {
        	buttonSign     = addButton(new GuiButton(3, width / 2 - 100, 196, 98, 20, I18n.format("book.signButton")));
            buttonDone     = addButton(new GuiButton(0, width / 2 + 2, 196, 98, 20, I18n.format("gui.done")));
            buttonFinalize = addButton(new GuiButton(5, width / 2 - 100, 196, 98, 20, I18n.format("book.finalizeButton")));
            buttonCancel   = addButton(new GuiButton(4, width / 2 + 2, 196, 98, 20, I18n.format("gui.cancel")));
        }

        updateButtons();
    }

    protected void updateButtons()
    {
        buttonNextPage.visible     = !isSigning() && (!isLastPage() || isUnsigned());
        buttonPreviousPage.visible = !isSigning() && !isFirstPage();
        buttonDone.visible         = !isSigning() || isSigned();

        if (isSigned()) return;
          
        buttonSign.visible     = !isSigning();
        buttonCancel.visible   = isSigning();
        buttonFinalize.visible = isSigning();
        buttonFinalize.enabled = !title().trim().isEmpty();  
    }

	public int updateTicks() {
		return updateTicks;
	}
    
	/**
	 * Toggles between true and false every time the given amount of milliseconds has elapses.</br> 
	 * Minimum is 50 milliseconds.
	 * 
	 * @param ms Milliseconds to elapse.
	 * @return true or false at intervals of the given time span.
	 */
	public boolean millisElapsed(int ms) {
		if (ms < 50) ms = 50;
		return this.updateTicks / (ms/50) % 2 == 1;
	}
	
	
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        updateTicks++;
    }

    public void closeUI() {
    	this.mc.displayGuiScreen((GuiScreen)null);
    }
    
	public boolean isSigning() {
		return signing;
	}
	
	public void setSigning(boolean signing) {
		boolean old = this.signing;
		this.signing = signing;
		if (this.signing != old) onPageChange(-1);
	}
    	
	public int x() {
		// screen-width - book-width / 2;
		return (this.width - width()) / 2;
	}
	
	public int width() {
		return 192;
	}
	
	public int height() {
		return 192;
	}
	

	public int getRainbowARGB() {
		rainbowHue += 0.0001;
// Alternate method:
//		if (millisElapsed(50)) {
//			if (!rainbowupdate)
//				rainbowHue +=0.005;
//			rainbowupdate = true;
//		} else rainbowupdate = false;

    	if (rainbowHue >= 1) rainbowHue = 0;    	
    	return Color.hsb2argb(rainbowHue, rainbowSaturation, rainbowBrightness); 
	}
	
	public Color getRainbowColor() {	
    	return new Color(getRainbowARGB()); 
	}
	
	private void drawSigningPage() {
		String title = title();

		//if (isUnsigned()) title = addCursor(title);

		String header = I18n.format("book.editTitle");
        String name = I18n.format("book.byAuthor", getPlayer().getName());
        String warning = I18n.format("book.finalizeWarning");
        
        
		int i = (this.width - 192) / 2;
		int textWidth = 0;
		
       
        textWidth = fontRenderer.getStringWidth(header);
        fontRenderer.drawString(header, i + 36 + (116 - textWidth) / 2, 34, 0);
        
        textWidth = fontRenderer.getStringWidth(title);
        fontRenderer.drawString(title, i + 36 + (116 - textWidth) / 2, 50, 0);
        
        textWidth = fontRenderer.getStringWidth(name);
        fontRenderer.drawString(TextFormatting.DARK_GRAY + name, i + 36 + (116 - textWidth) / 2, 60, 0);
        
        fontRenderer.drawSplitString(warning, i + 36, 82, 116, 0);
	}
		
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawBackground();
		
		if (isSigning())
		{
			drawSigningPage();
		}
		else
		{
			if (isUnsigned()) {
				
			}
            else if (this.cachedPage != page())
            {
            	cachePage(getPageText());
            }
			
			drawPageHeader();

			//TODO:
            if (this.cachedComponents == null)
            {
    			drawPageContent();			
            }
            else
            {
                int k1 = Math.min(128 / this.fontRenderer.FONT_HEIGHT, this.cachedComponents.size());

                for (int l1 = 0; l1 < k1; ++l1)
                {
                    ITextComponent itextcomponent2 = this.cachedComponents.get(l1);
                    //this.fontRenderer.drawString(itextcomponent2.getUnformattedText(), x + 36, 34 + l1 * this.fontRenderer.FONT_HEIGHT, 0);
                }

                ITextComponent itextcomponent1 = this.getClickedComponentAt(mouseX, mouseY);

                if (itextcomponent1 != null)
                {
                    this.handleComponentHover(itextcomponent1, mouseX, mouseY);
                }
            }
		}
	
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	public void drawBackground() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURES);
        
        int x = x();
        int y = 2;
        int w = width();
        int h = height();
        
        this.drawTexturedModalRect(x, y, 0, 0, w, h);
	}
	
	public void drawPageContent() {
		boolean selection = false;
		boolean cursor = false;
		int lineCount = textinfo().lineCount();
		
		int x = x() + 36;
		int y = 34;
		
		for (int index = 0; index < lineCount; index++) {
			TextChunk line = textinfo().line(index);
			TextChunk[] chunks = line.wordWrap();
			
			for (int cindex = 0; cindex < chunks.length; cindex++) {
				TextChunk chunk = chunks[cindex];
				
				int color = rainbowText ? getRainbowARGB() : Color_Text;
		        fontRenderer.drawString(chunk.text, x, y, color); // TODO: drawStringalined?

		        if (chunk.isCursorWithin() && !cursor) {
		        	drawCursor(x + chunk.cursorWidth(), y);
		        	cursor = true;
		        }
		        
		        if(textinfo().selected) {
			        if(chunk.isSelectionStartWithin() || chunk.isSelectionEndWithin())
			        	selection = drawSelection(x, y, chunk);
			        else if (selection)
			        	drawSelection(x, y, chunk);
				}
			
				y += fontRenderer.FONT_HEIGHT;
			} 
		}
		
		
		if (!cursor) drawCursor(x, y);
	}
	
	private void drawCursor(int x, int y) {
		int color = rainbowCoursor ? getRainbowARGB() : Color_Cursor;

		// every 300ms hide the cursor
		if (millisElapsed(300)) color = Color_CursorBlink; 

        if (textinfo().isCursorWithin())
        	drawCursorVertical(x, y, color);
        else if (Color.get(color).getAlpha() > 0)
        	drawCursorHorizontal(x, y, color);
	}
	
	private int cursorWidth() {
		// - 1 => Leave a gap between the cursor and the next character.
		int w = fontRenderer.getCharWidth(textinfo().currentChar()) - 1;
		if (w <= 1) return 2; // min size 2px
		if (w > cursorWidth_Override) return cursorWidth_Override;
		return w;
	}
	
	private void drawCursorVertical(int x, int y, int color) {
		// x -= 1; <= Alternative: Move cursor 1px to the left.
		y -= 1;
    	int w = textinfo().overwrite ? cursorWidth() : cursorWidth_Normal;
		int h = this.fontRenderer.FONT_HEIGHT + 1;

    	drawInvertRect(x, y, w, h, color);
	}
	
	private void drawCursorHorizontal(int x, int y, int color) {
    	y += this.fontRenderer.FONT_HEIGHT - 1;
    	int w = cursorWidth_Override;
    	int h = 1;

    	drawInvertRect(x, y, w, h, color);
	}
	
	private boolean drawSelection(int x, int y, TextChunk chunk) {
		boolean selStart = chunk.isSelectionStartWithin();
		boolean selEnd = chunk.isSelectionEndWithin();
		boolean selection = selStart && !selEnd;
		
		int width = chunk.width();
        int selx = 0;
        int sely = 0;

		if(selStart) {
			selx = chunk.selectionStartWidth();
			width = (selEnd ? chunk.selectionEndWidth() : chunk.width()) - selx;
		} else if(selEnd) {
			width = chunk.selectionEndWidth();
		}
		
		x += selx;
		y += sely;
	
		int color = rainbowSelection ? getRainbowARGB() : Color_Selection;
		drawInvertRect(x, y, width, fontRenderer.FONT_HEIGHT, color);
		return selection;
	}
	
    private void drawInvertRect(int x, int y, int width, int height, int argb){
    	int x2 = x + width;
    	int y2 = y + height;

    	//Unravel colorfuckery
    	Color color = new Color(argb); 
 	    
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.color(color.red, color.green, color.blue, color.alpha);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)x, (double)y2, 0.0D).endVertex();
        bufferbuilder.pos((double)x2, (double)y2, 0.0D).endVertex();
        bufferbuilder.pos((double)x2, (double)y, 0.0D).endVertex();
        bufferbuilder.pos((double)x, (double)y, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }
	
	public void drawPageHeader() {
	    String pageIndicator = I18n.format("book.pageIndicator", page() + 1, pageCount());
	    
	    int textwidth = this.fontRenderer.getStringWidth(pageIndicator);
		int offset    = 44;
		int x         = x() + width() - textwidth - offset;
		int y         = 18;
 
        this.fontRenderer.drawString(pageIndicator, x, y, 0);
	}

	private void cachePage(String pageContent) {
        if (ItemWrittenBook.validBookTagContents(getItem().getTagCompound()))
        {
            try
            {
                ITextComponent itextcomponent = ITextComponent.Serializer.jsonToComponent(pageContent);
                this.cachedComponents = itextcomponent != null ? GuiUtilRenderComponents.splitText(itextcomponent, 116, this.fontRenderer, true, true) : null;
            }
            catch (JsonParseException ex)
            {
                this.cachedComponents = null;
            }
        }
        else
        {
            TextComponentString textcomponentstring = new TextComponentString(TextFormatting.DARK_RED + "* Invalid book tag *");
            this.cachedComponents = Lists.newArrayList(textcomponentstring);
        }

        this.cachedPage = page();
	}
	
    @Nullable
    public ITextComponent getClickedComponentAt(int p_175385_1_, int p_175385_2_)
    {
        if (this.cachedComponents == null)
        {
            return null;
        }
        else
        {
            int i = p_175385_1_ - (this.width - 192) / 2 - 36;
            int j = p_175385_2_ - 2 - 16 - 16;

            if (i >= 0 && j >= 0)
            {
                int k = Math.min(128 / this.fontRenderer.FONT_HEIGHT, this.cachedComponents.size());

                if (i <= 116 && j < this.mc.fontRenderer.FONT_HEIGHT * k + k)
                {
                    int l = j / this.mc.fontRenderer.FONT_HEIGHT;

                    if (l >= 0 && l < this.cachedComponents.size())
                    {
                        ITextComponent itextcomponent = this.cachedComponents.get(l);
                        int i1 = 0;

                        for (ITextComponent itextcomponent1 : itextcomponent)
                        {
                            if (itextcomponent1 instanceof TextComponentString)
                            {
                                i1 += this.mc.fontRenderer.getStringWidth(((TextComponentString)itextcomponent1).getText());

                                if (i1 > i)
                                {
                                    return itextcomponent1;
                                }
                            }
                        }
                    }

                    return null;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
    }
    	

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

	public int getColor_Text() {
		return Color_Text;
	}

	public void setColor_Text(int color_Text) {
		Color_Text = color_Text;
	}

	public int getColor_Cursor() {
		return Color_Cursor;
	}

	public void setColor_Cursor(int color_Cursor) {
		Color_Cursor = color_Cursor;
	}

	public int getColor_CursorBlink() {
		return Color_CursorBlink;
	}

	public void setColor_CursorBlink(int color_CursorBlink) {
		Color_CursorBlink = color_CursorBlink;
	}

	public int getColor_Selection() {
		return Color_Selection;
	}

	public void setColor_Selection(int color_Selection) {
		Color_Selection = color_Selection;
	}

	public boolean isRainbowFont() {
		return rainbowText;
	}

	public void setRainbowText(boolean rainbowText) {
		this.rainbowText = rainbowText;
	}

	public boolean isRainbowCoursor() {
		return rainbowCoursor;
	}

	public void setRainbowCoursor(boolean rainbowCoursor) {
		this.rainbowCoursor = rainbowCoursor;
	}

	public float getRainbowBrightness() {
		return rainbowBrightness;
	}

	public void setRainbowBrightness(float rainbowBrightness) {
		this.rainbowBrightness = rainbowBrightness;
	}
    
    
}
