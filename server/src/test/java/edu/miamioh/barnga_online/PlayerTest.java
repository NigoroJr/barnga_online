package edu.miamioh.barnga_online;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class PlayerTest extends TestCase {
    private WorldState world;
    private BarngaOnlineConfigsDefault configs;

    public PlayerTest(String testName) {
        super(testName);

        world = new WorldState();
        configs = new BarngaOnlineConfigsDefault(world);
    }

    public static Test suite() {
        return new TestSuite(PlayerTest.class);
    }

    public void testPlayer() {
        // Player that belongs to Team 0
        Player p = new Player(42, 0, new Coordinates(42, 42), configs);

        assertTrue(p.appearsTo(0) == 0);
        assertTrue(p.appearsTo(1) == 2);
        assertTrue(p.appearsTo(2) == 0);
        assertTrue(p.appearsTo(3) == BarngaOnlineConfigsDefault.INVISIBLE);

        p = new Player(42, 1, new Coordinates(42, 42), configs);
        assertTrue(p.appearsTo(0) == BarngaOnlineConfigsDefault.INVISIBLE);
        assertTrue(p.appearsTo(1) == 1);
        assertTrue(p.appearsTo(2) == 1);
        assertTrue(p.appearsTo(3) == 1);

        p = new Player(42, 2, new Coordinates(42, 42), configs);
        assertTrue(p.appearsTo(0) == BarngaOnlineConfigsDefault.INVISIBLE);
        assertTrue(p.appearsTo(1) == 3);
        assertTrue(p.appearsTo(2) == 2);
        assertTrue(p.appearsTo(3) == BarngaOnlineConfigsDefault.INVISIBLE);

        p = new Player(42, 3, new Coordinates(42, 42), configs);
        assertTrue(p.appearsTo(0) == BarngaOnlineConfigsDefault.INVISIBLE);
        assertTrue(p.appearsTo(1) == 0);
        assertTrue(p.appearsTo(2) == 3);
        assertTrue(p.appearsTo(3) == 3);
    }

    public void testFoodEatable() {
        Coordinates coord = new Coordinates(0, 0);
        Coordinates foodCoord = new Coordinates(coord);
        Player p = new Player(42, 0, coord, configs);

        Food f = new Food(42, 0, foodCoord, configs);
        assertTrue(p.canEat(f));

        f.coord.x = p.coord.x + Player.VALID_RANGE - 1;
        assertTrue(p.canEat(f));

        f.coord.x = p.coord.x + Player.VALID_RANGE + 1;
        assertFalse(p.canEat(f));

        f.coord.x = Player.VALID_RANGE;
        f.coord.y = Player.VALID_RANGE;
        assertFalse(p.canEat(f));
    }
}
