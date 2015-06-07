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


package de.dakror.liturfaliarcest.game.animation;

import java.awt.Graphics2D;

import org.json.JSONException;
import org.json.JSONObject;

import de.dakror.liturfaliarcest.game.entity.Entity;

/**
 * @author Dakror
 */
public class AnimationSpot extends Entity {
	Animation anim;
	
	public AnimationSpot(int x, int y, JSONObject meta) throws JSONException {
		super(x, y, 0, 0, meta);
		anim = Animation.getAnimationInstance(meta.getInt("animID"));
		width = meta.has("width") ? meta.getInt("width") : anim.getDefaultWidth();
		height = meta.has("height") ? meta.getInt("height") : anim.getDefaultHeight();
		anim.init(width, height, meta.has("smooth") ? meta.getBoolean("smooth") : false, meta.has("endless") ? meta.getBoolean("endless") : true);
		if (meta.has("randomIndex") && meta.getBoolean("randomIndex")) anim.randomizeIndex();
	}
	
	@Override
	public void draw(Graphics2D g) {
		anim.draw(x, y, g);
	}
	
	@Override
	protected void tick(int tick) {
		anim.update(tick);
		if (anim.isDone()) kill();
	}
}
