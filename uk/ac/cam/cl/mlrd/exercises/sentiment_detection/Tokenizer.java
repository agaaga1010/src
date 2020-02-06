package uk.ac.cam.cl.mlrd.exercises.sentiment_detection;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordTokenFactory;

public class Tokenizer {

    /**
     * Tokenizes the contents of textFile.
     *
     * @param textFile
     *            {@link Path} to a text file.
     * @return A list of tokens from the text of the file.
     * @throws IOException
     */
    public static List<String> tokenize(Path textFile) throws IOException {
        List<String> result = new ArrayList<String>();
        try (BufferedReader reader = Files.newBufferedReader(textFile)) {
            PTBTokenizer<Word> tokenizer = new PTBTokenizer<Word>(reader, new WordTokenFactory(),
                    "untokenizable=noneDelete");
            while (tokenizer.hasNext()) {
                String token = tokenizer.next().word().toLowerCase();
                result.add(token);
            }
        } catch (IOException e) {
            throw new IOException("Can't access file " + textFile, e);
        }
        return result;
    }

}
