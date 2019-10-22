package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.LineString;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GeoJSON {
	private final FeatureCollection fc;
	private Map<Position, Station> stations = new HashMap<Position, Station>();
	
	public GeoJSON(String year, String month, String day) throws IOException {
		String mapString = String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson", year, month, day);
		URL url = new URL(mapString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		conn.connect();
			
		InputStream is = conn.getInputStream();
		InputStreamReader isReader = new InputStreamReader(is);
		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(isReader);
		String mapSource = root.toString();
		this.fc = FeatureCollection.fromJson(mapSource);
		System.out.println(mapSource.length());

		// Fill the stations map
		for (Feature f : fc.features()) {
			double coins = f.getProperty("coins").getAsDouble();
			double power = f.getProperty("power").getAsDouble();
			Point p = (Point) f.geometry();
			List<Double> coordinates = p.coordinates();
			double longitude = coordinates.get(0);
			double latitude = coordinates.get(1);
			Position pos = new Position(latitude, longitude);
			this.stations.put(pos, new Station(pos, coins, power));
		}
	}
	
	public Map<Position, Station> getStations(){
		return stations;
	}
	
	
	public void addPathToJSON(List<Position> pathPositions) {
		ArrayList<Point> pointsList = new ArrayList<Point>();
		for (Position pos : pathPositions) {
			pointsList.add(Point.fromLngLat(pos.longitude, pos.latitude));
		}
		LineString flightPath = LineString.fromLngLats(pointsList);
		Feature f = Feature.fromGeometry(flightPath);
		fc.features().add(f);
	}
	
	public void printGeoJsonToFile() throws FileNotFoundException {
		PrintWriter output = new PrintWriter("output.geojson");
		output.println(fc.toJson());
		output.close();
	}
	
	public String toString() {
		return "GeoJSON object representing map with " + stations.size() + " stations";
	}
}
