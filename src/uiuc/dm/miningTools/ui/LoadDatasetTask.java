
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
package uiuc.dm.miningTools.ui;

import java.util.Hashtable;
import javax.swing.SwingWorker;
import uiuc.dm.moveMine.domain.Trajectory;
import uiuc.dm.moveMine.utils.ReadFile;

/**
 * a threaded task to load data set from online database.
 *
 * @author klei2
 */
class LoadDatasetTask extends SwingWorker<Void, Void> {

    private String path;
    private Hashtable<String, Trajectory> trajsMap;
    private String exceptionMsg;
    private boolean failToParse;

    public LoadDatasetTask(String path) {
        failToParse = false;
        this.path = path;
    }

    public Hashtable<String, Trajectory> getTrajsMap() {
        return trajsMap;
    }

    public boolean isFailToParse() {
        return failToParse;
    }

    public String getErrorMsg() {
        return exceptionMsg;
    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            setProgress(0);
            trajsMap = ReadFile.read(path);
        } catch (Exception e) {
            e.printStackTrace();
            failToParse = true;
            exceptionMsg = "<html>Data set must contains four columns: <br>1) individual-local-identifier,<br>2) timestamp,<br> 3) location-long, and <br> 4) location-lat<br></html>";
        }
        return null;
    }
}
