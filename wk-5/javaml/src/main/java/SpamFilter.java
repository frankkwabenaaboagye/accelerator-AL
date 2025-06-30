import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import java.util.Random;

/**
 * SpamFilter: A simple spam classifier using Weka's NaiveBayes algorithm.
 *
 * Steps:
 * 1. Load a dataset (ARFF or CSV format, labeled as spam/not spam).
 * 2. Train a NaiveBayes classifier.
 * 3. Evaluate the model.
 * 4. Classify new emails.
 */
public class SpamFilter {
    private NaiveBayes classifier;
    private Instances trainingData;

    /**
     * Loads the dataset from a file.
     */
    public void loadDataset(String filePath) throws Exception {
        DataSource source = new DataSource(filePath);
        trainingData = source.getDataSet();
        if (trainingData.classIndex() == -1) {
            trainingData.setClassIndex(trainingData.numAttributes() - 1);
        }
    }

    /**
     * Trains the NaiveBayes classifier.
     */
    public void train() throws Exception {
        classifier = new NaiveBayes();
        classifier.buildClassifier(trainingData);
    }

    /**
     * Evaluates the classifier using cross-validation.
     */
    public void evaluate() throws Exception {
        Evaluation eval = new Evaluation(trainingData);
        eval.crossValidateModel(classifier, trainingData, 10, new Random(1));
        System.out.println(eval.toSummaryString("\nResults\n======\n", false));
    }

    /**
     * Classifies a new email instance.
     */
    public String classify(Instance email) throws Exception {
        double label = classifier.classifyInstance(email);
        return trainingData.classAttribute().value((int) label);
    }

    public static void main(String[] args) {
        try {
            SpamFilter filter = new SpamFilter();
            // Example: Load ARFF dataset (replace with your dataset path)
            filter.loadDataset("src/main/resources/spambase.arff");
            filter.train();
            filter.evaluate();
            // To classify a new email, create an Instance and call filter.classify(instance)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 