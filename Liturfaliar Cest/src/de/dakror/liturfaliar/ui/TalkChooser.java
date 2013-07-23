package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;

public class TalkChooser extends Component
{
  String   raw;
  Button[] choices;
  
  public TalkChooser(String r)
  {
    super(0, 0, 0, 0);
    raw = r;
    
    parse();
  }
  
  public void parse()
  {
    return;
  }
  
  public String getClearedString()
  {
    return raw;
  }
  
  @Override
  public void update()
  {}
  
  @Override
  public void draw(Graphics2D g, Viewport v)
  {}
  
}
