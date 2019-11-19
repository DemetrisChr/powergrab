package uk.ac.ed.inf.powergrab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Drone {
    protected Position position;
    protected double power = GameRules.INITIAL_POWER;
    protected double coins = 0;
    protected Random rnd = new Random();
    protected List<Move> moveHistory = new ArrayList<Move>();
    protected GameMap gameMap = null;

    public Drone(Position position, GameMap gameMap, long randomSeed) {
        this.position = position;
        this.gameMap = gameMap;
        this.rnd.setSeed(randomSeed);
    }

    public void charge(double powerIncr) {
        this.power += powerIncr;
    }

    public void receiveCoins(double coinsIncr) {
        this.coins += coinsIncr;
    }

    public Position getPosition() {
        return position;
    }

    public double getCoins() {
        return coins;
    }

    public double getPower() {
        return power;
    }

    public abstract void planPath();

    public abstract String getDroneType();

    public List<Move> getMoveHistory() {
        return this.moveHistory;
    }

    public List<Position> getPath() {
        ArrayList<Position> path = new ArrayList<Position>();
        path.add(moveHistory.get(0).getPositionBefore());
        moveHistory.forEach( (move) -> path.add(move.getPositionAfter()) );
        return path;
    }

    public void outputMoveHistoryToFile(String fileName) throws FileNotFoundException {
        PrintWriter outputMoveHistory = new PrintWriter(fileName+".txt");
        List<Move> moveHistory = this.getMoveHistory();
        for (Move move : moveHistory)
            outputMoveHistory.println(move);
        outputMoveHistory.close();
    }
}
