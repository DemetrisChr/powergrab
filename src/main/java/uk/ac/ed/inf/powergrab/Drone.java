package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Drone {
    protected Position position;
    protected double power = GameRules.INITIAL_POWER;
    protected double coins = 0;
    protected final Random rnd;
    protected final List<Move> moveHistory = new ArrayList<Move>();
    protected final GameMap gameMap;

    public Drone(Position position, GameMap gameMap, long randomSeed) {
        this.position = position;
        this.gameMap = gameMap;
        this.rnd = new Random(randomSeed);
    }

    public abstract void findPath();

    // Moves the drone to the specified direction. Updates the drone's position and power, and connects with the station
    // that is within range (if such station exists).
    public void move(Direction d) {
        this.position = this.position.nextPosition(d);
        Station stationToConnect = gameMap.getStationToConnect(this.position);
        if (stationToConnect != null)
            stationToConnect.connect(this);
        this.power -= GameRules.POWER_CONSUMPTION;
    }

    // Adds or subtracts deltaPower from the drone's power (depending on the sign of deltaPower)
    public double addOrSubtrPower(double deltaPower) {
        if (deltaPower < 0 && this.power < -deltaPower) {
            // The drone has less power than the amount to be subtracted
            double powerBefore = this.power;
            this.power = 0;
            return deltaPower + powerBefore;
        } else {
            this.power += deltaPower;
            return 0;
        }
    }

    // Adds or subtracts deltaCoins from the drone's coins (depending on the sign of deltaCoins)
    public double addOrSubtrCoins(double deltaCoins) {
        if ((deltaCoins < 0) && (this.coins < -deltaCoins)) {
            // The drone has less coins than the amount to be subtracted
            double coinsBefore = this.coins;
            this.coins = 0;
            return deltaCoins + coinsBefore;
        } else {
            this.coins += deltaCoins;
            return 0;
        }
    }

    // Returns a list of the sequence of positions the drone has visited
    public List<Position> getPath() {
        ArrayList<Position> path = new ArrayList<Position>();
        path.add(moveHistory.get(0).getPositionBefore());
        moveHistory.forEach( (move) -> path.add(move.getPositionAfter()) );
        return path;
    }

    // Outputs the drone's move history to a text file with the given fileName
    // The
    public void outputMoveHistoryToFile(String fileName) throws FileNotFoundException {
        PrintWriter outputMoveHistory = new PrintWriter(fileName+".txt");
        boolean firstMove = true;
        for (Move move : this.moveHistory) {
            // Go to the next line if the move is not the first one (avoids empty new line at the start)
            if (firstMove)
                firstMove = false;
            else
                outputMoveHistory.println();
            outputMoveHistory.print(move);
        }
        outputMoveHistory.close();
    }

    public Position getPosition() { return position; }

    public double getCoins() { return coins; }

    public double getPower() { return power; }

    public abstract String getDroneType();
}
