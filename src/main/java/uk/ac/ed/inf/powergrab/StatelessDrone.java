package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;

public class StatelessDrone extends Drone {

    public StatelessDrone(Position position, GameMap gameMap, long randomSeed) {
        super(position, gameMap, randomSeed);
    }

    public void findPath() {
        int numMoves = 0;
        while (this.power >= GameRules.POWER_CONSUMPTION && numMoves < 250) {
            numMoves++;
            Move move = this.nextMove();
            move.executeMove();
            this.moveHistory.add(move);
        }
    }

    // Find the Move that will yield the maximum coin gain and avoid connecting to negative stations
    private Move nextMove() {
        // The move directions that will yield maximum coin gain (or minimum coin loss)
        ArrayList<Direction> bestDirections = new ArrayList<Direction>();

        double maxCoins = Double.NEGATIVE_INFINITY;
        for (Direction d : Direction.values()) {
            Position p = this.position.nextPosition(d);
            Station s = this.gameMap.getStationToConnect(p);
            if (p.inPlayArea()) {
                double coins = (s == null) ? 0 : s.getCoins();
                if (Math.abs(coins - maxCoins) < 1.0E-7)
                    // This move direction leads to a connection with a station which gives maximum coins
                    bestDirections.add(d);
                else if (coins > maxCoins) {
                    // Found a move direction which leads to a connection with a station that gives more than any other
                    // move direction already encountered.
                    maxCoins = coins;
                    bestDirections.clear();
                    bestDirections.add(d);
                }
            }
        }
        // Pick one of the best move directions at random
        Collections.shuffle(bestDirections, this.rnd);
        Direction moveDirection = bestDirections.get(0);
        return new Move(this, moveDirection);
    }

    public String getDroneType() { return "stateless"; }
}
