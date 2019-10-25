package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.Map;

public class Game {
	private GeoJSON gameMap = null;
	private Drone drone = null;
	private Map<Position, Station> stations = null;

	public static final double CONNECT_DISTANCE = 0.00025;
	private static final Game GAME_INSTANCE = new Game();
	
	private Game() {}

	public static Game getInstance() {
		return GAME_INSTANCE;
	}

	public void setDrone(Drone drone) {
		this.drone = drone;
	}

	public void setGameMap(String year, String month, String day) throws IOException {
		this.gameMap = new GeoJSON(year, month, day);
		this.stations = this.gameMap.getStationsFromMap();
	}

	public GeoJSON getGameMap() {
		return this.gameMap;
	}
	
	public Station getNearestPositiveStation(Position pos) {
		double minDistance = Double.MAX_VALUE;
		Station nearestStation = null; 
		for (Station s: this.stations.values()) {
			double dist = pos.distance(s.getPosition());
			if ((dist < minDistance) && (s.getCoins() > 0)) {
				minDistance = dist;
				nearestStation = s;
			}
		}
		return nearestStation;
	}
	
	public Station getConnectedStation(Position pos) {
		Station connectedStation = null;
		double distanceFromDrone = Double.MAX_VALUE;
		for (Position stationPosition : this.stations.keySet()) {
			double dist = pos.distance(stationPosition);
			if (dist < distanceFromDrone) {
				connectedStation = this.stations.get(stationPosition);
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
