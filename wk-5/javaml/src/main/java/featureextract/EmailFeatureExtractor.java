package featureextract;

import weka.core.DenseInstance;
import weka.core.Instances;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EmailFeatureExtractor: Extracts features from raw email text to match the spambase.arff format.
 */
public class EmailFeatureExtractor {
    // List of words and characters as per spambase.arff (partial for brevity)
    private static final String[] WORDS = {
        "make", "address", "all", "3d", "our", "over", "remove", 
        "internet", "order", "mail", "receive", "will", "people", 
        "report", "addresses", "free", "business", "email", "you", 
        "credit", "your", "font", "000", "money", "hp", "hpl", "george", 
        "650", "lab", "labs", "telnet", "857", "data", "415", "85", 
        "technology", "1999", "parts", "pm", "direct", "cs", "meeting", 
        "original", "project", "re", "edu", "table"
    };
    private static final char[] CHARS = {';', '(', '[', '!', '$', '#'};

    /**
     * Extracts features from an email and returns a DenseInstance.
     * @param emailText The raw email text.
     * @param dataset The Instances object (for attribute structure).
     * @return DenseInstance with features.
     */
    public static DenseInstance extractFeatures(String emailText, Instances dataset) {
        double[] features = new double[dataset.numAttributes()];
        String lower = emailText.toLowerCase();
        String[] words = lower.split("\\W+");
        int totalWords = words.length;
        int totalChars = emailText.length();

        // Word frequencies
        for (int i = 0; i < WORDS.length; i++) {
            int count = 0;
            for (String w : words) {
                if (w.equals(WORDS[i])) count++;
            }
            features[i] = totalWords > 0 ? (count / (double) totalWords) * 100.0 : 0.0;
        }
        // Character frequencies
        for (int i = 0; i < CHARS.length; i++) {
            int count = 0;
            for (int j = 0; j < emailText.length(); j++) {
                if (emailText.charAt(j) == CHARS[i]) count++;
            }
            features[WORDS.length + i] = totalChars > 0 ? (count / (double) totalChars) * 100.0 : 0.0;
        }
        // Capital run statistics
        int maxRun = 0, totalRun = 0, runCount = 0, currRun = 0;
        for (int i = 0; i < emailText.length(); i++) {
            char c = emailText.charAt(i);
            if (Character.isUpperCase(c)) {
                currRun++;
            } else {
                if (currRun > 0) {
                    totalRun += currRun;
                    if (currRun > maxRun) maxRun = currRun;
                    runCount++;
                    currRun = 0;
                }
            }
        }
        if (currRun > 0) {
            totalRun += currRun;
            if (currRun > maxRun) maxRun = currRun;
            runCount++;
        }
        features[WORDS.length + CHARS.length] = runCount > 0 ? (totalRun / (double) runCount) : 0.0; // average
        features[WORDS.length + CHARS.length + 1] = maxRun; // longest run
        features[WORDS.length + CHARS.length + 2] = totalRun; // total caps
        // Class label left unset
        DenseInstance inst = new DenseInstance(1.0, features);
        inst.setDataset(dataset);
        return inst;
    }
} 