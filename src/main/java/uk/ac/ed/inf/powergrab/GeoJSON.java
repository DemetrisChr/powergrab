package uk.ac.ed.inf.powergrab;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GeoJSON {
	private final JsonObject featureCollection;
	private Map<Position, Station> stations = new HashMap<Position, Station>();
	
	public GeoJSON(String year, String month, String day) throws MalformedURLException, IOException {
		URL url = new URL(String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson", year, month, day));
		URLConnection connection = url.openConnection();
		connection.connect();
			
		InputStream is = connection.getInputStream();
		InputStreamReader isR = new InputStreamReader(is);
		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(isR);
		this.featureCollection = root.getAsJsonObject();
		
		// Fill the stations map
		JsonArray features = featureCollection.get("features").getAsJsonArray();
		for (JsonElement feature : features) {
			JsonObject featureObject = feature.getAsJsonObject();
			JsonObject properties = featureObject.get("properties").getAsJsonObject();
			double coins = properties.get("coins").getAsDouble();
			double power = properties.get("power").getAsDouble();
			JsonArray coordinates = featureObject.get("geometry").getAsJsonObject().get("coordinates").getAsJsonArray();
			double longitude = coordinates.get(0).getAsDouble();
			double latitude = coordinates.get(1).getAsDouble();
			Position pos = new Position(latitude, longitude);
			this.stations.put(pos, new Station(pos, coins, power));
		}
		System.out.println(featureCollection);
	}
	
	public Map<Position, Station> getStations(){
		return stations;
	}
	
	
	public void addPathToJSON(List<Position> pathPositions) {
		JsonArray features = featureCollection.get("features").getAsJsonArray();
		JsonObject lineString = new JsonObject();
		lineString.addProperty("type", "Feature");
		lineString.add("properties", new JsonObject());
		JsonObject geometry = new JsonObject();
		geometry.addProperty("type", "LineString");
		JsonArray coordinates = new JsonArray();
		geometry.add("coordinates", coordinates);
		for (Position pos : pathPositions) {
			JsonArray positionCoords = new JsonArray();
			positionCoords.add(pos.longitude);
			positionCoords.add(pos.latitude);
			coordinates.add(positionCoords);
		}
		lineString.add("geometry", geometry);
		features.add(lineString);
	}
	
	public void printGeoJsonToFile() throws FileNotFoundException {
		PrintWriter output = new PrintWriter("output.geojson");
		output.println(featureCollection.toString());
		output.close();
	}
	
	public String toString() {
		return "GeoJSON object representing map with " + stations.size() + " stations";
	}
}
