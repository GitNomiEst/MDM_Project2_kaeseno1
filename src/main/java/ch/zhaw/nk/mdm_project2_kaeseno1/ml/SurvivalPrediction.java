package ch.zhaw.nk.mdm_project2_kaeseno1.ml;

import ai.djl.*;
import ai.djl.inference.Predictor;
import ai.djl.nn.*;
import ai.djl.nn.core.*;
import ai.djl.training.*;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.dataset.ArrayDataset;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.translate.NoopTranslator;
import ai.djl.translate.TranslateException;
import ai.djl.metric.Metrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class SurvivalPrediction {

    Model model;

    public void trainModel() {
        Block block = new SequentialBlock()
                .add(Linear.builder().setUnits(128).build())
                .add(Activation.reluBlock())
                .add(Linear.builder().setUnits(64).build())
                .add(Activation.reluBlock())
                .add(Linear.builder().setUnits(1).build());

        model = Model.newInstance("survivalclassifier");
        model.setBlock(block);

        DefaultTrainingConfig config = new DefaultTrainingConfig(Loss.l2Loss())
                .addTrainingListeners(TrainingListener.Defaults.logging())
                .addEvaluator(new Accuracy());

        Trainer trainer = model.newTrainer(config);

        trainer.setMetrics(new Metrics());

        int batchSize = 32;
        int numEpochs = 30;

        try (NDManager manager = NDManager.newBaseManager()) {
            List<String> lines;
            try {
                lines = Files.readAllLines(Paths.get("./data/titanic3.csv"));

                float[][] features = new float[lines.size() - 1][];
                float[] labels = new float[lines.size() - 1];

                for (int i = 1; i < lines.size(); i++) {
                    String[] tokens = lines.get(i).split(",");
                    float[] row = new float[tokens.length];
            
                    // Parse and encode sex value
                    float sexValue = -1; // Default value for unknown
                    if (tokens[1].equals("male")) {
                        sexValue = 0;
                    } else if (tokens[1].equals("female")) {
                        sexValue = 1;
                    } else {
                        // Handle unknown gender value here, e.g., log a warning or skip the record
                        System.err.println("Unknown gender value: " + tokens[1]);
                        continue; // Skip this record
                    }
            
                    // Encode passenger class as numerical value
                    float classValue = tokens[2].equals("1st") ? 1 : tokens[2].equals("2nd") ? 2 : 3;

                    row[0] = sexValue;
                    row[1] = Float.parseFloat(tokens[3]); // age
                    row[2] = classValue;
                    row[3] = Float.parseFloat(tokens[4]); // sibsp
                    row[4] = Float.parseFloat(tokens[5]); // parch


                    // Extract features (all but last column)
                    features[i - 1] = Arrays.copyOf(row, row.length - 1);

                    // Extract labels (last column)
                    labels[i - 1] = row[row.length - 1];
                }

                NDArray featureArray = manager.create(features);
                NDArray labelArray = manager.create(labels);
                System.out.println("Features size: " + featureArray.size());
                System.out.println("Labels size: " + labelArray.size());

                ArrayDataset dataset = new ArrayDataset.Builder()
                        .setData(featureArray)
                        .optLabels(labelArray)
                        .optDevice(Device.cpu())
                        .setSampling(batchSize, false) // batch size and whether to shuffle
                        .build();

                RandomAccessDataset[] datasets = dataset.randomSplit(8, 2);

                EasyTrain.fit(trainer, numEpochs, datasets[0], datasets[1]);

                TrainingResult result = trainer.getTrainingResult();
                
                model.setProperty("Epoch", String.valueOf(numEpochs));
                model.setProperty("MSE", String.format("%.5f", result.getValidateLoss()));
                model.setProperty("Accuracy", String.format("%.5f", result.getValidateEvaluation("Accuracy")));

                String epochs = model.getProperty("Epoch");
                String mse = model.getProperty("MSE");
                String acc = model.getProperty("Accuracy");

                System.out.println("Training completed:");
                System.out.println("Total Epochs: " + epochs);
                System.out.println("Mean Squared Error on Validation Set: " + mse);
                System.out.println("Accuracy: "+acc);

                model.save(Paths.get("model"), "latestmodel");

                System.out.println("Model saved & prediction completed");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TranslateException e) {
                e.printStackTrace();
            }
        }
    }

    public String predict(String sex, int age, String passengerClass, int sibsp, int parch) {
        if (model == null) {
            trainModel();
            
        }
        try (NDManager manager = NDManager.newBaseManager()) {
            // Convert categorical features to numerical values
            float sexValue = sex.equals("male") ? 0 : 1;
            float classValue = passengerClass.equals("1st") ? 1 : passengerClass.equals("2nd") ? 2 : 3;
    
            // Create an array of features
            float[] features = { sexValue, age, classValue, sibsp, parch };
    
            // Creating an NDList for input which contains a single NDArray with reshaped features
            NDList input = new NDList(manager.create(features).reshape(new Shape(1, features.length)));
    
            // Using a try-with-resources statement to automatically close the predictor after usage
            try (Predictor<NDList, NDList> predictor = model.newPredictor(new NoopTranslator())) {
                // Perform the prediction
                NDList output = predictor.predict(input);
                // Assuming the output NDList contains a single NDArray and we need the first float value
                float prediction = output.singletonOrThrow().getFloat();
    
                // Return the prediction result as a String
                return prediction >= 0.5 ? "Survived" : "Not Survived";
            }
        } catch (Exception e) {
            System.err.println("Error during prediction: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}