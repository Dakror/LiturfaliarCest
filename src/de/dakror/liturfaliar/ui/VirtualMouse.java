package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import de.dakror.liturfaliar.Viewport;

public class VirtualMouse extends Component
{
	private boolean mouse1, mouse2, mouse3;
	
	public VirtualMouse(int x, int y)
	{
		super(x, y, 99, 136);
		this.mouse1 = this.mouse2 = this.mouse3 = false;
	}
	
	@Override
	public void update()
	{}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.drawImage(Viewport.loadImage("system/mouse.png"), getX(), getY(), getWidth(), getHeight(), Viewport.w);
		if (this.mouse1) g.drawImage(Viewport.loadImage("system/mouse1.png"), getX(), getY(), getWidth(), getHeight(), Viewport.w);
		if (this.mouse2) g.drawImage(Viewport.loadImage("system/mouse2.png"), getX(), getY(), getWidth(), getHeight(), Viewport.w);
		if (this.mouse3) g.drawImage(Viewport.loadImage("system/mouse3.png"), getX(), getY(), getWidth(), getHeight(), Viewport.w);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == 1) this.mouse1 = true;
		if (e.getButton() == 3) this.mouse2 = true;
		if (e.getButton() == 2) this.mouse3 = true;
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == 1) this.mouse1 = false;
		if (e.getButton() == 3) this.mouse2 = false;
		if (e.getButton() == 2) this.mouse3 = false;
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{}
	
	@Override
	public void mouseExited(MouseEvent e)
	{}
	
	@Override
	public void keyTyped(KeyEvent e)
	{}
	
	@Override
	public void keyPressed(KeyEvent e)
	{}
	
	@Override
	public void keyReleased(KeyEvent e)
	{}
}
