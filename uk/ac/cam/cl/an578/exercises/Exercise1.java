package uk.ac.cam.cl.an578.exercises;

import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Exercise1 implements IExercise1 {
    /**
     * Read the lexicon and determine whether the sentiment of each review in
     * the test set is positive or negative based on whether there are more
     * positive or negative words.
     *
     * @param testSet     {@link Set}<{@link Path}> Paths to reviews to classify
     * @param lexiconFile {@link Path} Path to the lexicon file
     * @return {@link Map}<{@link Path}, {@link Sentiment}> Map of calculated
     * sentiment for each review
     * @throws IOException
     */



    @Override
    public Map<Path, Sentiment> simpleClassifier(Set<Path> testSet, Path lexiconFile) throws IOException {
        //convert the lexicon file into a hashmap:
        // tokenize it, then iterate through the list of strings (for each row), each row has 9 things, 2nd position has word and 8th has sentiment
        // convert sentiment to enum, then create a hash map of word and sentiment

        List<String> lexiconList = Tokenizer.tokenize(lexiconFile);
        String key = "a";
        Sentiment sent = null;
        HashMap<String,Sentiment> sentimentMap = new HashMap<>();
        for(int i = 0; i < lexiconList.size(); i++) {
            if (i % 9 == 2) {
                key = lexiconList.get(i);
            }
            if (i % 9 == 8) {
                String sentiment = lexiconList.get(i);
                if (sentiment.equals("positive")) {
                    sent = Sentiment.POSITIVE;
                }
                if (sentiment.equals("negative")) {
                    sent = Sentiment.NEGATIVE;
                }
                sentimentMap.put(key, sent);
            }
        }
        //do below for each ( for(Path path:testSet) )
        //then tokenise the reviews and iterate through the list that the tokenizer gives, and check whether each word is in the lexicon hashmap
        //check sentiment, if pos add 1, if neg -1, if not present ignore, then create hashmap with path to the review, and sentiment (overall)

        int posCounter = 0;
        int negCounter = 0;

        HashMap<Path,Sentiment> resultSentiments = new HashMap<>();

        for (Path path:testSet) {
            List<String> wordList = Tokenizer.tokenize(path);
            for (int i = 0; i < wordList.size(); i++) {
                String currentWord = wordList.get(i);

                //check if it's in sentimentMap

                if (sentimentMap.containsKey(currentWord)) {
                    if (sentimentMap.get(currentWord) == Sentiment.POSITIVE){
                        posCounter = posCounter + 1;
                    }
                    else {
                        negCounter = negCounter + 1;
                    }
                }
            }

            if (posCounter >= negCounter) { //POSITIVE OUTCOME
                resultSentiments.put(path, Sentiment.POSITIVE);
                posCounter = 0;
                negCounter = 0;
        }
        else {
            resultSentiments.put(path, Sentiment.NEGATIVE);
            posCounter = 0;
            negCounter = 0;
        }
        }

        return resultSentiments;
    }

    /**
     * Calculate the proportion of predicted sentiments that were correct.
     *
     * @param trueSentiments      {@link Map}<{@link Path}, {@link Sentiment}> Map of correct
     *                            sentiment for each review
     * @param predictedSentiments {@link Map}<{@link Path}, {@link Sentiment}> Map of calculated
     *                            sentiment for each review
     * @return <code>double</code> The overall accuracy of the predictions
     */
    @Override
    public double calculateAccuracy(Map<Path, Sentiment> trueSentiments, Map<Path, Sentiment> predictedSentiments) {
        double numCorrect = 0;

        for (Map.Entry<Path, Sentiment> entry : trueSentiments.entrySet()) {
            Path currentKey = entry.getKey();

            if (predictedSentiments.containsKey(currentKey)) {
                Sentiment trueValue = entry.getValue();
                Sentiment predictedValue = predictedSentiments.get(currentKey);
                if (trueValue == predictedValue) {
                    numCorrect = numCorrect + 1;
                }
            }
        }


        double size = predictedSentiments.size();

        double accuracy = numCorrect / size;

        return accuracy;
    }

    /**
     * Use the training data to improve your classifier, perhaps by choosing an
     * offset for the classifier cutoff which works better than 0.
     *
     * @param testSet     {@link Set}<{@link Path}> Paths to reviews to classify
     * @param lexiconFile {@link Path} Path to the lexicon file
     * @return {@link Map}<{@link Path}, {@link Sentiment}> Map of calculated
     * sentiment for each review
     * @throws IOException
     */
    @Override
    public Map<Path, Sentiment> improvedClassifier(Set<Path> testSet, Path lexiconFile) throws IOException {

        // Take into account the intensity of the word
        // Take into account negation?

        List<String> lexiconList = Tokenizer.tokenize(lexiconFile);
        String key = "a";
        String intensity = "b";
        Sentiment sent = null;
        HashMap<String, Sentiment> sentimentMap = new HashMap<>();
        HashMap<String, String> intensityMap = new HashMap<>();
        for (int i = 0; i < lexiconList.size(); i++) {
            if (i % 9 == 2) {               //could use buffered file reader instead or csv files to make this more readable
                key = lexiconList.get(i);
            }
            if (i % 9 == 5) {
                intensity = lexiconList.get(i);
            }
            if (i % 9 == 8) {
                String sentiment = lexiconList.get(i);
                if (sentiment.equals("positive")) {
                    sent = Sentiment.POSITIVE;
                }
                if (sentiment.equals("negative")) {
                    sent = Sentiment.NEGATIVE;
                }
                sentimentMap.put(key, sent);
                intensityMap.put(key, intensity);
            }
        }

        int posCounter = 0;
        int negCounter = 0;
        int posWeakCounter = 0;
        int negWeakCounter = 0;

        HashMap<Path, Sentiment> resultSentiments = new HashMap<>();

        for (Path path : testSet) {
            posCounter = 0;
            negCounter = 0;
            posWeakCounter = 0;
            negWeakCounter = 0;
            List<String> wordList = Tokenizer.tokenize(path);
            for (int i = 0; i < wordList.size(); i++) {
                String currentWord = wordList.get(i);

                //check if it's in sentimentMap

                if (sentimentMap.containsKey(currentWord)) {
                    if (sentimentMap.get(currentWord) == Sentiment.POSITIVE) {
                        if (intensityMap.get(currentWord).equals("strong")) {
                        posCounter = posCounter + 1;
                        } else {
                        posWeakCounter = posWeakCounter + 1;
                        }
                    } else {
                        if (intensityMap.get(currentWord).equals("strong"))
                        negCounter = negCounter + 1;
                        else {
                        negWeakCounter = negWeakCounter + 1;
                        }
                    }
                }
            }

            if (posCounter > negCounter) { //POSITIVE OUTCOME
                resultSentiments.put(path, Sentiment.POSITIVE);
            } else if (posCounter < negCounter) {
                resultSentiments.put(path, Sentiment.NEGATIVE);
            }
            else {
                if (posWeakCounter > negWeakCounter) {
                    resultSentiments.put(path, Sentiment.POSITIVE);
                }
                else {
                    resultSentiments.put(path, Sentiment.NEGATIVE);
                }
            }
        }


        return resultSentiments;
    }
    }

