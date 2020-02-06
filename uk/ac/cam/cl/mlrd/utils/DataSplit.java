package uk.ac.cam.cl.mlrd.utils;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataSplit<T> {
    public Map<Path, T> trainingSet = new HashMap<Path, T>();
    public Map<Path, T> validationSet = new HashMap<Path, T>();

    int validationSizePerLabel = 100;

    public DataSplit(Map<Path, T> dataSet, int randSeed) {
        Set<T> possibleLabels = new HashSet<T>(dataSet.values());
        Map<T, List<Path>> labelPaths = new HashMap<T, List<Path>>();
        for (T label: possibleLabels) {
            // Select all data point with the label.
            List<Path> paths = dataSet.keySet().stream()
                    .filter(p -> dataSet.get(p).equals(label))
                    .collect(Collectors.toList());
            // Randomize the order.
            Collections.shuffle(paths, new Random(randSeed));
            labelPaths.put(label, paths);
        }
        // Assume balanced data. Gets balanced 8:1:1 train:validation:test split
        for (T label: labelPaths.keySet()) {
            List<Path> paths = labelPaths.get(label);
            validationSet.putAll(paths
                    .subList(0, validationSizePerLabel).stream()
                    .collect(Collectors.toMap(Function.identity(), p -> label)));
            trainingSet.putAll(labelPaths.get(label)
                    .subList(validationSizePerLabel, paths.size()).stream()
                    .collect(Collectors.toMap(Function.identity(), p -> label)));
        }
    }
}
