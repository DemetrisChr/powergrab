package uk.ac.ed.inf.powergrab;

import java.lang.reflect.Array;
import java.util.*;

import org.apache.commons.collections4.queue.CircularFifoQueue;

public class StatefulDrone extends Drone {

	private Queue<Position> recentPositions;
	private Station targetStation = null;


	public StatefulDrone(Position position, long randomSeed) {
		super(position, randomSeed);
		this.recentPositions = new CircularFifoQueue<Position>(GameRules.NUM_RECENT_POSITIONS);
		this.recentPositions.add(this.position);
	}

	public String getDroneType() {
		return "stateful";
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
						dist =  GameRules.NEGATIVE_STATION_PENALTY * dist;
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

    public Move nextRandomMove() {
        Map<Direction, Position> adjacentPositions = this.position.getAdjacentPositions();
        ArrayList<Direction> bestDirections = new ArrayList<Direction>();
        for (Direction d : adjacentPositions.keySet()) {
            Position p = adjacentPositions.get(d);
            Station s = Game.getInstance().getConnectedStation(p);
            double coins;
            double maxCoins = Double.NEGATIVE_INFINITY;
            if (s == null)
                coins = 0;
            else
                coins = s.getCoins();
            if (coins == maxCoins) {
                bestDirections.add(d);
            } else if (coins > maxCoins) {
                maxCoins = coins;
                bestDirections.clear();
                bestDirections.add(d);
            }
        }
        // Direction moveDirection = bestDirections.get(this.rnd.nextInt(bestDirections.size()));
        Collections.shuffle(bestDirections, this.rnd);
        Direction moveDirection = bestDirections.get(0);
        return new Move(this, moveDirection);
    }

	private ArrayList<Move> calculatePathToTarget(Station targetStation) {
		Position targetPosition = targetStation.getPosition();
		Position startPosition = this.position;
		// Set of discovered positions that may need to be expanded
		// Initially only the starting position has been discovered
		ArrayList<Position> open = new ArrayList<Position>();
		open.add(startPosition);
		// Map where for Position p, cameFromPosition[p] is the position
		// preceding it on the cheapest path to p currently known
		HashMap<Position, Position> cameFromPosition = new HashMap<Position, Position>();
		// Map where for Position p, cameFromDirection[p] is the move direction
		// to reach p from the preceding position on th cheapest path to p currently known
		HashMap<Position, Direction> cameFromDirection = new HashMap<Position, Direction>();
		// gScore[p] is cost of cheapest path known to p (number of moves * TRAVEL_DISTANCE)
		HashMap<Position, Double> gScore = new HashMap<Position, Double>();
		gScore.put(startPosition, 0.0);
		// fScore[p] = gScore[p] + euclidian distance from p to the goalPosition
		HashMap<Position, Double> fScore = new HashMap<Position, Double>();
		fScore.put(startPosition, startPosition.distance(targetPosition));

		while (!open.isEmpty()) {
			double minFScore = Double.POSITIVE_INFINITY;
			// node in open with the lowest fScore value
			Position current = open.get(0);
			for (Position p : open) {
				double f = fScore.getOrDefault(p, Double.POSITIVE_INFINITY);
				if (f < minFScore) {
					minFScore = f;
					current = p;
				}
			}
			Station connectedStationToCurrent = Game.getInstance().getConnectedStation(current);
			if ((connectedStationToCurrent != null) && connectedStationToCurrent.equals(targetStation)) {
				return reconstructPath(cameFromDirection, cameFromPosition, current);
			}
			open.remove(current);
			Map<Direction, Position> adjacentPositionsToCurrent = current.getAdjacentPositions();
			for (Direction moveDir : adjacentPositionsToCurrent.keySet()) {
				/// int penaltyCoefficient = 1;
				Position neighbourPos = adjacentPositionsToCurrent.get(moveDir);
				Station connectedStation = Game.getInstance().getConnectedStation(neighbourPos);
				// if (!(connectedStation == null) && (connectedStation.getCoins() < 0)) {
				//	 penaltyCoefficient = GameRules.NEGATIVE_STATION_PENALTY;
				// }
				// tentative_gScore is the distance from the start to the neighbour through current
				double coins;
				if (connectedStation == null)
					coins = 0;
				else
					coins = connectedStation.getCoins();
				double tentative_gScore = (-1) * coins / GameRules.TRAVEL_DISTANCE +
						gScore.getOrDefault(current, Double.POSITIVE_INFINITY);
				if (tentative_gScore < gScore.getOrDefault(neighbourPos, Double.POSITIVE_INFINITY)) {
					cameFromDirection.put(neighbourPos, moveDir);
					cameFromPosition.put(neighbourPos, current);
					gScore.put(neighbourPos, tentative_gScore);
					fScore.put(neighbourPos, tentative_gScore + (-1) * targetStation.getCoins() / neighbourPos.distance(targetPosition));
					if (!open.contains(neighbourPos)) {
						open.add(neighbourPos);
					}
				}
			}
		}
		return null;
	}

	private ArrayList<Move> reconstructPath(HashMap<Position, Direction> cameFromDirection, HashMap<Position, Position> cameFromPosition, Position current) {
		ArrayList<Move> totalPath = new ArrayList<Move>();
		while (cameFromDirection.keySet().contains(current)) {
			totalPath.add(new Move(this, cameFromDirection.get(current)));
			current = cameFromPosition.get(current);
		}
		Collections.reverse(totalPath);
		return totalPath;
	}
	
	public void planPath() {
		int numMoves = 0;
		ArrayList<Move> plannedMoves = new ArrayList<Move>();
		HashSet<Station> unreachable = new HashSet<Station>();
		Move move;
		while (this.power >= GameRules.POWER_CONSUMPTION && numMoves < GameRules.NUM_OF_MOVES) {
			numMoves++;
			if (plannedMoves == null || plannedMoves.isEmpty()) {
				if (plannedMoves == null)
					unreachable.add(targetStation);
				targetStation = Game.getInstance().getNearestPositiveStation(this.position, unreachable);
				if (targetStation == null) {
					System.out.println("random move");
					move = this.nextRandomMove();
					plannedMoves = null;
				} else {
					plannedMoves = this.calculatePathToTarget(targetStation);
					move = plannedMoves.remove(0);
				}
			} else {
				// Get the next move to be executed
				move = plannedMoves.remove(0);
			}
			// Execute the move
			move.move();
			this.moveHistory.add(move);
		}
	}
}
