package uk.ac.cam.cl.an578.exercises;

import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.DataPreparation1;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Tokenizer;
import uk.ac.cam.cl.mlrd.utils.DataSplit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SupervisionOne {

    public static Sentiment improvedClassifier(Path reviewPath, Path lexiconFile) throws IOException {

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


            posCounter = 0;
            negCounter = 0;
            posWeakCounter = 0;
            negWeakCounter = 0;
            List<String> wordList = Tokenizer.tokenize(reviewPath);
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

            Sentiment finalResult;

            if (posCounter > negCounter) { //POSITIVE OUTCOME
                finalResult = Sentiment.POSITIVE;
            } else if (posCounter < negCounter) {
                finalResult = Sentiment.NEGATIVE;
            }
            else {
                if (posWeakCounter > negWeakCounter) {
                    finalResult = Sentiment.POSITIVE;
                }
                else {
                    finalResult = Sentiment.POSITIVE;
                }
            }



        return finalResult;
    }

    public static Sentiment naiveBayes(Path bookReview, Map<String, Map<Sentiment, Double>> tokenLogProbs, Map<Sentiment, Double> classProbabilities) throws IOException {

        // What you want to do is calculate the sum of P(c) and P(w|c) for both positive and negative and then classify the review as whichever probability
        // is higher.

        double posClassProb = classProbabilities.get(Sentiment.POSITIVE);
        double negClassProb = classProbabilities.get(Sentiment.NEGATIVE);



            List<String> temp = Tokenizer.tokenize(bookReview);
            double OverallPosProb = 0;
            double OverallNegProb = 0;
            double totalPosLogProbs = 0;
            double totalNegLogProbs = 0;
            // PROBABILITY CALCULATION
            for (String key : temp) {
                if (tokenLogProbs.containsKey(key)) {
                    if (tokenLogProbs.get(key).containsKey(Sentiment.POSITIVE)) {
                        double tokenPosProb = tokenLogProbs.get(key).get(Sentiment.POSITIVE);
                        totalPosLogProbs = totalPosLogProbs + tokenPosProb;
                    }
                    if (tokenLogProbs.get(key).containsKey(Sentiment.NEGATIVE)) {
                        double tokenNegProb = tokenLogProbs.get(key).get(Sentiment.NEGATIVE);
                        totalNegLogProbs = totalNegLogProbs + tokenNegProb;
                    }
                }
            }
            OverallPosProb = posClassProb + totalPosLogProbs;
            OverallNegProb = negClassProb + totalNegLogProbs;

            Sentiment result;

            if (OverallPosProb >= OverallNegProb) {
                result = Sentiment.POSITIVE;
            } else {
                result = Sentiment.NEGATIVE;
            }

        return result;
    }

    public static Map<String, Map<Sentiment, Double>> calculateSmoothedLogProbs(Map<Path, Sentiment> trainingSet) throws IOException {
        Set<Path> keySet = trainingSet.keySet();

        List<String> allWords = new ArrayList<>();
        List<String> posWords = new ArrayList<>();
        List<String> negWords = new ArrayList<>();
        Set<String> allWordsNoDupes = new HashSet<>();


        for (Path path : keySet) {
            List<String> temp = Tokenizer.tokenize(path);
            allWords.addAll(temp);
            allWordsNoDupes.addAll(temp);
            if (trainingSet.get(path) == Sentiment.POSITIVE) {
                posWords.addAll(temp);
            } else {
                negWords.addAll(temp);
            }
        }
        Map<String, Double> posOccurrences = new HashMap<>();
        for (String word : posWords) {
            if (posOccurrences.containsKey(word)) {
                posOccurrences.put(word, posOccurrences.get(word) + 1.0);
            } else {
                posOccurrences.put(word, 1.0);
            }
        }
        Map<String, Double> negOccurrences = new HashMap<>();
        for (String word : negWords) {
            if (negOccurrences.containsKey(word)) {
                double current = negOccurrences.get(word);
                negOccurrences.put(word, current + 1.0);
            } else {
                negOccurrences.put(word, 1.0);
            }
        }

        Map<String, Map<Sentiment, Double>> finalResult = new HashMap<>();

        for (String key : allWordsNoDupes) {
            Map<Sentiment, Double> innerSentimentMap = new HashMap<>(); // Must be new each time?
            double posProb = 0;
            double negProb = 0;
            posProb = (posOccurrences.getOrDefault(key, 0.0) + 1.0) / (double) (posWords.size() + allWordsNoDupes.size());
            negProb = (negOccurrences.getOrDefault(key, 0.0) + 1.0) / (double) (negWords.size() + allWordsNoDupes.size());

            innerSentimentMap.put(Sentiment.POSITIVE, Math.log(posProb));
            innerSentimentMap.put(Sentiment.NEGATIVE, Math.log(negProb));
            finalResult.put(key, innerSentimentMap);
        }
        return finalResult;

    }

    public static void main(String[] args) throws IOException {

        Map<Sentiment, Double> classProbs = new HashMap<>();
        classProbs.put(Sentiment.POSITIVE, 0.5);
        classProbs.put(Sentiment.NEGATIVE, 0.5);

        Path dataDirectory = Paths.get("data/sentiment_dataset");
        Path sentimentFile = dataDirectory.resolve("review_sentiment");
        Map<Path, Sentiment> dataSet = DataPreparation1.loadSentimentDataset(dataDirectory.resolve("reviews"),
                sentimentFile);
        DataSplit<Sentiment> split = new DataSplit<Sentiment>(dataSet, 0);

        Map<String, Map<Sentiment, Double>> smoothedLogProbs = calculateSmoothedLogProbs(split.trainingSet);

        Path path = Paths.get("Data/book_review.txt");
        Path lexicon = Paths.get("Data/sentiment_lexicon.txt");

        //System.out.println(improvedClassifier(path, lexicon));
        System.out.println(naiveBayes(path, smoothedLogProbs, classProbs));

    }
}
