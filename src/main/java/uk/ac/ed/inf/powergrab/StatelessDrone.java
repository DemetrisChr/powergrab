package uk.ac.ed.inf.powergrab;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;


public class StatelessDrone extends Drone {

	public StatelessDrone(Position position, long randomSeed) {
		super(position, randomSeed);
	}
	
	public void nextMove() {
		ArrayList<Direction> bestDirections = new ArrayList<Direction>();
		double maxCoins = Double.NEGATIVE_INFINITY;
		for (Direction d : Direction.values()) {
			Position p = this.position.nextPosition(d);
			Station s = game.getConnectedStation(p);
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
		Position nextPosition = this.position.nextPosition(moveDirection);
		this.position = nextPosition;
		Station connectedStation = this.game.getConnectedStation(this.position);
		if (connectedStation != null) {
			connectedStation.connect(this);
		}
		this.power -= POWER_CONSUMPTION;
	}
	
	public List<Position> planPath() {
		int numMoves = 0;
		ArrayList<Position> path = new ArrayList<Position>();
		path.add(this.position);
		while (this.power >= POWER_CONSUMPTION && numMoves < 250) {
			numMoves++;
			this.nextMove();
			path.add(this.position);
		}
		if (this.power < POWER_CONSUMPTION) {
			System.out.println("Out of power!");
		}
		return path;
	}

}
