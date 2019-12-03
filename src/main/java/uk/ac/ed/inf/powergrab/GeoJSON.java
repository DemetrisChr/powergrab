package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.LineString;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


public class GeoJSON {
    private final FeatureCollection fc;

    // Returns the GeoJSON object for the specified date. Connects to the server, retrieves and parses the Geo-JSON
    // document for the given date. Throws IOException if unable to connect to server or if the URL is malformed
    public static GeoJSON retrieveDocumentForDate(String year, String month, String day) throws IOException {
        // Connect to the server
        String mapString = String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson", year, month, day);
        URL url = new URL(mapString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        // Parse the Geo-JSON document and store it as a Feature Collection
        // (Geo-JSON documents represent a collection of geographical features)
        InputStream is = conn.getInputStream();
        InputStreamReader isReader = new InputStreamReader(is);
        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse(isReader);
        String mapSource = root.toString();
        FeatureCollection featureCollection = FeatureCollection.fromJson(mapSource);
        return new GeoJSON(featureCollection);
    }

    private GeoJSON(FeatureCollection featureCollection) {
        this.fc = featureCollection;
    }

    // Returns a Set of the stations within the Geo-JSON document
    public Set<Station> getStationsFromMap(){
        Set<Station> stations = new HashSet<Station>();
        for (Feature f : fc.features()) {
            // For each station read its coins, power & coordinates and construct a Station object with these attributes
            double coins = f.getProperty("coins").getAsDouble();
            double power = f.getProperty("power").getAsDouble();
            Point p = (Point) f.geometry();
            List<Double> coordinates = p.coordinates();
            double longitude = coordinates.get(0);
            double latitude = coordinates.get(1);
            Position pos = new Position(latitude, longitude);
            stations.add(new Station(pos, coins, power));
        }
        return stations;
    }

    // Adds the given path (given as a list of positions) on the Geo-JSON document
    public void addPathToGeoJSON(List<Position> pathPositions) {
        ArrayList<Point> pointsList = new ArrayList<Point>();
        for (Position pos : pathPositions)
            pointsList.add(Point.fromLngLat(pos.longitude, pos.latitude));
        LineString flightPath = LineString.fromLngLats(pointsList);
        Feature f = Feature.fromGeometry(flightPath);
        fc.features().add(f);
    }

    @Override
    public String toString() {
        return this.fc.toJson();
    }
}
