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
 

package de.dakror.liturfaliarcest.settings;

/**
 * An object - representation of a jsonarray talk for the JS Engine
 * 
 * @author Dakror
 */
public class Talk {
	public int index;
	public int step;
	public String flags;
	public String modifiers;
	public String text;
	
	public Talk(int index, int step, String flags, String modifiers, String text) {
		this.index = index;
		this.step = step;
		this.flags = flags;
		this.modifiers = modifiers;
		this.text = text;
	}
	
	public Talk() {
		this(0, 0, "", "", "");
	}
	
	public Talk(String over, boolean ok) {
		this(-1024 - (ok ? 0 : 1024), 0, "", "", "");
	}
}
