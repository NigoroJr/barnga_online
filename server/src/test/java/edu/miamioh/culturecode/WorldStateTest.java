package edu.miamioh.culturecode;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class WorldStateTest extends TestCase {
    private WorldState world;
    private CulturecodeConfigsDefault configs;
    private Player p, p2, p3, p4, p5;

    public WorldStateTest(String testName) {
        super(testName);

        world = new WorldState();
        configs = new CulturecodeConfigsDefault(world);

        Coordinates coord = new Coordinates(0, 0);
        p = new Player(1, 0, coord, configs);

        Coordinates p2Coord = new Coordinates(coord.x + 10, coord.y);
        p2 = new Player(2, 0, p2Coord, configs);

        Coordinates p3Coord = new Coordinates(coord.x + 30, coord.y);
        p3 = new Player(3, 1, p3Coord, configs);

        Coordinates p4Coord = new Coordinates(coord.x + 15, coord.y);
        p4 = new Player(4, 1, p4Coord, configs);

        Coordinates p5Coord = new Coordinates(coord.x + 100, coord.y);
        p5 = new Player(5, 2, p5Coord, configs);

        world.addPlayer(p, 0);
        world.addPlayer(p2, 0);
        world.addPlayer(p3, 1);
        world.addPlayer(p4, 1);
        world.addPlayer(p5, 2);
    }

    public static Test suite() {
        return new TestSuite(WorldStateTest.class);
    }

    public void testPlayersNear() {
        assertTrue(world.playersNear(p.coord, 20).size() == 3);
        assertTrue(world.playersNear(p3.coord, 200).size() == 5);
    }

    public void testVisiblePlayerNear() {
        assertTrue(world.visiblePlayerNear(p, 20) == p2);
        // Team 1 can see team 0
        Player tmp = world.visiblePlayerNear(p4, 20);
        assertTrue(tmp == p || tmp == p2 || tmp == p3);
        // p5 is alone!
        assertTrue(world.visiblePlayerNear(p5, 5) == null);

        // Team 2 can see team 0
        assertTrue(world.visiblePlayerNear(p5, 75) == p3);
    }
}
