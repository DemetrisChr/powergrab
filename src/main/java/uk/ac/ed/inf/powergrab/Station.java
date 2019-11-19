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
        return this.position.distance(pos);
    }

    public void connect(Drone drone) {
        double coinsTransfer;
        double powerTransfer;
        if ((this.coins < 0) && (drone.getCoins() < -this.coins))
            coinsTransfer = - drone.getCoins();
        else
            coinsTransfer = this.coins;
        if ((this.power < 0) && (drone.getPower() < -this.power))
            powerTransfer = - drone.getPower();
        else
            powerTransfer = this.power;
        drone.charge(powerTransfer);
        drone.receiveCoins(coinsTransfer);
        this.power -= powerTransfer;
        this.coins -= coinsTransfer;
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
