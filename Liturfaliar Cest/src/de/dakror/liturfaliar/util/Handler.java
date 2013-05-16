package de.dakror.liturfaliar.util;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

public class Handler
{
  private static boolean             listenersEnabled = true;
  private static ArrayList<Listener> listeners        = new ArrayList<Listener>();
  private static ArrayList<Listener> disabled         = new ArrayList<Listener>();
  
  public static boolean areListenersEnabled()
  {
    return listenersEnabled;
  }
  
  public static void setListenersEnabled(boolean listenersEnabled)
  {
    Handler.listenersEnabled = listenersEnabled;
  }
  
  public static void addListener(Listener l)
  {
    Handler.listeners.add(l);
  }
  
  public static void setListenerEnabled(Listener l, boolean e)
  {
    if (e)
    {
      if (Handler.disabled.contains(l))
      {
        Handler.listeners.add(l);
        Handler.disabled.remove(Handler.disabled.indexOf(l));
      }
      else Handler.listeners.add(l);
    }
    else
    {
      if (Handler.listeners.contains(l))
      {
        Handler.disabled.add(l);
        Handler.listeners.remove(Handler.listeners.indexOf(l));
      }
      else Handler.disabled.add(l);
    }
  }
  
  public static void removeListener(Listener l)
  {
    if (Handler.listeners.indexOf(l) > -1)
    {
      Handler.listeners.set(Handler.listeners.indexOf(l), null);
    }
  }
  
  public static void keyTyped(KeyEvent e)
  {
    try
    {
      for (Listener l : Handler.listeners)
      {
        if (l != null)
          l.keyTyped(e);
      }
    }
    catch (Exception e1)
    {
      return;
    }
  }
  
  public static void keyPressed(KeyEvent e)
  {
    if (!Handler.listenersEnabled)
      return;
    try
    {
      for (Listener l : Handler.listeners)
      {
        if (l != null)
          l.keyPressed(e);
      }
    }
    catch (Exception e1)
    {
      return;
    }
  }
  
  public static void keyReleased(KeyEvent e)
  {
    if (!Handler.listenersEnabled)
      return;
    try
    {
      for (Listener l : Handler.listeners)
      {
        if (l != null)
          l.keyReleased(e);
      }
    }
    catch (Exception e1)
    {
      return;
    }
  }
  
  public static void mouseWheelMoved(MouseWheelEvent e)
  {
    if (!Handler.listenersEnabled)
      return;
    try
    {
      for (Listener l : Handler.listeners)
      {
        if (l != null)
          l.mouseWheelMoved(e);
      }
    }
    catch (Exception e1)
    {
      return;
    }
  }
  
  public static void mouseDragged(MouseEvent e)
  {
    if (!Handler.listenersEnabled)
      return;
    try
    {
      for (Listener l : Handler.listeners)
      {
        if (l != null)
          l.mouseDragged(e);
      }
    }
    catch (Exception e1)
    {
      return;
    }
  }
  
  public static void mouseMoved(MouseEvent e)
  {
    if (!Handler.listenersEnabled)
      return;
    try
    {
      for (Listener l : Handler.listeners)
      {
        if (l != null)
          l.mouseMoved(e);
      }
    }
    catch (Exception e1)
    {
      return;
    }
  }
  
  public static void mouseClicked(MouseEvent e)
  {
    if (!Handler.listenersEnabled)
      return;
    try
    {
      for (Listener l : Handler.listeners)
      {
        if (l != null)
          l.mouseClicked(e);
      }
    }
    catch (Exception e1)
    {
      return;
    }
  }
  
  public static void mousePressed(MouseEvent e)
  {
    if (!Handler.listenersEnabled)
      return;
    try
    {
      for (Listener l : Handler.listeners)
      {
        if (l != null)
          l.mousePressed(e);
      }
    }
    catch (Exception e1)
    {
      return;
    }
  }
  
  public static void mouseReleased(MouseEvent e)
  {
    if (!Handler.listenersEnabled)
      return;
    try
    {
      for (Listener l : Handler.listeners)
      {
        if (l != null)
          l.mouseReleased(e);
      }
    }
    catch (Exception e1)
    {
      return;
    }
  }
  
  public static void mouseEntered(MouseEvent e)
  {
    if (!Handler.listenersEnabled)
      return;
    try
    {
      for (Listener l : Handler.listeners)
      {
        if (l != null)
          l.mouseEntered(e);
      }
    }
    catch (Exception e1)
    {
      return;
    }
  }
  
  public static void mouseExited(MouseEvent e)
  {
    if (!Handler.listenersEnabled)
      return;
    try
    {
      for (Listener l : Handler.listeners)
      {
        if (l != null)
          l.mouseExited(e);
      }
    }
    catch (Exception e1)
    {
      return;
    }
  }
}
