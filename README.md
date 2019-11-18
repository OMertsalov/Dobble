# Dobble

## Multiplayer game
<br />
<br />

### Opis aplikacji
<br />

Java SE multiplayer game. 52 card's, related to "Shopping" , allow you to play with 5 players in one session. Every user, could choose ip-version to connect with and set up own Nickname. Whole project is composed of "Server", "Client with GUI" ,"Cards" and "Protocol" that was specifically designed for it.

<br />

- Klienci - łączą się z serwerem , wysyłają pakiet typu «HANDSHAKE», podłączają
    się do pokoju, aż póki on się nie zapełni, grają razem.
- Serwer — zarządza grami klientów, w danej chwili mogą być prowadzone kilka
    gier.

### ____________
<br />

### Mechanizmy:
<br />

- Zarówno klient, jak i serwer, określają protokół przesyłu danych
- Serwer przez cały czas swojego działania zapisuje do logów informacje o aktualnym
    zdarzeniu (połączył się klient - z jakim IP, portem, ...)
- Serwer i klient powinni obsługiwać protokół IPv4 oraz IPv6
- Zaimplementowana logika, wykorzystuje więcej niż 3 rodzaje przesyłanych wiadomości
- Synchronizacja wątków
- Serwer obsługuje 2+ graczy jednocześnie
<br />
<br />

### Opis protokołu
<br />

#### 1.1 Ogólny format pakietów
<br />

Protokól « _DBLgame_ » używany w komunikacji między serwerem, a klientem pochodzi z
rodziny protokołów « _DBLprot_ » przyznaczonej dla tej gry. DBLprot korzysta z protokołu
TCP/IP.
<br />
Wszystkie teksty są kodowane przy użyciu zestawu znaków UTF-8.Przy opisie struktur,
założono, że char ma rozmiar 1 bajtu ,a int 4 bajtów.
<br />

_Każdy pakiet zawiera :_

1. Trzy stałe bajty, które oznaczają początek wiadomości <0x64,0x6f,0x62>.
2. Cztery bajty, reprezentujące rozmiar pakietu(czyli int).Maksymalny rozmiar to
    166kb.
3. « _Header_ », zajmuje 1bajt i odpowiada za ogólne przyznaczenie pakietu.
4. « _Field_ », zajmuje 1bajt i odpowiada za szczegółowe przyznaczenie pakietu.
5. (Opcjonalnie)Dane.( _W zależności od pola «Field» mogą być reprzezentowane w róznych_
    _formatach._)
6. Trzy stałe bajty które oznaczają koniec wiadomości <0x62,0x6c,0x65>.

### ____________
* Jeśli połączyć pierwsze 3 bajty i ostatnie 3, to dostaniemy słowo dobble(nazwa gry).
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

Najpierw, Klient wysyła do Serwera pakiet typu « _Handshake_ ».
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
    protokól, który jest wykorzystywany w grze(czyli « _DBLgame_ »), przypisana mu wartość
    «1»;
- _protocol_version_ — wersja protokołu używanego przez klienta. « _DBLgame_ » jest w
    wersji «1.0». W postaci bajtowej ma wartość «0x10».
- _client_version_ — wersja zainstalowanego klienta.

<br />
<br />

Jeśli serwer obsluguje ten protokół i wersję, a klient używa najnowszej wersji
programu, to odpowie klientowi:
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

W tym pakiecie, « _Dane_ » , to kod odpowiedzi, który zajmuje 1 bajt.
<br />
<br />

#### 2.2 Option ROOM
<br />

Żeby dołączyć się do pokoju,w którym będzie prowadzona gra, klient musi wysłac pakiet
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
w przedziale [0x20,0x7e) oraz długość tego pola w przedziale [0x03,0x10].
<br />
<br />

Jeśli nickname jest poprawny i jest pokój do którego można dodać klienta, serwer odpowie
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
- _game_id_ — id gry.Ta wartość będzie się zmieniać w zależności od ilośći
    prowadzonych gier w pokoju .(Tzn. Pierwsza gra ma id=1,druga id=2 ... i td.).
- _max_players_ — ile osób maksymalnie może być podłączonych do tego pokoju.
- _players_info_ — informacja o podłączonych do tego pokoju klientów. Informacja jest
    typu String, w formacie «1klientID\r1klientNickname\n2klientID\r2klientNickname\
    n».Klient odejmuje od długości całego pakietu 3 ostatnie bajty ,żeby wiedzić, ile
    bajtów zajmuje to pole.
<br />
<br />

Kiedy klient został dodany do pokoju, wszyscy uczestnicy w tym pokoju otrzymają
wiadomość od serwera:
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

Kiedy klient opuści pokój, wszyscy uczestnicy otrzymają wiadomość:
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

Gra się zaczyna wtedy, gdy w pokoju jest maksymalna liczba klientów i wszyscy są gotowi. Aby
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

Od użytkownika zależy ,czy wyśle klient odpowiedź, czy nie. Jeśli tak, to pakiet będzie
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

W protokołach DBLprot tylko te dwa pakiety nie mają pola dla danych.
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

Bajt nr. 9 odpowiada za ilość kart, który serwer wydał każdemu klientu. Gra trwa dopóki w
pokoju nie zostanie jeden klient z kartami.
<br />
<br />

#### 2.4.2 Proces gry
<br />

Serwer wysyła każdemu uczestniku gry po jednej karcie:
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
zawiera numer i listę obrazków, które są na niej. Każdy obrazek to klasa, która zawiera
numer i ścieżkę do obrazku na urządzeniu klienta.
<br />
<br />

Następnie serwer wysyła do wszystkich uczestników gry ten sam obrazek(plik), który jest
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

Każdy z uczestników może przesłać swoją odpowiedź reprezentowaną jako numer obrazku:
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

Jeśli obrazek, na który wskazał klient jest wsród obrazków karty serwera, to wtedy karta klienta
zostanie zaakceptowana i zadaniem serwera jest poinformowanie  id uczestnika, który był pierwszy:
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

Gra się kończy wysłaniem z serwera do wszystkich uczestników pakietu :
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


####  3.0 Obsługa blędów
<br />

W przypadku gdy klient przesłał niepoprawny pakiet , serwer odpowie:
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

- PACKET_LENGTH(0x16) — długość pakietu jest niepoprawna
- PACKAGE_SYNTAX(0x17) — pakiet nie jest standardem DBLprot
- OPTION(0x18) — nieprawidłowe pole OPTION
- FIELD(0x19) — nieprawidłowe pole FIELD
<br />
<br />

W przypadku gdy jest problem z pakietem HANDSHAKE:
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

- PROTOCOL(0x27) — klient żąda komunikować się, używając protokółu nieznanego dla 
    serwera.
- PROTOCOL_VERSION(0x28) — klient używa wersji protokółu, której serwer nie
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

- NICKNAME_LENGTH(0x46) — długość nickname jest niepoprawna.
- NICKNAME_CHARACHTERS(0x47) — nickname posiada niedozwolone znaki.
- ROOMS_ARE_FULL(0x48) — nie ma możliwości dołączyć klienta pokoju, bo
    wszyscy są zajęte.
<br />
<br />

W przypadku niegotowności klienta do rozpoczecią gry:
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

W przypadku gdy gra się nie zaczęła:
<br />

nmr.bajtu | przeznaczenie
------------ | -------------
0 1 2 | 0x64,0x6f,0x62
3 4 5 6 | length
7 | GAME
8 | ERROR
9 | NOT_STARTED(0x51)
10,11,12 | 0x62,0x6c,0x65
