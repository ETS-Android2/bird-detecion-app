# Sobre o projeto
O projeto consistem na identificação da espécie de pássaros baseado em seu canto.
É composto por uma Rede Neural Artificial treinada por nós, com a finalidade de classificar um áudio baseado em algumas features que ele possui, na tentativa de categorizar, e futuramente prever a qual categoria (espécie) este áudio representa.

### Como funciona      
Através de um aplicativo Android, o usuário grava um áudio que é enviado a um servidor python (Flask) que extrairá features e executará uma predição da rede neural com base em áudios que foi treinada.
A partir disto, o aplicativo exibirá ao usuário a espécie identificada, e algumas informações relevantes/curiosidades sobre.
<hr>  
      
# Tecnologias utilizadas 
#### :snake: Python
#### :iphone: Android
<hr>  

# Como executar o projeto
## Webserver
```bash
git clone ...

cd webserver

python webserver.py
# server em execução..
```

Endpoint **/detect** request

Request
```bash
curl -X POST -H "Content-type:application/json" --data-binary "@body.txt" http://localhost:5000/detect
```

Response esperado
```bash
{"image":"https://<...some image url...>.jpg","species":"<specie name>"}
```

## Android App
...
<hr> 

# Próximos objetivos

## IA
- [ ] Filtro dos dados de treino baseado em insights obtidos
- [ ] Acréscimo de novas classes
- [ ] Redução de ruído do áudio
- [ ] Implementar callback no fit
- [ ] Testes na inicialização (otimizadores)

## Webserver
- [ ] Melhoria salvamento arquivo

## App
- [ ] 
<hr>  

# Autores
[Gustavo Lucas da Rosa] (https://github.com/guslucas) <br>
[Bruno Lemos Haddad] (https://github.com/Bhaddad10)
<hr>  

# Orientador e Professor
Vandeir Aniceto Pinheiro @ UNIFAJ
