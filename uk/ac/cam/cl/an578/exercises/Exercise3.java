package uk.ac.cam.cl.an578.exercises;

import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.DataPreparation1;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Tokenizer;
import uk.ac.cam.cl.mlrd.utils.BestFit;
import uk.ac.cam.cl.mlrd.utils.ChartPlotter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class Exercise3<sorted> {

    // First must find the frequency of each word in the data set and rank them according to their freq
    // Should use tokenizer and re-use code from before which forms a hash map with the word as the key and frequency as its value.

    public static Map<String, Double> findWordFreqMap(Path datasetFile) throws IOException {
        // METHOD just for calcuating the word freq map to prevent laptop from bursting into flames

        Map<String, Double> wordFreqMap = new HashMap<>();
        Set<Path> dataSet = DataPreparation1.loadDataset(datasetFile);
        for (Path path : dataSet) {
            List<String> temp = Tokenizer.tokenize(path);
            for (String word : temp) {
                if (wordFreqMap.containsKey(word)) {
                    wordFreqMap.replace(word, wordFreqMap.get(word) + 1.0);
                } else {
                    wordFreqMap.put(word, 1.0);
                }
            }
        }
        return wordFreqMap;
    }

    public static List<BestFit.Point> Rank(Map<String, Double> wordFreqMap) throws IOException {

        //List<Map.Entry<String, Integer>> rankList = new ArrayList<>();
        //rankList.addAll(wordFreqMap.entrySet());
        //rankList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        //List<String> ranked = new ArrayList<>();

        //for (Map.Entry<String, Integer> entry : rankList) {
        //    ranked.add(entry.getKey());
        //}

        // You want to create a list which is sorted then use your hashmap to find the frequency of words.
        // List of entries ordered by value

        List<Map.Entry<String, Double>> sorted = wordFreqMap
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(10000L)
                .collect(Collectors.toList());

        List<Double> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : sorted) {
            result.add(entry.getValue());
        }


        List<BestFit.Point> rankList = new ArrayList<>();
        double i = 1.0;
        for (Double freq : result) {
            BestFit.Point point = new BestFit.Point(Math.log(i), Math.log(freq));
            rankList.add(point);
            i++;
        }
        return rankList;

    }

    public static List<BestFit.Point> tenWords(Map<String, Double> wordFreqMap) throws IOException {

        List<Map.Entry<String, Double>> sorted = wordFreqMap
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(10000L)
                .collect(Collectors.toList());

        List<String> my10words = new ArrayList<String>();
        my10words.add(0, "best");
        my10words.add(1, "amazing");
        my10words.add(2, "incredible");
        my10words.add(3, "brilliant");
        my10words.add(4, "funny");
        my10words.add(5, "awful");
        my10words.add(6, "bad");
        my10words.add(7, "horrible");
        my10words.add(8, "relaxing");
        my10words.add(9, "worst");

        List<BestFit.Point> my10wordsPointsList = new ArrayList<>();

        for (Map.Entry<String, Double> entry : sorted) {
            if (my10words.contains(entry.getKey())) {
                System.out.println(entry.getKey());
                System.out.println("freq = " + wordFreqMap.get(entry.getKey()));
                System.out.println(sorted.indexOf(new AbstractMap.SimpleEntry<String, Double>(entry.getKey(), entry.getValue()))); // This is rank
                BestFit.Point point = new BestFit.Point(sorted.indexOf(new AbstractMap.SimpleEntry<String, Double>(entry.getKey(),
                        entry.getValue())), wordFreqMap.get(entry.getKey()));
                my10wordsPointsList.add(point);
            }
        }
        return my10wordsPointsList;
    }

    public static Map<BestFit.Point, Double> bestFitLine(Map<String, Double> wordFreqMap) throws IOException {

        List<Map.Entry<String, Double>> sorted = wordFreqMap
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(10000L)
                .collect(Collectors.toList());

        List<Double> freqList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : sorted) {
            freqList.add(entry.getValue());
        }

        // create the map of point and frequency
        Map<BestFit.Point, Double> pointFreqMap = new HashMap<>();
        double j = 1.0;
        for (Double freq : freqList) {
            BestFit.Point point = new BestFit.Point(Math.log(j), Math.log(freq));
            pointFreqMap.put(point, freq);
            j++;
        }

        return pointFreqMap;
    }

    public static List<BestFit.Point> intercepts(Map<BestFit.Point, Double> pointFreqMap) {
        List<BestFit.Point> interceptList = new ArrayList<>();

        BestFit.Line line = BestFit.leastSquares(pointFreqMap);
        interceptList.add(new BestFit.Point(0, line.yIntercept));
        //interceptList.add(new BestFit.Point((-line.yIntercept/line.gradient),0)); // Don't do this as it messes up the range
        Double maxXvalue = Math.log(10000.0);
        interceptList.add(new BestFit.Point(maxXvalue, line.gradient * maxXvalue + line.yIntercept));

        return interceptList;
    }

    public static Double expectedFrequency(Double rank, Map<BestFit.Point, Double> pointFreqMap) {
        // rank = x value, freq = y value
        // given x value, find y value
        BestFit.Line bestFitLine = BestFit.leastSquares(pointFreqMap);
        Double expectedFreq = bestFitLine.gradient * rank + bestFitLine.yIntercept;


        return expectedFreq;
    }

    public static Map<String, Double> findingParameters(Map<BestFit.Point, Double> pointFreqMap) {
        Map<String, Double> parameters = new HashMap<>();
        BestFit.Line bestFitLine = BestFit.leastSquares(pointFreqMap);
        parameters.put("alpha", -1 * bestFitLine.gradient);
        parameters.put("k", Math.exp(bestFitLine.yIntercept));
        return parameters;
    }

    public static void plotHeapsLaw(Path datasetFile) throws IOException {
        List<String> wordList = new ArrayList<>();
        Set<Path> dataSet = DataPreparation1.loadDataset(datasetFile);
        List<BestFit.Point> dataPoints = new ArrayList<>();
        int noUnique = 0;
        int noTokens = 0;
        int nextPower = 1;
        for (Path path : dataSet) {
            List<String> temp = Tokenizer.tokenize(path);
            for (String word : temp) {
                noTokens++;
                if (!wordList.contains(word)) {
                    noUnique++;
                    wordList.add(word);
                }
                if (noTokens >= nextPower) {
                    dataPoints.add(new BestFit.Point(Math.log(noTokens), Math.log(noUnique)));
                    nextPower = nextPower * 2;
                }
            }
        }

        ChartPlotter.plotLines(dataPoints);
    }

    public static void main(String[] args) throws IOException {

        // Plotting log-log graph to find show the power relationship between the frequency and rank

        Path path = Paths.get("data/large_dataset");
        //Map<String, Double> wordFreqMap = findWordFreqMap(path);
        //Map<BestFit.Point, Double> pointFreqMap = bestFitLine(wordFreqMap);

        plotHeapsLaw(path);
        //System.out.println(findingParameters(pointFreqMap));
        //ChartPlotter.plotLines(Rank(wordFreqMap), tenWords(wordFreqMap));
        //System.out.println(Math.exp((expectedFrequency(Math.log(113.0), pointFreqMap))));

    }
}
