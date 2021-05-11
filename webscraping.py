import requests
import json
import time
import random
import math
import pandas
from collections.abc import Iterable
import urllib.request
import os
import numpy as np
import ast
import sys

def main():

    ImageDir = 'Images_saved'

    #Uncomment to request and fetch data from Vivino website
    print(delete_fetch_and_save_data(ImageDir))

    df= get_data_from_file()
    print("Downloading done")

    #WineType
    df["WineType"] = getListWineType(df["type"])
    #WineName
    df["WineName"] = getWineName(df)
    #PinotNoir
    df["Pinot Noir"] = contain(df['WineName'],"Pinot Noir")
    #PinotGris
    df["Pinot Gris"] = contain(df['WineName'],"Pinot Gris")
    #PinotBlanc
    df["Pinot Blanc"] = contain(df['WineName'],"Pinot Blanc")
    #Riesling
    df["Riesling"] = contain(df['WineName'],"Riesling")
    #Auxerrois
    df["Auxerrois"] = contain(df['WineName'],"Auxerrois")
    #Rivaner
    df["Rivaner"] = contain(df['WineName'],"Rivaner")
    #Elbling
    df["Elbling"] = contain(df['WineName'],"Elbling")
    #Gewürztraminer
    df["Gewürztraminer"] = contain(df['WineName'],"Gewürztraminer")
    #Chardonnay
    df["Chardonnay"] = contain(df['WineName'],"Chardonnay")
    #Brut
    df["Brut"] = contain(df['WineName'],"Brut")
    #Crémant de Luxembourg
    df["Crémant de Luxembourg"] = contain(df['WineName'],"Crémant de Luxembourg")
    #Vin Classé
    df["Vin Classé"] = contain(df['WineName'],"Vin Classé")
    #Grand Premier Cru
    df["Grand Premier Cru"] = contain(df['WineName'],"Grand Premier Cru")
    #Premier Cru
    df["Premier Cru"] = contain(df['WineName'],"Premier Cru")
    #GrapeVariety
    df["GrapeVariety"] = getGrapeVariety(df)
    #Appellation
    df["Appellation"] = getAppellation(df)

    writeSqlFile(df)
    print(df.columns)

def getData(page = 1):
    r = requests.get(
        "https://www.vivino.com/api/explore/explore",
        params = {
            "country_code": "LU",
            "country_codes[]":"lu",
            "currency_code":"EUR",
            "grape_filter":"",
            "min_rating":"",
            "order_by":"price",
            "order":"asc",
            "page": page,
            "price_range_max":"500",
            "price_range_min":"0",
            "wine_type_ids[]":["1","2","3","4"]
        },
        headers= {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:66.0) Gecko/20100101 Firefox/66.0"
        }
    )
    r.encoding = r.apparent_encoding
    print(r)
    return r

def getImage(path, ImageDir):
    filename = ImageDir + "\\img_"+str(len(os.listdir("Images_saved")))+".jpg"
    urllib.request.urlretrieve("http:"+path, filename)
    print("Image added: "+ filename)
    return filename

def fetch_data(ImageDir):
    baseData = getData().json()
    nTotal = baseData['explore_vintage']["records_matched"]
    nPage = len(baseData['explore_vintage']["matches"])
    Df = pandas.DataFrame(columns=["id", "name", "image_path", "type", "region", "country", "winery", "tastes", "year"])
    Df.set_index('id')
    for i in range(math.ceil(nTotal/nPage)):
        waitingtime = random.Random().random()*5
        print("Step " +str(i) + "/"+str(math.ceil(nTotal/nPage))+" wait " + str(waitingtime) + "sec")
        time.sleep(waitingtime)
        data = getData(i).json()
        for w in data['explore_vintage']["matches"]:
            # Id
            Id = w["vintage"]["id"]
            # Image
            image_path = getImage(w["vintage"]["image"]["location"], ImageDir)
            # Nom
            WineName = w["vintage"]["name"]
            # Type : {1: "Red",2:"White", 3:"Sparkling",4:"Rosé",5:"Dessert",6:"Fortified"}
            WineType = w["vintage"]["wine"]["type_id"]
            # Region
            Region = w["vintage"]["wine"]["region"]["name"]
            # Country
            Country = w["vintage"]["wine"]["region"]["country"]["name"]
            # Winery
            Winery = w["vintage"]["wine"]["winery"]["name"]
            # Taste
            Taste = w["vintage"]["wine"]["taste"]["flavor"]
            # Year
            Year = w["vintage"]["year"]
            if isinstance(Taste, Iterable):
                Tastes = []
                for t in Taste:
                    n = t["group"]
                    c = t["stats"]["count"]
                    s = t["stats"]["score"]
                    Tastes.append([n, c, s])
            else:
                Tastes=""
            Df.loc[Id] = [Id, WineName, image_path, WineType, Region, Country, Winery, Tastes, Year]
            print("Wine added : " + str(WineName))
        print("done")
    #Df.columns=["id", "name", "image_path", "type", "region", "country", "winery", "tastes", "year"]
    return Df

def delete_fetch_and_save_data(ImageDir):
    for f in os.listdir(ImageDir):
        os.remove(os.path.join(ImageDir, f))
    try:
        os.remove("VivinoData.csv")
    except:
        pass
    data = fetch_data(ImageDir)
    data.to_csv("VivinoData.csv",sep=";", encoding="utf-8")
    return data

def get_data_from_file():
    return pandas.read_csv ('VivinoData.csv',sep=";",header=0,index_col=0)




def red():
    return "Red"
def white():
    return "White"
def sparkling():
    return "Sparkling"
def rose():
    return "Rosé"
def dessert():
    return "Dessert"
def fortified():
    return "Fortified"
def getSwitcherWineType():
    return {
        1: red,
        2: white,
        3: sparkling,
        4: rose,
        5: dessert,
        6: fortified
    }

def getWineType(type):
    # Get the function from switcher dictionary
    func = getSwitcherWineType().get(type, np.nan)
    # Execute the function
    return func()

def getListWineType(list):
    lwt=[]
    for x in list:
        lwt.append(getWineType(x))
    return lwt

def getWineName(df):
    data = df[['name','year','winery']].values
    for x in data:
        x[0] = x[0].replace(' '+x[1],'').replace(x[2]+' ','').replace("'","''")
    return data[:,0]

def getGrapeVariety(df):
    gv=[]
    for x in df.iloc:
        c = ""
        for i in ['Pinot Noir', 'Pinot Gris', 'Pinot Blanc', 'Riesling', 'Auxerrois', 'Rivaner', 'Elbling', 'Gewürztraminer', 'Chardonnay']:
            if x[i] == True:
                c=i
        gv.append(c)
    return gv

def getAppellation(df):
    ap=[]
    for x in df.iloc:
        a = ""
        for i in ['Vin Classé', 'Crémant de Luxembourg', 'Premier Cru', 'Grand Premier Cru']:
            if x[i] == True:
                a = i
        ap.append(a)
    return ap

def contain(data,chain):
    list=[]
    for x in data:
        if x.find(chain) !=-1:
            list.append(True)
        else:
            list.append(False)
    return list

def writeSqlFile(df):
    s = ""
    w = ''
    imgs = ""
    for x in df.country.unique():
        s += "INSERT INTO Country (id, country) VALUES (nextval('country_id_seq'),'" + x + "');\n"

    for x in df.region.unique():
        s += "INSERT INTO Region (id,region,country_pkey) VALUES (nextval('region_id_seq'),'"+x+"',(Select c.id FROM country c where country='"+df[df.region == x]['country'].iloc[0]+"'));\n"

    for x in df.WineType.unique():
        s+="INSERT INTO WineType(id, winetype) VALUES (nextval('winetype_id_seq'),'"+x+"');\n"

    for x in df.winery.unique():
        s+="INSERT INTO Producer (id, producername, region_pkey) VAlUES (nextval('producer_id_seq'),'"+x.replace("'","''")+"',(Select r.id FROM region r where region='"+df[df.winery == x]['region'].iloc[0]+"'));\n"

    for x in ['Vin Classé', 'Grand Premier Cru', 'Premier Cru', 'Crémant de Luxembourg']:
        s+="INSERT INTO Appellation(id, appellation, region_pkey) VALUES (nextval('appellation_id_seq'), '"+x+"', (SELECT r.id FROM region r where region='"+df[df[x] == True]['region'].iloc[0]+"'));\n"

    for x in ['Pinot Noir', 'Pinot Gris', 'Pinot Blanc', 'Riesling', 'Auxerrois', 'Rivaner', 'Elbling', 'Gewürztraminer', 'Chardonnay', 'Brut']:
        s+="INSERT INTO GrapeVariety(id, grape_variety) VALUES (nextval('grapevariety_id_seq'), '"+x+"');\n"

    listOfTastes = []
    for x in df['tastes'].iloc:
        try:
            for i in list(ast.literal_eval(x)):
                listOfTastes.append(i[0])
        except:
            pass
    for x in set(listOfTastes):
        s+="INSERT INTO WineStyle(id, winestyle) VALUES (nextval('winestyle_id_seq'), '"+x+"');\n"

    i = 1
    for x in df.iloc:
        if x.year == 'N.V.':
            year='NULL'
        else:
            year = x.year
        if x.GrapeVariety == '':
            grape_variety = 'NULL'
        else :
            grape_variety = "(SELECT gv.id FROM GrapeVariety gv WHERE grape_variety = '"+x['GrapeVariety']+"')"
        if x.Appellation == '':
            appellation = 'NULL'
        else:
            appellation = "(SELECT a.id FROM Appellation a WHERE appellation = '"+x['Appellation']+"')"
        producer = "(SELECT p.id FROM Producer p WHERE producername = '"+x['winery'].replace("'","''")+"')"
        s+="INSERT INTO Bottle(id, name, vintage, winetype_pkey, grapeVariety_pkey, appellation_pkey, producer_pkey, isInformationNeeded) VALUES (nextval('bottle_id_seq')," \
            " '"+x['WineName']+"',"+str(year)+"," \
            " (SELECT t.id FROM WineType t Where winetype = '"+x['WineType']+"'),"\
            " "+grape_variety+"," \
            " "+appellation+"," \
            " "+producer+"," \
            " false);\n"

        #Insertion des styles/goûts
        try:
            for j in list(ast.literal_eval(x['tastes'])):
                try :
                    w+="INSERT INTO BottleWineStyle(id, bottle_pkey, winestyle_pkey, number_of_votes, score) VALUES (nextval('bottlewinestyle_id_seq'),"+str(i)+", (SELECT ws.id FROM WineStyle ws WHERE winestyle = '"+str(j[0])+"'), "+str(j[1])+","+str(j[2])+");\n"
                except Exception as e:
                    print(e)
        except:
            pass
        print(i)
        print(x['image_path'])
        imgs += "INSERT INTO Image(id, path, bottle_pkey) VALUES (nextval('image_id_seq'),'"+x['image_path']+"',"+str(i)+");\n"
        i+=1

    with open("src\\main\\resources\\import.sql", "w") as text_file:
        text_file.write(s+w+imgs)

if __name__ == "__main__":
    main()