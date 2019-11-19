package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;

public class App {
    public static void main( String[] args ) {
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
        GameMap gameMap = new GameMap(year, month, day);
        Drone drone = (droneType.equals("stateful"))
                ? new StatefulDrone(new Position(latitude, longitude), gameMap, randomSeed)
                : new StatelessDrone(new Position(latitude, longitude), gameMap, randomSeed);
        drone.planPath();
        gameMap.addDronePathToMap(drone);
        String fileName = "./outputs/"+drone.getDroneType()+"-"+day+"-"+month+"-"+year;
        try {
            gameMap.outputMapToFile(fileName);
            drone.outputMoveHistoryToFile(fileName);

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
        String result = droneType+","+year+"-"+month+"-"+day+","+drone.getCoins()+","+ gameMap.getPerfectScore();
        System.out.println(result);
        return result;
    }
}
