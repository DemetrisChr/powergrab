package uk.ac.ed.inf.powergrab;

public class Station {
    private Position position;
    private double coins;
    private double power;

    public Station(Position position, double coins, double power) {
        this.position = position;
        this.coins = coins;
        this.power = power;
    }

    public double distanceFromPosition(Position pos) {
        // Calculates the euclidean distance from the given position to the position of this station
        return this.position.distance(pos);
    }

    public boolean positionInRange(Position pos) {
        // Checks whether the given position is within the range (connect distance) of this station
        return this.distanceFromPosition(pos) < GameRules.CONNECT_DISTANCE;
    }

    public void connect(Drone drone) {
        // Connects the given drone to this station. i.e. Coins & Power are given to or taken away from the drone
        // If the station is negative and the drone has less coins/power than the absolute value of the coins/power
        // of the station then the drone loses as many coins/power as possible, i.e. all of its coins/power
        this.power = drone.addOrSubtrPower(this.power);
        this.coins = drone.addOrSubtrCoins(this.coins);
    }

    public boolean isPositive() {
        return this.coins > 0;
    }

    public boolean isNegative() {
        return this.coins < 0;
    }

    public double getCoins() { return coins; }

    public double getPower() { return power; }

    @Override
    public String toString() { return "Station at "+this.position.latitude+" , "+this.position.longitude; }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (this.getClass() != o.getClass())
            return false;
        Station s = (Station) o;
        return (s.position.equals(this.position))
                && (Double.compare(s.coins, this.coins) == 0)
                && (Double.compare(s.power, this.power) == 0);
    }
}
