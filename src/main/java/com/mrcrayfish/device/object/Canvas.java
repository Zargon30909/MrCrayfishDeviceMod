package com.mrcrayfish.device.object;

import java.awt.Color;

import com.mrcrayfish.device.app.Application;
import com.mrcrayfish.device.app.Component;
import com.mrcrayfish.device.app.Laptop;
import com.mrcrayfish.device.app.Layout;
import com.mrcrayfish.device.object.Picture.Size;
import com.mrcrayfish.device.object.tools.ToolBucket;
import com.mrcrayfish.device.object.tools.ToolEraser;
import com.mrcrayfish.device.object.tools.ToolEyeDropper;
import com.mrcrayfish.device.object.tools.ToolPencil;
import com.mrcrayfish.device.util.GuiHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class Canvas extends Component 
{
	private Tool currentTool;
	public static final Tool PENCIL = new ToolPencil();
	public static final Tool BUCKET = new ToolBucket();
	public static final Tool ERASER = new ToolEraser();
	public static final Tool EYE_DROPPER = new ToolEyeDropper();
	
	public int[][] pixels;
	private int red, green, blue;
	private int currentColour = Color.BLACK.getRGB();
	
	private boolean drawing = false;
	private boolean showGrid = false;
	private boolean existingImage = false;
	
	public Picture picture;
	
	private int gridColour = new Color(200, 200, 200, 150).getRGB();
	
	public Canvas(int x, int y, int left, int top)
	{
		super(x, y, left, top);
		this.currentTool = PENCIL;
	}
	
	public void createPicture(String name, String author, Size size)
	{
		this.existingImage = false;
		this.picture = new Picture(name, author, size);
		this.pixels = new int[picture.size.width][picture.size.height];
	}
	
	public void setPicture(Picture picture)
	{
		this.existingImage = true;
		this.picture = picture;
		this.pixels = picture.copyPixels();
	}
	
	@Override
	public void init(Layout layout) {}

	@Override
	public void render(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, float partialTicks) 
	{
		drawRect(xPosition, yPosition, xPosition + picture.getWidth() * picture.getPixelWidth() + 2, yPosition + picture.getHeight() * picture.getPixelHeight() + 2, Color.DARK_GRAY.getRGB());
		drawRect(xPosition + 1, yPosition + 1, xPosition + picture.getWidth() * picture.getPixelWidth() + 1, yPosition + picture.getHeight() * picture.getPixelHeight() + 1, Color.WHITE.getRGB());
		for(int y = 0; y < picture.getHeight(); y++)
		{
			for(int x = 0; x < picture.getWidth(); x++)
			{
				int pixelX = xPosition + x * picture.getPixelWidth() + 1;
				int pixelY = yPosition + y * picture.getPixelHeight() + 1;
				drawRect(pixelX, pixelY, pixelX + picture.getPixelWidth(), pixelY + picture.getPixelHeight(), pixels[x][y]);
				if(showGrid)
				{
					drawRect(pixelX, pixelY, pixelX + picture.getPixelWidth(), pixelY + 1, gridColour);
					drawRect(pixelX, pixelY, pixelX + 1, pixelY + picture.getPixelHeight(), gridColour);
				}
			}
		}
	}
	
	@Override
	public void handleClick(Application app, int mouseX, int mouseY, int mouseButton) 
	{
		int startX = xPosition + 1;
		int startY = yPosition + 1;
		int endX = startX + picture.getWidth() * picture.getPixelWidth() - 1;
		int endY = startY + picture.getHeight() * picture.getPixelHeight() - 1;
		if(GuiHelper.isMouseInside(mouseX, mouseY, startX, startY, endX, endY))
		{
			this.drawing = true;
			int pixelX = (mouseX - startX) / picture.getPixelWidth();
			int pixelY = (mouseY - startY) / picture.getPixelHeight();
			this.currentTool.handleClick(this, pixelX, pixelY);
		}
	}
	
	@Override
	public void handleRelease(int mouseX, int mouseY) 
	{
		this.drawing = false;
		
		int startX = xPosition + 1;
		int startY = yPosition + 1;
		int endX = startX + picture.getWidth() * picture.getPixelWidth() - 1;
		int endY = startY + picture.getHeight() * picture.getPixelHeight() - 1;
		if(GuiHelper.isMouseInside(mouseX, mouseY, startX, startY, endX, endY))
		{
			int pixelX = (mouseX - startX) / picture.getPixelWidth();
			int pixelY = (mouseY - startY) / picture.getPixelHeight();
			this.currentTool.handleRelease(this, pixelX, pixelY);
		}
	}
	
	@Override
	public void handleDrag(int mouseX, int mouseY) 
	{
		int startX = xPosition + 1;
		int startY = yPosition + 1;
		int endX = startX + picture.getWidth() * picture.getPixelWidth() - 1;
		int endY = startY + picture.getHeight() * picture.getPixelHeight() - 1;
		if(GuiHelper.isMouseInside(mouseX, mouseY, startX, startY, endX, endY))
		{
			int pixelX = (mouseX - startX) / picture.getPixelWidth();
			int pixelY = (mouseY - startY) / picture.getPixelHeight();
			this.currentTool.handleDrag(this, pixelX, pixelY);
		}
	}
	
	public int[][] getPixels()
	{
		return this.pixels;
	}
	
	public int getPixel(int x, int y)
	{
		return this.pixels[x][y];
	}
	
	public void setPixel(int x, int y, int colour)
	{
		this.pixels[x][y] = colour;
	}

	public boolean isExistingImage() 
	{
		return existingImage;
	}

	public void setColour(Color colour)
	{
		this.currentColour = colour.getRGB();
	}
	
	public void setColour(int colour)
	{
		this.currentColour = colour;
	}
	
	public void setRed(float red)
	{
		this.red = (int) (255 * Math.min(1.0, red));
		compileColour();
	}
	
	public void setGreen(float green)
	{
        this.green = (int) (255 * Math.min(1.0, green));
        compileColour();
	}
	
	public void setBlue(float blue)
	{
		this.blue = (int) (255 * Math.min(1.0, blue));
		compileColour();
	}
	
	public void compileColour()
	{
		 this.currentColour = ((255 & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 0);
	}
	
	public int getCurrentColour()
	{
		return currentColour;
	}
	
	public void setCurrentTool(Tool currentTool) 
	{
		this.currentTool = currentTool;
	}
	
	public void setShowGrid(boolean showGrid) 
	{
		this.showGrid = showGrid;
	}
	
	public int[][] copyPixels()
	{
		int[][] copiedPixels = new int[pixels.length][pixels.length];
		for(int i = 0; i < pixels.length; i++)
		{
			for(int j = 0; j < pixels.length; j++)
			{
				copiedPixels[j][i] = pixels[j][i];
			}
		}
		return copiedPixels;
	}
	
	public void clear()
	{
		if(pixels != null)
		{
			for(int i = 0; i < pixels.length; i++)
			{
				for(int j = 0; j < pixels[0].length; j++)
				{
					pixels[i][j] = 0; 
				}
			}
		}
	}
}