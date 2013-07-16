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
  Container  c1;
  Scene_Game sg;
  Button     load;
  Button     quit;
  
  public OVScene_Death(Scene_Game sg)
  {
    this.sg = sg;
  }
  
  @Override
  public void construct(Viewport v)
  {
    this.v = v;
    c1 = new Container(0, 0, v.w.getWidth(), 55);
    c1.tileset = null;
    load = new Button(v.w.getWidth() / 4 - 40, v.w.getHeight() / 2 - 60, v.w.getWidth() / 4, "Spiel laden", Color.white, 60f);
    quit = new Button(v.w.getWidth() / 2 + 40, v.w.getHeight() / 2 - 60, v.w.getWidth() / 4, "zum Hauptmenü", Color.white, 60f);
  }
  
  @Override
  public void destruct()
  {}
  
  @Override
  public void update(long timePassed)
  {
    load.update();
    quit.update();
    
    if(load.getState() == 1) {
      v.setScene(new Scene_LoadGame());
    }
    if(quit.getState() == 1) {
      v.setScene(new Scene_MainMenu());
    }
  }
  
  @Override
  public void draw(Graphics2D g)
  {
    Assistant.Shadow(v.w.getBounds(), Color.black, 0.6f, g);
    c1.draw(g, v);
    Assistant.drawHorizontallyCenteredString("Du bist tot!", v.w.getWidth(), 43, g, 45, Color.white);
    g.drawImage(Viewport.loadImage("system/tombstone.png"), v.w.getWidth() / 2 - 16, v.w.getHeight() / 2 - 32, v.w);
    load.draw(g, v);
    quit.draw(g, v);
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
    
    construct(v);
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    load.mouseReleased(e);
    quit.mouseReleased(e);
  }
}
