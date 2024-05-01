package ch.zhaw.nk.mdm_project2_kaeseno1;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ch.zhaw.nk.mdm_project2_kaeseno1.ml.SurvivalAnalysis;
import ch.zhaw.nk.mdm_project2_kaeseno1.model.Guest;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ServiceController {

    DataAccessor dataAccessor = new DataAccessor();
    SurvivalAnalysis survivalInference = new SurvivalAnalysis(); // Instantiate SurvivalInference

    @GetMapping("/guests")
    public ArrayList<Guest> getIndex(){
        ArrayList<Guest> guests = new ArrayList<>();
        guests = dataAccessor.getAllEntries();
        return guests;
    }

    @PostMapping("/predict")
    public String predictSurvival(
            @RequestParam("sex") String sex,
            @RequestParam("age") int age,
            @RequestParam("class") String passengerClass
    ) {
        // Call a method to predict survival based on the input data
        String survivalResult = survivalInference.predictSurvival(sex, age, passengerClass);

        // Return the prediction result as a String
        if (survivalResult == "Survived") {
            return "You would survive!";
        } else {
            return "Unfortunately, you would not survive.";
        }
    }
}
