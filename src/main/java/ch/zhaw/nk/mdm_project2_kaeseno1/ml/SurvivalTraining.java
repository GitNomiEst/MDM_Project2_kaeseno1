package ch.zhaw.nk.mdm_project2_kaeseno1.ml;

import ai.djl.Model;
import ai.djl.basicdataset.tabular.CsvDataset;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingConfig;
import ai.djl.training.TrainingResult;
import ai.djl.training.dataset.Batch;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.translate.TranslateException;
import ch.zhaw.nk.mdm_project2_kaeseno1.ml.SurvivalModels;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SurvivalTraining {

    private static final int BATCH_SIZE = 32;
    private static final int EPOCHS = 10;

    public void trainModel() throws IOException, TranslateException {
        // Load Titanic dataset
        CsvDataset dataset = CsvDataset.builder()
                .optCsvUrl("data/titanic3.csv")
                .setSampling(BATCH_SIZE, true)
                .build();

        
        // Split dataset into training and validation datasets
        RandomAccessDataset[] datasets = dataset.randomSplit(80, 20);

        // Define loss function
        Loss loss = Loss.softmaxCrossEntropyLoss();

        // Setup training configuration
        TrainingConfig config = setupTrainingConfig(loss);

        // Initialize model
        Model model = getModel();
        model.getBlock().clear(); // Clear existing blocks

        //Trainer trainer;
        

        
        Trainer trainer = null;
        // Initialize trainer with proper input shape
        try  {
            trainer = model.newTrainer(config);
            trainer.initialize(getSampleShape(dataset));

            // Train the model
            EasyTrain.fit(trainer, EPOCHS, datasets[0], datasets[1]);
        } catch(Exception e){
            e.printStackTrace();
        }

        // Save the trained model
        Path modelDir = Paths.get("models");
        model.save(modelDir, SurvivalModels.MODEL_NAME);

        // Save model properties
        TrainingResult result = trainer.getTrainingResult();
        model.setProperty("Epoch", String.valueOf(EPOCHS));
        model.setProperty("Accuracy", String.format("%.5f", result.getValidateEvaluation("Accuracy")));
        model.setProperty("Loss", String.format("%.5f", result.getValidateLoss()));
        //SurvivalModels.saveSynset(modelDir, dataset.getSynset());
    }

    private TrainingConfig setupTrainingConfig(Loss loss) {
        return new DefaultTrainingConfig(loss)
                .addEvaluator(new Accuracy())
                .addTrainingListeners(TrainingListener.Defaults.logging());
    }

    private Model getModel() {
        // Implement your model creation logic here
        return null;
    }

    private Shape getSampleShape(CsvDataset dataset) {
        // Get the shape of the input sample
        int numFeatures = dataset.getFeatures().size();
        return new Shape(1, numFeatures);
    }
}
