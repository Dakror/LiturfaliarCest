package de.dakror.liturfaliarcest.layer;

import java.awt.Graphics2D;

import org.json.JSONArray;
import org.json.JSONException;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.entity.creature.NPC;
import de.dakror.liturfaliarcest.settings.FlagManager;

/**
 * @author Dakror
 */
public class TalkLayer extends Layer
{
	NPC source;
	JSONArray talk;
	String activeText;
	int index;
	
	public TalkLayer(JSONArray t, NPC s)
	{
		talk = t;
		source = s;
		index = -1;
		s.setFrozen(true);
		Game.player.setFrozen(true);
	}
	
	@Override
	public void init()
	{
		next();
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		if (activeText != null)
		{
			Helper.drawContainer(50, Game.getHeight() - 200, Game.getWidth() - 100, 175, true, true, g);
			Helper.drawStringWrapped(activeText, 75, Game.getHeight() - 160, Game.getWidth() - 100, g, 30);
		}
	}
	
	@Override
	public void update(int tick)
	{}
	
	public void next()
	{
		if (index == talk.length() - 1)
		{
			source.setFrozen(false);
			Game.player.setFrozen(false);
			Game.currentGame.removeLayer(this);
			return;
		}
		
		try
		{
			index++;
			
			JSONArray a = talk.getJSONArray(index);
			for (int i = 0; i < a.length(); i++)
			{
				JSONArray o = a.getJSONArray(i);
				if (o.getString(0).length() == 0 || FlagManager.matchesFlags(o.getString(0)))
				{
					activeText = o.getString(2);
					String activeName = o.getString(1);
					if (source.getMeta().has("name"))
					{
						activeName = activeName.replace("%e", source.getMeta().getString("name"));
						activeText = activeName + ": " + activeText;
					}
					
					break;
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
}
