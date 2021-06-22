from helper_functions import *
import sys
import os 

from sklearn.linear_model import LogisticRegression
from sklearn import svm
from sklearn.naive_bayes import BernoulliNB  
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split

# Create classifiers
classifiers = {}
classifiers['Logistic Regression'] = LogisticRegression(random_state=0, max_iter=300)
classifiers['Support Vector Machines'] = svm.SVC(probability=True, random_state=42)
classifiers['Naive Bayes'] = BernoulliNB()
classifiers['Decision Tree'] = DecisionTreeClassifier(random_state=42, criterion='entropy')
classifiers['Random Forest'] = RandomForestClassifier(random_state=0, criterion='entropy')


if __name__ == "__main__":
	data_path = sys.argv[1]
	results_destination_folder = sys.argv[2]
	
	# In the future these parameters should be configurable for further experiments.
	java_keywords_path = "experiments/resources/java-keywords.txt"
	loc_threshold = 3 # 3 lines is equal to 1 line of code (the brackets of the method are counted as 2 lines)
	ngram_range = (1, 1)
	min_df = 2 # min_df = 2 : Discard words that appear only once. 

	with open(data_path) as json_file:
		data = json.load(json_file)

	with open(java_keywords_path) as text_file:
		keywords = text_file.read().splitlines()     

	remove_small_methods(data, loc_threshold)

	labels = get_labels(data)

	dataset = vectorize_data(data, min_df, keywords, ngram_range)

	dataset, labels = balance_data(dataset, labels)


	# Create results folder if it doesn't exist
	if not os.path.exists(results_destination_folder):
		os.mkdir(results_destination_folder)

	# Answering RQ1: Classifiers' performance
	results_rq1 = pd.DataFrame(columns = ['Acc', 'BA', 'AUC', 'Precision', 'Recall', 'F1', 'Time'])
	for name, clf in classifiers.items(): 
	    print("Validating " + name)
	    temp = train_and_evaluate_classifier(dataset, labels, clf, name)
	    results_rq1 = results_rq1.append(temp)
		
	results_rq1.index.name = "Classifier"
	results_rq1.to_csv(results_destination_folder + "/classifiers.csv")


	# Answering RQ2: Value of words
	values = data.values()
	corpus = [' '.join(value['words']) for value in values]
	vectorizer = TfidfVectorizer(stop_words=keywords, min_df=min_df, ngram_range=ngram_range)
	vectorizer.fit_transform(corpus)
	vocabulary = vectorizer.get_feature_names()

	ig = []
	total = len(vocabulary)
	for i in range(total):
	    current = information_gain(data, vocabulary[i])
	    ig.append(current)

	results_rq2 = pd.DataFrame({'Information Gain': ig}, index = vocabulary)
	results_rq2.index.name = 'Word'
	results_rq2.to_csv(results_destination_folder + "/vocabulary.csv")


	# Answering RQ3: Size vs Accuracy