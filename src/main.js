var game, tilesize;

/**
 * Teleports the given entity to a given location on the active map
 * 
 * @param e - Entity to teleport
 * @param x - new X Position
 * @param y - new Y Position
 * @param bumpPos - if position should be set for bumpRectangle or for top-left Corner of e
 */
function teleport(e, x, y, bumpPos)
{
	e.setX(x - (bumpPos ? e.bumpX : 0));
	e.setY(y - (bumpPos ? e.bumpY : 0));
}

/**
 * Teleports the player to a given location on a different map
 * 
 * @param x - new X Position
 * @param y - new Y Position
 * @param map - name of new map
 * @param bumpPos - if position should be set for bumpRectangle or for top-left Corner of e
 */
function teleportMap(x, y, map, bumpPos)
{
	game.fadeTo(1.0, 0.05);
	game.actionOnFade = "function() {game.setWorld('" + map + "'); teleport(game.player, " + x + ", " + y + ", " + bumpPos + ");game.fadeTo(0, 0.05);}";
}
