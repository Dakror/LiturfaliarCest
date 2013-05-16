package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;

import de.dakror.liturfaliar.util.Assistant;

public class HelpOverlay
{
  private static HelpOverlayContainer[] hoc     = new HelpOverlayContainer[] {};
  private static boolean                visible = false;
  
  public static void add(HelpOverlayContainer... container)
  {
    if (HelpOverlay.hoc != null && HelpOverlay.hoc.length > 0)
    {
      ArrayList<HelpOverlayContainer> c = new ArrayList<HelpOverlayContainer>(Arrays.asList(HelpOverlay.hoc));
      c.addAll(Arrays.asList(container));
      HelpOverlay.hoc = c.toArray(HelpOverlay.hoc);
    }
    else
    {
      HelpOverlay.hoc = container;
    }
  }
  
  public static void add(HelpOverlayContainer container)
  {
    if (HelpOverlay.hoc != null && HelpOverlay.hoc.length > 0)
    {
      ArrayList<HelpOverlayContainer> c = new ArrayList<HelpOverlayContainer>(Arrays.asList(HelpOverlay.hoc));
      c.add(container);
      HelpOverlay.hoc = c.toArray(HelpOverlay.hoc);
    }
    else
    {
      HelpOverlay.hoc = new HelpOverlayContainer[] { container };
    }
  }
  
  public static void clear()
  {
    HelpOverlay.hoc = null;
  }
  
  public static void draw(Graphics2D g, Window w)
  {
    if (!HelpOverlay.visible)
      return;
    Area shadowed = new Area(w.getBounds());
    if (HelpOverlay.hoc == null || HelpOverlay.hoc.length == 0)
      return;
    for (HelpOverlayContainer h : HelpOverlay.hoc)
    {
      shadowed.subtract(h.getArea());
      if (h != null)
        h.draw(g, w);
    }
    Assistant.Shadow(shadowed, Color.decode("#222222"), 0.8f, g);
  }
  
  public static void keyPressed(KeyEvent e)
  {
    if (e.getExtendedKeyCode() == KeyEvent.VK_F1)
    {
      HelpOverlay.visible = true;
    }
  }
  
  public static void keyReleased(KeyEvent e)
  {
    if (e.getExtendedKeyCode() == KeyEvent.VK_F1)
    {
      HelpOverlay.visible = false;
    }
  }
}
