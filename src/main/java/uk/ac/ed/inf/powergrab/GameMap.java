package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameMap {
    private GeoJSON geoJsonDocument;
    private Set<Station> stations;
    private double perfectScore = 0;

    public GameMap(String year, String month, String day) throws IOException {
        this.geoJsonDocument = new GeoJSON(year, month, day);
        this.stations = this.geoJsonDocument.getStationsFromMap();
        for (Station s : stations)
            this.perfectScore += Math.max(0, s.getCoins());
    }

    public Station getConnectedStation(Position pos) {
        // Find station within range of the given position. If no such station exists, null is returned.
        Station connectedStation = Collections.min(this.stations, Comparator.comparing(s -> s.distanceFromPosition(pos)));
        if (connectedStation.positionInRange(pos))
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
            if (s.isPositive() && (dist < minDistance)) {
                minDistance = dist;
                nearestPositiveStation = s;
            }
        }
        return nearestPositiveStation;
    }

    public void outputMapToFile(String fileName) throws FileNotFoundException {
        PrintWriter outputGeoJson = new PrintWriter(fileName+".geojson");
        outputGeoJson.println(geoJsonDocument);
        outputGeoJson.close();
    }

    public void addDronePathToMap(Drone drone) { this.geoJsonDocument.addDronePathToGeoJSON(drone); }

    public double getPerfectScore() { return perfectScore; }
}
