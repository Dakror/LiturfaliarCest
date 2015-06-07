/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.dakror.liturfaliarcest.game.entity.creature;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.dakror.liturfaliarcest.game.Game;
import de.dakror.liturfaliarcest.game.entity.Entity;
import de.dakror.liturfaliarcest.settings.Attributes.Attribute;

import org.json.JSONObject;

import de.dakror.gamesetup.util.Helper;
import de.dakror.gamesetup.util.Vector;

/**
 * @author Dakror
 */
public abstract class Creature extends Entity {
	protected String tex;
	int startTick, frame, dir, questIcon, emoticonFrame;
	
	public Creature(int x, int y, int width, int height, JSONObject meta) {
		super(x, y, width, height, meta);
		questIcon = -1;
	}
	
	@Override
	public void draw(Graphics2D g) {
		if (tex.length() == 0) return;
		
		Helper.setRenderingHints(g, false);
		
		BufferedImage img = Game.getImage(tex);
		
		Helper.drawImage(img, x, y, width, height, frame * img.getWidth() / 4, dir * img.getHeight() / 4, img.getWidth() / 4, img.getHeight() / 4, g);
		
		Helper.setRenderingHints(g, true);
		
		if (questIcon > -1) {
			int size = 32;
			Helper.drawImage(Game.getImage("system/emoticon.png"), x + width / 2, y - size, size, size, emoticonFrame * 32, questIcon * 32, 32, 32, g);
		}
		
		// g.drawRect(x + bumpX, y + bumpY, bumpWidth, bumpHeight);
	}
	
	@Override
	protected void tick(int tick) {
		if (questIcon > -1 && tick % 5 == 0) emoticonFrame = (emoticonFrame + 1) % 7;
		
		if (!frozen) {
			if (target != null && startTick == 0) {
				startTick = tick;
				return;
			}
			
			if (target != null && (tick - startTick) % (30 / attr.get(Attribute.SPEED)) == 0) frame = (frame + 1) % 4;
			
			if (target != null) {
				float degs = pos.clone().add(new Vector(width / 2, height / 2)).sub(target.clone().add(new Vector(width / 2, height / 2))).getAngleOnXAxis();
				if (degs < 0) degs += 360;
				
				if (degs < 45 || degs > 315) dir = 1;
				else if (degs > 135 && degs < 225) dir = 2;
				else if (degs > 45 && degs < 135) dir = 3;
				else dir = 0;
			}
		}
	}
	
	public String getTexture() {
		return tex;
	}
}
