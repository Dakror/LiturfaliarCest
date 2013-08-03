package de.dakror.liturfaliar.ui;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseEvent;

public class CountButton extends Button
{
  private int value;
  int         min;
  int         max;
  
  public CountButton(int x, int y, int w, int h, int init, int min, int max, Image bg)
  {
    super(x, y, w, h, 0, 0, bg.getWidth(null), bg.getHeight(null), bg);
    this.setValue(init);
    this.min = min;
    this.max = max;
    this.tileset = null;
    this.hovermod = 4;
    this.clickmod = 4;
    this.size = 22;
    this.round = false;
    this.imagelower = true;
    this.centered = true;
    this.c = Color.white;
    this.handle.allowRightClick = true;
    this.soundMOVER = false;
  }
  
  public void update()
  {
    super.update();
    this.title = "" + this.getValue();
  }
  
  @Override
  public void mouseReleased(MouseEvent e)
  {
    HelpOverlayClicked(e, (this.title != null) ? this.title : "countbutton");
    if (!this.disabled)
    {
      this.handle.mouseReleased(e);
      if (this.handle.state == 1)
      {
        if (e.getButton() == 1 && this.getValue() < this.max) this.setValue(this.getValue() + 1);
        if (e.getButton() == 3 && this.getValue() > this.min) this.setValue(this.getValue() - 1);
      }
    }
  }
  
  public int getValue()
  {
    return value;
  }
  
  public void setValue(int value)
  {
    this.value = value;
  }
}
