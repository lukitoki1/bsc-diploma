import xml.etree.ElementTree as ET
import os

TEST_ASSETS_DIR = '../../app/src/androidTest/assets/text_recognition'
DATA_SET_DIR = f'{TEST_ASSETS_DIR}/SceneTrialTest'
WORDS_XML_FILE = f'{DATA_SET_DIR}/words.xml'
WORDS_TXT_FILE = f'{TEST_ASSETS_DIR}/words.txt'


def delete_images_without_text():
    images_with_text = []
    root = ET.parse(WORDS_XML_FILE).getroot()
    for image in root:
        image_name = image.find("imageName").text
        rectangles = image.find("taggedRectangles")
        if len(rectangles.findall("taggedRectangle")) > 0:
            images_with_text.append(image_name.split('/')[1])

    dir_names = [f'{DATA_SET_DIR}/{d}' for d in os.listdir(DATA_SET_DIR) if os.path.isdir(os.path.join(DATA_SET_DIR, d))]
    for dir_name in dir_names:
        for file in os.listdir(f'{dir_name}'):
            if file not in images_with_text:
                print(f'Image {dir_name}/{file} does not contain text - removing.')
                os.remove(f'{dir_name}/{file}')


def merge_text_by_image():
    images = []

    root = ET.parse(WORDS_XML_FILE).getroot()
    for image in root:
        words = []
        rectangles = image.find("taggedRectangles")
        image_name = image.find("imageName").text
        for element in rectangles.findall("taggedRectangle"):
            words.append(element.find('tag').text)
        images.append({'name': image_name, 'words': words})

    images.sort(key=lambda im: im['name'])

    with open(WORDS_TXT_FILE, "w+") as output:
        for image in images:
            output.write(" ".join(image['words']) + '\n')


if __name__ == "__main__":
    delete_images_without_text()
    merge_text_by_image()
