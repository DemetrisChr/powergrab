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

    public double addOrSubtrPower(double deltaPower) {
        if (deltaPower < 0 && this.power < -deltaPower) {
            this.power = 0;
            return deltaPower + this.power;
        } else {
            this.power += deltaPower;
            return 0;
        }
    }

    public double addOrSubtrCoins(double deltaCoins) {
        if (deltaCoins < 0 && this.power < -deltaCoins) {
            this.coins = 0;
            return deltaCoins + this.coins;
        } else {
            this.coins += deltaCoins;
            return 0;
        }
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
