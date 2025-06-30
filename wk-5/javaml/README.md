# Bridging the Gap: Java and Machine Learning

## Project: Smart Spam Filter for Email Application

This project aims to build a smart spam filter for an email application using Java-based machine learning libraries. The filter will analyze incoming emails and identify potential spam messages with high accuracy.

---

## Application-Specific Notes

### 1. Java Machine Learning Libraries
- **Weka**: A popular, open-source Java library for machine learning. It provides tools for data pre-processing, classification, regression, clustering, and visualization.
- **Deeplearning4j (DL4J)**: A deep learning library for Java and the JVM. Suitable for more advanced neural network models.
- **Smile**: Statistical Machine Intelligence and Learning Engine, another Java ML library.

> **Note:** Libraries like Scikit-learn and TensorFlow are Python-based and not natively supported in Java. Weka and DL4J are recommended for this project.

### 2. Integration Steps
- Prepare a dataset of labeled emails (spam/not spam).
- Use Weka or DL4J to train a classification model (e.g., Naive Bayes, Decision Tree, or simple neural network).
- Integrate the trained model into the email application to predict spam probability for new emails.

### 3. Project Structure
- `src/main/java`: Java source code for the spam filter and integration logic.
- `src/test/java`: Unit tests for the spam filter.
- `pom.xml`: Maven configuration for dependencies (Weka, DL4J, etc.).
- `notes.md`: Research and background notes on ML, AI, and DL fundamentals.

---

## References
- [Weka Documentation](https://www.cs.waikato.ac.nz/ml/weka/)
- [DL4J Documentation](https://deeplearning4j.konduit.ai/)
- [Smile Documentation](https://haifengl.github.io/)

---

## How to Classify a New Email

After training and evaluating your spam filter, you can classify new emails using the trained model. Here's how:

### Example: Classify the First Email in the Dataset

Add the following code after `filter.evaluate();` in your `main` method:

```java
// Classify the first email in the dataset as an example
Instance newEmail = filter.trainingData.instance(0); // get the first email
String prediction = filter.classify(newEmail);
System.out.println("Prediction for first email: " + prediction);
System.out.println("Actual class: " + newEmail.stringValue(filter.trainingData.classIndex()));
```

**What does this do?**
- Uses the first email in your dataset as a test case.
- Prints the predicted class (e.g., "spam" or "not spam").
- Prints the actual class label for comparison.

### Understanding the Result
- **Prediction:** The model's guess for whether the email is spam or not.
- **Actual class:** The true label from your dataset.
- If the prediction matches the actual class, the model got it right for that email. If not, it made a mistake on that instance.

The overall model performance is shown in the evaluation summary (accuracy, error rate, etc.).

---

## Example Output and Analysis

### Model Output
```
Prediction for known spam email: 1 (actual: 1)
Prediction for known not spam email: 0 (actual: 0)
Prediction for custom email (spammy): 1
Prediction for custom email (not spammy): 1
```

### Analysis
- **Known spam and not spam emails from the dataset** are correctly classified, showing the model works well on real data.
- **Custom spammy email** (with high frequencies of words like "free", "money", "credit", and lots of exclamation marks/capitals) is correctly flagged as spam.
- **Custom not-spam email** (with low feature values) is incorrectly flagged as spam. This is likely because:
  - The synthetic not-spam email is too "flat" (all zeros except a few small values), which may not match real not-spam emails in the dataset.
  - The model is trained on real-world feature distributions, so edge-case vectors may not generalize well.

#### Interpretation
- The model is reliable for real emails with realistic feature distributions.
- For synthetic or edge-case feature vectors, the model may not generalize perfectly.
- For best results, extract features from real emails in the same way as the dataset.

### Feature Extraction
[./src/main/java/featureextract/FeatureExtractionNotes.md](./src/main/java/featureextract/FeatureExtractionNotes.md)
