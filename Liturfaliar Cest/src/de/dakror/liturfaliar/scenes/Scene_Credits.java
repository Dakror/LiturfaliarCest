package de.dakror.liturfaliar.scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.json.JSONArray;
import org.json.JSONException;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.util.Assistant;

public class Scene_Credits implements Scene
{
  int       height;
  int       lastY;
  JSONArray credits;
  Viewport  v;
  
  @Override
  public void init(Viewport v)
  {
    this.v = v;
    v.play();
    try
    {
      credits = new JSONArray(Assistant.getURLContent(getClass().getResource("/json/credits.json")));
      for (int i = 0; i < credits.length(); i++)
      {
        height += 45 + credits.getJSONObject(i).getString("name").length() * 30;
      }
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    lastY = 0;
  }
  
  @Override
  public void update(long timePassed)
  {}
  
  @Override
  public void draw(Graphics2D g)
  {
    Font old = g.getFont();
    g.setFont(new Font("Arial", old.getStyle(), old.getSize()));
    lastY = 0;
    try
    {
      for (int i = 0; i < credits.length(); i++)
      {
        int nx = Assistant.drawHorizontallyCenteredString(credits.getJSONObject(i).getString("name"), v.w.getWidth(), lastY + 35, g, 30, Color.decode("#994444"));
        Font f = g.getFont();
        g.setFont(f.deriveFont(30.0f));
        Assistant.Rect(nx - g.getFontMetrics().stringWidth(credits.getJSONObject(i).getString("name")), lastY + 40, g.getFontMetrics().stringWidth(credits.getJSONObject(i).getString("name")), 1 / 2, Color.decode("#994444"), Color.decode("#994444"), g);
        g.setFont(f);
        lastY += 40;
        for (int j = 0; j < credits.getJSONObject(i).getJSONArray("list").length(); j++)
        {
          Assistant.drawHorizontallyCenteredString(credits.getJSONObject(i).getJSONArray("list").getString(j), v.w.getWidth(), lastY + 25, g, 20, Color.white);
          lastY += 25;
        }
      }
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    g.setFont(old);
  }
  
  @Override
  public void keyTyped(KeyEvent e)
  {}
  
  @Override
  public void keyPressed(KeyEvent e)
  {
    switch (e.getExtendedKeyCode())
    {
      case KeyEvent.VK_ESCAPE:
        v.playSound("002-System02");
        v.setScene(new Scene_MainMenu());
        break;
    }
  }
  
  @Override
  public void keyReleased(KeyEvent e)
  {}
  
  @Override
  public void mouseClicked(MouseEvent e)
  {}
  
  @Override
  public void mousePressed(MouseEvent e)
  {}
  
  @Override
  public void mouseReleased(MouseEvent e)
  {}
  
  @Override
  public void mouseEntered(MouseEvent e)
  {}
  
  @Override
  public void mouseExited(MouseEvent e)
  {}
  
  @Override
  public void mouseDragged(MouseEvent e)
  {}
  
  @Override
  public void mouseMoved(MouseEvent e)
  {}
  
  @Override
  public void mouseWheelMoved(MouseWheelEvent e)
  {}
}
