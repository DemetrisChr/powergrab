package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;

public class StatelessDrone extends Drone {

    public StatelessDrone(Position position, long randomSeed) {
        super(position, randomSeed);
    }

    public String getDroneType() {
        return "stateless";
    }

    public void nextMove() {
        ArrayList<Direction> bestDirections = new ArrayList<Direction>();
        double maxCoins = Double.NEGATIVE_INFINITY;
        for (Direction d : Direction.values()) {
            Position p = this.position.nextPosition(d);
            Station s = this.game.getConnectedStation(p);
            double coins;
            if (p.inPlayArea()) {
                if (s == null) {
                    coins = 0;
                } else {
                    coins = s.getCoins();
                }
                if (coins == maxCoins) {
                    bestDirections.add(d);
                } else if (coins > maxCoins) {
                    maxCoins = coins;
                    bestDirections.clear();
                    bestDirections.add(d);
                }
            }
        }
        // Direction moveDirection = bestDirections.get(this.rnd.nextInt(bestDirections.size()));
        Collections.shuffle(bestDirections, this.rnd);
        Direction moveDirection = bestDirections.get(0);
        Move move = this.move(moveDirection);
        this.moveHistory.add(move);
    }

    public void planPath() {
        int numMoves = 0;
        while (this.power >= GameRules.POWER_CONSUMPTION && numMoves < 250) {
            numMoves++;
            this.nextMove();
        }
    }
}
