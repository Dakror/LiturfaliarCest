package de.dakror.liturfaliar.ui;

import java.awt.Graphics2D;

import de.dakror.liturfaliar.Viewport;
import de.dakror.liturfaliar.settings.CFG;

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
    if(raw.lastIndexOf("(") == -1) return;
    String rawString = raw.substring(raw.lastIndexOf("(") + 1, raw.lastIndexOf(")"));
    raw = raw.substring(0, raw.lastIndexOf("("));
    CFG.p(raw);
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
