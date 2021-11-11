# Bird Detection Project :parrot::deciduous_tree:

## Sobre o projeto
O projeto consiste na identificação da espécie de pássaros baseado em seus cantos. :musical_note::bird:<br>
É composto por uma Rede Neural Artificial treinada por nós, com a finalidade de classificar um áudio baseado em algumas features que ele possui, na tentativa de categorizar, e futuramente prever a qual categoria (espécie) este áudio pertence.

#### Como funciona      
Através de um aplicativo Android, o usuário grava um áudio e envia a um servidor python (Flask) que extrairá features e executará uma predição da rede neural com base em áudios que foi treinada. <br>
A partir disto, o aplicativo exibirá ao usuário a espécie identificada, e algumas informações relevantes/curiosidades sobre.
<hr>  
      
## Tecnologias utilizadas 
##### :snake: Python
##### :iphone: Android
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

Abrir a pasta pelo Android Studio ou outro compilador desejado

Executar em um dispotivo android 8.0.1 ou superior
```
<hr> 

## Objetivos
### IA
- [X] Unificação dos dados
- [X] Extração de features
- [X] Estudo dos dados
- [X] Treino e salvamento
- [ ] Filtro dos dados de treino baseado em insights obtidos
- [ ] Acréscimo de novas classes
- [ ] Redução de ruído do áudio
- [ ] Implementar callback no fit
- [ ] Testes na inicialização (otimizadores)

### Webserver
- [X] Execução da Rede Neural
- [X] Decoding de áudio base64
- [X] Uso de arquivo da requisição
- [X] Retorno de URL de imagem da especie identificada
- [ ] Melhoria salvamento arquivo

### App
- [X] Encoding do áudio em base64
- [X] Post do base 64 para o webserver
- [X] Get da imagem do pássaro junto da espécie
- [ ] Get de informações sobre a espécie
<hr>  

## Autores
[Gustavo Lucas da Rosa](https://github.com/guslucas) <br>
[Bruno Lemos Haddad](https://github.com/Bhaddad10)
<hr>  

## Orientador e Professor
Vandeir Aniceto Pinheiro @ UNIFAJ
