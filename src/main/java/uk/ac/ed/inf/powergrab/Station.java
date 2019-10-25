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
	
	public Position getPosition() {
		return position;
	}
	
	public double getCoins() {
		return coins;
	}
	
	public double getPower() {
		return power;
	}
	
	public void connect(Drone drone) {
		drone.charge(this.power);
		drone.receiveCoins(this.coins);
		this.power = 0;
		this.coins = 0;
	}
	
	public String toString() {
		return "Station at "+this.position.latitude+" , "+this.position.longitude;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (this.getClass() != o.getClass())
			return false;
		Station s = (Station) o;
		return (s.getPosition().equals(this.position))
			&& (Double.compare(s.getCoins(), this.coins) == 0)
			&& (Double.compare(s.getPower(), this.power) == 0);
	}
}
