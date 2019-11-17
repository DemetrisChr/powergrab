package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Game {
    private GeoJSON gameMap;
    private Drone drone;
    private Map<Position, Station> stations;
    private String year;
    private String month;
    private String day;
    private double perfectScore = 0;


    public Game(String year, String month, String day, Drone drone) throws IOException {
        this.year = year;
        this.month = month;
        this.day = day;
        this.drone = drone;
        drone.setGame(this);
        this.initialiseGameMap();
    }

    private void initialiseGameMap() throws IOException {
        this.gameMap = new GeoJSON(year, month, day);
        this.stations = this.gameMap.getStationsFromMap();
        double ps = 0;
        for (Station s : stations.values())
            ps += Math.max(0, s.getCoins());
        this.perfectScore = ps;
    }

    public Station getConnectedStation(Position pos) {
        Station connectedStation = Collections.min(this.stations.values(), Comparator.comparing(s -> pos.distance(s.getPosition())));
        if (connectedStation.getPosition().distance(pos) < GameRules.CONNECT_DISTANCE)
            return connectedStation;
        else
            return null;
    }

    public Station getNearestPositiveStation(Position pos, Set<Station> excludedStations) {
        // Finds the nearest positive station to given position, excluding a set of stations
        // If there are no positive stations that are not excluded, null is returned
        Set<Station> stationsToCheck = new HashSet<Station>(this.stations.values());
        stationsToCheck.removeAll(excludedStations);
        stationsToCheck.remove(this.getConnectedStation(pos));
        stationsToCheck.removeIf(s -> s.getCoins() <= 0);
        if (stationsToCheck.isEmpty())
            return null;
        else
            return Collections.min(stationsToCheck, Comparator.comparing(s -> pos.distance(s.getPosition())));
    }

    public void outputToFiles() throws FileNotFoundException {
        String fileName = "./outputs/"+drone.getDroneType()+"-"+day+"-"+month+"-"+year;
        PrintWriter outputGeoJson = new PrintWriter(fileName+".geojson");
        outputGeoJson.println(gameMap);
        outputGeoJson.close();
        PrintWriter outputMoveHistory = new PrintWriter(fileName+".txt");
        List<Move> moveHistory = drone.getMoveHistory();
        for (Move move : moveHistory)
            outputMoveHistory.println(move);
        outputMoveHistory.close();
    }

    public void addPathToMap() { this.gameMap.addDronePathToGeoJSON(drone); }

    public double getPerfectScore() { return perfectScore; }
}
