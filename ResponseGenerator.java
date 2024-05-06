import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.HashMap;

/**
 * Utility that maps a prompt from a user to a response.
 * Objective: answer simple user input from gathered 10-K data
 * 
 * @author Grace Zhang
 * @version 1.0
 */
public class ResponseGenerator {
    private static ResponseGenerator responseGenerator = null;
    private Map<String, String> keywordToReponseMap;

    /**
     * Singleton constructor.
     * Read file contents into keywordResponse list.
     * 
     * @param filename file containing keyword/response info with tokens delimited
     *                 by '\u001C'
     */
    private ResponseGenerator(String filename) throws IOException {
        this.keywordToReponseMap = new HashMap<>();
        List<String> keywordResponse = new ArrayList<>(); // should follow Keyword1 -> Response1 -> Keyword2 -> etc.

        File inFile;
        Scanner fileScanner;
        String delimiter = "\u001C";
        String responses = null;
        try {
            inFile = new File(filename);
            fileScanner = new Scanner(inFile);
            while (fileScanner.hasNextLine()) {
                responses = fileScanner.nextLine();
            }
            String[] parts = responses.split(delimiter);
            for (String part : parts) {
                keywordResponse.add(part);
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException caught: " + e.getMessage());
            System.out.println("Double check the file path!!!");
        } catch (NullPointerException n) {
            System.err.println("NullPointerException caught: " + n.getMessage());
            System.out.println("It appears that the file does not contain anything!!!");
        }

        for (int i = 0; i < keywordResponse.size(); i += 2) {
            keywordToReponseMap.put(keywordResponse.get(i), keywordResponse.get(i + 1));
        }
    }

    /**
     * Returns the only existing ResponseGenerator.
     * 
     * @return the ResponseGenerator
     */
    public static ResponseGenerator getResponseGenerator() throws IOException {
        if (responseGenerator != null) {
            return responseGenerator;
        }

        responseGenerator = new ResponseGenerator("keyword_response.txt");
        return responseGenerator;
    }

    /**
     * Returns the Chatbot response to a user's prompt.
     * 
     * @param prompt Prompt entered by a user
     * @return Chatbot response
     */
    public String getResponseToPrompt(String prompt) {
        // Uses a simple rule based system that maps a prompt to its most common
        // keyword's response

        Map<String, Integer> keywordToOccurrencesMap = new HashMap<>();

        for (String keyword : keywordToReponseMap.keySet()) {
            keywordToOccurrencesMap.put(keyword, 0);
        }

        Scanner scan = new Scanner(prompt);
        while (scan.hasNext()) {
            String currWord = ResponseGenerator.sanitizeWord(scan.next());
            if (keywordToOccurrencesMap.containsKey(currWord)) { // currWord is a keyword
                keywordToOccurrencesMap.put(currWord, keywordToOccurrencesMap.get(currWord) + 1);
            }
        }
        scan.close();

        String mostFrequentKeyword = null;
        int mostOccurrencesCount = 0;

        for (String keyword : keywordToOccurrencesMap.keySet()) {
            int currOccurrences = keywordToOccurrencesMap.get(keyword);
            if (currOccurrences > mostOccurrencesCount) {
                mostFrequentKeyword = keyword;
                mostOccurrencesCount = currOccurrences;
            }
        }

        if (mostFrequentKeyword == null) { // no keywords matched
            return randomResponse();
        }

        return keywordToReponseMap.get(mostFrequentKeyword);
    }

    /**
     * Strips punctuation from a token and makes it lowercase.
     * 
     * @param word The word to sanitize
     */
    private static String sanitizeWord(String word) {
        word = word.toLowerCase();
        int firstCharIdx = 0;
        int lastCharIdx = word.length() - 1;

        while (firstCharIdx < word.length() && !(Character.isLetterOrDigit(word.charAt(firstCharIdx)))) {
            ++firstCharIdx;
        }

        while (lastCharIdx > -1 && !(Character.isLetterOrDigit(word.charAt(lastCharIdx)))) {
            --lastCharIdx;
        }

        if (firstCharIdx == word.length() || lastCharIdx == -1) {
            return "";
        }

        return word.substring(firstCharIdx, lastCharIdx + 1);
    }

    /**
     * Returns a random mess of 10-K related buzzwords.
     * 
     * @return A random response
     */
    private String randomResponse() {
        StringBuilder response = new StringBuilder();
        Random rand = new Random();
        int numWords = rand.nextInt(50);

        for (int i = 0; i < numWords; ++i) {
            String word;
            switch (rand.nextInt(5)) {
                case 0:
                    word = "finance";
                    break;
                case 1:
                    word = "report";
                    break;
                case 2:
                    word = "yearly";
                    break;
                case 3:
                    word = "Microsoft";
                    break;
                case 4:
                default:
                    word = "company";
            }

            response.append(word);
            response.append(" ");
        }

        return response.toString();
    }
}
