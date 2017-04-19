package ie.gmit.sw.ai.node;

/**
 * Created by Martin Coleman on 19/04/2017.
 */
public class PlayerNode extends Node {
    private int health;
    private int bombs;
    private int swords;
    private int noOfEnemies;

    public PlayerNode(int row, int col, int id) {
        super(row, col, id);

        //Init Variables
        this.health=100;
        this.bombs=0;
        this.swords=0;
        this.noOfEnemies=0;
    }

    public int getHealth() {
        return health;
    }

    public void increaseHealth(int health) {
        this.health += health;
    }

    public void decreaseHealth(int health) {
        this.health -= health;
    }

    public int getBombs() {
        return bombs;
    }

    public void increaseBombs(int bombs) {
        this.bombs += bombs;
    }

    public void decreaseBombs(int bombs) {

        this.bombs -= bombs;
    }

    public int getSwords() {
        return swords;
    }

    public void increaseSwords(int swords) {
        this.swords += swords;
    }

    public void decreaseSwords(int swords) {
        this.swords -= swords;
    }

    public int getNoOfEnemies() {
        return noOfEnemies;
    }

    public void increaseNoOfEnemies(int noOfEnemies) {
        this.noOfEnemies += noOfEnemies;
    }

    public void decreaseNoOfEnemies(int noOfEnemies) {
        this.noOfEnemies -= noOfEnemies;
    }
}
