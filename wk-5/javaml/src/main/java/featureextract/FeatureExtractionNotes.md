# Feature Extraction Notes for Real Emails

## Overview
To classify real emails using the spam filter model trained on spambase.arff, you must convert raw email text into a feature vector matching the dataset's format.

## Key Feature Types
- **Word Frequencies:** Percentage of specific words (e.g., "free", "money") in the email.
- **Character Frequencies:** Percentage of specific characters (e.g., '!', '$').
- **Capital Run Statistics:** Average, longest, and total runs of consecutive capital letters.

## Steps for Feature Extraction
1. **Tokenize the Email:**
   - Split the email into words and characters.
2. **Count Word Frequencies:**
   - For each word in the feature list, count its occurrences and calculate the percentage of total words.
3. **Count Character Frequencies:**
   - For each character in the feature list, count its occurrences and calculate the percentage of total characters.
4. **Calculate Capital Run Statistics:**
   - Find all sequences of consecutive capital letters.
   - Compute average run length, longest run, and total number of capital letters.
5. **Build the Feature Vector:**
   - Arrange all calculated values in the same order as the ARFF attributes (excluding the class label).

## Example Formulae
- **Word Frequency:**
  ```
  word_freq_X = (count of X / total words) * 100
  ```
- **Character Frequency:**
  ```
  char_freq_Y = (count of Y / total characters) * 100
  ```
- **Capital Run Average:**
  ```
  average = total capital run length / number of runs
  ```

## Practical Tips
- Always preprocess the email (e.g., convert to lowercase) for consistent counting.
- Ensure the feature vector matches the order and number of attributes in the training ARFF file.
- If a word or character does not appear, its frequency is 0.
- The class label should be left unset when classifying new emails.

## Automation
- Use the `EmailFeatureExtractor` Java class to automate this process for any email text. 