import json
import requests

def clean_name(name):
    names = name.split(' ')
    new_names = []
    for name in names:
        if len(name) > 2:
            new_names.append('{}{}'.format(name[0].upper(), name[1:].lower()))
        else:
            new_names.append(name.lower())
    return ' '.join(new_names)

def get_markers():
    json_data = json.load(open('./src/markers.json'))
    markers = {}

    for group in json_data['markerGroups']:
        group_name = clean_name(group['label'])
        markers[group_name] = []
        for marker in group['markers']:
            if marker['phone']:
                if marker['address']:
                    message = '{}\n{}'.format(marker['address'], marker['address'])
                else:
                    message = marker['phone']
            elif marker['address']:
                message = marker['address']
            markers[group_name].append({
                'title' : marker['title'],
                'message' : message,
                'lat' : marker['pos'][0],
                'lon' : marker['pos'][1]
            })

    return markers
