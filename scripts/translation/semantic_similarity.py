from typing import List

from semantic_text_similarity.models import WebBertSimilarity
import pandas as pd

TEST_RESULTS_FILE = '../test_results/translation.xlsx'

web_model = WebBertSimilarity(device='cpu')


def calculate_similarity_for_row(row: pd.Series):
    predictions: List[int] = web_model.predict([(row.expected_translated_text, row.actual_translated_text)])
    prediction = predictions[0] / 5
    print(f'Calculated prediction: {prediction}')
    return prediction


def calculate_semantic_similarities():
    data_frame: pd.DataFrame = pd.read_excel(TEST_RESULTS_FILE)
    # noinspection PyTypeChecker
    data_frame['semantic_similarity'] = data_frame.apply(calculate_similarity_for_row, axis=1)
    if 'Unnamed: 0' in data_frame.columns:
        data_frame = data_frame.drop(columns=['Unnamed: 0'])
    data_frame.to_excel(TEST_RESULTS_FILE, index=False)


if __name__ == '__main__':
    calculate_semantic_similarities()
