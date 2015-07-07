package edu.miamioh.culturecode;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class FoodTest extends TestCase {
    public static final int I = CulturecodeConfigsDefault.INVISIBLE;

    private WorldState world = new WorldState();
    private CulturecodeConfigsDefault configs = new CulturecodeConfigsDefault(world);

    public FoodTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(FoodTest.class);
    }

    public void testFood() {
        Food f;

        f = new Food(42, 0, new Coordinates(42, 42), configs);
        assertTrue(f.appearsTo(0) == 0);
        assertTrue(f.appearsTo(1) == I);
        assertTrue(f.appearsTo(2) == 0);
        assertTrue(f.appearsTo(3) == 3);

        f = new Food(42, 1, new Coordinates(42, 42), configs);
        assertTrue(f.appearsTo(0) == I);
        assertTrue(f.appearsTo(1) == 1);
        assertTrue(f.appearsTo(2) == 1);
        assertTrue(f.appearsTo(3) == 3);

        f = new Food(42, 2, new Coordinates(42, 42), configs);
        assertTrue(f.appearsTo(0) == 2);
        assertTrue(f.appearsTo(1) == I);
        assertTrue(f.appearsTo(2) == 2);
        assertTrue(f.appearsTo(3) == 3);

        f = new Food(42, 3, new Coordinates(42, 42), configs);
        assertTrue(f.appearsTo(0) == I);
        assertTrue(f.appearsTo(1) == I);
        assertTrue(f.appearsTo(2) == 3);
        assertTrue(f.appearsTo(3) == 3);
    }
}
