/*
 * @File Messages.java
 * @Authors : David González León
 * @Date 26 mars 2021
 */
package ch.world.app.util.protocol;

/**
 * An enum that defines all possible messages defined in the Protocol.md file.
 */
public enum Messages {
   CONN, CONN_OK, LOGIN, LOGIN_OK, GET_ALL_DATA, GET_WORLD_DATA, GET_ARTICLE_DATA, SEND_DATA, ADD_WORLD,
   ADD_ARTICLE_ADDITION_OK, UPDATE_ARTICLE, UPDATE_WORLD, UPDATE_OK, DELETE_ARTICLE, DELETE_WORLD, DELETE_OK, STOP,
   STOP_OK, ERROR
}
