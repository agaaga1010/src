package uk.ac.cam.cl.mlrd.exercises.sentiment_detection;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public interface IExercise2 {

    /**
     * Calculate the probability of a document belonging to a given class based
     * on the training data.
     *
     * @param trainingSet
     *            {@link Map}<{@link Path}, {@link Sentiment}> Training review
     *            paths
     * @return {@link Map}<{@link Sentiment}, {@link Double}> Class
     *         probabilities.
     * @throws IOException
     */
    public Map<Sentiment, Double> calculateClassProbabilities(Map<Path, Sentiment> trainingSet) throws IOException;

    /**
     * For each word and sentiment present in the training set, estimate the
     * unsmoothed log probability of a word to occur in a review with a
     * particular sentiment.
     *
     * @param trainingSet
     *            {@link Map}<{@link Path}, {@link Sentiment}> Training review
     *            paths
     * @return {@link Map}<{@link String}, {@link Map}<{@link Sentiment},
     *         {@link Double}>> Estimated log probabilities
     * @throws IOException
     */
    public Map<String, Map<Sentiment, Double>> calculateUnsmoothedLogProbs(Map<Path, Sentiment> trainingSet)
            throws IOException;

    /**
     * For each word and sentiment present in the training set, estimate the
     * smoothed log probability of a word to occur in a review with a particular
     * sentiment. Use the smoothing technique described in the instructions.
     *
     * @param trainingSet
     *            {@link Map}<{@link Path}, {@link Sentiment}> Training review
     *            paths
     * @return {@link Map}<{@link String}, {@link Map}<{@link Sentiment},
     *         {@link Double}>> Estimated log probabilities
     * @throws IOException
     */
    public Map<String, Map<Sentiment, Double>> calculateSmoothedLogProbs(Map<Path, Sentiment> trainingSet)
            throws IOException;

    /**
     * Use the estimated log probabilities to predict the sentiment of each
     * review in the test set.
     *
     * @param testSet
     *            {@link Set}<{@link Path}> Test review paths
     * @param tokenLogProbs
     *            {@link Map}<{@link String}, {@link Map}<{@link Sentiment},
     *            {@link Double}>> Log probabilities
     * @return {@link Map}<{@link Path}, {@link Sentiment}> Predicted sentiments
     * @throws IOException
     */
    public Map<Path, Sentiment> naiveBayes(Set<Path> testSet, Map<String, Map<Sentiment, Double>> tokenLogProbs,
                                           Map<Sentiment, Double> classProbabilities) throws IOException;
}
