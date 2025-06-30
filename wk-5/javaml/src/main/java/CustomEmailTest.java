import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.DenseInstance;
import weka.core.converters.ConverterUtils.DataSource;

public class CustomEmailTest {
    public static void main(String[] args) {
        try {
            // Load dataset
            DataSource source = new DataSource("src/main/resources/spambase.arff");
            Instances data = source.getDataSet();
            if (data.classIndex() == -1) {
                data.setClassIndex(data.numAttributes() - 1);
            }

            // Train classifier
            NaiveBayes classifier = new NaiveBayes();
            classifier.buildClassifier(data);

            // Example 1: Use a known spam instance (class 1)
            Instance spamEmail = data.instance(0); // First instance (likely spam)
            double predSpam = classifier.classifyInstance(spamEmail);
            String predSpamLabel = data.classAttribute().value((int) predSpam);
            System.out.println("Prediction for known spam email: " + predSpamLabel + " (actual: " + spamEmail.stringValue(data.classIndex()) + ")");

            // Example 2: Use a known not spam instance (class 0)
            // Let's use an instance from line 2201+ (previously found to be not spam)
            Instance notSpamEmail = data.instance(2200); // 2201st instance (class 0)
            double predNotSpam = classifier.classifyInstance(notSpamEmail);
            String predNotSpamLabel = data.classAttribute().value((int) predNotSpam);
            System.out.println("Prediction for known not spam email: " + predNotSpamLabel + " (actual: " + notSpamEmail.stringValue(data.classIndex()) + ")");

            // Example 3: Classify a fully custom email (not from dataset)
            // Let's say this email contains the words 'free', 'money', and 'credit' frequently, and lots of exclamation marks (typical spam features)
            double[] customSpamFeatures = new double[data.numAttributes()];
            // Set all features to 0 by default
            for (int i = 0; i < customSpamFeatures.length; i++) customSpamFeatures[i] = 0.0;
            // Set some features to simulate a spammy email
            customSpamFeatures[15] = 5.0; // word_freq_free
            customSpamFeatures[23] = 3.0; // word_freq_money
            customSpamFeatures[19] = 2.0; // word_freq_credit
            customSpamFeatures[50] = 10.0; // char_freq_!
            customSpamFeatures[54] = 5.0; // capital_run_length_average
            customSpamFeatures[55] = 20.0; // capital_run_length_longest
            customSpamFeatures[56] = 50.0; // capital_run_length_total
            // The class attribute is not set (unknown)
            Instance customSpam = new DenseInstance(1.0, customSpamFeatures);
            customSpam.setDataset(data);
            double predCustomSpam = classifier.classifyInstance(customSpam);
            String predCustomSpamLabel = data.classAttribute().value((int) predCustomSpam);
            System.out.println("Prediction for custom email (spammy): " + predCustomSpamLabel);

            // Now try a custom not-spam email (few special words, low punctuation, low capitals)
            double[] customNotSpamFeatures = new double[data.numAttributes()];
            for (int i = 0; i < customNotSpamFeatures.length; i++) customNotSpamFeatures[i] = 0.0;
            // Set some features to simulate a normal email
            customNotSpamFeatures[12] = 0.5; // word_freq_people
            customNotSpamFeatures[17] = 0.5; // word_freq_email
            customNotSpamFeatures[54] = 1.0; // capital_run_length_average
            customNotSpamFeatures[55] = 2.0; // capital_run_length_longest
            customNotSpamFeatures[56] = 5.0; // capital_run_length_total
            Instance customNotSpam = new DenseInstance(1.0, customNotSpamFeatures);
            customNotSpam.setDataset(data);
            double predCustomNotSpam = classifier.classifyInstance(customNotSpam);
            String predCustomNotSpamLabel = data.classAttribute().value((int) predCustomNotSpam);
            System.out.println("Prediction for custom email (not spammy): " + predCustomNotSpamLabel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 