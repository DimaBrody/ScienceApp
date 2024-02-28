PATH_TO_JSON = "./jsons/subjects.json"
OUTPUT_JSON_PATH = './jsons/subjects_min.json'

import json

def update_category(json_data):
    for item in json_data:
        if item.get("type") == "SUBJECT":
            update_items(item["items"])

def update_items(items):
    for item in items:
        if item.get("type") == "SUB_CATEGORY":
            item["type"] = "CATEGORY"

            # Recursively update items if it has nested items
            if "items" in item:
                update_items(item["items"])

with open(PATH_TO_JSON, "r") as json_file:
    data = json.load(json_file)

# Create a copy of the data to update
updated_data = data.copy()

# Update the copied JSON data
update_category(updated_data["subjects"])

with open(OUTPUT_JSON_PATH, "w") as json_file:
    json.dump(updated_data, json_file, separators=(',', ':'))