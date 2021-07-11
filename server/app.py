from pymongo import MongoClient

client = MongoClient("mongodb://127.0.0.1:27017/?compressors=zlib&gssapiServiceName=mongodb ")
db = client['Searchium']




indexerCollection = db['Indexer']


def retrive_range(offset, word):
    prev = offset*10
    query = {"word" : word}
    
    if offset != 0:
        result = indexerCollection.find(query).skip(db.collection.count() - prev).limit(5)
    else :
        result = indexerCollection.find(query).skip(db.collection.count() - prev).limit(5)
    for res in result:
        print(res)


retrive_range(0, "curv")

'''
result = indexerCollection.find()
for res in result:
    print(res)
'''
