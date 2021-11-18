# Bird Detection Project :parrot::deciduous_tree:

## Sobre o projeto
O projeto consiste na identificação da espécie de pássaros baseado em seus cantos. :musical_note::bird:<br>
É composto por uma Rede Neural Artificial treinada por nós, com a finalidade de classificar um áudio baseado em algumas features que ele possui, na tentativa de categorizar, e futuramente prever a qual categoria (espécie) este áudio pertence.

#### Como funciona      
Através de um aplicativo Android, o usuário grava um áudio e envia a um servidor python (Flask) que extrairá features e executará uma predição da rede neural com base em áudios que foi treinada. <br>
A partir disto, o aplicativo exibirá ao usuário a espécie identificada, e algumas informações relevantes/curiosidades sobre.
<hr>  
      
## Tecnologias utilizadas 
- :snake: Python
- :iphone: Android

### Bibliotecas
- :lotion_bottle: Flask
- :musical_note: Librosa
- :brain: Tensorflow

<hr>  

## Como executar o projeto
### Webserver
```bash
git clone # ... repo

cd webserver

python webserver.py
# server em execução..
```

Endpoint **/detect** request

Request
```bash
curl -X POST -H "Content-type:application/json" --data-binary "@body.txt" http://localhost:5000/detect
# obs. '@body.txt' faz referência a um arquivo na pasta ./webserver com o corpo da requisição
```

Response esperado
```json
{"image":"https://<...some image url...>.jpg","species":"<specie name>"}
```

### Android App
```bash
cd app 

cd projectApp

Caso deseje executar o aplicativo no android studio, é necessario que o mesmo esteja na versão artic fox
Disponivel para download em : https://android-developers.googleblog.com/2021/05/android-studio-arctic-fox-beta.html

cd apk
Caso deseje instalar o apk, basta instalar o apk em um dispotivo android com a versão 8.0.1 ou superior.
```
<hr> 

## Objetivos
### IA
- [X] Unificação dos dados
- [X] Extração de features
- [X] Estudo dos dados
- [X] Treino e salvamento
- [X] Filtro dos dados de treino baseado em insights obtidos 
- [X] Acréscimo de novas classes
- [X] Redução de ruído do áudio
- [X] Implementar callback no fit
- [ ] Testes na inicialização (otimizadores)

### Webserver
- [X] Execução da Rede Neural
- [X] Decoding de áudio base64
- [X] Input de arquivo da requisição + Scaler
- [X] Retorno de URL de imagem da especie identificada

### App
- [X] Encoding do áudio em base64
- [X] Post do base64 para o webserver
- [X] Get da imagem do pássaro junto da espécie
- [ ] Get de informações sobre a espécie
<hr>  

# Desenvolvimento :computer:
## Dados
### Xeno-canto
Usamos um script python para a aquisição das gravações dos pássaros, através da [API disponível no site](https://www.xeno-canto.org/explore/api).

Filtramos apenas pássaros do Brasil

### Análise
#### Licença
Licenças Creative Commons
```python
df['lic'].value_counts()
#----
//creativecommons.org/licenses/by-nc-sa/4.0/    32919
//creativecommons.org/licenses/by-nc-nd/2.5/    11087
//creativecommons.org/licenses/by-nc-sa/3.0/     9256
//creativecommons.org/licenses/by-nc-nd/4.0/     4927
//creativecommons.org/licenses/by-nc-nd/3.0/      158
//creativecommons.org/licenses/by-sa/4.0/          25
//creativecommons.org/licenses/by-sa/3.0/           2
//creativecommons.org/licenses/by-nc/4.0/           1
```

#### Qualidade
Observamos que haviam muitos áudios de grande duração, e que portanto não são tão relevantes tendo em vista a abordagem utilizada para tratativa das gravações. (próxima sessão).
```python
# Distribuição do tamanho dos áudios
allAudios.groupby('length-cat').size()
#----
5s: 0-5             4379
10s: 6-10           6462
15s: 11-15          6277
20: 16-20           6129
30s: 21-30         10617
01m: 31-60         16319
02m: 61-120         6406
05m: 121-300        1671
Very long: >300      115
```

Também haviam vários categorizados com "outros pássaros", segundo a documentação da API:
```python
# Distribuição de quantidade de pássaros nos áudios
allAudios.groupby(['alson']).size()
#----
alson
0     41989
1      7253
2      3902
3      2327
4      1377
5       689
6       383
7       203
8        94
9        66
10       35
11       24
12       13
13        3
14        4
15        3
17        3
18        1
19        3
24        1
26        1
27        1
```

Por estes motivos, decidimos filtrar o dataset baseado nessas informações.

O filtro aplicado foi:
1. Áudios com **até 10 segundos**;
2. Áudios com **nenhum** outro pássaro identificado em background;

Em um script python, realizamos o download dos áudios, dos registros filtrados.

### IA
Ainda em questão de filtro, observamos que algumas espécies continham mais áudios que outras, o que poderia gerar um certo bias.

Distribuição de quantidade de áudio por espécie:

![Distribuição de quantidade de áudio por espécie](https://user-images.githubusercontent.com/48332376/142470705-34517aee-8ed5-4031-acbc-3879636bab16.png)

Portanto, mais um filtro que decidimos fazer foi pela quantidade de áudios disponíveis:
```python
# Filtro de espécies com quantidade de áudios >= 5 e <=20
data.groupby('label').filter(lambda group: len(group) >= 5 and len(group) <= 20)
```

<hr>

## Treino
A maneira que escolhemos para categorizar e representar as gravações, e treinar a IA foi o uso de extração de features de um áudio.

Usamos a biblioteca *librosa*, que possui funções utilitárias que ajudam nessa tarefa:

```python
# ...

y, sr = librosa.load(birdname, mono=True)

# ...

rms = librosa.feature.rms(y=y)
chroma_stft = librosa.feature.chroma_stft(y=y, sr=sr)
spec_cent = librosa.feature.spectral_centroid(y=y, sr=sr)
spec_bw = librosa.feature.spectral_bandwidth(y=y, sr=sr)
rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr)
zcr = librosa.feature.zero_crossing_rate(y)
mfcc = librosa.feature.mfcc(y=y, sr=sr)

# ...
```

Por conta de nossos áudios serem relativamente curtos, decidimos tirar a média das features no decorrer do áudio, chegando a um único valor representando aquela feature:
```python
# ...

to_append = f'{filename} {np.mean(chroma_stft)} {np.mean(rms)} {np.mean(spec_cent)} {np.mean(spec_bw)} {np.mean(rolloff)} {np.mean(zcr)}'    
for e in mfcc:
    to_append += f' {np.mean(e)}'
    
# ...
```

<hr>

## Modelo Utilizado
Layers:
```python
model = Sequential()
model.add(layers.Dense(4096, activation='relu', input_shape=(X_train.shape[1],)))
model.add(layers.Dropout(rate=0.5))
model.add(layers.Dense(2048, activation='relu'))
model.add(layers.Dropout(rate=0.5))
model.add(layers.Dense(1024, activation='relu'))
model.add(layers.Dropout(rate=0.5))
model.add(layers.Dense(512, activation='relu'))
model.add(layers.Dense(256, activation='relu'))
model.add(layers.Dense(len(species), activation='softmax'))
```
Durante desenvolvimento e testes, percebemos um overfit, com alta acurácia no treino, mas baixa acurácia nos testes.

Uma possível solução que encontramos foi a adição de Dropout nas layer, de forma a reduzir este comportamento.

Percebemos uma sutil melhora na acurácia, mas mesmo assim, ficou próxima a 8%.

![Performance graph](https://user-images.githubusercontent.com/48332376/142472224-8440ea84-9aed-4857-b57f-6c10775f639a.png)

![Final accuracy](https://user-images.githubusercontent.com/48332376/142472238-17ccd895-60e3-4732-8172-06bf36252d53.png)


## Autores
[Gustavo Lucas da Rosa](https://github.com/guslucas) <br>
[Bruno Lemos Haddad](https://github.com/Bhaddad10)
<hr>  

## Orientador e Professor
Vandeir Aniceto Pinheiro @ UNIFAJ
