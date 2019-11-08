package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;

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
        // Run the simulation
        try {
            runSimulation(day, month, year, latitude, longitude, randomSeed, droneType);
        } catch (IOException e) {
            System.out.println("Error reading map");
            return;
        }
    }

    public static String runSimulation(String day, String month, String year, double latitude, double longitude, long randomSeed, String droneType) throws IOException {
        Drone drone;
        if (droneType.equals("stateful")) {
            drone = new StatefulDrone(new Position(latitude, longitude), randomSeed);
        } else {
            drone = new StatelessDrone(new Position(latitude, longitude), randomSeed);
        }
        Game game = new Game(year, month, day, drone);
        drone.planPath();
        game.getGameMap().addPathToGeoJSON(drone.getPath());
        try {
            game.outputToFiles();
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
        String result = droneType+","+year+"-"+month+"-"+day+","+drone.getCoins()+","+game.getPerfectScore();
        System.out.println(result);
        return result;
    }
}
