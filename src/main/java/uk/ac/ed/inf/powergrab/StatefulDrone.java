package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class StatefulDrone extends Drone {

	private Queue<Position> recentPositions;
	private Station targetStation = null;


	public StatefulDrone(Position position, long randomSeed) {
		super(position, randomSeed);
		this.recentPositions = new CircularFifoQueue<Position>(GameRules.NUM_RECENT_POSITIONS);
		this.recentPositions.add(this.position);
	}
	
	public Move nextMove(Station targetStation) {
		// Next move is the reachable position that is closer to the target station
		ArrayList<Direction> bestDirections = new ArrayList<Direction>();
		double minDistance = Double.MAX_VALUE;
		for (Direction d : Direction.values()) {
			Position p = this.position.nextPosition(d);
			if (p.inPlayArea() && !(this.recentPositions.contains(p))) {
				Station connectedStation = Game.getInstance().getConnectedStation(p);
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
		Move move = this.move(moveDirection);
		this.moveHistory.add(move);
		this.recentPositions.add(this.position);
		return move;
	}
	
	public void planPath() {
		int numMoves = 0;
		this.targetStation = Game.getInstance().getNearestPositiveStation(this.position);
		while (this.power >= GameRules.POWER_CONSUMPTION && numMoves < 250) {
			numMoves++;
			Move move = this.nextMove(targetStation);
			// check if target has been reached
			Station connectedStation = move.getConnectedStation();
			if ((connectedStation != null) && (connectedStation.equals(targetStation)))
				targetStation = Game.getInstance().getNearestPositiveStation(this.position);
		}
		if (this.power < GameRules.POWER_CONSUMPTION) {
			System.out.println("Out of power!");
		}
		System.out.println(this.coins + " Coins Collected");
	}
}
