package net.icelane.typeex.book;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.icelane.typeex.book.io.KeyHandler;
import net.icelane.typeex.book.io.KeyInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
abstract class BookInput extends BookRender {

	public BookInput(EntityPlayer player, ItemStack item, boolean unsigned) {
		super(player, item, unsigned);
	}

    public void handleKeyboardInput() throws IOException
    {
        keyTyped(new KeyInfo(Keyboard.getEventKey(), Keyboard.getEventCharacter(), Keyboard.getEventKeyState()));
        
        this.mc.dispatchKeypresses();
    }
    
	public void keyTyped(KeyInfo keyinfo) {
		try {
			super.keyTyped(keyinfo.getCharacter(), keyinfo.getKeyCode());
		} catch (IOException ex) { }
		
		//DEBUG
		System.out.println(String.format("%16s %s%s%s%s%s",
				keyinfo.toString(),
				textinfo().overwrite ? "Ins" : "   ",
				KeyInfo.isControlHeld() ? " CTRL" : "",
				KeyInfo.isAltHeld() ? " Alt" : "",
				KeyInfo.isShiftHeld() ? " Shift" : "",
				KeyInfo.isMetaHeld() ? " Meta" : ""));
			
		if (isSigned()) return;
		if (!keyinfo.getKeyState()) return;
		
		// initialize ...
		//textinfo.text = isSigning() ? title() : getPageText();
		textinfo().maxLength = isSigning() ? 16 : 256; //TODO

		// handle special keys ...
		boolean keyHandled = KeyHandler.handleKey(keyinfo, textinfo());
		
		if (isSigning()){
	        switch (keyinfo.getKeyCode())
	        {
	            case KeyInfo.Backspace:
	                this.updateButtons();
	                break;
	                
	            case KeyInfo.Enter:
	            case KeyInfo.Num_Enter:
	                if (title().isEmpty()) return;
					try {
						this.sendBookToServer(true);
		                closeUI();
					} catch (IOException e) {
						System.err.println("Unable to sing book!");
						e.printStackTrace();
					}
	                break;
	        }
		}
		
		if (!keyHandled && keyinfo.IsAllowed()) {
			// handle selection overwrite ...
			if (textinfo().selected) textinfo().removeSelection();
			
			if (isSigning()){
		        textinfo().insert(keyinfo.getString());
	            this.updateButtons();
	            setModified();
			} else {
				textinfo().insert(keyinfo.getString());
			}
		}
		
		// update UI
		if (isSigning()) title(textinfo().text());
		else setPageText(textinfo().text());	
	}

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
