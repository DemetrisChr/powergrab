package uk.ac.ed.inf.powergrab;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

public class Game {
	private Drone drone;
	private Map<Position, Station> stations;
	public static final double CONNECT_DISTANCE = 0.00025;
	
	public Game(Drone drone, Map<Position, Station> stations) {
		this.drone = drone;
		this.stations = stations;
		drone.setGame(this);
	}
	
	public Collection<Station> getStations() {
		return stations.values();
	}
	
	public Station getNearestPositiveStation(Position pos) {
		double minDistance = Double.MAX_VALUE;
		Station nearestStation = null; 
		for (Station s: stations.values()) {
			double dist = pos.distance(s.getPosition());
			if ((dist > CONNECT_DISTANCE) && (dist < minDistance) && (s.getCoins() > 0)) {
				minDistance = dist;
				nearestStation = s;
			}
		}
		return nearestStation;
	}
	
	public Station getConnectedStation(Position pos) {
		Station connectedStation = null;
		double distanceFromDrone = Double.MAX_VALUE;
		for (Position stationPosition : stations.keySet()) {
			double dist = pos.distance(stationPosition);
			if (dist < distanceFromDrone) {
				connectedStation = stations.get(stationPosition);
				distanceFromDrone = dist;
			}
		}
		if (distanceFromDrone < CONNECT_DISTANCE) {
			return connectedStation;
		} else {
			return null;
		}
	}
}
