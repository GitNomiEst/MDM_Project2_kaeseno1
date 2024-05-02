package ch.zhaw.nk.mdm_project2_kaeseno1;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ai.djl.translate.TranslateException;
import ch.zhaw.nk.mdm_project2_kaeseno1.ml.SurvivalTraining;

@SpringBootApplication
public class MdmProject2Kaeseno1Application {

    public static void main(String[] args) {
        SpringApplication.run(MdmProject2Kaeseno1Application.class, args);
        trainModel();
    }

    private static void trainModel() {
        SurvivalTraining survivalTraining = new SurvivalTraining();
        try {
            try {
				survivalTraining.trainModel();
			} catch (TranslateException e) {
				e.printStackTrace();
			}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
