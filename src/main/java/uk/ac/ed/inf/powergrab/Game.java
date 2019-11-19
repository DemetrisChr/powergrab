package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {
    private GeoJSON gameMap;
    private Drone drone;
    private Set<Station> stations;
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
        for (Station s : stations)
            this.perfectScore += Math.max(0, s.getCoins());
    }

    public Station getConnectedStation(Position pos) {
        // Find station within range of the given position. If no such station exists, null is returned.
        Station connectedStation = Collections.min(this.stations, Comparator.comparing(s -> s.distanceFromPosition(pos)));
        if (connectedStation.distanceFromPosition(pos) < GameRules.CONNECT_DISTANCE)
            return connectedStation;
        else
            return null;
    }

    public Station getNearestPositiveStation(Position pos, Set<Station> excludedStations) {
        // Finds the nearest positive station to given position, excluding a set of stations
        // If there are no positive stations found, null is returned
        // If there is a station within range of the given position it is ignored
        Set<Station> stationsToCheck = new HashSet<Station>(this.stations);
        stationsToCheck.removeAll(excludedStations);
        stationsToCheck.remove(this.getConnectedStation(pos));

        double minDistance = Double.POSITIVE_INFINITY;
        Station nearestPositiveStation = null;
        for (Station s : stationsToCheck) {
            double dist = s.distanceFromPosition(pos);
            if ((s.getCoins() > 0) && (dist < minDistance)) {
                minDistance = dist;
                nearestPositiveStation = s;
            }
        }
        return nearestPositiveStation;
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
