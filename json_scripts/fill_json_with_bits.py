import json

PATH_TO_JSON = "./jsons/subjects_min.json"
OUTPUT_JSON_PATH = './jsons/subjects_with_bits.json'

def sort_items(data):
    data['subjects'].sort(key=lambda x: x['id'])
    for subject in data['subjects']:
        if 'items' in subject:
            subject['items'].sort(key=lambda x: x['id'])
            for category in subject['items']:
                if 'items' in category:
                    category['items'].sort(key=lambda x: x['id'])

def collect_ids(data):
    subject_ids = set()
    category_ids = set()
    sub_category_ids = set()

    for subject in data['subjects']:
        subject_ids.add(subject['id'])
        if 'items' in subject:
            for category in subject['items']:
                category_ids.add(category['id'])
                if 'items' in category:
                    for sub_category in category['items']:
                        sub_category_ids.add(sub_category['id'])

    # Sort and assign bit values
    subject_bit_values = {sid: i + 1 for i, sid in enumerate(sorted(subject_ids))}
    category_bit_values = {cid: i + 1 for i, cid in enumerate(sorted(category_ids))}
    sub_category_bit_values = {scid: i + 1 for i, scid in enumerate(sorted(sub_category_ids))}

    return subject_bit_values, category_bit_values, sub_category_bit_values

def update_json_with_bits(data, subject_bits, category_bits, sub_category_bits):
    for subject in data['subjects']:
        subject['id_bit'] = subject_bits[subject['id']]
        if 'items' in subject:
            for category in subject['items']:
                category['id_bit'] = category_bits[category['id']]
                if 'items' in category:
                    for sub_category in category['items']:
                        sub_category['id_bit'] = sub_category_bits[sub_category['id']]

def process_json(path_to_json, output_path):
    with open(path_to_json, 'r') as file:
        data = json.load(file)
    
    sort_items(data)
    subject_bits, category_bits, sub_category_bits = collect_ids(data)
    update_json_with_bits(data, subject_bits, category_bits, sub_category_bits)
    
    with open(output_path, 'w') as file:
        json.dump(data, file, indent=2)

process_json(PATH_TO_JSON, OUTPUT_JSON_PATH)
