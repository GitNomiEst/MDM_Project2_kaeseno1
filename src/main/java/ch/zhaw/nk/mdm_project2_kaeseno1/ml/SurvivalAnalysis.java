package ch.zhaw.nk.mdm_project2_kaeseno1.ml;

import org.slf4j.LoggerFactory;

import ch.zhaw.nk.mdm_project2_kaeseno1.DataAccessor;
import ch.zhaw.nk.mdm_project2_kaeseno1.model.Guest;

import java.util.ArrayList;

import org.slf4j.Logger;

public class SurvivalAnalysis {

    DataAccessor dataAccessor = new DataAccessor();
    private static final  Logger logger = LoggerFactory.getLogger(SurvivalAnalysis.class);

    public void trainModel(){
        ArrayList<Guest> guests = dataAccessor.getAllEntries();
        
        try{   
            //Train your model here!

        } catch (Exception e){
            logger.error("Predicotr error", e);
        }
        
    }


    
}
