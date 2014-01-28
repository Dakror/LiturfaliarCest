package de.dakror.liturfaliarcest.layer;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;

import de.dakror.gamesetup.layer.Layer;
import de.dakror.gamesetup.util.Helper;
import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.settings.Attributes.Attribute;

/**
 * @author Dakror
 */
public class HUDLayer extends Layer
{
	float staminaAlpha;
	
	@Override
	public void init()
	{
		staminaAlpha = 0;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		Helper.drawProgressBar(Game.getWidth() / 2 - 300, Game.getHeight() - 80, 300, Game.player.getAttributes().get(Attribute.HEALTH) / Game.player.getAttributes().get(Attribute.HEALTH_MAX), "ff3232", g);
		if (Game.player.getAttributes().get(Attribute.MANA_MAX) > 0) Helper.drawProgressBar(Game.getWidth() / 2, Game.getHeight() - 80, 300, Game.player.getAttributes().get(Attribute.MANA) / Game.player.getAttributes().get(Attribute.MANA_MAX), "009ab8", g);
		
		Composite c = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, staminaAlpha));
		Helper.drawProgressBar(Game.getWidth() / 2 - 300, Game.getHeight() - 62, 300, Game.player.getAttributes().get(Attribute.STAMINA) / Game.player.getAttributes().get(Attribute.STAMINA_MAX), "ffc744", g);
		g.setComposite(c);
	}
	
	@Override
	public void update(int tick)
	{
		if (Game.player.getAttributes().get(Attribute.STAMINA) < Game.player.getAttributes().get(Attribute.STAMINA_MAX) && staminaAlpha < 1) staminaAlpha += 0.1f;
		if (Game.player.getAttributes().get(Attribute.STAMINA) == Game.player.getAttributes().get(Attribute.STAMINA_MAX) && staminaAlpha > 0) staminaAlpha -= 0.05f;
		
		if (staminaAlpha > 1) staminaAlpha = 1;
		if (staminaAlpha < 0) staminaAlpha = 0;
	}
}
