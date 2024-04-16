package ch.zhaw.nk.mdm_project2_kaeseno1;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.nk.mdm_project2_kaeseno1.model.Guest;

@RestController
public class ServiceController {

    DataAccessor dataAccessor = new DataAccessor();

    @GetMapping("/guests")
    public ArrayList<Guest> getIndex(){
        ArrayList<Guest> guests = new ArrayList<>();
        guests = dataAccessor.getAllEntries();
        return guests;
    }

    
    
}
