package ch.zhaw.nk.mdm_project2_kaeseno1;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ch.zhaw.nk.mdm_project2_kaeseno1.ml.SurvivalPrediction;
import ch.zhaw.nk.mdm_project2_kaeseno1.model.Guest;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ServiceController {

    DataAccessor dataAccessor = new DataAccessor();

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
            @RequestParam("class") String passengerClass,
            @RequestParam("sibsp") int sibsp,
            @RequestParam("parch") int parch
    ) {
        SurvivalPrediction survivalPrediction = new SurvivalPrediction();

        String survivalResult = survivalPrediction.predict(sex, age, passengerClass, sibsp, parch);

        if ("Survived".equals(survivalResult)) {
            return "You would survive!";
        } else {
            return "Unfortunately, you would not survive.";
        }
    }
}
