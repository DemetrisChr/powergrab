package uk.ac.ed.inf.powergrab;

public class GameRules {
    public static final double CONNECT_DISTANCE = 0.00025;
    public static final double POWER_CONSUMPTION = 1.25;
    public static final double TRAVEL_DISTANCE = 0.0003;
    public static final int NUM_RECENT_POSITIONS = 5;
    public static final int NUM_OF_MOVES = 250;
    public static final double INITIAL_POWER = 250;
    public static final double MIN_NEGATIVE_STATION_PENALTY = 5;

    // Play area boundaries
    public static final double TOP = 55.946233;
    public static final double BOTTOM = 55.942617;
    public static final double RIGHT = -3.184319;
    public static final double LEFT = -3.192473;
}
