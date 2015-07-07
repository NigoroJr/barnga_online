package edu.miamioh.culturecode;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class TeamTest extends TestCase {
    WorldState world = new WorldState();
    CulturecodeConfigsDefault configs = new CulturecodeConfigsDefault(world);

    public TeamTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TeamTest.class);
    }

    public void testAppearsTo() {
        Team<Player> t;

        t = new Team<Player>(0, configs);
        assertTrue(t.appearsTo(0) == 0);
        assertTrue(t.appearsTo(1) == 2);
        assertTrue(t.appearsTo(2) == 0);
        assertTrue(t.appearsTo(3) == CulturecodeConfigsDefault.INVISIBLE);

        t = new Team<Player>(1, configs);
        assertTrue(t.appearsTo(0) == CulturecodeConfigsDefault.INVISIBLE);
        assertTrue(t.appearsTo(1) == 1);
        assertTrue(t.appearsTo(2) == 1);
        assertTrue(t.appearsTo(3) == 1);

        t = new Team<Player>(2, configs);
        assertTrue(t.appearsTo(0) == CulturecodeConfigsDefault.INVISIBLE);
        assertTrue(t.appearsTo(1) == 3);
        assertTrue(t.appearsTo(2) == 2);
        assertTrue(t.appearsTo(3) == CulturecodeConfigsDefault.INVISIBLE);

        t = new Team<Player>(3, configs);
        assertTrue(t.appearsTo(0) == CulturecodeConfigsDefault.INVISIBLE);
        assertTrue(t.appearsTo(1) == 0);
        assertTrue(t.appearsTo(2) == 3);
        assertTrue(t.appearsTo(3) == 3);
    }

    public void testCanSeeFood() {
        // Can see food?
        Food f;
        Team<Player> t;
        world.addPlayer(new Player(42, 0, new Coordinates(42, 42), configs), 0);
        world.addPlayer(new Player(42, 1, new Coordinates(42, 42), configs), 1);
        world.addPlayer(new Player(42, 2, new Coordinates(42, 42), configs), 2);
        world.addPlayer(new Player(42, 3, new Coordinates(42, 42), configs), 3);

        // Team 0
        t = world.getTeam(0);
        f = new Food(42, 0, new Coordinates(42, 42), configs);
        assertTrue(t.canSee(f));

        f = new Food(42, 1, new Coordinates(42, 42), configs);
        assertTrue(!t.canSee(f));

        // Team 3
        t = world.getTeam(3);
        f = new Food(42, 1, new Coordinates(42, 42), configs);
        assertTrue(t.canSee(f));
    }

    public void testCanSeePlayer() {
        Player target;
        Team<Player> t;
        world.addPlayer(new Player(42, 0, new Coordinates(42, 42), configs), 0);
        world.addPlayer(new Player(42, 1, new Coordinates(42, 42), configs), 1);
        world.addPlayer(new Player(42, 2, new Coordinates(42, 42), configs), 2);
        world.addPlayer(new Player(42, 3, new Coordinates(42, 42), configs), 3);

        t = world.getTeam(0);
        target = new Player(42, 0, new Coordinates(42, 42), configs);
        assertTrue(t.canSee(target));
        target = new Player(42, 1, new Coordinates(42, 42), configs);
        assertTrue(!t.canSee(target));
        target = new Player(42, 2, new Coordinates(42, 42), configs);
        assertTrue(!t.canSee(target));
        target = new Player(42, 3, new Coordinates(42, 42), configs);
        assertTrue(!t.canSee(target));

        t = world.getTeam(1);
        target = new Player(42, 0, new Coordinates(42, 42), configs);
        assertTrue(t.canSee(target));
        target = new Player(42, 1, new Coordinates(42, 42), configs);
        assertTrue(t.canSee(target));
        target = new Player(42, 2, new Coordinates(42, 42), configs);
        assertTrue(t.canSee(target));
        target = new Player(42, 3, new Coordinates(42, 42), configs);
        assertTrue(t.canSee(target));
    }
}
