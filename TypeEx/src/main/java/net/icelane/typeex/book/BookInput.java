package net.icelane.typeex.book;

import java.awt.event.KeyEvent;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.icelane.typeex.book.io.KeyHandler;
import net.icelane.typeex.book.io.KeyInfo;
import net.icelane.typeex.book.io.TextInfo;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
abstract class BookInput extends BookRender {

	TextInfo textinfo = new TextInfo();

	
	public BookInput(EntityPlayer player, ItemStack item, boolean signed) {
		super(player, item, signed);
	}

    public void handleKeyboardInput() throws IOException
    {
        keyTyped(new KeyInfo(Keyboard.getEventKey(), Keyboard.getEventCharacter(), Keyboard.getEventKeyState()));
        
        this.mc.dispatchKeypresses();
    }
    
	public void keyTyped(KeyInfo keyinfo) {
		
		//DEBUG
		System.out.println(String.format("%16s %s%s%s%s%s",
				keyinfo.toString(),
				textinfo.overwrite ? "Ins" : "   ",
				KeyInfo.isControlHeld() ? " CTRL" : "",
				KeyInfo.isAltHeld() ? " Alt" : "",
				KeyInfo.isShiftHeld() ? " Shift" : "",
				KeyInfo.isMetaHeld() ? " Meta" : ""));
			
		
		textinfo.text = getPageText();

		if (!keyinfo.getKeyState()) return;
		
		// handle special keys ...
		boolean keyHandled = KeyHandler.handleKey(keyinfo, textinfo);
		
		// handle normal char keys ...
		if (!keyHandled && keyinfo.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {						
	
			// handle selection overwrite ...
			if (textinfo.selected) textinfo.removeSelection();
			
			if (!isSigned()) {
		        if (isSigned()){
		//		                this.keyTypedInTitle(typedChar, keyCode);
		        } else {
			    	if (ChatAllowedCharacters.isAllowedCharacter(keyinfo.getKeyChar())) {
						textinfo.insert(Character.toString(keyinfo.getKeyChar()));
						
						// update UI
						setPageText(textinfo.text);	
			    	}
			    }
			}
		}


	}
	
//    private void keyTypedInTitle(char typedChar, int keyCode) throws IOException
//    {
//        switch (keyCode)
//        {
//            case 14:
//
//                if (!this.bookTitle.isEmpty())
//                {
//                    this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
//                    this.updateButtons();
//                }
//
//                return;
//            case 28:
//            case 156:
//
//                if (!this.bookTitle.isEmpty())
//                {
//                    this.sendBookToServer(true);
//                    this.mc.displayGuiScreen((GuiScreen)null);
//                }
//
//                return;
//            default:
//
//                if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(typedChar))
//                {
//                    this.bookTitle = this.bookTitle + Character.toString(typedChar);
//                    this.updateButtons();
//                    this.bookIsModified = true;
//                }
//        }
//    }

	  
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            ITextComponent itextcomponent = this.getClickedComponentAt(mouseX, mouseY);

            if (itextcomponent != null && this.handleComponentClick(itextcomponent))
            {
                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    

	
    /**
     * Executes the click event specified by the given chat component
     */
    public boolean handleComponentClick(ITextComponent component)
    {
        ClickEvent clickevent = component.getStyle().getClickEvent();

        if (clickevent == null)
        {
            return false;
        }
        else if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE)
        {
            String s = clickevent.getValue();

            try
            {
                int i = Integer.parseInt(s) - 1;

                if (i >= 0 && i < pageCount() && i != page())
                {
                    page(i);
                    updateButtons();
                    return true;
                }
            }
            catch (Exception ex) { }

            return false;
        }
        else
        {
            boolean flag = super.handleComponentClick(component);

            if (flag && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND)
            {
                this.mc.displayGuiScreen((GuiScreen)null);
            }

            return flag;
        }
    }


}
