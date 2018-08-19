package net.icelane.typeex.book;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BookUI extends BookInput {

	public BookUI(EntityPlayer player, ItemStack item, boolean signed) {
		super(player, item, signed);
	}

	
    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
    	if (!button.enabled) return;
    	
    	switch (button.id) {
		case 0:
			this.mc.displayGuiScreen((GuiScreen)null);
			this.sendBookToServer(false);
			break;
			
		case 1:
			if (isLastPage()) {				
				if (!isSigned() && newPage()) pageIncrement();
			} else {
				pageIncrement();
			}
			break;
			
		case 2:
			if (!isFirstPage()) pageDecrement();
			break;
			
		case 3:
			if (!isSigned()) setSigning(true);
			break;
			
		case 4:
			if (isSigning()) setSigning(false);
			break;
			
		case 5:
			if (isSigning()) {
	            this.sendBookToServer(true);
	            this.mc.displayGuiScreen((GuiScreen)null);
			}
			break;
		}
    	
    	this.updateButtons();
    }
	

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }
}
