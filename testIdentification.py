import requests
from requests_toolbelt.multipart.encoder import MultipartEncoder
import time
import os

r = requests.get("http://localhost:8098/bottles/v1")
data = r.json()
ImageDir = 'Images_received'
for f in os.listdir(ImageDir):
    os.remove(os.path.join(ImageDir, f))

nfinded = 0
print("Number of bottles : " + str(len(data)))
i = 0
problem = []
timing = []
for x in data:
    imgpath = x["images"][0]["path"]
    mp_encoder = MultipartEncoder(
        fields={
            'file': ('spam.txt', open(imgpath, 'rb'), 'text/plain'),
        }
    )
    start = time.time()
    r = requests.post(
        'http://localhost:8098/bottleidentification/v1',
        data=mp_encoder,
        headers={'Content-Type': mp_encoder.content_type}
    )
    timing.append(time.time()-start)
    print(r)
    print(time.time()-start)
    bottlefinded = r.json()
    if imgpath == bottlefinded["images"][0]["path"]:
        nfinded +=1
        print("Find !")
    else :
        print("Not find !")
        problem.append([imgpath, bottlefinded["images"][0]["path"]])
    i+=1
    print(str(nfinded) + "/" + str(i) + " Finded")
with open("problem.txt", "w") as text_file:
    text_file.write(str(problem))
print("done")
print("Average time : " + str(sum(timing)/len(timing))+ "s")