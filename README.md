# Dobble

## Multiplayer game
<br />
<br />

### Opis programu
<br />

- Klienci - łączą się z serwerem , wysylają pakiet typu «HANDSHAKE», podłączają
    się do wolnego pokoju, czekają aż póki on się nie zapełni, grają razem.
- Serwer — zarządza grami klientów, w danej chwili może być prowadzonych kilka
    gier.

### ____________
<br />

### Mechanizmy:
<br />

- Zarówno klient jak i serwer, określają protokół przesyłu danych
- Serwer przez cały czas swojego działania zapisuje do logów informacje o aktualnym
    zdarzeniu (połączył się klient - z jakim IP, portem, ...)
- Serwer i klient powinni obsługują protokół IPv4 oraz IPv
- Zaimplementowana logika, wykorzystuje więcej niż 3 rodzaje przesyłanych wiadomości
- Synchronizacja wątków
- Serwer obsługuje 2+ graczy jednocześnie
<br />
<br />

### Opis protokołu
<br />

#### 1.1 Ogólny format pakietów
<br />

Protokól « _DBLgame_ » użuwany w komunikacji między serwerem, a klientem pochodzi z
rodziny protokolów « _DBLprot_ » przyznaczonej dla tej gry. DBLprot korzysta z protokołu
TCP/IP.
<br />
Wszystkie teksty są kodowane przy użyciu zestawu znaków UTF-8.Przy opisie struktur,
założono, że char ma rozmiar 1 bajtu ,a int 4 bajtów.
<br />

_Każdy pakiet zawiera :_

1. Trzy stałe bajty, które oznaczają początek wiadomości <0x64,0x6f,0x62>.
2. Cztery bajty, reprezentujące rozmiar pakietu(czyli int).Maksymalny rozmiar, to
    166kb.
3. « _Header_ », zajmuje 1bajt i odpowiada za ogólne przyznaczenie pakietu.
4. « _Field_ », zajmuje 1bajt i odpowiada za szczególowe przyznaczenie pakietu.
5. Dane.( _W zależności od pola «Field» mogą być reprzezentowane w róznych_
    _formatach._ )
6. Trzy stałe bajty które oznaczają koniec wiadomości <0x62,0x6c,0x65>.

### ____________
* Jeśli polączyć pierwsze 3 bajty i ostatnie 3, to dostaniemy slowo dobble(nazwa gry).
<br />

#### 1.2 Możliwe wartości dla pól Header i Field
<br />

**DBLprot**

```
OPTION : PAKIET_DATA(0x10), REQUEST(0x30), RESPONSE(0x31).

FIELDS : ERROR(0x05)

OPTION : HANDSHAKE(0x20)

FIELDS: CONNECT(0x21),ERROR(0x05)
```
<br />

**DBLgame**

```

OPTION : ROOM(0x40)

FIELDS : JOIN(0x41), LEAVE(0x42),NEW_PLAYER(0x43),PLAYER_LEAVE(0x44),ERROR(0x05)

OPTION : GAME(0x50)

FIELD : START(0x52),CLIENT_CARD(0x53),SERVER_CARD(0x54),ANSWER(0x55),ANSWERED(0x56),END(0x57),ERROR(0x05)
```
<br />
<br />

####  2.1 Option HANDHSAKE
<br />

Pierwszy pakiet który serwer może odebrać od klienta jest pakiet typu « _Handshake_ ».
Przykładowy pakiet wyglądą następująco:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
 0 1 2 | 0x64,0x6f,0x62
 3 4 5 6|length
 7| HANDSHAKE
 8| CONNECT
 9|protocol_id
 10|prtocol_version
 11|client_version
 12 13 14| 0x62,0x6c,0x65


<br />
Gdzie :

- _protocol_id_ — wybór protokołu.W tym momencie « _DBLprot»_ posiada tylko jeden
    protokól, który jest wykorzystany w grze(czyli « _DBLgame_ »), przypisana mu wartość
    «1»;
- _protocol_version_ — wersja protokołu używanego przez klienta. « _DBLgame_ » jest w
    wersji «1.0». W postaci bajtowej ma wartość «0x10».
- _client_version_ — wersja zainstalowanego klienta.

<br />
<br />

Jeśli serwer obsluguje taki protokól , w takiej samej wersji i klient używa najnowszej wersji
programu, to odpowie:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | HANDSHAKE
8 | CONNECT
9 | SUCCESS
10,11,12 | 0x62,0x6c,0x65

<br />

W tym pakiecie, « _Dane_ » , to kod odpowiedżi, który zajmuje 1 bajt.
<br />
<br />

#### 2.2 Option ROOM
<br />

Żeby dolączyć się do pokoju,w którym będzię prowadzana gra, klient muśi wysłac pakiet
typu «ROOM» :
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | ROOM
8 | JOIN
9 | nickname_length
10-26 | nickname
27 28 29 | 0x62,0x6c,0x65
<br />

W typ pakiecie klient przesyła swój _nickname_ , który może posiadać znaki z tablicy ASCII
w przedziałie [0x20,0x7e) oraz dlugość tego pola w przedzilie [0x03,0x10].
<br />
<br />

Jeśli nickname jest poprawny i jest pokój do którego można dodać klienta serwer odpowie
pakietem :

<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | ROOM
8 | JOIN
9 | room_nbr
10 | game_id
11 | max_players
12-? | players_info
?,?,? | 0x62,0x6c,0x65
<br />

Gdzie :

- _room_nbr_ — numer pokoju do którego będzie dodany klient.
- _game_id_ — id gry.Ta wartość, będzie zmieniała się, w załeżności od ilośći
    prowadzonych gier w pokoju .(Tzn. Pierwsza gra ma id=1,druga id=2 ... i td.).
- _max_players_ — ile osób maksymalnie może być podłączono do tego pokoju.
- _players_info_ — informacja o podłącząnych do tego pokoju klientów. Informacja jest
    typu String, w formacie «1klientID\r1klientNickname\n2klientID\r2klientNickname\
    n».Klient odejmuje od długości calego pakietu 3 ostatnie bajty ,żeby wiedzić ile
    bajtów zajmuję to pole.
<br />
<br />

Kiedy klient zośtał dodany do pokoju,wszyscy uczestniki w tym pokoju otrzymają
wiadomość od serwera.Przykłądowo:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | ROOM
8 | NEW_PLAYER
9-? | player_info
?,?,? | 0x62,0x6c,0x65

_player_info_ — informacja o nowym uczestniku.Informacja jest typu String, w formacie
«klientID\rklientNickname\n».
<br />
<br />

Kiedy klient opuści pokój, wszyscy uczestniki otrzymają wiadomość:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | ROOM
8 | PLAYER_LEAVE
9 | playerID
10,11,12 | 0x62,0x6c,0x65
<br />
<br />

#### 2.3 Option REQUEST , RESPONSE
<br />

Gra się zaczyna, kiedy w pokoju jest maksymalna liczba klientów i wszyscy są gotowi.Zeby
sprawdzić ich gotowność, serwer wysyła żądanie :
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | REQUEST
8 | READY
9,10,11 | 0x62,0x6c,0x65
<br />
<br />

Od uzytkownika załeży ,czy wyślę klient odpowiedż, czy nie. Jeśli tak to pakiet będzie
wygłądał :
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | RESPONSE
8 | READY
10,11,12 | 0x62,0x6c,0x65
<br />

W protokolach DBLprot tyłko te dwa pakiety nie mają pola dła danych.
<br />
<br />

#### 2.4.1 Option GAME
<br />

Gra się zaczyna, gdy serwer wyśli do wszystkich użytkowników pokoju pakiet :
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | GAME
8 | START
9 | amount_of_card
10,11,12 | 0x62,0x6c,0x65
<br />

Bajt nr. 9 odpowiada za ilość kart, który serwer wydał każdemu klientu. Gra trwa do póki w
pokoju nie zostanie jeden klient z kartami.
<br />
<br />

#### 2.4.2 Proces gry
<br />

Serwer wysyła każdemu uczestniku gry, po jednej karcie:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | GAME
8 | CLIENT_CARD
9-? | Card
?,?,? | 0x62,0x6c,0x65
<br />

W tym pakiecie, karta reprezentowana jako obiekt classy « _Card_ ». Każda karta w sobie
zawiera numer i listę obrazków, które są na niej. Każdy obrazek, to klasa, która zawiera
numer i śćieżke do obrazku na urządzeniu klienta.
<br />
<br />

Następnie serwer wysyłą do wszystkich uczestników gry, ten sam obrazek(plik) który jest
kartą serwera:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | Protocol.GAME
8 | Protocol.SERVER_CARD
9-? | Card
?,?,? | 0x62,0x6c,0x65
<br />
<br />

Każdy z uczestników może przesłać swoją odpowiedż reprezentowana, jako numer obrazku:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | GAME
8 | ANSWER
9 | imageID
10,11,12 | 0x62,0x6c,0x65
<br />
<br />

Jeśli obrazek na który wskazał klient jest wsród obrazków karty serwera to wtedy ta karta
będzie zaakceptowana i serwer wyśli do wszystkich , id uczestnika który był pierwszy:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | GAME
8 | ANSWERED
9 | playerID
10,11,12 | 0x62,0x6c,0x65
<br />
<br />

Gra się kończy wysłąniem z serwera do wszystkich uczestników pakietu :
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | GAME
8 | END
9 | result_table
10,11,12 | 0x62,0x6c,0x65
<br />

_result_table_ — String w formacie «id1\nid2\nid3\nid4\n».
<br />
<br />


####  3.0 Obsluga blędów
<br />

W przypadku gdy klient przesłal nie poprawny pakiet , serwer odpowie:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | PACKET_DATA
8 | ERROR
9 | kod_odpowiedzi
10,11,12 | 0x62,0x6c,0x65
<br />

kod_odpowiedzi:

- PACKET_LENGTH(0x16) — dlugość pakietu jest nie poprawna
- PACKAGE_SYNTAX(0x17) — pakiet nie jest standartem DBLprot
- OPTION(0x18) — nie prawidlowe pole OPTION
- FIELD(0x19) — nie prawidlowe pole FIELD
<br />
<br />

W przypadku gdy jest probłem z pakietem HANDSHAKE:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | HANDSHAKE
8 | ERROR
9 | kod_odpowiedzi
10,11,12 | 0x62,0x6c,0x65
<br />

kod_odpowiedzi:

- PROTOCOL(0x27) — klient żada komunikować używając protokól nieznany dłą
    serwera.
- PROTOCOL_VERSION(0x28) — klient używa wersji protokolu której serwer nie
    posiada.
- CLIENT_VERSION(0x29) — klient ma starszą wersją programu.

<br />
<br />

W przypadku problemów z dodawaniem klienta do pokoju:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | ROOM
8 | ERROR
9 | kod_odpowiedzi
10,11,12 | 0x62,0x6c,0x65
<br />

kod_odpowiedzi:

- NICKNAME_LENGTH(0x46) — długość nickname jest nie poprawna.
- NICKNAME_CHARACHTERS(0x47) — nickname posiada niedozwolone znaki.
- ROOMS_ARE_FULL(0x48) — nie ma możliwości dołaczyć klienta pokoju, bo
    wszyscy są zajęte.
<br />
<br />

W przypadku nie gotowności klienta do rozpoczecią gry:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | REQUEST
8 | ERROR
9 | NOT_READY(0x36)
10,11,12 | 0x62,0x6c,0x65
<br />
<br />

W przypadku gdy gra się nie zaczeła:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | GAME
8 | ERROR
9 | NOT_STARTED(0x51)
10,11,12 | 0x62,0x6c,0x65
