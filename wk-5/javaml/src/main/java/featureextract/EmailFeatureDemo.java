package featureextract;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class EmailFeatureDemo {
    public static void main(String[] args) {
        try {
            // Load the ARFF structure (no need to train on all data for demo)
            DataSource source = new DataSource("src/main/resources/spambase.arff");
            Instances data = source.getDataSet();
            if (data.classIndex() == -1) {
                data.setClassIndex(data.numAttributes() - 1);
            }

            // Train classifier on all data
            NaiveBayes classifier = new NaiveBayes();
            classifier.buildClassifier(data);

            // Example: Real email text (not from dataset)
            String realEmail = "Congratulations! You have won a free ticket to Bahamas. Click here to claim your prize now!!!";

            // Extract features
            DenseInstance emailInstance = EmailFeatureExtractor.extractFeatures(realEmail, data);

            // Classify
            double pred = classifier.classifyInstance(emailInstance);
            String predLabel = data.classAttribute().value((int) pred);
            System.out.println("Email: " + realEmail);
            System.out.println("Prediction: " + predLabel + " (" + (predLabel.equals("1") ? "spam" : "not spam") + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 