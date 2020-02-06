package uk.ac.cam.cl.an578.exercises;

import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.IExercise4;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Sentiment;
import uk.ac.cam.cl.mlrd.exercises.sentiment_detection.Tokenizer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Exercise4 implements IExercise4 {
    /**
     * Modify the simple classifier from Exercise1 to include the information about the magnitude of a sentiment.
     *
     * @param testSet     {@link Set}<{@link Path}> Paths to reviews to classify
     * @param lexiconFile {@link Path} Path to the lexicon file
     * @return {@link Map}<{@link Path}, {@link Sentiment}> Map of calculated
     * sentiment for each review
     * @throws IOException
     */
    @Override
    public Map<Path, Sentiment> magnitudeClassifier(Set<Path> testSet, Path lexiconFile) throws IOException {
        // convert the lexicon file into a hashmap:
        // tokenize it, then iterate through the list of strings (for each row), each row has 9 things, 2nd position has word and 8th has sentiment
        // convert sentiment to enum, then create a hash map of word and sentiment

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

        HashMap<Path, Sentiment> resultSentiments = new HashMap<>();

        for (Path path : testSet) {
            List<String> wordList = Tokenizer.tokenize(path);
            for (int i = 0; i < wordList.size(); i++) {
                String currentWord = wordList.get(i);

                //check if it's in sentimentMap

                if (sentimentMap.containsKey(currentWord)) {
                    if (sentimentMap.get(currentWord) == Sentiment.POSITIVE) {
                        if (intensityMap.get(currentWord).equals("strong")) {
                            posCounter = posCounter + 2;
                        } else {
                            posCounter = posCounter + 1;
                        }
                    } else {
                        if (intensityMap.get(currentWord).equals("strong")) {
                            negCounter = negCounter + 2;
                        } else {
                            negCounter = negCounter + 1;
                        }
                    }
                }
            }

            if (posCounter >= negCounter) { //POSITIVE OUTCOME
                resultSentiments.put(path, Sentiment.POSITIVE);
                posCounter = 0;
                negCounter = 0;
            } else {
                resultSentiments.put(path, Sentiment.NEGATIVE);
                posCounter = 0;
                negCounter = 0;
            }
        }

        return resultSentiments;
    }


    /**
     * Implement the two-sided sign test algorithm to determine if one
     * classifier is significantly better or worse than another.
     * The sign for a result should be determined by which
     * classifier is more correct, or if they are equally correct should be 0.5
     * positive, 0.5 negative and the ceiling of the least common sign total
     * should be used to calculate the probability.
     *
     * @param actualSentiments {@link Map}<{@link Path}, {@link Sentiment}>
     * @param classificationA  {@link Map}<{@link Path}, {@link Sentiment}>
     * @param classificationB  {@link Map}<{@link Path}, {@link Sentiment}>
     * @return <code>double</code>
     */
    @Override
    public double signTest(Map<Path, Sentiment> actualSentiments, Map<Path, Sentiment> classificationA, Map<Path, Sentiment> classificationB) {

        double Plus = 0; // The nu// mber of cases where A is better
        double Minus = 0; // The number of cases where A is worse
        double Null = 0; // The number of cases where A and B are the same

        for (Map.Entry<Path, Sentiment> entry : actualSentiments.entrySet()) {

            Path currentKey = entry.getKey();

            if (classificationA.containsKey(currentKey) && classificationB.containsKey(currentKey)) {
                Sentiment trueValue = entry.getValue();
                Sentiment A_predicted = classificationA.get(currentKey);
                Sentiment B_predicted = classificationB.get(currentKey);
                if ((A_predicted == trueValue && B_predicted == trueValue) || A_predicted != trueValue && B_predicted != trueValue) {
                    Null = Null + 1;
                } else if (A_predicted == trueValue && B_predicted != trueValue) {
                    Plus = Plus + 1;
                } else if (A_predicted != trueValue && B_predicted == trueValue) {
                    Minus = Minus + 1;
                }
            }
        }

        // Calculating the p-value
        BigInteger n = BigDecimal.valueOf(2 * Math.round(Null / 2.0) + Plus + Minus).toBigIntegerExact();
        BigInteger k = BigDecimal.valueOf(Math.round(Null / 2) + Math.min(Plus, Minus)).toBigIntegerExact();

        BigInteger p_value = BigInteger.ZERO;
        BigInteger temp_result;

        for (BigInteger i = BigInteger.ZERO; !i.equals(k.add(BigInteger.ONE)); i = i.add(BigInteger.ONE)) {
            temp_result = nChooseR(n,i);
            BigInteger temp_result1 = temp_result.divide(BigInteger.TWO.pow(n.intValueExact()));
            p_value = p_value.add(temp_result1);
        }

        p_value = p_value.multiply(BigInteger.TWO);
        System.out.println(p_value);

        double myResult = p_value.doubleValue();
        System.out.println(myResult);

        return myResult;

    }


    public static BigInteger factorial(BigInteger n) {
        if (n.equals(BigInteger.ZERO)) {
            return BigInteger.ONE;
        }
        return n.multiply(factorial(n.subtract(BigInteger.ONE)));
    }

    public static BigInteger nChooseR(BigInteger n, BigInteger r) {
        // Calculating n choose r in steps to not confuse myself too much
        BigInteger result = factorial(n).divide(factorial(r).multiply(factorial(n.subtract(r))));
        return result;
    }

    public static void main(String[] args) {

    }
}
