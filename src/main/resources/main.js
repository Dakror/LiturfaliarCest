importClass(Packages.de.dakror.liturfaliarcest.settings.FlagManager);
importClass(Packages.de.dakror.liturfaliarcest.game.Game);

var game, tilesize;
/**
 * Teleports the given entity to a given location on the active map
 * 
 * @param e {(Entity|int)} - Entity (-UID) to teleport
 * @param x {int} - new X Position
 * @param y {int} - new Y Position
 * @param bumpPos {boolean} - if position should be set for bumpRectangle or for top-left Corner of e
 */
function teleport(e, x, y, bumpPos)
{
	if ((typeof e) == "number") e = Game.world.getEntityForUID(parseInt(e));
	e.setX(x - (bumpPos ? e.bumpX : 0));
	e.setY(y - (bumpPos ? e.bumpY : 0));
}

/**
 * Teleports the player to a given location on a different map
 * 
 * @param x {int} - new X Position
 * @param y {int} - new Y Position
 * @param map {String} - name of new map
 * @param bumpPos {boolean} - if position should be set for bumpRectangle or for top-left Corner of e
 */
function teleportMap(x, y, map, bumpPos)
{
	game.fadeTo(1.0, 0.05);
	game.actionOnFade = "function() {game.setWorld('" + map + "'); teleport(game.player, " + x + ", " + y + ", " + bumpPos + ");game.fadeTo(0, 0.05);}";
}

/**
 * Sets the given flag in the Flag System if it has not been set yet.
 * 
 * @param flag {String} - name of the flag, preferrably in captial letters
 */
function setFlag(flag)
{
	FlagManager.setFlag(flag);
}

/**
 * Adds the given flag to the Flag System.
 * 
 * @param flag {String} - name of the flag, preferrably in captial letters
 */
function addFlag(flag)
{
	FlagManager.addFlag(flag);
}

/**
 * Removes the given flag from the Flag System.
 * 
 * @param flag {String} - name of the flag, preferrably in captial letters
 */
function removeFlag(flag)
{
	FlagManager.removeFlag(flag);
}

/**
 * Returns the state of the given flag.
 * 
 * @param flag {String} - name of the flag, preferrably in captial letters
 * @returns boolean
 */
function isFlag(flag)
{
	return FlagManager.isFlag(flag);
}

/**
 * Returns the amount this flag occurs in the Flag System.
 * @param flag {String} - name of the flag, preferrably in captial letters
 * @returns int
 */
function countFlag(flag){
	return FlagManager.countFlag(flag);
}

/**
 * Checks if the given flag expression is matching
 * 
 * @param flags {String} - flag expression to be matched against
 * @returns boolean
 */
function matches(flags)
{
	return FlagManager.matchesFlags(flags);
}

/**
 * Manually triggers the talk of the given entity
 * 
 * @param {(Entity|int)} entity - Entity (-UID) whose talk will be started
 */
function talk(entity)
{
	if ((typeof entity) == "number") entity = Game.world.getEntityForUID(parseInt(entity));
	game.player.startTalk(entity);
}

/**
 * Manually ends the active talk
 */
function endTalk()
{
	game.endTalk();
}
