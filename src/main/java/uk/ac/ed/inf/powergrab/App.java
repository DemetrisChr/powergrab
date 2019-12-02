package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;

public class App {
    public static void main( String[] args ) {
        // Read arguments
        String day = args[0];
        String month = args[1];
        String year = args[2];
        double latitude = Double.parseDouble(args[3]);
        double longitude = Double.parseDouble(args[4]);
        long randomSeed = Integer.parseInt(args[5]);
        String droneType = args[6];

        // Run the simulation
        try {
            runSimulation(day, month, year, latitude, longitude, randomSeed, droneType);
        } catch (FileNotFoundException e) {
            System.out.println("Save directory for output files does not exist."); return;
        } catch (IOException e) {
            System.out.println("Unable to retrieve game map."); return;
        }
    }

    public static String runSimulation(String day, String month, String year, double latitude, double longitude, long randomSeed, String droneType) throws IOException, FileNotFoundException {
        GameMap gameMap = new GameMap(year, month, day); // Load and parse the game map

        // Create the drone object (Stateful or stateless depending on the droneType argument)
        Drone drone = (droneType.equals("stateful"))
                ? new StatefulDrone(new Position(latitude, longitude), gameMap, randomSeed)
                : new StatelessDrone(new Position(latitude, longitude), gameMap, randomSeed);

        drone.findPath(); // Plan the path of the drone in the map
        gameMap.addDronePathToMap(drone); // Add the drone's path to the Geo-JSON document

        // Output the Geo-JSON document (with the added drone path) and the drone's move history to files
        String fileName = drone.getDroneType()+"-"+day+"-"+month+"-"+year;
        gameMap.outputMapToFile(fileName);
        drone.outputMoveHistoryToFile(fileName);

        // Print and return the details of the map alongside the score achieved by the drone and the perfect score
        String result = droneType+","+year+"-"+month+"-"+day+","+drone.getCoins()+","+ gameMap.getPerfectScore();
        System.out.println(result);
        return result;
    }
}
