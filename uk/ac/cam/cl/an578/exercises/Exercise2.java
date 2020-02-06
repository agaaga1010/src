package uk.ac.cam.cl.an578.exercises;

import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.IExercise2;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Tokenizer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

// ASK ABOUT improving efficiency by creating some additional class files, because there is a lot of repetition for neg and pos

// Uses a "bag of words assumption", meaning that the order of the words is considered irrelevant , only the frequency of their appearance

public class Exercise2 implements IExercise2 {

    @Override
    public Map<Sentiment, Double> calculateClassProbabilities(Map<Path, Sentiment> trainingSet) throws IOException {

        Set<Path> keySet = trainingSet.keySet();

        // must be able to use a structure (hashmap?) with the associated path and word
        Set<Path> posPaths = new HashSet<>();
        Set<Path> negPaths = new HashSet<>();

        for (Path path : keySet) {
            if (trainingSet.get(path) == Sentiment.POSITIVE) {
                posPaths.add(path);
            } else {
                negPaths.add(path);
            }
        }

        // now must read the reviews in positive/negative path and create hashmap of the words as keys and number of times seen as value
        // use buffered file reader? OR could just tokenise? As we don't care about order of words/meanings like with lexiconList

        double posProb = (double) posPaths.size() / (double) (posPaths.size() + negPaths.size());
        double negProb = (double) negPaths.size() / (double) (posPaths.size() + negPaths.size());

        HashMap<Sentiment, Double> result = new HashMap<>();
        result.put(Sentiment.POSITIVE, posProb);
        result.put(Sentiment.NEGATIVE, negProb);

        return result;
    }


    @Override
    public Map<String, Map<Sentiment, Double>> calculateUnsmoothedLogProbs(Map<Path, Sentiment> trainingSet) throws IOException {
        // Passes with 0.5 apparently?

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
            if (posOccurrences.containsKey(key)) {
                posProb = posOccurrences.get(key) / (double) (posWords.size());
            }
            if (negOccurrences.containsKey(key)) {
                negProb = negOccurrences.get(key) / (double) (negWords.size());
            }
            innerSentimentMap.put(Sentiment.POSITIVE, Math.log(posProb));
            innerSentimentMap.put(Sentiment.NEGATIVE, Math.log(negProb));
            finalResult.put(key, innerSentimentMap);
        }
        return finalResult;

    }


    @Override
    public Map<String, Map<Sentiment, Double>> calculateSmoothedLogProbs(Map<Path, Sentiment> trainingSet) throws IOException {

        // Same code as before, only thing that has to change is how the PROBABILITY IS CALCULATED.

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
            // Need to give values for both posProb and negProb every time
            // getOrDefault provides workaround for if a word is only seen in positive or only seen in negative reviews

            // BECAUSE of the add 1, these probabilities can't just stay as 0, MUST use getOrDefault instead, to return 0, if the key isn't present
            innerSentimentMap.put(Sentiment.POSITIVE, Math.log(posProb));
            innerSentimentMap.put(Sentiment.NEGATIVE, Math.log(negProb));
            finalResult.put(key, innerSentimentMap);
        }
        return finalResult;

    }

    public static void main(String[] args) {

    }
    @Override
    public Map<Path, Sentiment> naiveBayes(Set<Path> testSet, Map<String, Map<Sentiment, Double>> tokenLogProbs, Map<Sentiment, Double> classProbabilities) throws IOException {

        // What you want to do is calculate the sum of P(c) and P(w|c) for both positive and negative and then classify the review as whichever probability
        // is higher.

        double posClassProb = classProbabilities.get(Sentiment.POSITIVE);
        double negClassProb = classProbabilities.get(Sentiment.NEGATIVE);

        Map<Path, Sentiment> result = new HashMap<>();

        for (Path path : testSet) {
            List<String> temp = Tokenizer.tokenize(path);
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

            if (OverallPosProb >= OverallNegProb) {
                result.put(path, Sentiment.POSITIVE);
            } else {
                result.put(path, Sentiment.NEGATIVE);
            }
        }

        System.out.println(result);
        return result;
    }
}


