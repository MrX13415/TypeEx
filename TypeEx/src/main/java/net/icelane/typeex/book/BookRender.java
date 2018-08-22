package net.icelane.typeex.book;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;

import net.icelane.typeex.book.io.TextInfo.ChunckInfo;
import net.icelane.typeex.book.io.TextInfo.LineInfo;
import net.icelane.typeex.book.ui.NextPageButton;
import net.icelane.typeex.util.ColorUtil;
import net.minecraft.client.gui.Gui;
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

    // Colors: ARGB
    private int Color_Cursor = 0xFF000000;

	private int Color_CursorBlink = 0x00FFFFFF;
    private int Color_Selection = 0xFF0000FF;

    
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
	public void onPageChange() {
		super.onPageChange();
		if(isSigning()) textinfo().text(title());
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
		if (this.signing != old) onPageChange();
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
			LineInfo line = textinfo().line(index);
			ChunckInfo[] chuncks = line.wordWrap();
			
			for (int cindex = 0; cindex < chuncks.length; cindex++) {
				ChunckInfo chunck = chuncks[cindex];
				
		        fontRenderer.drawString(chunck.text, x, y, 0); // TODO: drawStringalined?

		        if (chunck.isCursorWithin() && !cursor) {
		        	drawCursor(x + chunck.cursorWidth(), y);
		        	cursor = true;
		        }
		        
		        int selWidth = 0;
		        int selx = x;
		        int sely = y;
		        
		        boolean selStart = chunck.isSelectionStartWithin();
		        boolean selEnd = chunck.isSelectionEndWithin();
		        
				if(selStart) {
					selx = chunck.selectionStartWidth();
					selWidth = (selEnd ? chunck.selectionEndWidth() : chunck.width()) - selx;
				} else if(selEnd) {
					selWidth = chunck.selectionEndWidth();
				}
				
				drawSelection(selx, sely, selWidth);
				
				y += fontRenderer.FONT_HEIGHT;
			} 
		}
		
		
		if (!cursor) drawCursor(x, y);
	}
	
	private void drawCursor(int x, int y) {
		int color = Color_Cursor;
		if (this.updateTicks / 6 % 2 == 0) color = Color_CursorBlink; 
		
        if (textinfo().isCursorWithin()) 
        	Gui.drawRect(x, y - 1, x + 1, y + 1 + this.fontRenderer.FONT_HEIGHT, color);
        else if (ColorUtil.get(color).getAlpha() > 0)
        	this.fontRenderer.drawString("_", x, y, color);
	}

	
	private void drawSelection(int x, int y, int width) {
		drawSelectionBox(x, y, x + width, y + fontRenderer.FONT_HEIGHT);
	}
	
    /**
     * Draws the blue selection box.
     */
    private void drawSelectionBox(int startX, int startY, int endX, int endY)
    {	/*
        if (startX < endX)
        {
            int i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY)
        {
            int j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > this.x + this.width)
        {
            endX = this.x + this.width;
        }

        if (startX > this.x + this.width)
        {
            startX = this.x + this.width;
        }
        */
    	
    	//Unravel colorfuckery
    	int red = ColorUtil.get(Color_Selection).getRed();
    	int green = ColorUtil.get(Color_Selection).getGreen(); 
    	int blue = ColorUtil.get(Color_Selection).getBlue();
    	int alpha = ColorUtil.get(Color_Selection).getAlpha();
    	
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.color(red, green, blue, alpha);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)startX, (double)endY, 0.0D).endVertex();
        bufferbuilder.pos((double)endX, (double)endY, 0.0D).endVertex();
        bufferbuilder.pos((double)endX, (double)startY, 0.0D).endVertex();
        bufferbuilder.pos((double)startX, (double)startY, 0.0D).endVertex();
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
    
}
