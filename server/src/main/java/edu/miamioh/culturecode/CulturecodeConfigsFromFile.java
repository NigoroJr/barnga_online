package edu.miamioh.culturecode;

import java.io.File;
import java.util.List;

import com.moandjiezana.toml.Toml;

public class CulturecodeConfigsFromFile extends CulturecodeConfigsDefault {
    protected Toml toml;

    public CulturecodeConfigsFromFile(WorldState world, String fileName) {
        super(world);

        readFile(fileName);
    }

    public void readFile(String filename) {
        toml = new Toml();

        try {
            toml.parse(new File(filename));
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
            System.err.printf("Rules file %s misformatted!\n", filename);
            System.err.println("Falling back to default configs");
            return;
        }
        // FileNotFoundException
        catch (RuntimeException e) {
            e.printStackTrace();
            System.err.printf("Configuration file %s not found!\n", filename);
            System.err.println("Falling back to default configs");
            return;
        }

        Long val;
        if ((val = toml.getLong("grid_size")) != null) {
            CLIENT_GRID_SIZE = val.intValue();
        }

        if ((val = toml.getLong("World.size_x")) != null) {
            WORLD_X = val.intValue();
        }

        if ((val = toml.getLong("World.size_y")) != null) {
            WORLD_Y = val.intValue();
        }

        if ((val = toml.getLong("World.teams")) != null) {
            TEAM_NUMBER = val.intValue();
            playerVisibility = new int[TEAM_NUMBER][TEAM_NUMBER];
            foodVisibility = new int[TEAM_NUMBER][TEAM_NUMBER];
            foodEatability = new int[TEAM_NUMBER][TEAM_NUMBER];
        }

        if ((val = toml.getLong("World.food_per_player")) != null) {
            FOOD_PER_PLAYER = val.intValue();
        }

        if ((val = toml.getLong("World.maximum_food_per_team")) != null) {
            MAXIMUM_FOOD_PER_TEAM = val.intValue();
        }

        if ((val = toml.getLong("World.own_team_food_points")) != null) {
            OWN_TEAM_FOOD_POINTS = val.intValue();
        }

        if ((val = toml.getLong("World.other_team_food_points")) != null) {
            OTHER_TEAM_FOOD_POINTS = val.intValue();
        }

        if ((val = toml.getLong("World.food_regeneration")) != null) {
            FOOD_REGENERATION = val.intValue();
        }

        if ((val = toml.getLong("World.maximum_points")) != null) {
            MAXIUMUM_POINTS = val.intValue();
        }

        processTable("Visibility.player", toml, playerVisibility);
        processTable("Visibility.food_visible", toml, foodVisibility);
        processTable("Visibility.food_eatable", toml, foodEatability);
        processList("Player.speed", toml, speed);
        getTeamAssignment("World.team_assignment", toml);

        // Dump tables that were read
        dumpTable("Player Visibility", playerVisibility);
        dumpTable("Food Visibility", foodVisibility);
        dumpTable("Food Eatability", foodEatability);
        dumpList("Team Assignment", teamAssignment);
        dumpList("Speed", speed);
    }

    private void processTable(String tableName, Toml toml, int[][] toModify) {
        for (int i = 0; i < TEAM_NUMBER; i++) {
            String key = String.format("%s[%d]", tableName, i);
            List<Long> cols = toml.getList(key);

            if (cols == null) {
                return;
            }

            for (int j = 0; j < cols.size(); j++) {
                toModify[i][j] = cols.get(j).intValue();
            }
        }
    }

    private void getTeamAssignment(String listName, Toml toml) {
        List<Long> list = toml.getList(listName);

        if (list == null) {
            return;
        }

        teamAssignment = new int[list.size()];
        for (int i = 0; i < teamAssignment.length; i++) {
            teamAssignment[i] = list.get(i).intValue();
        }
    }

    private void processList(String listName, Toml toml, int[] arr) {
        List<Long> list = toml.getList(listName);

        if (list == null) {
            return;
        }

        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i).intValue();
        }
    }

    private void dumpTable(String name, int[][] table) {
        Util.debug(name);
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                Util.debug("%2d ", table[i][j]);
            }
            Util.debug("");
        }
    }

    private void dumpList(String name, int[] list) {
        Util.debug(name);
        for (int i = 0; i < list.length; i++) {
            Util.debug("%2d ", list[i]);
        }
        Util.debug("");
    }
}
