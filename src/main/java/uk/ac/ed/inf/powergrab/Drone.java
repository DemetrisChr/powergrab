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

    public abstract void planPath();

    public void move(Direction d) {
        this.position = this.position.nextPosition(d);
        Station connectedStation = gameMap.getConnectedStation(this.position);
        if (connectedStation != null)
            connectedStation.connect(this);
        this.power -= GameRules.POWER_CONSUMPTION;
    }

    public double addPower(double deltaPower) {
        double initialPower = this.power;
        this.power = Math.max(0, initialPower + deltaPower);
        return deltaPower - (this.power - initialPower);
    }

    public double addCoins(double deltaCoins) {
        double initialCoins = this.coins;
        this.coins = Math.max(0, initialCoins + deltaCoins);
        return deltaCoins - (this.coins - initialCoins);
    }

    public List<Position> getPath() {
        ArrayList<Position> path = new ArrayList<Position>();
        path.add(moveHistory.get(0).getPositionBefore());
        moveHistory.forEach( (move) -> path.add(move.getPositionAfter()) );
        return path;
    }

    public void outputMoveHistoryToFile(String fileName) throws FileNotFoundException {
        PrintWriter outputMoveHistory = new PrintWriter(fileName+".txt");
        for (Move move : this.moveHistory)
            outputMoveHistory.println(move);
        outputMoveHistory.close();
    }

    public Position getPosition() { return position; }

    public double getCoins() { return coins; }

    public double getPower() { return power; }

    public abstract String getDroneType();
}
