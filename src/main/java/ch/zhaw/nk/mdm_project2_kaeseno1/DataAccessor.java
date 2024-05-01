package ch.zhaw.nk.mdm_project2_kaeseno1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ch.zhaw.nk.mdm_project2_kaeseno1.model.Guest;

public class DataAccessor {

    public ArrayList<Guest> getAllEntries() {
        ArrayList<Guest> guests = new ArrayList<>();

        boolean firstLineSkipped = false;
        String line = "";
        String splitBy = ";";
        try {
            try (// parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader("data/titanic3.CSV"))) {
                while ((line = br.readLine()) != null) // returns a Boolean value
                {
                    if (!firstLineSkipped) {
                        firstLineSkipped = true;
                        continue; // Skip processing the first line
                    }

                    Guest guest = new Guest();
                    String[] entry = line.split(splitBy); // use comma as separator
                    guest.setName(entry[2]);
                    guest.setSex(entry[3]);
                    boolean survived = entry[0].equals(1);
                    guest.setSurvived(survived);
                    guest.setPclass(Integer.parseInt(entry[0]));
                    guests.add(guest);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return guests;

    }
}
