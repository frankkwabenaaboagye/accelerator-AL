import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.Instance;

public class SpamFilterTest {
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

            // Classify the first email in the dataset
            Instance newEmail = data.instance(0); // get the first email
            double label = classifier.classifyInstance(newEmail);
            String prediction = data.classAttribute().value((int) label);
            System.out.println("Prediction for first email: " + prediction);
            System.out.println("Actual class: " + newEmail.stringValue(data.classIndex()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 