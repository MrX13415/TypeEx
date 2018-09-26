package net.icelane.typeex.book.ui;

import net.icelane.typeex.book.BookRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NextPageButton extends GuiButton {
    private final boolean isForward;

    public NextPageButton(int buttonId, int x, int y, boolean isForwardIn)
    {
        super(buttonId, x, y, 23, 13, "");
        this.isForward = isForwardIn;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(BookRender.BOOK_GUI_TEXTURES);
            int i = 0;
            int j = 192;

            if (flag)
            {
                i += 23;
            }

            if (!this.isForward)
            {
                j += 13;
            }

            this.drawTexturedModalRect(this.x, this.y, i, j, 23, 13);
        }
    }
}
