package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class CompareScores {
    public static void main(String[] args) {
        String fileName = "./outputs/compareScores.csv";
        PrintWriter outputFile;
        try {
            outputFile = new PrintWriter(fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to find output file.");
            return;
        }
        LocalDate start = LocalDate.of(2019,1,1);
        LocalDate end = LocalDate.of(2020,12,31);
        outputFile.println("droneType,date,score,perfectScore");
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            String day = String.format("%02d", date.getDayOfMonth());
            String month = String.format("%02d", date.getMonthValue());
            String year = String.format("%02d", date.getYear());
            List<String> droneTypes = Arrays.asList("stateless", "stateful");
            for (String droneType : droneTypes) {
                double latitude = 55.944425;
                double longitude = -3.188396;
                long randomSeed = 5678;
                try {
                    String result = App.runSimulation(day, month, year, latitude, longitude, randomSeed, droneType);
                    outputFile.println(result);
                } catch (IOException e) {
                    System.out.println("Failed to fetch map.");
                    return;
                }
            }
        }
        outputFile.close();
    }
}
