## Zadanie 1 - Docker

Należy stworzyć obraz, który zawiera Javę w wersji 8, Scalę w wersji
2.12, najnowszą wersję sbt oraz npm. Dockerfile z obrazem należy
umieścić w repozytorium git, a link do docker huba z obrazem należy
dodać do README.md w repozytorium git. Wersje zainstalowanych paczek
można sprawdzić uruchamiając kontener z odpowiednią komendą, np.:

$ docker run -it kprzystalski/ebiznes:latest java --version

Proszę również udostępnić porty (EXPOSE) dla aplikacji w React'cie
oraz aplikacji w Play. Dodatkowo proszę przeznaczyć jeden folder do
wymiany danych pomiędzy hostem a kontenerem (VOLUME).

### Podsumowując
- Java 8
- Scala 2.12
- sbt
- npm

### Link do dockerhuba:

https://hub.docker.com/repository/docker/dominikajadach/zadanie1

## Zadanie 2 - kontrolery

Należy stworzyć dziesięć kontrolerów oraz odpowiadającą metodom
kontrolerów tablicę routingu. Należy przyjąć jako przykład sklep oraz
odpowiadającą sklepu 10 kontrolerów, w każdym kontrolerze powinny być
metody CRUD (Create Read Update Delete). Metody powinny być wydmuszką
(mock), czyli bez istotnej implementacji.
