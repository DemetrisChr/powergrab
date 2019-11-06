package uk.ac.ed.inf.powergrab;

import java.util.HashMap;
import java.util.Map;

public class Position {
    public final double latitude;
    public final double longitude;

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Position nextPosition(Direction direction) {
        double newLatitude = this.latitude + GameRules.TRAVEL_DISTANCE * direction.cosine;
        double newLongitude = this.longitude + GameRules.TRAVEL_DISTANCE * direction.sine;
        return new Position(newLatitude, newLongitude);
    }

    public boolean inPlayArea() {
        return (longitude < GameRules.RIGHT)
                && (longitude > GameRules.LEFT)
                && (latitude > GameRules.BOTTOM)
                && (latitude < GameRules.TOP);
    }

    public double distance(Position pos) {
        return Math.sqrt(Math.pow(this.latitude - pos.latitude, 2) + Math.pow(this.longitude - pos.longitude, 2));
    }

    public Map<Direction, Position> getAdjacentPositions() {
        HashMap<Direction, Position> adjacentPositions = new HashMap<Direction, Position>();
        for (Direction d : Direction.values()) {
            Position p = this.nextPosition(d);
            if (p.inPlayArea()) {
                adjacentPositions.put(d, p);
            }
        }
        return adjacentPositions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (this.getClass() != o.getClass())
            return false;
        Position p = (Position) o;
        return (Double.compare(this.latitude, p.latitude) == 0)
                && (Double.compare(this.longitude, p.longitude) == 0);
    }

    @Override
    public String toString() {
        return "( "+latitude + ", " + longitude+ " )";
    }
}
