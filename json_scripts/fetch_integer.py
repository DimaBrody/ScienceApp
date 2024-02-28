import json

PATH_TO_JSON = './jsons/subjects_with_bits.json'

def load_data(path_to_json):
    with open(path_to_json, 'r') as file:
        return json.load(file)

def find_item(data, item_id):
    for subject in data['subjects']:
        if subject['id'] == item_id:
            return subject, 'SUBJECT'
        if 'items' in subject:
            for category in subject['items']:
                if category['id'] == item_id:
                    return category, 'CATEGORY', subject
                if 'items' in category:
                    for sub_category in category['items']:
                        if sub_category['id'] == item_id:
                            return sub_category, 'SUB_CATEGORY', category, subject
    return None, None

def construct_link(data, item_id):
    item, item_type, *parents = find_item(data, item_id)
    if item is None:
        print(f"Item with ID {item_id} not found.")
        return None

    # Initialize all parts of the link with zero
    subject_bits, category_bits, sub_category_bits = 0, 0, 0
    
    if item_type == 'SUBJECT':
        subject_bits = item['id_bit']
    elif item_type == 'CATEGORY':
        subject_bits, category_bits = parents[0]['id_bit'], item['id_bit']
    elif item_type == 'SUB_CATEGORY':
        subject_bits, category_bits = parents[1]['id_bit'], parents[0]['id_bit']
        sub_category_bits = item['id_bit']

    # Construct the whole link integer
    whole_link = (subject_bits << 28) | (category_bits << 8) | sub_category_bits
    return whole_link

def main(item_id):
    data = load_data(PATH_TO_JSON)
    whole_link = construct_link(data, item_id)
    if whole_link is not None:
        print(f"Whole link for item ID '{item_id}': {whole_link} (binary: {bin(whole_link)})")

item_id = "cs.CR"  # Example item ID
main(item_id)
