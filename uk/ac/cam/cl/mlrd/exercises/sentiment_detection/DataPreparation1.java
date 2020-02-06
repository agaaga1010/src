
package uk.ac.cam.cl.mlrd.exercises.sentiment_detection;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class DataPreparation1 {

    /**
     * Loads the true labels of the review files.
     *
     * @param trueFile
     *            The file with file-sentiment pairings.
     * @return A map from a file name {@link String} to the sentiment
     *         {@link String}: 'POS' or 'NEG'.
     * @throws IOException
     */
    public static Map<String, String> loadTrueLabels(Path trueFile) throws IOException {
        Map<String, String> sentiments = new HashMap<String, String>();
        try (BufferedReader reader = Files.newBufferedReader(trueFile)) {
            reader.lines().forEach(new Consumer<String>() {
                @Override
                public void accept(String line) {
                    String[] tokens = line.split("\\s+");
                    sentiments.put(tokens[0], tokens[1]);
                }
            });
        } catch (IOException e) {
            throw new IOException("Can't access the file " + trueFile, e);
        }
        return sentiments;
    }

    /**
     * Loads in a set of reviews and matches them with their sentiments.
     *
     * @param reviewsDirectory
     *            {@link Path} to the directory containing the review files.
     * @param trueFile
     *            {@link Path} to the file with correct file-sentiment pairings.
     * @return A map from a review {@link Path} to its {@link Sentiment}.
     * @throws IOException
     */
    public static Map<Path, Sentiment> loadSentimentDataset(Path reviewsDirectory, Path trueFile) throws IOException {
        Map<String, String> trueLabels = loadTrueLabels(trueFile);
        Map<Path, Sentiment> dataSet = new HashMap<Path, Sentiment>();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(reviewsDirectory)) {
            for (Path item : files) {
                if (trueLabels.get(item.getFileName().toString()).equals("POS")) {
                    dataSet.put(item, Sentiment.POSITIVE);
                } else {
                    dataSet.put(item, Sentiment.NEGATIVE);
                }
            }
        } catch (IOException e) {
            throw new IOException("Can't read the reviews.", e);
        }
        return dataSet;
    }
    public static Set<Path> loadDataset(Path reviewsDirectory) throws IOException {
        Set<Path> dataSet = new HashSet<>();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(reviewsDirectory)) {
            for (Path item : files) {
                dataSet.add(item);
            }
        } catch (IOException e) {
            throw new IOException("Can't read the reviews.", e);
        }
        return dataSet;
    }

}