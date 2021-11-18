from datetime import time
from flask import Flask, request
import matplotlib.pyplot as plt
import librosa
import pandas as pd
import numpy as np
import csv
from io import StringIO
import pathlib
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler

import base64

from tensorflow import keras
from keras import layers
from keras.models import Sequential
import IPython.display as ipd

#import warnings
#warnings.filterwarnings('ignore')

from simple_image_download import simple_image_download as simp

downloader = simp.simple_image_download

#option = 'all'
option = 'specific'
species = None
recognizer = None
means = None
stds = None

# Carregando o Modelo
if option == 'all':
    species = ['Anabacerthia', 'Anhima', 'Anhinga', 'Anodorhynchus', 'Anthracothorax', 'Antrostomus', 'Anumbius', 'Aphantochroa', 'Aramides', 'Aramus', 'Aratinga', 'Arenaria', 'Asio', 'Asthenes', 'Athene', 'Augastes', 'Automolus', 'Bartramia', 'Baryphthengus', 'Berlepschia', 'Brachygalba', 'Bubo', 'Busarellus', 'Butorides', 'Calidris', 'Campylopterus', 'Capito', 'Caracara', 'Cariama', 'Cercomacra', 'Cercomacroides', 'Certhiaxis', 'Charadrius', 'Chauna', 'Chelidoptera', 'Chlorostilbon', 'Chordeiles', 'Claravis', 'Clibanornis', 'Clytolaema', 'Colibri', 'Coragyps', 'Crax', 'Cymbilaimus', 'Cypseloides', 'Deconychura', 'Dendrexetastes', 'Diopsittaca', 'Egretta', 'Elanoides', 'Eleoscytalopus', 'Epinecrophylla', 'Euchrepomis', 'Eupetomena', 'Eurypyga', 'Florisuga', 'Forpus', 'Gallinula', 'Gampsonyx', 'Geositta', 'Geranoaetus', 'Glaucidium', 'Glaucis', 'Glyphorynchus', 'Grallaria', 'Gymnopithys', 'Harpagus', 'Harpia', 'Heliactin', 'Heliobletus', 'Heliomaster', 'Heliornis', 'Himantopus', 'Hoploxypterus', 'Hylexetastes', 'Hylocharis', 'Hylopezus', 'Hylophylax', 'Hypocnemis', 'Hypoedaleus', 'Ictinia', 'Jacamerops', 'Jacana', 'Leptasthenura', 'Leptodon', 'Leptotila', 'Leucochloris', 'Limnornis', 'Lochmias', 'Lophornis', 'Mackenziaena', 'Malacoptila', 'Megaceryle', 'Mergus', 'Merulaxis', 'Mesembrinibis', 'Monasa', 'Myiopsitta', 'Myrmelastes', 'Myrmoborus', 'Nasica', 'None', 'Nonnula', 'Notharchus', 'Nothura', 'Nycticorax', 'Nyctidromus', 'Nystalus', 'Odontophorus', 'Ortalis', 'Orthopsittaca', 'Pandion', 'Parabuteo', 'Pardirallus', 'Penelope', 'Phaetusa', 'Phimosus', 'Phlegopsis', 'Phleocryptes', 'Pionites', 'Pionopsitta', 'Pipile', 'Piprites', 'Pluvialis', 'Podilymbus', 'Polytmus', 'Porphyrio', 'Porzana', 'Primolius', 'Pseudastur', 'Pseudoscops', 'Psilorhamphus', 'Psittacara', 'Pulsatrix', 'Pygiptila', 'Pyriglena', 'Pyrilia', 'Rhea', 'Rhegmatorhina', 'Rhopias', 'Rhopornis', 'Rhynchotus', 'Rostrhamus', 'Rupornis', 'Sakesphorus', 'Sciaphylax', 'Sclateria', 'Scytalopus', 'Setopagis', 'Streptoprocne', 'Stymphalornis', 'Syrigma', 'Systellura', 'Tachornis', 'Tachybaptus', 'Taoniscus', 'Tapera', 'Taraba', 'Terenura', 'Thalurania', 'Thamnomanes', 'Tigrisoma', 'Touit', 'Triclaria', 'Tyrannulus', 'Tyto', 'Willisornis', 'Zenaida']
    recognizer = keras.models.load_model('./models/all/birdRecognizer')
    means = [ 2.71379984e-02,  3.83624464e-01,  4.88296270e+03,  1.96285611e+03,
        7.00235869e+03,  4.03250489e-01, -4.97106667e+02, -1.80591373e+02,
       -7.88112706e+01,  6.85170245e+01,  2.31213439e+01, -1.70429558e+00,
       -2.91060913e+01,  6.55531633e+00,  5.21162494e+00,  4.68708503e+00,
       -6.54298439e+00,  2.35262506e+00, -1.63262463e+00,  2.97925361e+00,
       -3.87194306e+00,  3.06576259e+00, -1.87667362e+00,  2.56014221e+00,
       -3.68982159e+00]
    varss = [7.25263965e-04, 1.34432076e-02, 6.62790752e+05, 1.48281792e+05,
       1.45141712e+06, 8.29864785e-03, 1.04386090e+04, 2.60904845e+03,
       1.33542578e+03, 3.94068987e+02, 4.05747236e+02, 2.58971245e+02,
       1.78409523e+02, 1.73510787e+02, 1.11578859e+02, 9.54196542e+01,
       7.01775458e+01, 6.58794140e+01, 5.98202600e+01, 4.39572859e+01,
       3.70370075e+01, 3.06202341e+01, 2.65278528e+01, 2.60356094e+01,
       2.64686243e+01]
else:
    species = ['Amazilia versicolor', 'Amazona vinacea', 'None', 'Pitangus sulphuratus']
    recognizer = keras.models.load_model('./models/specific/birdRecognizer')
    means = [ 2.38713841e-02,  3.80407659e-01,  4.56491860e+03,  1.84327761e+03,
        6.55746983e+03,  3.68929987e-01, -5.45108520e+02, -1.49194615e+02,
       -7.54090528e+01,  6.66108012e+01,  2.23828432e+01, -9.50031301e+00,
       -2.29352253e+01,  7.06730107e+00,  2.82252637e+00,  7.60914490e+00,
       -7.57361634e+00,  3.36379815e-01, -7.88233304e-01,  4.43778256e+00,
       -4.75439964e+00,  3.07678719e+00, -1.72193898e+00,  1.35669595e+00,
       -1.62852748e+00]
    varss = [5.40392135e-04, 1.00248518e-02, 6.42946026e+05, 2.79647842e+05,
       1.90497969e+06, 5.08076638e-03, 3.97990297e+04, 3.46820681e+03,
       7.69633731e+02, 1.57837814e+02, 2.46374233e+02, 2.92405394e+02,
       1.42866723e+02, 7.29194860e+01, 7.21481801e+01, 5.89068176e+01,
       5.00161746e+01, 4.31292142e+01, 3.59460138e+01, 2.62393852e+01,
       2.27215493e+01, 2.35946276e+01, 1.41031795e+01, 1.43086760e+01,
       1.46648655e+01]


app = Flask(__name__)

def salvaMp3(base64String, filepath):
    mp3File = base64.b64decode(base64String)

    with open(filepath, 'wb') as pcm:
        pcm.write(mp3File)

def getImageUrl(keyword):
    return downloader().urls(f'{keyword} bird', 1)[0]

def scale_data(array):
    stds=np.array(varss)**0.5
    return (array-means)/stds

@app.route("/detect", methods=["POST"])
def detect():
    
    filename = './audios/request.mp3'
    salvaMp3(request.get_json()["Audio"], filename)

    y, sr = librosa.load(filename, mono=True, duration=30)

    # Features
    rms = librosa.feature.rms(y=y)
    chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr)
    spec_cent = librosa.feature.spectral_centroid(y=y, sr=sr)
    spec_bw = librosa.feature.spectral_bandwidth(y=y, sr=sr)
    rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr)
    zcr = librosa.feature.zero_crossing_rate(y)
    mfcc = librosa.feature.mfcc(y=y, sr=sr)

    to_append = None
    
    to_append = f'{np.mean(rms)} {np.mean(chroma_stft)} {np.mean(spec_cent)} {np.mean(spec_bw)} {np.mean(rolloff)} {np.mean(zcr)}'

    for e in mfcc:
        to_append += f' {np.mean(e)}'

    #print(to_append)

    result = pd.read_csv(StringIO(to_append), sep=' ', header=None)
    #result = result.drop(result.columns[0],axis=1)
    result = result.iloc[:, :-1]
    
    scaled_data = scale_data(result)
    index = np.argmax(recognizer.predict(np.expand_dims(scaled_data, axis=0)))
    especie_identificada = species[index]
    
    print('Response =>', {"species": especie_identificada, "image": getImageUrl(especie_identificada)})

    return {"species": especie_identificada, "image": getImageUrl(especie_identificada)}
#app.run()
app.run(host='0.0.0.0', port='5000')