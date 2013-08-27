package de.dakror.liturfaliar.ovscenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.scenes.Scene_Game;
import de.dakror.liturfaliar.scenes.Scene_LoadGame;
import de.dakror.liturfaliar.scenes.Scene_MainMenu;
import de.dakror.liturfaliar.ui.Button;
import de.dakror.liturfaliar.ui.Container;
import de.dakror.liturfaliar.util.Assistant;

public class OVScene_Death extends OVScene
{
	Container c1;
	Scene_Game sg;
	Button load;
	Button quit;
	
	public OVScene_Death(Scene_Game sg)
	{
		this.sg = sg;
	}
	
	@Override
	public void construct()
	{
		c1 = new Container(0, 0, Viewport.w.getWidth(), 55);
		c1.tileset = null;
		load = new Button(Viewport.w.getWidth() / 4 - 40, Viewport.w.getHeight() / 2 - 60, Viewport.w.getWidth() / 4, "Spiel laden", Color.white, 60f);
		quit = new Button(Viewport.w.getWidth() / 2 + 40, Viewport.w.getHeight() / 2 - 60, Viewport.w.getWidth() / 4, "zum Hauptmenü", Color.white, 60f);
	}
	
	@Override
	public void destruct()
	{}
	
	@Override
	public void update(long timePassed)
	{
		load.update();
		quit.update();
		
		if (load.getState() == 1)
		{
			Viewport.setScene(new Scene_LoadGame());
		}
		if (quit.getState() == 1)
		{
			Viewport.setScene(new Scene_MainMenu());
		}
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Assistant.Shadow(Viewport.w.getBounds(), Color.black, 0.6f, g);
		c1.draw(g);
		Assistant.drawHorizontallyCenteredString("Du bist tot!", Viewport.w.getWidth(), 43, g, 45, Color.white);
		g.drawImage(Viewport.loadImage("system/tombstone.png"), Viewport.w.getWidth() / 2 - 16, Viewport.w.getHeight() / 2 - 32, Viewport.w);
		load.draw(g);
		quit.draw(g);
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		load.mouseMoved(e);
		quit.mouseMoved(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		load.mousePressed(e);
		quit.mousePressed(e);
		
		construct();
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		load.mouseReleased(e);
		quit.mouseReleased(e);
	}
}
