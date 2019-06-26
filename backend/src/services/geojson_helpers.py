from geojson import Feature, Point, FeatureCollection


def posts_to_geojson(posts):
    feature_collection_list = []
    for post in posts:
        print(post.post_time)
        if post.point_x and post.point_y:
            new_feature = Feature(
                geometry=Point((post.point_y, post.point_x)),
                properties={
                    'description' : '<strong>{}</strong><p>{}</p>'.format(post.title, post.message),
                }
            )
        else:
            new_feature = Feature(
                geometry=Point((0,0)),
                properties={
                    'description' : '<strong>{}</strong><p>{}</p>'.format(post.title, post.message),
                }
            )
        feature_collection_list.append(new_feature)

    return feature_collection_list