# Notes: Fundamentals of ML, AI, and DL

## 1. Artificial Intelligence (AI)
- AI is the broad science of mimicking human abilities.
- Encompasses reasoning, learning, perception, and language understanding.
- AI includes both rule-based systems and learning-based systems.

## 2. Machine Learning (ML)
- ML is a subset of AI focused on algorithms that learn from data.
- Instead of hard-coding rules, ML models identify patterns and make predictions.
- **Types of ML:**
  - **Supervised Learning:** Model learns from labeled data (e.g., spam vs. not spam).
  - **Unsupervised Learning:** Model finds patterns in unlabeled data (e.g., clustering emails).
  - **Reinforcement Learning:** Model learns by trial and error, receiving feedback from actions.

### Supervised Learning for Spam Classification
- **Goal:** Train a model to classify emails as spam or not spam.
- **Common Algorithms:**
  - Naive Bayes
  - Decision Trees
  - Support Vector Machines (SVM)
  - Neural Networks
- **Steps:**
  1. Collect and label a dataset of emails.
  2. Extract features (e.g., word frequencies, sender info).
  3. Train the model on labeled data.
  4. Evaluate accuracy on test data.
  5. Deploy the model to classify new emails.

## 3. Deep Learning (DL)
- DL is a subset of ML using neural networks with many layers.
- Excels at learning complex patterns from large datasets.
- Used for advanced tasks like image recognition, NLP, and more.
- In Java, DL4J is a popular library for deep learning.

---

## Lab Expansion: Building a Spam Filter
- **Dataset:** Use a collection of emails labeled as spam or not spam.
- **Feature Engineering:** Convert emails into numerical features (e.g., bag-of-words, TF-IDF).
- **Model Training:** Use Weka or DL4J to train a classifier.
- **Evaluation:** Measure accuracy, precision, recall, and F1-score.
- **Integration:** Use the trained model to filter incoming emails in the application.

---

## Key Takeaways
- ML enables applications to learn from data and improve over time.
- Supervised learning is ideal for spam filtering.
- Java libraries like Weka and DL4J make ML accessible in Java projects. 