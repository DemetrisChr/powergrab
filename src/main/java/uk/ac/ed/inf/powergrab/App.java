package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

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

		Drone drone;
		System.out.println(droneType);
		if (droneType.equals("stateful")) {
			drone = new StatefulDrone(new Position(latitude, longitude), randomSeed);
		} else {
			drone = new StatelessDrone(new Position(latitude, longitude), randomSeed);
		}
		Game.getInstance().setDrone(drone);
		try {
			Game.getInstance().setGameMap(year, month, day);
		} catch (MalformedURLException e) {
			System.out.println("URL is malformed.");
			return;
		} catch (IOException e) {
			System.out.println("Error reading input");
			return;
		}
		drone.planPath();
        Game.getInstance().getGameMap().addPathToGeoJSON(drone.getPath());
        try {
			Game.getInstance().outputToFiles();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		}
    }
}
