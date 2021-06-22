import sys
import json
import math
import random
import csv
import pandas as pd

from sklearn.feature_extraction.text import TfidfVectorizer
from imblearn.over_sampling import SMOTE
from sklearn.model_selection import cross_validate

def load_data(data_path):
    with open(data_path) as json_file:
        data = json.load(json_file)
    return data

def vectorize_data(data, min_df, keywords, ngram_range):
    values = data.values()
    corpus = [' '.join(value['words']) for value in values]
    vectorizer = TfidfVectorizer(stop_words=keywords, min_df=min_df, ngram_range=ngram_range)
    dataset = vectorizer.fit_transform(corpus)
    return dataset

def balance_data(dataset, labels):
    ros = SMOTE(random_state=42)
    dataset, labels = ros.fit_resample(dataset, labels)
    return dataset, labels

def remove_small_methods (data, loc_threshold):
    keys = []
    
    for key, value in data.items():
        if value['LOC'] <= loc_threshold:
            keys.append(key)
    
    for key in keys:
        del data[key]
        
def get_labels (data):
    values = data.values()
    return [1 if value['logged'] else 0 for value in values]
    
def train_and_evaluate_classifier(X, y, clf, classifier_name):
    scoring = ['accuracy', 'balanced_accuracy', 'roc_auc', 'precision', 'recall', 'f1']
    scores = cross_validate(clf, X, y, scoring=scoring, cv=10, n_jobs=-1, return_estimator=True)
    results = {}
    results['Acc'] = [round(scores['test_accuracy'].mean(), 3)]
    results['BA'] = [round(scores['test_balanced_accuracy'].mean(), 3)]
#     auc = roc_auc_score(test_y, clf.decision_function(test_x))
    results['AUC'] = [round(scores['test_roc_auc'].mean(),3 )]
    results['Precision'] = [round(scores['test_precision'].mean(),3 )]
    results['Recall'] = [round(scores['test_recall'].mean(),3 )]
    results['F1'] = [round(scores['test_f1'].mean(),3 )]
    results['Time'] = [round(scores['fit_time'].mean(), 0)]
    return pd.DataFrame(results, index=[classifier_name])

def entropy(data):
    number_of_logged_methods = len([x for x in data.values() if x['logged']])
    
    p_logged = prob(number_of_logged_methods, len(data))
    p_not_logged = prob(len(data) - number_of_logged_methods, len(data))
    return - p_log_p(p_logged) - p_log_p(p_not_logged)

def prob(c, total):
    if total > 0:
        return c / total
    else:
        return 0

def p_log_p(p):
    if p > 0: 
        return p * math.log(p)
    else :
        return 0

def information_gain (data, word):
    left = {k: v for k, v in data.items() if word in v['words']}
    right =  {k: v for k, v in data.items() if not word in v['words']}
    entropy_parent = entropy(data)
    entropy_left = entropy(left)
    entropy_right = entropy(right)

    information_gain = entropy_parent - (entropy_left + entropy_right) / 2
    return information_gain

def frequencies (data, word):
    occurences = len([v for k,v in data.items() if word in v['words']])
    frequency = len([v for k,v in data.items() if word in v['words']]) / len(data)
    return occurences, frequency