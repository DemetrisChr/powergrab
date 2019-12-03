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
    private double perfectScore = 0; // The perfect score is the sum of the coins of all positive stations

    public GameMap(String year, String month, String day) throws IOException {
        this.geoJsonDocument = GeoJSON.retrieveDocumentForDate(year, month, day);
        this.stations = this.geoJsonDocument.getStationsFromMap();
        // Calculate the perfect score
        for (Station s : stations)
            this.perfectScore += Math.max(0, s.getCoins());
    }

    // Finds the station the given position pos is within range of (i.e. The station a drone at pos can connect to)
    public Station getStationToConnect(Position pos) {
        Station closestStation = Collections.min(this.stations, Comparator.comparing(s -> s.distanceFromPosition(pos)));
        if (closestStation.positionInRange(pos))
            // If pos is within range of the closest station, then that station is the station to connect
            return closestStation;
        else
            // pos is not within the range of any stations
            return null;
    }

    // Finds the nearest positive station to given position pos, excluding the excludedStations
    // If there are no positive stations found, null is returned
    public Station getNearestPositiveStation(Position pos, Set<Station> excludedStations) {
        Set<Station> stationsToCheck = new HashSet<Station>(this.stations);
        stationsToCheck.removeAll(excludedStations); // Ignore the excluded stations
        stationsToCheck.remove(this.getStationToConnect(pos)); // Ignore the station pos is within range of

        double minDistance = Double.POSITIVE_INFINITY;
        Station nearestPositiveStation = null;
        // Find the nearest positive station (Euclidean distance used)
        for (Station s : stationsToCheck) {
            double dist = s.distanceFromPosition(pos);
            if (s.isPositive() && (dist < minDistance)) {
                minDistance = dist;
                nearestPositiveStation = s;
            }
        }
        return nearestPositiveStation;
    }

    // Saves the Geo-JSON document to a file
    public void outputMapToFile(String fileName) throws FileNotFoundException {
        PrintWriter outputGeoJson = new PrintWriter(fileName+".geojson");
        outputGeoJson.println(geoJsonDocument);
        outputGeoJson.close();
    }

    // Plots the drone's path to the Geo-JSON document
    public void addDronePathToMap(Drone drone) { this.geoJsonDocument.addPathToGeoJSON(drone.getPath()); }

    public double getPerfectScore() { return perfectScore; }
}
