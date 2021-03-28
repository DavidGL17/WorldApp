# Protocole Server world app <!-- omit in toc -->

- [1. Introduction](#1-introduction)
  - [1.1. Protocol's objectives](#11-protocols-objectives)
- [2. Message syntax](#2-message-syntax)
  - [2.1. Messages that transport data](#21-messages-that-transport-data)
    - [2.1.1. publicKey](#211-publickey)
    - [2.1.2. user_id](#212-user_id)
    - [2.1.3. id](#213-id)
    - [2.1.4. data](#214-data)
  - [2.2. Error messages](#22-error-messages)
  - [2.3. Message Encryption](#23-message-encryption)

## 1. Introduction

This protocol has been made for the World app application. It describes the messages that can be sent between the client
and the server.

### 1.1. Protocol's objectives

The aim of this protocol is to allow the server and client to share data. It allows the server to store and keep up to
date the data that is then sent to the client when requested.

## 2. Message syntax

|     Message      |                                Description                                | Used by | Carries Data |
| :--------------: | :-----------------------------------------------------------------------: | :-----: | :----------: |
|       CONN       |       Allows the user to establish a communication with the server        | Client  |  publicKey   |
|     CONN_OK      |                Allows the server to confirm the connection                | Server  |  publicKey   |
|      LOGIN       |                    logins using the user's id and pswd                    | Client  |    userId    |
|     LOGIN_OK     |                              confirms login                               | Server  |      no      |
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

Every messages end is defined by CRLF. All messages except the CONN and CONN_OK must be encrypted. The details of the
encryption method are shared bellow

### 2.1. Messages that transport data

The messages that carry data are divided into three cathegories :

#### 2.1.1. publicKey

The first message sent by both parties is used to share both public keys, from then on, all comunication is made
encrypted

```
CONN publicKey CRLF
```

#### 2.1.2. user_id

Messages that carry the user_id carry also it's password. It follows the following syntax :

```
LOGIN user_id pswd CRLF
```

#### 2.1.3. id

Messages that carry an id (of a world or article) follow the following syntax :

```
ADD_WORLD id CRLF
```

The id is a number positive non-zero.

#### 2.1.4. data

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

### 2.3. Message Encryption

All messages are encrypted using a public/private key, Encoded using the RSA 1024 bit method. Each message is encrypted
with the sender's private key. The public keys are exchanged at the beginning of the conversation with the CONN and
CONN_OK message.