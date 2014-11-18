
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
package uiuc.dm.moveMine.algorithms;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import uiuc.dm.miningTools.ui.ParametersSelectionFrame;
import uiuc.dm.moveMine.domain.Trajectory;
import uiuc.dm.moveMine.functions.domain.ForceGraph;
import uiuc.dm.moveMine.functions.domain.ForceLink;
import uiuc.dm.moveMine.functions.domain.ForceNode;
import uiuc.dm.moveMine.functions.domain.JsonOutput;
import uiuc.dm.moveMine.utils.JsonUtils;

/**
 * Compute pair-wise distance matrix It is a threaded method.
 *
 * @author klei2
 */
public class DistanceMatrixMethod extends SwingWorker<Void, Void> {
    private String outputPath;
    private List<Trajectory> trajs;
    private ForceGraph graph;
    private long spentTime;
    private StringBuilder csvOutput;
    private static DecimalFormat formatter = new DecimalFormat("#.##");
    private static final String RESULT_JSON_PATH = "result/";
    private String Dataset_name;
    private static final String distance_path = "html/distance.html";
    private static final String json_path     = "html/js/distance.js";
    public String get_outputPath(){
        return this.outputPath;
    }
    
    private static void copyFileUsingJava7Files(File source, File dest)  throws IOException {

    Files.copy(source.toPath(), dest.toPath());

    }

    public DistanceMatrixMethod(List<Trajectory> trajs, String dname) {
        this.Dataset_name = dname;
        this.outputPath   = RESULT_JSON_PATH;
        	   DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
	   //get current date time with Date()
	   Date date = new Date();
        this.outputPath  += "DistMatrix_"+this.Dataset_name.substring(0,4).replace(" ","_")+"_"+dateFormat.format(date)+"/";
        new File(this.outputPath).mkdirs();
        try{
            String orgp =  new File(distance_path).getCanonicalPath();
            String jp   =  new File(json_path).getCanonicalPath();
            String outp =  new File(this.outputPath+"distance.html").getCanonicalPath();
            String jpout=  new File(this.outputPath+"distance.js").getCanonicalPath();
           File org = new File(orgp);
           File out = new File(outp);
           File jpf = new File(jp);
           File jpo = new File(jpout);

            //overwrite existing file, if exists
            copyFileUsingJava7Files(org,out);
            copyFileUsingJava7Files(jpf,jpo);
        } catch (IOException ex) {
            Logger.getLogger(DistanceMatrixMethod.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        this.trajs = trajs;
        this.graph = new ForceGraph();
        csvOutput  = new StringBuilder();
    }

    @Override
    protected Void doInBackground() throws Exception {
        setProgress(0);
        long startTime = System.currentTimeMillis();
        // build nodes
        int groupId = 0;
        for (Trajectory traj : trajs) {
            graph.addNode(new ForceNode(traj.getId(), groupId++));
        }

        // build links
        int n = trajs.size();
        double totalStep = (n - 1) * (n) / 2;
        double curStep = 0;
        csvOutput = new StringBuilder("id1,id2,geo distance(meters)\n");
        List<Double> dists = new ArrayList<Double>();
        for (int i = 0; i < n; i++) {
            Trajectory traj1 = trajs.get(i);
            for (int j = i + 1; j < n; j++) {
                Trajectory traj2 = trajs.get(j);
                double value = traj1.toAvgDistanceOld(traj2);
                dists.add(value);
                ForceLink link = new ForceLink(i, j, value, formatter.format(value) + " meter(s)");
                graph.addLink(link);
                curStep++;
                setProgress((int) ((curStep / totalStep) * 100));
                csvOutput.append(traj1.getId())
                        .append(",")
                        .append(traj2.getId())
                        .append(",").append(value)
                        .append("\n");
            }
        }

        addDescriptiveStats(dists);
        spentTime = System.currentTimeMillis() - startTime;
        System.out.println("spent: " + spentTime + " ms ");
        // create json
        JsonOutput jsonOutput = new JsonOutput();
        jsonOutput.setDisplayMethod("distance calculation");
        jsonOutput.setGraph(graph);
        Gson gson = new Gson();
        String resultJson = gson.toJson(jsonOutput);

        // output the json to a file
        JsonUtils.writeToFile(this.outputPath+"distance.json", resultJson);
        
        Writer write = new FileWriter(this.outputPath+"distance.csv");
        write.write(this.csvOutput.toString());
        write.close();
        setProgress(100);
        return null;
    }

    private void addDescriptiveStats(List<Double> values) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (Double v : values) {
            stats.addValue(v);
        }

        double mean = stats.getMean();
        double median = stats.getPercentile(50);
        double std = stats.getStandardDeviation();

        StringBuilder builder = new StringBuilder();
        builder.append("mean,median,stdev\n");
        builder.append(mean).append(",")
                .append(median).append(",")
                .append(std).append("\n");

        csvOutput.insert(0, builder);
    }

    public long getComputedTime() {
        return spentTime;
    }

    public String getCsv() {
        return csvOutput.toString();
    }
}
