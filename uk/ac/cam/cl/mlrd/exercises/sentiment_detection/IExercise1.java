package uk.ac.cam.cl.mlrd.exercises.sentiment_detection;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public interface IExercise1 {

    /**
     * Read the lexicon and determine whether the sentiment of each review in
     * the test set is positive or negative based on whether there are more
     * positive or negative words.
     *
     * @param testSet
     *            {@link Set}<{@link Path}> Paths to reviews to classify
     * @param lexiconFile
     *            {@link Path} Path to the lexicon file
     * @return {@link Map}<{@link Path}, {@link Sentiment}> Map of calculated
     *         sentiment for each review
     * @throws IOException
     */
    public Map<Path, Sentiment> simpleClassifier(Set<Path> testSet, Path lexiconFile) throws IOException;

    /**
     * Calculate the proportion of predicted sentiments that were correct.
     *
     * @param trueSentiments
     *            {@link Map}<{@link Path}, {@link Sentiment}> Map of correct
     *            sentiment for each review
     * @param predictedSentiments
     *            {@link Map}<{@link Path}, {@link Sentiment}> Map of calculated
     *            sentiment for each review
     * @return <code>double</code> The overall accuracy of the predictions
     */
    public double calculateAccuracy(Map<Path, Sentiment> trueSentiments, Map<Path, Sentiment> predictedSentiments);

    /**
     * Use the training data to improve your classifier, perhaps by choosing an
     * offset for the classifier cutoff which works better than 0.
     *
     * @param testSet
     *            {@link Set}<{@link Path}> Paths to reviews to classify
     * @param lexiconFile
     *            {@link Path} Path to the lexicon file
     * @return {@link Map}<{@link Path}, {@link Sentiment}> Map of calculated
     *         sentiment for each review
     * @throws IOException
     */
    public Map<Path, Sentiment> improvedClassifier(Set<Path> testSet, Path lexiconFile) throws IOException;

}
