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
from sklearn.preprocessing import LabelEncoder, StandardScaler

from pygame import mixer  # Load the popular external library
from playsound import playsound

import base64

from tensorflow import keras
from keras import layers
from keras.models import Sequential
import IPython.display as ipd

import warnings
warnings.filterwarnings('ignore')

app = Flask(__name__)

# @app.route("/playSound", methods=["POST"])
# def playSound():
#     r = request.get_json()

#     if r["audio"] == "1":
#         playsound('C:/Users/gabri/Desktop/bird-detecion-app/webserver/audios/AnuBranco/AnuBranco1.mp3')
#     elif r["audio"] == "2":
#         playsound('C:/Users/gabri/Desktop/bird-detecion-app/webserver/audios/AnuBranco/audioTeste.mp3')
#     elif r["audio"] == "3":
#         playsound('C:/Users/gabri/Desktop/bird-detecion-app/webserver/audios/AnuBranco/Juliette - Diferenca Mara.mp3')
#     else:
#         playsound('C:/Users/gabri/Desktop/bird-detecion-app/webserver/audios/AnuBranco/inexistente.mp3')

#     return "teste"

# @app.route("/mixer", methods=["POST"])
# def mixerSound():
#     r = request.get_json()

#     mixer.init()

#     if r["audio"] == "1":
#         mixer.music.load('C:/Users/gabri/Desktop/bird-detecion-app/webserver/audios/AnuBranco/AnuBranco1.mp3')
#     elif r["audio"] == "2":
#         mixer.music.load('C:/Users/gabri/Desktop/bird-detecion-app/webserver/audios/AnuBranco/audioTeste.mp3')
#     elif r["audio"] == "3":
#         mixer.music.load('C:/Users/gabri/Desktop/bird-detecion-app/webserver/audios/AnuBranco/Juliette - Diferenca Mara.mp3')
#     else:
#         mixer.music.load('C:/Users/gabri/Desktop/bird-detecion-app/webserver/audios/AnuBranco/inexistente.mp3')

#     mixer.music.play()

#     return ""

@app.route("/salvaMp3", methods=["POST"])
def salvaMp3():

    base64Json = request.get_json()
    base64String = base64Json["Audio"]

    mp3File = base64.b64decode(base64String)

    with open("C:/Users/gabri/Desktop/bird-detecion-app/webserver/audios/AnuBranco/audioTeste.mp3", 'wb') as pcm:
        pcm.write(mp3File) 

    return ""

@app.route("/detect", methods=["POST"])
def detect():

    #s = request.get_data()
    #bodyJson = request.get_json()
    
    ## Example of reading from base64
    # import base64
    #
    # data_in_base64 = ''
    #
    ## get bytes
    # decoded_data = base64.b64decode(data)
    #
    ## save to file
    # with open("filename.mp3", 'wb') as pcm:
    #   pcm.write(decoded_data) 

    s = 'AnuBranco'

    filename = s+'1.mp3'

    birdname = f'./audios/{s}/{filename}'

    y, sr = librosa.load(birdname, mono=True, duration=30)

    # Features
    chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr)
    spec_cent = librosa.feature.spectral_centroid(y=y, sr=sr)
    spec_bw = librosa.feature.spectral_bandwidth(y=y, sr=sr)
    rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr)
    zcr = librosa.feature.zero_crossing_rate(y)
    mfcc = librosa.feature.mfcc(y=y, sr=sr)

    to_append = f'{filename} {np.mean(chroma_stft)} {np.mean(0)} {np.mean(spec_cent)} {np.mean(spec_bw)} {np.mean(rolloff)} {np.mean(zcr)}'

    for e in mfcc:
        to_append += f' {np.mean(e)}'

    result = pd.read_csv(StringIO(to_append), sep=' ', header=None)
    result = result.drop([0],axis=1)

    species = ['AnuBranco', 'BicoEncarnado', 'Bigodinho', 'CanarioCabeçaPreta', 'Chorão', 'Curió', 'TrincaFerroCinza']

    # Carregando o Modelo
    recognizer = keras.models.load_model('./birdRecognizer')

    # Predict
    species = ['AnuBranco', 'BicoEncarnado', 'Bigodinho', 'CanarioCabecaPreta', 'Chorao', 'Curio', 'TrincaFerroCinza']
    index = np.argmax(recognizer.predict(np.expand_dims(result.iloc[0,:], axis=0)))
    
    especie_identificada = species[index]

    return {"species": especie_identificada}

app.run()
