package ch.zhaw.nk.mdm_project2_kaeseno1.ml;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import ai.djl.Model;
import ai.djl.nn.Block;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.core.Linear;

public class SurvivalModels {
    
    // Number of output classes: 2 (survived or not survived)
    public static final int NUM_OF_OUTPUT = 1;

    // Name of the model
    public static final String MODEL_NAME = "survivalclassifier";

    private SurvivalModels() {}

    public static Model getModel() {
        // Create new instance of an empty model
        Model model = Model.newInstance(MODEL_NAME);

        // Define the neural network architecture
        Block block = new SequentialBlock();
        ((SequentialBlock) block).add(Linear.builder().setUnits(64).build());
        ((SequentialBlock) block).add(Linear.builder().setUnits(NUM_OF_OUTPUT).build());

        // Set the neural network to the model
        model.setBlock(block);

        return model;
    }    
    
    public static void saveSynset(Path modelDir, List<String> synset) throws IOException {
        Path synsetFile = modelDir.resolve("synset.txt");
        try (Writer writer = Files.newBufferedWriter(synsetFile)) {
            writer.write(String.join("\n", synset));
        }
    }
}
