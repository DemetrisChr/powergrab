package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Game {
	private GeoJSON gameMap = null;
	private Drone drone = null;
	private Map<Position, Station> stations = null;
	private String year;
	private String month;
	private String day;
	private double perfectScore = 0;

	private static final Game GAME_INSTANCE = new Game();

	private Game() {}

	public static Game getInstance() {
		return GAME_INSTANCE;
	}

	public double getPerfectScore() {
		return perfectScore;
	}

	public void setDrone(Drone drone) {
		this.drone = drone;
	}

	public void setGameMap(String year, String month, String day) throws IOException {
		this.year = year;
		this.month = month;
		this.day = day;
		this.gameMap = new GeoJSON(year, month, day);
		this.stations = this.gameMap.getStationsFromMap();
		double ps = 0;
		for (Station s : stations.values())
			ps += Math.max(0, s.getCoins());
		this.perfectScore = ps;
	}

	public GeoJSON getGameMap() {
		return this.gameMap;
	}
	
	public Station getNearestPositiveStation(Position pos) {
		return getNearestPositiveStation(pos, new HashSet<Station>());
	}

	public Station getNearestPositiveStation(Position pos, Set<Station> excludedStations) {
		double minDistance = Double.MAX_VALUE;
		Station nearestStation = null;
		Set<Station> stationsToCheck = new HashSet<Station>(this.stations.values());
		stationsToCheck.removeAll(excludedStations);
		for (Station s: stationsToCheck) {
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
		if (distanceFromDrone < GameRules.CONNECT_DISTANCE) {
			return connectedStation;
		} else {
			return null;
		}
	}

	public void outputToFiles() throws FileNotFoundException {
		String fileName = "./outputs/"+drone.getDroneType()+"-"+day+"-"+month+"-"+year;
		PrintWriter outputGeoJson = new PrintWriter(fileName+".geojson");
		outputGeoJson.println(gameMap.getFeatureCollection().toJson());
		outputGeoJson.close();
		PrintWriter outputMoveHistory = new PrintWriter(fileName+".txt");
		List<Move> moveHistory = drone.getMoveHistory();
		for (Move move : moveHistory)
			outputMoveHistory.println(move);
		outputMoveHistory.close();
	}
}
