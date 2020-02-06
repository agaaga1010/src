package uk.ac.cam.cl.mlrd.exercises.sentiment_detection.an578;

import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Testing {
    public static HashMap<String,Sentiment> simpleClassifier(Path lexiconFile) throws IOException {
        List<String> lexiconList = Tokenizer.tokenize(lexiconFile);
        String key = "a";
        Sentiment sent = null;
        HashMap<String, Sentiment> sentimentMap = new HashMap<>();
        for (int i = 0; i < lexiconList.size(); i++) {
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
        return sentimentMap;
    }

        public static double calculateAccuracy(Map<Path, Sentiment> trueSentiments, Map<Path, Sentiment> predictedSentiments) {
            int numCorrect = 0;

            for (Map.Entry<Path, Sentiment> entry : trueSentiments.entrySet()) {
                Path currentKey = entry.getKey();
                System.out.println(currentKey);
            }

            double accuracy = numCorrect / trueSentiments.size();

            return accuracy;
        }




    public static void main(String[] args) throws IOException {
        double a = (957/1800) * 100;
        System.out.println(a);

    }
}
