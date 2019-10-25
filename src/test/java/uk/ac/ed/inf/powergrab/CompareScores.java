package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class CompareScores {
    public static void main(String[] args) {
        // 03 03 2019 55.944425 -3.188396 5678 stateful
        PrintStream fileOut;
        try {
            fileOut = new PrintStream("./outputs/compareScores.csv");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        }
        System.setOut(fileOut);
        LocalDate start = LocalDate.of(2019,1,1);
        LocalDate end = LocalDate.of(2021,1,1);
        System.out.println("droneType,date,score,perfectScore");
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            String day = String.format("%02d", date.getDayOfMonth());
            String month = String.format("%02d", date.getMonthValue());
            String year = String.format("%02d", date.getYear());
            List<String> droneTypes = Arrays.asList("stateless", "stateful");
            for (String droneType : droneTypes) {
                String[] mainArgs = {day, month, year, "55.944425", "-3.188396", "5678", droneType};
                App.main(mainArgs);
            }
        }
    }
}
