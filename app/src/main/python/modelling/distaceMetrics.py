import random
from statistics import mean

import pandas as pd
from pandas import DataFrame
from scipy.spatial.distance import cdist
import csv
from os.path import dirname, join

# importare lista da Java
#a = []


def calculateDistaceMetrics(a, alg):

    if alg=="kms":
        filmPath = join(dirname(__file__), "film_cluster_kmeans.csv")
        list=[0]*10
    elif alg=="dbs":
        filmPath = join(dirname(__file__), "film_cluster_dbscan.csv")
        list=[0]*18
    else:
        return "errore"

    # Aprire il file csv e ottenere un oggetto DataFrame
    #with open(filename, newline='') as csvfile:
        #reader = csv.reader(csvfile)
        #for row in reader:
            #print(row)

    table = pd.read_csv(filmPath, sep=',')

    numericCol = ["erotismo", "tensione", "impegno", "ritmo", "humor", "voti_totali"]

    # creare un  dizionario (chiave, valore) con (titolo, cluster)
    # array da 0 a 9 contenente per ogni cella il numero di contenuti della lista appartenenti
    # a quel determinato cluster
    d = dict()

    #print(list)

    tableLista = DataFrame(columns=['voti_totali', 'humor', 'ritmo',
                                      'impegno', 'tensione', 'erotismo',
                                      'titolo_italiano', 'anno'])
    j=0
    # per ogni elemento della lista, trovare il cluster di appartenenza
    for i in range(len(table)):
        for elemento in a:
            #if elemento.getTitolo() == table['titolo_italiano'].loc[i]: #and elemento.getAnnoRilascio() == table['anno'].loc[i]:
                #print(table['Unnamed: 0.1'])
            if table['Unnamed: 0.1'].loc[i] == elemento.getId():
                #key = elemento.getTitolo() #+ '--' + elemento.getAnnoRilascioString()
                key= elemento.getId()
                d[key] = table['Cluster'].loc[i]
                list[table['Cluster'].loc[i]+1] += 1
                tableLista.loc[j]= [table['voti_totali'].loc[i], table['humor'].loc[i], table['ritmo'].loc[i],
                                    table['impegno'].loc[i], table['tensione'].loc[i], table['erotismo'].loc[i],
                                    table['titolo_italiano'].loc[i], table['anno'].loc[i]]
                j+=1


    # prendere max dell'array = cluster di riferimento
    # ricavare l'indice del max dell'array
    max = list[0]
    cluster = 0
    for i, x in enumerate(list):
        if x > max:
            max = x
            cluster = i-1


    # salvo la lista che hanno nel dizionario il cluster == indice del max dell'array
    #newList = []
    #for elemento in a:
    #    key = elemento.getTitolo() + '--' + elemento.getAnnoRilascioString()
    #    if d[key] == cluster:
    #        newList.append([elemento['voti_totali'], elemento['humor'], elemento['ritmo'],
    #                        elemento['impegno'], elemento['tensione'], elemento['erotismo']])
    #        newList.append(elemento)


    tableCluster = DataFrame(columns=['voti_totali', 'humor', 'ritmo',
                                      'impegno', 'tensione', 'erotismo',
                                      'titolo_italiano', 'anno', 'Unnamed: 0.1'])
    y = 0
    for i in range(len(table)):
        if table['Cluster'].loc[i] == cluster:
            tableCluster.loc[y] = [table['voti_totali'].loc[i], table['humor'].loc[i], table['ritmo'].loc[i],
                                   table['impegno'].loc[i], table['tensione'].loc[i], table['erotismo'].loc[i],
                                   table['titolo_italiano'].loc[i], table['anno'].loc[i], table['Unnamed: 0.1'].loc[i]]
            y += 1



    """
        DISTANCE METRIC
    """
    # calcoli la media tra le distanze metriche degli elementi della lista e tutti gli altri cluster
    #print(newList)
    #dist = DistanceMetric.get_metric('euclidean')

    numTableCluster = ['voti_totali', 'humor', 'ritmo', 'impegno', 'tensione', 'erotismo']
    #distanze = dist.pairwise(newList, tableCluster[numericCol])

    distanze= cdist(tableLista[numericCol], tableCluster[numericCol], metric='euclidean')

    # recuperiamo l'elemento con distanza minima
    min = None
    for i, x in enumerate(distanze):
        #print(len(x))
        media = mean(x)
        if min is None or media < min[0]:
            min = (media, i)

    return [tableCluster['Unnamed: 0.1'].loc[min[1]], tableCluster['titolo_italiano'].loc[min[1]]]