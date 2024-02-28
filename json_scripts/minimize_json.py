import json

# Paths for the input and output JSON files
INPUT_JSON_PATH = './jsons/subjects_with_bits.json'  # Path to the original JSON file
OUTPUT_MIN_JSON_PATH = '../shared/subjects/data/src/main/assets/subjects_min.json'  # Path for the minimized JSON file

def minimize_json(input_path, output_path):
    # Load the original JSON data
    with open(input_path, 'r') as file:
        data = json.load(file)
    
    # Save the minimized JSON data
    with open(output_path, 'w') as file:
        json.dump(data, file, separators=(',', ':'))

minimize_json(INPUT_JSON_PATH, OUTPUT_MIN_JSON_PATH)
