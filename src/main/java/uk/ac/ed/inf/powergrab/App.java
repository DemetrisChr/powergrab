package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	// Reading Arguments
    	String day = args[0];
    	String month = args[1];
    	String year = args[2];
    	double latitude = Double.parseDouble(args[3]);
    	double longitude = Double.parseDouble(args[4]);
    	long randomSeed = Integer.parseInt(args[5]);
    	String droneType = args[6];
    	
        GeoJSON gj = null;
		try {
			gj = new GeoJSON(year, month, day);
		} catch (MalformedURLException e) {
			System.out.println("URL is malformed.");
			return;
		} catch (IOException e) {
			System.out.println("Error reading input");
			return;
		}
		Map<Position, Station> stations = gj.getStations();
		Drone drone;
		System.out.println(droneType);
		if (droneType.equals("stateful")) {
			drone = new StatefulDrone(new Position(latitude, longitude), randomSeed);
		} else {
			drone = new StatelessDrone(new Position(latitude, longitude), randomSeed);
		}
		Game game = new Game(drone, stations);
		drone.planPath();
		List<Position> path = drone.getPath();
        gj.addPathToJSON(path);
        try {
			gj.printGeoJsonToFile();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
    }
}
