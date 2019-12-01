package uk.ac.ed.inf.powergrab;

import java.util.*;

public class StatefulDrone extends Drone {

    private Station targetStation = null;

    public StatefulDrone(Position position, GameMap gameMap, long randomSeed) { super(position, gameMap, randomSeed); }

    public void planPath() {
        int numMoves = 0;
        LinkedList<Move> plannedMoves = new LinkedList<Move>();
        HashSet<Station> unreachable = new HashSet<Station>();
        Move move;
        boolean allTargetsReached = false;

        while (this.power >= GameRules.POWER_CONSUMPTION && numMoves < GameRules.NUM_OF_MOVES) {
            numMoves++;
            // If all planned moves have been executed and there are targets left, find new target and plan moves to reach it
            // If no targets left set allTargetsReached to true
            if (!allTargetsReached && plannedMoves.isEmpty()) {
                do {
                    this.targetStation = this.gameMap.getNearestPositiveStation(this.position, unreachable);
                    if (targetStation == null) {
                        allTargetsReached = true;
                        break;
                    }
                    plannedMoves = this.nextBatchOfMovesToTarget();
                } while (plannedMoves == null);
            }
            // If all targets have been reached perform a random move otherwise execute the head move of the planned moves
            if (allTargetsReached) {
                Direction oppositeDirectionFromLastMove = this.moveHistory.get(this.moveHistory.size() - 1).getMoveDirection().getOppositeDirection();
                move = new Move(this, oppositeDirectionFromLastMove);
            }
            else
                move = plannedMoves.removeFirst();
            move.executeMove();
            this.moveHistory.add(move);
        }
    }

    private LinkedList<Move> nextBatchOfMovesToTarget() {
        Node.clearNodeSet();
        PriorityQueue<Node> frontier = new PriorityQueue<Node>(); // Discovered nodes that may need to be expanded

        Node rootNode = Node.getNodeWithPosition(this.position);
        // Add the root (starting) node to the sets
        frontier.add(rootNode);
        rootNode.g = 0;
        rootNode.calculateHandF(this.targetStation);

        while (!frontier.isEmpty()) {
            Node current = frontier.poll(); // Retrieve and remove the node with the lowest f-score

            // If the drone can connect to the target station at the current node the goal has been reached
            // and the path is returned
            Station connectedStationToCurrent = this.gameMap.getConnectedStation(current.position);
            if ((connectedStationToCurrent != null) && connectedStationToCurrent.equals(targetStation))
                return reconstructPath(current);

            // As the target station has not been reached expand the current node
            Map<Direction, Node> adjacentNodesToCurrent = current.expandNode();

            for (Direction moveDir : adjacentNodesToCurrent.keySet()) {
                Node neighbour = adjacentNodesToCurrent.get(moveDir);
                // Calculate the distance from start to neighbour through current and add the negative station penalty
                double tentativeGscore = current.g + GameRules.TRAVEL_DISTANCE + calculatePenalty(neighbour);
                if (tentativeGscore < neighbour.g) {
                    // This path to neighbour is better than any previous one recorded
                    neighbour.cameFromDirection = moveDir;
                    neighbour.cameFromNode = current;
                    neighbour.g = tentativeGscore;
                    // Calculate the heuristic (h) and f
                    neighbour.calculateHandF(this.targetStation);
                    // If the neighbour is not already in the frontier, add it
                    if (!frontier.contains(neighbour))
                        frontier.add(neighbour);
                }
            }
        }
        return null; // Frontier is empty and target has not been reached
    }

    public double calculatePenalty(Node node) {
        double penalty = 0;
        Station connectedStation = gameMap.getConnectedStation(node.position);
        if (connectedStation != null && connectedStation.isNegative())
            penalty = Math.max(GameRules.MIN_NEGATIVE_STATION_PENALTY,
                    (-1)*(Math.min(connectedStation.getCoins(), connectedStation.getPower())));
        return penalty;
    }

    private LinkedList<Move> reconstructPath(Node current) {
        LinkedList<Move> totalPath = new LinkedList<Move>();
        do {
            totalPath.addFirst(new Move(this, current.cameFromDirection));
            current = current.cameFromNode;
        } while (current.cameFromDirection != null);
        return totalPath;
    }

    public String getDroneType() { return "stateful"; }

    private static class Node implements Comparable<Node> {
        private static Set<Node> allNodes = new HashSet<Node>();

        public Position position;
        public double g = Double.POSITIVE_INFINITY; // Distance travelled on shortest path to reach node
        public double h = Double.POSITIVE_INFINITY; // Estimated distance to target
        public double f = Double.POSITIVE_INFINITY; // f = g + h
        public Node cameFromNode = null; // Preceding node on the shortest path to this node
        public Direction cameFromDirection = null; // Direction of travel on the shortest path, from previous node to this

        private Node(Position p) {
            this.position = p;
        }

        public static void clearNodeSet() {
            Node.allNodes.clear();
        }

        public static Node getNodeWithPosition(Position pos) {
            Node newNode = new Node(pos);
            for (Node n : allNodes)
                if (n.equals(newNode))
                    return n;
            allNodes.add(newNode);
            return newNode;
        }

        public int compareTo(Node otherNode) {
            return Double.compare(this.f, otherNode.f); // Nodes with lower f score are ranked higher
        }

        public void calculateHandF(Station targetStation) {
            this.h = targetStation.distanceFromPosition(this.position);
            this.f = this.g + this.h;
        }

        public Map<Direction, Node> expandNode() {
            Map<Direction, Node> adjacentNodes = new HashMap<Direction, Node>();
            for (Direction d : Direction.values()) {
                Position pos = this.position.nextPosition(d);
                if (pos.inPlayArea())
                    adjacentNodes.put(d, Node.getNodeWithPosition(pos));
            }
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
        public int hashCode() { return this.position.hashCode(); }
    }
}
