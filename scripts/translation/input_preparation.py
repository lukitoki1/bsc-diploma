TEST_ASSETS_DIR = '../../app/src/androidTest/assets/translation'
DATA_SET_DIR = f'{TEST_ASSETS_DIR}/German-English_WordAlignment'

WORDS_EN_INPUT_FILE = f'{DATA_SET_DIR}/test.en'
WORDS_DE_INPUT_FILE = f'{DATA_SET_DIR}/test.de'

WORDS_EN_OUTPUT_FILE = f'{TEST_ASSETS_DIR}/words.en'
WORDS_DE_OUTPUT_FILE = f'{TEST_ASSETS_DIR}/words.de'

FORBIDDEN_CHARACTERS = ['.', ',', ':', ';', '!', '?', "'", "'s", "s"]


def remove_punctuation(input_file: str, output_file: str):
    file_words = []
    with open(input_file, 'r') as file:
        for line in file.readlines():
            line = line.strip().split(" ")
            words = [word for word in line if word not in FORBIDDEN_CHARACTERS]
            file_words.append(words)

    with open(output_file, 'w+') as output:
        for line in file_words:
            output.write(" ".join(line) + '\n')


if __name__ == '__main__':
    remove_punctuation(WORDS_EN_INPUT_FILE, WORDS_EN_OUTPUT_FILE)