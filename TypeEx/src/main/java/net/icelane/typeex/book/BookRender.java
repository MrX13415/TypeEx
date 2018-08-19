package net.icelane.typeex.book;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;

import net.icelane.typeex.book.ui.NextPageButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
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
	
    
    private NextPageButton buttonNextPage;
    private NextPageButton buttonPreviousPage;
    
    private GuiButton buttonDone;
    /** The GuiButton to sign this book. */
    private GuiButton buttonSign;
    private GuiButton buttonFinalize;
    private GuiButton buttonCancel;
	
    private List<ITextComponent> cachedComponents;
    private int cachedPage = -1;
    
    /** Update ticks since the gui was opened */
    private int updateTicks;
    
    /** Determines if the signing screen is open */
    private boolean signing;

	
	public BookRender(EntityPlayer player, ItemStack item, boolean signed) {
		super(player, item, signed);
	}

	
    
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);

        int i = (width - 192) / 2;
        buttonNextPage     = (NextPageButton) addButton(new NextPageButton(1, i + 120, 156, true));
        buttonPreviousPage = (NextPageButton) addButton(new NextPageButton(2, i + 38, 156, false));
        
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
        buttonNextPage.visible     = !isSigning() && (!isLastPage() || !isSigned());
        buttonPreviousPage.visible = !isSigning() && !isFirstPage();
        buttonDone.visible         = !isSigning() || isSigned();

        if (isSigned()) return;
          
        buttonSign.visible     = !isSigning();
        buttonCancel.visible   = isSigning();
        buttonFinalize.visible = isSigning();
        buttonFinalize.enabled = !getTitle().trim().isEmpty();  
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



	public boolean isSigning() {
		return signing;
	}
	
	public void setSigning(boolean signing) {
		this.signing = signing;
	}
    	
	
	public void drawSigningPage() {
		String title = getTitle();

		if (!isSigned()) title = addCursor(title);
		
//        if (!isSigned())
//        {
//            if (this.updateCount / 6 % 2 == 0)
//            {
//            	title = title + "" + TextFormatting.BLACK + "_";
//            }
//            else
//            {
//            	title = title + "" + TextFormatting.GRAY + "_";
//            }
//        }
		
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
	
	private String addCursor(String text) {
        if (this.fontRenderer.getBidiFlag())
        {
        	text += "_";
        }
        else if (this.updateTicks / 6 % 2 == 0)
        {
        	text += "" + TextFormatting.BLACK + "_";
        }
        else
        {
        	text += "" + TextFormatting.GRAY + "_";
        }
        
		return text;
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
	
	/**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        this.mc.getTextureManager().bindTexture(BOOK_GUI_TEXTURES);
        
        int i = (this.width - 192) / 2;
        int j = 2;
        this.drawTexturedModalRect(i, 2, 0, 0, 192, 192);

        if (isSigning())
        {
        	drawSigningPage();
        }
        else
        {
            String s4 = I18n.format("book.pageIndicator", page() + 1, pageCount());
            String pageContent = getPageText();

            if (!isSigned())
            {
            	pageContent = addCursor(pageContent);
            }
            else if (this.cachedPage != page())
            {
            	cachePage(pageContent);
            }

            int j1 = this.fontRenderer.getStringWidth(s4);
            this.fontRenderer.drawString(s4, i - j1 + 192 - 44, 18, 0);

            if (this.cachedComponents == null)
            {
                this.fontRenderer.drawSplitString(pageContent, i + 36, 34, 116, 0);
            }
            else
            {
                int k1 = Math.min(128 / this.fontRenderer.FONT_HEIGHT, this.cachedComponents.size());

                for (int l1 = 0; l1 < k1; ++l1)
                {
                    ITextComponent itextcomponent2 = this.cachedComponents.get(l1);
                    this.fontRenderer.drawString(itextcomponent2.getUnformattedText(), i + 36, 34 + l1 * this.fontRenderer.FONT_HEIGHT, 0);
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
    
    
    
}
