package uiuc.dm.moveMine.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import org.joda.time.DateTime;
import uiuc.dm.moveMine.domain.Point;
import uiuc.dm.moveMine.domain.Trajectory;

public class ReadFile {

    private static HashSet<String> parseAttributes;

    static {
        parseAttributes = new HashSet<>();
        parseAttributes.add("timestamp");
        parseAttributes.add("location-long");
        parseAttributes.add("location-lat");
        parseAttributes.add("individual-local-identifier");
    }

    private static boolean tryParseDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private static DateTime tryParseDate(String dateStr) {
        // e.g. 2004-11-10 18:19:53.000
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS");
        try {
            return new DateTime(formatter.parse(dateStr));
        } catch (ParseException e) {
            // 2006-06-08T15:00:38.000Z
            formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            try {
                return new DateTime(formatter.parse(dateStr));
            } catch (ParseException e1) {
                return null;
            }
        }
    }

    // timestamp,location-long,location-lat,individual-local-identifier
    public static Hashtable<String, Trajectory> read(String fn) throws Exception {
        Hashtable<String, Trajectory> trajsMap = new Hashtable<>();
        BufferedReader br = new BufferedReader(new FileReader(fn));
        String line = br.readLine();
        if (line == null) {
            throw new Exception("Empty file");
        }
        Hashtable<String, Integer> headerOrder = new Hashtable<>();
        String[] header = line.split(",");

        if (header.length < 4) {
            throw new Exception("Invalid input file format");
        }

        for (int i = 0; i < header.length; i++) {
            if (parseAttributes.contains(header[i])) {
                headerOrder.put(header[i], i);
            }
        }

        line = br.readLine();
        while (line != null) {
            //System.out.println(line);
            String[] tokens = line.split(",");
            String id = tokens[headerOrder.get("individual-local-identifier")];
            DateTime timestamp = tryParseDate(tokens[headerOrder.get("timestamp")]);
            double x = Double.parseDouble(tokens[headerOrder.get("location-long")]);
            double y = Double.parseDouble(tokens[headerOrder.get("location-lat")]);
            Point p = new Point();
            p.setId(id);
            p.setValid(true);
            p.setX(x);
            p.setY(y);
            p.setTime(timestamp);
            if (trajsMap.containsKey(id)) {
                trajsMap.get(id).addPoint(p);
            } else {
                Trajectory traj = new Trajectory();
                traj.setId(id);
                traj.addPoint(p);
                trajsMap.put(id, traj);
            }
            line = br.readLine();
        }
        return trajsMap;
    }

    public static List<Trajectory> readMovebankFile(String filename)
            throws IOException {
        System.out.println("Start reading data...");
        List<Trajectory> trajs = new ArrayList<Trajectory>();
        FileInputStream fin = new FileInputStream(filename);
        DataInputStream in = new DataInputStream(fin);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        // the first line is the label
        String s = br.readLine();
        String[] labels = s.split(",");
        s = br.readLine();
        int line_n = 0;
        System.out.println("Reading file from " + filename + "...");
        while (s != null) {
            line_n++;
            if (line_n % 100000 == 0) {
                System.out.println("..." + line_n);
            }
            // Console.WriteLine(s);
            // Read a line and get a point
            String[] values = s.split(",");

            // check the number of elements read
            int flag = 0;
            Point p = new Point();
            for (int i = 0; i < values.length; i++) {
                String value = values[i].trim();
                String label = labels[i].trim();
                if (label.equals("location-long")) {
                    if (tryParseDouble(value)) {
                        p.setX(Double.parseDouble(value));
                    } else {
                        break;
                    }
                    flag++;
                } else if (label.equals("location-lat")) {
                    if (tryParseDouble(value)) {
                        p.setY(Double.parseDouble(value));
                    } else {
                        break;
                    }
                    flag++;
                } else if (label.equals("individual-local-identifier")) {
                    p.setId(value.substring(1, value.length() - 1));
                    flag++;
                } else if (label.equals("timestamp")) {
                    DateTime dt = tryParseDate(value);
                    if (dt != null) {
                        p.setTime(dt);
                        flag++;
                    } else {
                        throw new IllegalArgumentException(
                                "Received Illegel date format: " + value);
                    }
                }
                if (flag == 4) {
                    break;
                }
            }
            if (flag == 4) {
                // Find the trajectory corresponding to this ID
                String id = p.getId();
                boolean inTrajs = false;
                for (Trajectory traj : trajs) {
                    if (id.equals(traj.getId())) {
                        traj.addPoint(p);
                        inTrajs = true;
                        break;
                    }
                }
                if (!inTrajs) {
                    Trajectory traj = new Trajectory();
                    traj.setId(id);
                    traj.addPoint(p);
                    trajs.add(traj);
                }
            }
            s = br.readLine();
        }
        fin.close();
        br.close();
        System.out.println("Done reading data!");
        return trajs;
    }
}
