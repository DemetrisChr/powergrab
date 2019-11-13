package uk.ac.ed.inf.powergrab;

import java.util.*;

public class StatefulDrone extends Drone {

    private Station targetStation = null;

    public StatefulDrone(Position position, long randomSeed) { super(position, randomSeed); }

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
                    this.targetStation = this.game.getNearestPositiveStation(this.position, unreachable);
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
        Position targetPosition = this.targetStation.getPosition();
        Position startPosition = this.position;

        TreeSet<Node> open = new TreeSet<Node>(); // Set of discovered nodes that may need to be expanded
        TreeSet<Node> allNodes = new TreeSet<Node>(); // Set of all the nodes that have ever been encountered

        Node rootNode = new Node(startPosition, this.game);
        // Add the root (starting) node to the sets
        open.add(rootNode);
        allNodes.add(rootNode);
        rootNode.g = 0;
        rootNode.calculateHandF(targetPosition);

        while (!open.isEmpty()) {
            Node current = open.first(); // Current is the node with the lowest f score
            open.remove(current); // Remove the current node from the open set

            // If the drone can connect to the target station at the current node the goal has been reached
            // and the path is returned
            Station connectedStationToCurrent = this.game.getConnectedStation(current.position);
            if ((connectedStationToCurrent != null) && connectedStationToCurrent.equals(targetStation))
                return reconstructPath(current);

            // As the target station has not been reached expand the current node
            Map<Direction, Node> adjacentNodesToCurrent = current.expandNode();

            for (Direction moveDir : adjacentNodesToCurrent.keySet()) {
                Node neighbour = adjacentNodesToCurrent.get(moveDir);
                boolean nodeAlreadyExists = false;
                // If an equivalent node (same position) has been seen before, use the existing node object instead
                for (Node n : allNodes)
                    if (n.equals(neighbour)) {
                        neighbour = n;
                        nodeAlreadyExists = true;
                        break;
                    }
                if (!nodeAlreadyExists) allNodes.add(neighbour);
                double tentativeGscore = GameRules.TRAVEL_DISTANCE + current.g; // Distance from start to neighbour through current
                if (tentativeGscore < neighbour.g) {
                    // This path to neighbour is better than any previous one recorded
                    neighbour.cameFromDirection = moveDir;
                    neighbour.cameFromNode = current;
                    neighbour.g = tentativeGscore;
                    // Calculate the heuristic (h) and f
                    neighbour.calculateHandF(targetPosition);
                    open.add(neighbour); // As 'open' is a set, if 'open' already contains neighbour it will not be added
                }
            }
        }
        return null; // Open set is empty and target has not been reached
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
        public Position position;

        public double g = Double.POSITIVE_INFINITY; // Distance travelled on shortest path to reach node
        public double h = Double.POSITIVE_INFINITY; // Estimated distance to target
        public double f = Double.POSITIVE_INFINITY; // f = g + h
        public Node cameFromNode = null; // Preceding node on the shortest path to this node
        public Direction cameFromDirection = null; // Direction of travel on the shortest path, from previous node to this
        public Game game; // The game where this node is located on

        public Node(Position p, Game g) {
            this.position = p;
            this.game = g;
        }

        public int compareTo(Node otherNode) {
            return Double.compare(this.f, otherNode.f); // The f score is the comparison metric between nodes
        }

        public void calculateHandF(Position targetPosition) {
            Station connectedStation = this.game.getConnectedStation(this.position);
            double penalty = 1;
            if (connectedStation != null && connectedStation.getCoins() < 0)
                penalty = Math.max(GameRules.MIN_NEGATIVE_STATION_PENALTY,
                        Math.abs(Math.min(connectedStation.getCoins(), connectedStation.getPower())));
            this.h = penalty * this.position.distance(targetPosition);
            this.f = this.g + this.h;
        }

        public Map<Direction, Node> expandNode() {
            Map<Direction, Node> adjacentNodes = new HashMap<Direction, Node>();
            for (Direction d : Direction.values())
                adjacentNodes.put(d, new Node(this.position.nextPosition(d), this.game));
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
