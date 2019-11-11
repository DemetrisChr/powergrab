package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Arrays;
import java.util.TreeSet;

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

    static class NodeSet extends TreeSet<Node> {
        public NodeSet() {
            super();
        }

        public Node getIfContains(Node node) {
            for (Node n : this)
                if (n.equals(node)) return n;
            return node;
        }
    }

    static class Node implements Comparable<Node> {
        public Position position;
        public double f = Double.POSITIVE_INFINITY; // f = g + h
        public double g = Double.POSITIVE_INFINITY;
        public double h = Double.POSITIVE_INFINITY;
        public Node cameFromNode = null;
        public Direction cameFromDirection = null;
        public Game game;

        public Node(Position p, Game g) {
            this.position = p;
            this.game = g;
        }

        public int compareTo(Node otherNode) {
            return Double.compare(this.f, otherNode.f);
        }

        public void calculateHandF(Position targetPosition) {
            Station connectedStation = this.game.getConnectedStation(this.position);
            double penalty = 1;
            if ((connectedStation != null) && (connectedStation.getCoins() < 0))
                penalty = GameRules.NEGATIVE_STATION_PENALTY;
            this.h = penalty * this.position.distance(targetPosition);
            this.f = this.g + this.h;
        }

        public Map<Direction, Node> expandNode() {
            Map<Direction, Position> adjacentPositions = this.position.getAdjacentPositions();
            Map<Direction, Node> adjacentNodes = new HashMap<Direction, Node>();
            for (Direction d : adjacentPositions.keySet())
                adjacentNodes.put(d, new Node(adjacentPositions.get(d), this.game));
            return adjacentNodes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null)
                return false;
            if (this.getClass() != o.getClass())
                return false;
            Node n = (Node) o;
            return n.position.equals(this.position);
        }

        @Override
        public int hashCode() {
            return this.position.hashCode();
        }
    }

    private ArrayList<Move> nextBatchOfMovesToTarget(Station targetStation) {
        Position targetPosition = targetStation.getPosition();
        Position startPosition = this.position;
        // Set of discovered nodes that may need to be expanded
        // Initially only the starting position has been discovered
        NodeSet open = new NodeSet();
        Node rootNode = new Node(startPosition, this.game);
        open.add(rootNode);
        rootNode.g = 0;
        rootNode.calculateHandF(targetPosition);
        while (!open.isEmpty()) {
            Node current = open.first(); // Returns Node with lowest f
            Station connectedStationToCurrent = this.game.getConnectedStation(current.position);
            if ((connectedStationToCurrent != null) && connectedStationToCurrent.equals(targetStation))
                return reconstructPath(current);
            open.remove(current);
            Map<Direction, Node> adjacentNodesToCurrent = current.expandNode();
            for (Direction moveDir : adjacentNodesToCurrent.keySet()) {
                Node neighbour = open.getIfContains(adjacentNodesToCurrent.get(moveDir));
                // tentative_gScore is the distance from the start to the neighbour through current
                double tentative_gScore = GameRules.TRAVEL_DISTANCE + current.g;
                if (tentative_gScore < neighbour.g) {
                    // This path to neighbour is better than any previous one recorded
                    neighbour.cameFromDirection = moveDir;
                    neighbour.cameFromNode = current;
                    neighbour.g = tentative_gScore;
                    // Distance from neighbourPos to the range of the target station (distance from point to circle)
                    neighbour.calculateHandF(targetPosition);
                    if (!open.contains(neighbour))
                        open.add(neighbour);
                }
            }
        }
        // Open set is empty and target has not been reached
        return null;
    }

    private ArrayList<Move> reconstructPath(Node current) {
        ArrayList<Move> totalPath = new ArrayList<Move>();
        do {
            totalPath.add(new Move(this, current.cameFromDirection));
            current = current.cameFromNode;
        } while (current.cameFromDirection != null);
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
