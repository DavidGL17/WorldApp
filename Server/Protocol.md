# Protocole Server world app<!-- omit in toc -->>

- [1. Introduction](#1-introduction)
  - [1.1. Protocol's objectives](#11-protocols-objectives)
- [2. Message syntax](#2-message-syntax)
  - [2.1. Messages that transport data](#21-messages-that-transport-data)
    - [2.1.1. user_id](#211-user_id)
    - [2.1.2. id](#212-id)
    - [2.1.3. data](#213-data)
  - [2.2. Error messages](#22-error-messages)

## 1. Introduction

Ce protocole a été conçu pour que des clients utilisant l'application client World app puissent communiquer avec le
serveur, ceci afin de stocker les données des mondes qu'ils developpent.

### 1.1. Protocol's objectives

Le but de ce protocole est de définir les opérations précises que doit supporter le serveur afin de permettre l'envoi et
la réception de données.

## 2. Message syntax

|     Commande     |                                Description                                | Used by | Carries Data |
| :--------------: | :-----------------------------------------------------------------------: | :-----: | :----------: |
|       CONN       |       Allows the user to establish a communication with the server        | Client  |   user_id    |
|     CONN_OK      |                Allows the server to confirm the connection                | Server  |      no      |
|   GET_ALL_DATA   | Asks the server to send all the data linked to that user in a json format | Client  |      no      |
|  GET_WORLD_DATA  |               Requests the data linked to a specific world                | Client  |      id      |
| GET_ARTICLE_DATA |              Requests the data linked to a specific article               | Client  |      id      |
|    SEND_DATA     |         Sends the user a json file containing the data requested          | Server  |     yes      |
|    ADD_WORLD     |           Ask the server to add a world and link it to the user           | Client  |     yes      |
|   ADD_ARTICLE    | Asks the server to add an article to the database and link it to the user | Client  |     yes      |
|   ADDITION_OK    |                   Confirms the addition of the element                    | Server  |      id      |
|  UPDATE_ARTICLE  |                   Asks the server to update an article                    | Client  |     yes      |
|   UPDATE_WORLD   |                     Asks the server to update a world                     | Client  |     yes      |
|    UPDATE_OK     |                    Confirms the update of the element                     | Server  |      no      |
|  DELETE_ARTICLE  |                   Asks the server to delete an article                    | Client  |      id      |
|   DELETE_WORLD   |                     Asks the server to delete a world                     | Client  |      id      |
|    DELETE_OK     |                   Confirms the deletion of the element                    | Server  |      no      |
|       STOP       |                  Asks the server to close the connection                  | Client  |      no      |
|     STOP_OK      |                    Confirms the end of the connection                     | Server  |      no      |
|      ERROR       |     Indicates an error has occured (see bellow for more information)      |  Both   |      no      |

Every messages end is defined by CRLF.

### 2.1. Messages that transport data

The messages that carry data are divided into three cathegories :

#### 2.1.1. user_id

Messages that carry the user_id carry also it's password, encrypted (TBD). It follows the following syntax :

```
CONN user_id pswd CRLF
```

#### 2.1.2. id

Messages that carry an id (of a world or article) follow the following syntax :

```
ADD_WORLD id CRLF
```

The id is a number positive non-zero.

#### 2.1.3. data

Messages that carry data carry a json file. The file's end is defined by CRLF (\r\n). Data structure is TBD. The
messages will have the following syntax :

```
SEND_DATA data... CRLF
```

### 2.2. Error messages

In case of an error in the client or server side, they will send the ERROR message, accompanied by a number identifying
the error and the name of the error. This errors are described bellow :

| Error message | Number Id |                                 Description                                  |
| :-----------: | :-------: | :--------------------------------------------------------------------------: |
| SYSTEM ERROR  |    500    |                    There was an error on the server side                     |
| WRONG MESSAGE |    501    |               The message was not one defined in the protocol                |
| SYNTAX ERROR  |    400    |       The request was not well written, it did not follow the protocol       |
|   REF ERROR   |    404    | The id given in the message does not exist or is not accessible by this user |

The Error message is sent with the following syntax :

```
ERROR 400 SYNTAX ERROR CRLF
```