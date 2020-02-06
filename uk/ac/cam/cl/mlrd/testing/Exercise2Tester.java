package uk.ac.cam.cl.mlrd.testing;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

//TODO: Replace with your packages.
import uk.ac.cam.cl.an578.exercises.Exercise1;
import uk.ac.cam.cl.an578.exercises.Exercise2;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.DataPreparation1;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.IExercise1;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.IExercise2;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrd.utils.DataSplit;

public class Exercise2Tester {

    static final Path dataDirectory = Paths.get("data/sentiment_dataset");

    public static void main(String[] args) throws IOException {

        Path sentimentFile = dataDirectory.resolve("review_sentiment");
        // Loading the dataset.
        Map<Path, Sentiment> dataSet = DataPreparation1.loadSentimentDataset(dataDirectory.resolve("reviews"),
                sentimentFile);
        DataSplit<Sentiment> split = new DataSplit<Sentiment>(dataSet, 0);

        IExercise2 implementation = (IExercise2) new Exercise2();

        Map<Sentiment, Double> classProbabilities = implementation.calculateClassProbabilities(split.trainingSet);


        // Without smoothing
        Map<String, Map<Sentiment, Double>> logProbs = implementation.calculateUnsmoothedLogProbs(split.trainingSet);
        System.out.println("Log probabilities of unsmoothed classifier:");
        System.out.println(logProbs);
        System.out.println();

        Map<Path, Sentiment> NBPredictions = implementation.naiveBayes(split.validationSet.keySet(), logProbs,
                classProbabilities);

        // With smoothing
        Map<String, Map<Sentiment, Double>> smoothedLogProbs = implementation
                .calculateSmoothedLogProbs(split.trainingSet);
        System.out.println("Log probabilities of smoothed classifier:");
        System.out.println(smoothedLogProbs);
        System.out.println();

        Map<Path, Sentiment> smoothedNBPredictions = implementation.naiveBayes(split.validationSet.keySet(),
                smoothedLogProbs, classProbabilities);

        IExercise1 implementation1 = (IExercise1) new Exercise1();

        double NBAccuracy = implementation1.calculateAccuracy(split.validationSet, NBPredictions);
        System.out.println("Naive Bayes classifier accuracy without smoothing:");
        System.out.println(NBAccuracy);
        System.out.println();

        double smoothedNBAccuracy = implementation1.calculateAccuracy(split.validationSet, smoothedNBPredictions);
        System.out.println("Naive Bayes classifier accuracy with smoothing:");
        System.out.println(smoothedNBAccuracy);
        System.out.println();

        Path lexiconFile = Paths.get("Data/sentiment_lexicon.txt");

        Map<Path, Sentiment> simplePredictions = implementation1.simpleClassifier(split.validationSet.keySet(), lexiconFile);
        System.out.println("Simple classifier predictions:");
        System.out.println(simplePredictions);
        System.out.println();

        double simpleAccuracy = implementation1.calculateAccuracy(split.validationSet, simplePredictions);
        System.out.println("Simple classifier accuracy:");
        System.out.println(simpleAccuracy);
        System.out.println();
    }
}