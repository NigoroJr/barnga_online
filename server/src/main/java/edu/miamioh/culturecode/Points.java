package edu.miamioh.culturecode;

import java.util.ArrayList;

/**
 * Class that has the information of who earned how many points.
 */
public class Points {
    /**
     * Class that has information about who earned how many points.
     */
    public class Earner {
        private Player player;
        private int amount;

        public Earner(Player player, int amount) {
            this.player = player;
            this.amount = amount;
        }

        public Player getPlayer() {
            return player;
        }

        public int getAmount() {
            return amount;
        }
    }

    private int total;
    private ArrayList<Earner> earners;

    /**
     * Default constructor.
     */
    public Points() {
        earners = new ArrayList<Earner>();
    }

    public void addPoints(Player player, int amount) {
        earners.add(new Earner(player, amount));
        total += amount;
    }

    public void subtractPoints(Player player, int amount) {
        addPoints(player, -amount);
    }

    public ArrayList<Earner> getEarners() {
        return earners;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
