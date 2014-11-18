
/*
 *

 Implemented by Tobias Kin Hou Lei,
 Data Mining Research Group
 Department of Computer Science, University of Illinois at Urbana-Champaign
 201 N. Goodwin, Urbana, IL, 61801
 Office: 1117 Siebel Center
 E-mail: klei2 {at} illinois {d0t} edu
 http://tobiaslei.com

 */
package uiuc.dm.moveMine.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author klei2
 */
public class JsonUtils {

    public static void writeToFile(String relativePath, String json)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(relativePath));
        writer.write(json);
        writer.close();
    }
}
