package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Arrays;

public class StatefulDrone extends Drone {

    private Station targetStation = null;

    public StatefulDrone(Position position, long randomSeed) {
        super(position, randomSeed);
    }

    public String getDroneType() {
        return "stateful";
    }

    public Move nextRandomMove() {
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
        return new Move(this, moveDirection);
    }

    private ArrayList<Move> nextBatchOfMovesToTarget(Station targetStation) {
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
                if (f <= minFScore) {
                    minFScore = f;
                    current = p;
                }
            }
            Station connectedStationToCurrent = this.game.getConnectedStation(current);
            if ((connectedStationToCurrent != null) && connectedStationToCurrent.equals(targetStation))
                return reconstructPath(cameFromDirection, cameFromPosition, current);
            open.remove(current);
            Map<Direction, Position> adjacentPositionsToCurrent = current.getAdjacentPositions();
            for (Direction moveDir : adjacentPositionsToCurrent.keySet()) {
                Position neighbourPos = adjacentPositionsToCurrent.get(moveDir);
                // tentative_gScore is the distance from the start to the neighbour through current
                double tentative_gScore = GameRules.TRAVEL_DISTANCE + gScore.getOrDefault(current, Double.POSITIVE_INFINITY);
                if (tentative_gScore < gScore.getOrDefault(neighbourPos, Double.POSITIVE_INFINITY)) {
                    // This path to neighbour is better than any previous one recorded
                    cameFromDirection.put(neighbourPos, moveDir);
                    cameFromPosition.put(neighbourPos, current);
                    gScore.put(neighbourPos, tentative_gScore);
                    // Distance from neighbourPos to the range of the target station (distance from point to circle)
                    double distanceToTarget = neighbourPos.distance(targetPosition); // Math.max(neighbourPos.distance(targetPosition) - GameRules.CONNECT_DISTANCE, 0);
                    Station connectedStation = this.game.getConnectedStation(neighbourPos);
                    double penalty = 1;
                    if ((connectedStation != null) && (connectedStation.getCoins() < 0))
                        penalty = GameRules.NEGATIVE_STATION_PENALTY;
                    fScore.put(neighbourPos, gScore.get(neighbourPos) + penalty * distanceToTarget);
                    if (!open.contains(neighbourPos))
                        open.add(neighbourPos);
                }
            }
        }
        // Open set is empty and target has not been reached
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
                Station connectedStation = this.game.getConnectedStation(this.position);
                targetStation = this.game.getNearestPositiveStation(this.position, new HashSet<Station>(Arrays.asList(connectedStation)));
                if (targetStation == null) {
                    move = this.nextRandomMove();
                    plannedMoves = null;
                } else {
                    plannedMoves = this.nextBatchOfMovesToTarget(targetStation);
                    move = plannedMoves.remove(0);
                }
            } else {
                // Get the next move to be executed
                move = plannedMoves.remove(0);
            }
            // Execute the move
            move.executeMove();
            this.moveHistory.add(move);
        }
    }
}
