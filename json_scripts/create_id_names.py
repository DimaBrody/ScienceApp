import json

# Define the path to the JSON file
PATH_TO_JSON = './jsons/subjects.json'

OUTPUT_PATH = './jsons/id_name.json'

# Function to extract id -> name pairs from subjects JSON
def extract_id_name_pairs(json_path):
    # Load the JSON data
    with open(json_path, 'r') as file:
        data = json.load(file)
    
    # Dictionary to store id -> name pairs
    id_name_pairs = {}
    
    # Iterate through subjects, categories, and subcategories
    for subject in data.get('subjects', []):
        for item in subject.get('items', []):
            id_name_pairs[item['id']] = item['name']
            for sub_item in item.get('items', []):
                id_name_pairs[sub_item['id']] = sub_item['name']
                
    return id_name_pairs

# Extract the pairs
id_name_pairs = extract_id_name_pairs(PATH_TO_JSON)

# Save the id_name_pairs dictionary to a new JSON file
with open(OUTPUT_PATH, 'w') as output_file:
    json.dump(id_name_pairs, output_file, indent=2)

print(f"ID-Name pairs have been saved to {OUTPUT_PATH}")