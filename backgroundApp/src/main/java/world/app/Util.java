/*
 * @File world.app.Util.java
 * @Authors : David González León,
 * @Date 19 déc. 2020
 */
package world.app;

import java.util.Arrays;

public interface Util {

   String url = "jdbc:postgresql://192.168.1.25:8443";
   String database = "personnal";
   String user = "pi";
   char[] array = {'D', 'V', 'D', '1', '7', '0', '2', '7', '3', '8'};
   String schema = "worldproject";
   String string =
           url + "/" + database + "?currentSchema=" + schema + "&user=" + user + "&password=" + Arrays.toString(array);
}
