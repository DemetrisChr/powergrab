package uk.ac.ed.inf.powergrab;

public class GameRules {
    // The max distance between a drone and a station for a connection to be possible
    public static final double CONNECT_DISTANCE = 0.00025;

    // Amount of power consumed by the drone with each move
    public static final double POWER_CONSUMPTION = 1.25;

    // Distance drone travels with each move
    public static final double TRAVEL_DISTANCE = 0.0003;

    // Number of moves drone is allowed to make
    public static final int NUM_OF_MOVES = 250;

    // Initial power of drone
    public static final double INITIAL_POWER = 250;

    // The minimum penalty applied to negative stations in the stateful drone's strategy
    public static final double MIN_NEGATIVE_STATION_PENALTY = 50;

    // Play area boundaries
    public static final double TOP = 55.946233;
    public static final double BOTTOM = 55.942617;
    public static final double RIGHT = -3.184319;
    public static final double LEFT = -3.192473;
}
