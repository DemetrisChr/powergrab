package uk.ac.ed.inf.powergrab;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;


public class StatefulDrone extends Drone {

	public StatefulDrone(Position position, long randomSeed) {
		super(position, randomSeed);
	}
	
	public boolean nextMove(Station targetStation, List<Position> recentPositions) {
		// Next move is the reachable position that is closer to the target station
		boolean reachedTarget = false;
		ArrayList<Direction> bestDirections = new ArrayList<Direction>();
		double minDistance = Double.MAX_VALUE;
		for (Direction d : Direction.values()) {
			Position p = this.position.nextPosition(d);
			if (p.inPlayArea() && !(recentPositions.contains(p))) {
				Station connectedStation = this.game.getConnectedStation(p);
				if (targetStation == null) {
						bestDirections.add(d);
				} else {
					double dist = targetStation.getPosition().distance(p);
					if ((connectedStation != null) && (connectedStation.getCoins() < 0)) {
						dist =  2 * dist;
					}
					if (dist < minDistance) {
						minDistance = dist;
						bestDirections.clear();
						bestDirections.add(d);
					} else if (dist == minDistance) {
						bestDirections.add(d);
					}
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
			if (connectedStation.equals(targetStation)) {
				reachedTarget = true;
			}
			connectedStation.connect(this);
		}
		this.power -= POWER_CONSUMPTION;
		return reachedTarget;
	}
	
	public List<Position> planPath() {
		int numMoves = 0;
		ArrayList<Position> path = new ArrayList<Position>();
		path.add(this.position);
		Station targetStation = this.game.getNearestPositiveStation(this.position);
		while (this.power >= POWER_CONSUMPTION && numMoves < 250) {
			numMoves++;
			List<Position> recentPositions = path.subList(path.size()-Math.min(path.size(),5), path.size()-1);
			boolean reachedTarget = this.nextMove(targetStation, recentPositions);
			if (reachedTarget)
				targetStation = this.game.getNearestPositiveStation(this.position);
			path.add(this.position);
		}
		if (this.power < POWER_CONSUMPTION) {
			System.out.println("Out of power!");
		}
		System.out.println(this.coins + " Coins Collected");
		return path;
	}

}
