package uk.ac.ed.inf.powergrab;

public class Position {
	public final double latitude;
	public final double longitude;
	
	// Play area boundaries
	private static final double TOP = 55.946233;
	private static final double BOTTOM = 55.942617;
	private static final double RIGHT = -3.184319;
	private static final double LEFT = -3.192473;
	
	public static final double TRAVEL_DISTANCE = 0.0003;
	
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Position nextPosition(Direction direction) {
		double newLatitude = this.latitude + TRAVEL_DISTANCE * direction.cosine;
		double newLongitude = this.longitude + TRAVEL_DISTANCE * direction.sine;
		return new Position(newLatitude, newLongitude);
	}
	
	public boolean inPlayArea() {
		return (longitude < RIGHT) && (longitude > LEFT) && (latitude > BOTTOM) && (latitude < TOP);
	}
	
	public double distance(Position pos) {
		return Math.sqrt(Math.pow(this.latitude - pos.latitude, 2) + Math.pow(this.longitude - pos.longitude, 2));
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
}
