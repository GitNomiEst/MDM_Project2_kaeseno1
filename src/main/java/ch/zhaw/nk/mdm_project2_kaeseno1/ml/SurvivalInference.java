package ch.zhaw.nk.mdm_project2_kaeseno1.ml;

import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.FloatList;
import ai.djl.modality.cv.translator.FloatListTranslator;
import ai.djl.modality.cv.translator.ClassificationTranslator;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;



public class SurvivalInference {

    private Predictor<FloatList, Classifications> predictor;


    public SurvivalInference() {
        try {
            Model model = SurvivalModels.getModel();
            Path modelDir = Paths.get("models");
            model.load(modelDir, SurvivalModels.MODEL_NAME);

            // Specify translators for input and output data
            FloatListTranslator inputTranslator = new FloatListTranslator();
            ClassificationTranslator outputTranslator = new ClassificationTranslator();

            // Create a new predictor with the specified translators
            predictor = model.newPredictor(inputTranslator, outputTranslator);

        } catch (IOException | TranslateException e) {
            e.printStackTrace();
        }
    }

    public String predictSurvival(String sex, int age, String passengerClass) {
        // Prepare input data
        List<Float> inputData = prepareInputData(sex, age, passengerClass);

        try {
            // Perform prediction
            Classifications result = predictor.predict(inputData);

            // Process prediction result
            String prediction = processPrediction(result);

            return prediction;
        } catch (TranslateException e) {
            e.printStackTrace();
            return "Prediction failed";
        }
    }

    private List<Float> prepareInputData(String sex, int age, String passengerClass) {
        // Convert input data to feature vector
        // For simplicity, we assume the following mapping:
        // Sex: male=0, female=1
        // Passenger class: 1st=1, 2nd=2, 3rd=3

        int sexValue = sex.equalsIgnoreCase("male") ? 0 : 1;
        float classValue;
        switch (passengerClass.toLowerCase()) {
            case "1st":
                classValue = 1.0f;
                break;
            case "2nd":
                classValue = 2.0f;
                break;
            case "3rd":
                classValue = 3.0f;
                break;
            default:
                classValue = 3.0f; // Default to 3rd class
        }

        // Create input feature vector
        return Arrays.asList((float) sexValue, (float) age, classValue);
    }

    private String processPrediction(Classifications result) {
        // Process prediction result
        // For example, you can extract the predicted class and confidence score
        // and format it as a string.

        // Assuming a binary classification model, get the probability of the positive class
        float probability = (float) result.best().getProbability();
        // Define a threshold for classification (e.g., 0.5)
        float threshold = 0.5f;

        // Determine the predicted class based on the probability and threshold
        String predictedClass = probability >= threshold ? "Survived" : "Not Survived";
        // Format prediction as a string
        String prediction = String.format("Predicted class: %s, Probability: %.2f", predictedClass, probability);

        return prediction;
    }
}
