package ch.zhaw.nk.mdm_project2_kaeseno1.ml;

import ai.djl.Model;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;


public class SurvivalAnalysis {

    private Model model;

    public SurvivalAnalysis() {
        this.model = SurvivalModels.getModel();
    }

    public String predictSurvival(String sex, int age, String passengerClass) {
        // Preprocess input data
        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray input = manager.create(new float[]{age});
    
            // Make prediction
            try (Predictor<Float, Float> predictor = model.newPredictor(getTranslator())) {
                float prediction = predictor.predict(input.toFloatArray()[0]); // Convert NDArray to float
                return prediction >= 0.5 ? "Survived" : "Not Survived";
            }
        } catch (TranslateException e) {
            e.printStackTrace();
            return "Prediction Error";
        }
    }
    
    private Translator<Float, Float> getTranslator() {
        return new Translator<Float, Float>() {
            @Override
            public NDList processInput(TranslatorContext ctx, Float input) throws Exception {
                NDManager manager = ctx.getNDManager();
                // Convert Float input to NDArray
                NDArray ndInput = manager.create(new float[]{input});
                return (NDList) ndInput;
            }
    
            @Override
            public void prepare(TranslatorContext ctx) throws Exception {
                // No preparation needed
            }
    
            @Override
            public Float processOutput(TranslatorContext ctx, NDList list) throws Exception {
                // Implement output processing here
                // This method should return a Float
                throw new UnsupportedOperationException("Unimplemented method 'processOutput'");
            }
        };
    }
        
}

