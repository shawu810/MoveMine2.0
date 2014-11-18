

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
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;
import gnu.trove.TIntProcedure;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.apache.commons.math3.analysis.function.Acos;
import org.apache.commons.math3.analysis.function.Sin;
import uiuc.dm.moveMine.domain.MutableDouble;
import uiuc.dm.moveMine.domain.Point;
import uiuc.dm.moveMine.domain.Trajectory;
import uiuc.dm.moveMine.functions.domain.ForceGraph;
import uiuc.dm.moveMine.functions.domain.ForceLink;
import uiuc.dm.moveMine.functions.domain.ForceNode;
import uiuc.dm.moveMine.functions.domain.JsonOutput;
import uiuc.dm.moveMine.utils.JsonUtils;

/**
 * Compute pair-wise significant relationships(attraction/avoidance) mining
 *
 * @author klei2
 */
public class PairwiseSignificaneLevelMethod extends SwingWorker<Void, Void> {
    final private double SIGTHRES = 0.8;
    private List<Trajectory> trajs;
    private ForceGraph graph;
    private double distThres;
    private int numRound;
    private int[] randSeed;
    private long spentTime;
    private StringBuilder csvOutput;
    private static DecimalFormat formatter = new DecimalFormat("#.##");
    private static final String RESULT_JSON_PATH = "result/";
    public static final float CONSTANT = 1000.0f;
    private String Dataset_name;
    private static final String distance_path = "html/sigValue.html";
    private static final String json_path     = "html/js/sigValue.js";
    private static final String net_path      = "html/js/vis.js";
    private static String outputPath          = "";
    private List<String> nodesList;
    private List<String> EdgeList;
    private String nodes                      = "";
    private String edges                      = "";
    
    
        
    private String loadTemplate(String path) throws IOException{
        byte[] encoded = Files.readAllBytes(Paths.get(path));
         String out = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString(); 
        return out;
    }
    
    
    public String get_outputPath(){
        return this.outputPath;
    }
    private static void copyFileUsingJava7Files(File source, File dest)  throws IOException {

    Files.copy(source.toPath(), dest.toPath());

    }
    public PairwiseSignificaneLevelMethod(List<Trajectory> trajs,
            double distThres, int numRound, String dname) {
        nodesList = new ArrayList<String>();
        EdgeList  = new ArrayList<String>();
        this.Dataset_name = dname;
        this.outputPath   = RESULT_JSON_PATH;
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
	   //get current date time with Date()
	Date date = new Date();
        this.outputPath  += "SigMatrix_"+this.Dataset_name.substring(0,4).replace(" ","_")+"_"+dateFormat.format(date)+"/";
        new File(this.outputPath).mkdirs();
        try{
            String orgp =  new File(distance_path).getCanonicalPath();
            String jp   =  new File(json_path).getCanonicalPath();
            String jnp  =  new File(net_path).getCanonicalPath();
            String outp =  new File(this.outputPath+"sigValue.html").getCanonicalPath();
            String jpout=  new File(this.outputPath+"sigValue.js").getCanonicalPath();
            String neout=  new File(this.outputPath+"vis.js").getCanonicalPath();
            
            
            File org = new File(orgp);
            File out = new File(outp);
            File jpf = new File(jp);
            File jpo = new File(jpout);
            File orgjnp    = new File(jnp);
            File neoutfile = new File(neout);        
            //overwrite existing file, if exists
            copyFileUsingJava7Files(orgjnp,neoutfile);
            copyFileUsingJava7Files(org,out);
            copyFileUsingJava7Files(jpf,jpo);
        } catch (IOException ex) {
            Logger.getLogger(DistanceMatrixMethod.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        this.trajs = trajs;
        this.graph = new ForceGraph();
        this.distThres = distThres;
        this.numRound = numRound;
        csvOutput = new StringBuilder();
    }

    private void initRandSeeds(int n) {
        randSeed = new int[n];
        for (int i = 0; i < n; i++) {
            randSeed[i] = i;
        }
    }
    
    /***************************************************
    * Network visualization stuff
    *
    ****************************************************/
    private static final String headerFile    = "html/template/networkHeader.html";
    private static final String footerFile    = "html/template/networkFooter.html";
    private String plotNetwork() throws IOException{        
        String networkHeader = loadTemplate(this.headerFile);
        String networkFooter = loadTemplate(this.footerFile);
        String content       = "";
        int kk = nodes.length();
        int yy = edges.length();
        content  += "<script type=\"text/javascript\"> \n"
                + "  var nodes = [ \n" 
                +      this.nodes.substring(1,nodes.length())
                + "  ];\n"
                + "  var edges = [\n"
                +      this.edges.substring(1,edges.length())
                + "  ];\n";        
               return networkHeader+content+networkFooter;
    }
    
    private String constructNodes(){
        return "";
    }
    /**
     * Perform ADD operation of the a list of points' valid attributes
     *
     */
    // for example
    // intput:
    // 1111
    // 0101
    // output:
    // 0101
    private void andSequence(
            List<Point> points1,
            List<Point> points2,
            List<Integer> points1Recover,
            List<Integer> points2Recover) {

        // "AND" the valid points
        for (int i = 0; i < points1.size(); i++) {
            if (!points1.get(i).isValid() || !points2.get(i).isValid()) {
                if (!points1.get(i).isValid() && !points2.get(i).isValid()) {
                    // both are invalid
                } else {
                    if (points1.get(i).isValid()) {
                        points1Recover.add(i);
                    } else {
                        points2Recover.add(i);
                    }
                }
                points1.get(i).setValid(false);
                points2.get(i).setValid(false);
            }
        }
    }

    // reset add operations on the points sequences
    private static void recoverSequence(List<Point> points1,
            List<Point> points2,
            List<Integer> points1Recover,
            List<Integer> points2Recover) {
        // Recover the "AND"ed points back to the original sequence
        for (Integer i : points1Recover) {
            points1.get(i).setValid(true);
        }

        for (Integer i : points2Recover) {
            points2.get(i).setValid(true);
        }
    }

    // compute meeting(or co-location) frequency of two list of points
    private static int computeMeetingFreq(
            final List<Point> points1,
            final List<Point> points2,
            final double distThres) {
        int meetingFreq = 0;
        for (int i = 0; i < points1.size(); i++) {
            if (points1.get(i).isValid() && points2.get(i).isValid()
                    && points1.get(i).toDistance(points2.get(i)) < distThres) {
                meetingFreq++;
            }
        }
        return meetingFreq;
    }

    // return the points with valid indexes
    public static List<Integer> getValidIndexes(List<Point> points) {
        ArrayList<Integer> validPointIndexes = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).isValid()) {
                validPointIndexes.add(i);
            }
        }
        return validPointIndexes;
    }

    // baseline method without pruning rules
    private double getSigLevelWithNoPruning(
            final List<Point> oriPoints1,
            final List<Point> oriPoints2,
            final int round,
            final double distThres) {
        if (oriPoints1.size() != oriPoints2.size()) {
            System.out.println("points1.size != points2.size, "
                    + "points1.size=" + oriPoints1.size() + " points2.size = " + oriPoints2.size());
            System.exit(0);
        }
        if (round == 0) {
            return 0;
        }

        int numAttraction = 0;
        int numAvoidance = 0;
        int numIndependence = 0;
        int meetingFreq;
        List<Point> points1 = oriPoints1;
        List<Point> points2 = oriPoints2;
        List<Integer> points1Recover = new ArrayList<>();
        List<Integer> points2Recover = new ArrayList<>();

        // points1 "AND" points2
        andSequence(points1, points2, points1Recover, points2Recover);

        // Compute meeting frequency
        meetingFreq = computeMeetingFreq(points1, points2, distThres);

        // Store valid points indexes
        List<Integer> validPoints1Indexes = getValidIndexes(points1);
        List<Integer> validPoints2Indexes = getValidIndexes(points2);

        int points2Length = validPoints2Indexes.size();

        // Init random number
        initRandSeeds(points2Length);

        // Permutations
        for (int i = 0; i < round; i++) {
            double expectedFreq = 0;
            for (int j = 0; j < points2Length; j++) {
                int randi = (int) (Math.random() * (points2Length - j)) + j;
                int tmp = randSeed[j];
                randSeed[j] = randSeed[randi];
                int point1Index = validPoints1Indexes.get(j);
                int point2Index = validPoints2Indexes.get(randSeed[randi]);
                randSeed[randi] = tmp;
                if (points1.get(point1Index).toDistance(points2.get(point2Index)) < distThres) {
                    expectedFreq++;
                }
            }
            if (meetingFreq == expectedFreq) {
                numIndependence++;
            } else if (meetingFreq > expectedFreq) {
                numAttraction++;
            } else {
                numAvoidance++;
            }
        }

        recoverSequence(points1, points2, points1Recover, points2Recover);
        return (numAttraction + numIndependence / 2.0) / (double) round;
    }

    // The distance between two geographic coordinates (default for distance
    // calculation between two points)
    // Calculated reference: http://en.wikipedia.org/wiki/Great-circle_distance
    private static double toGeoDistance(
            double lat1,
            double long1,
            double lat2,
            double long2) {
        // PI / 180
        double constant = 0.0174532925199432957692369076848861271344287188854172;
        double d_long = (long2 - long1) * constant;
        //double d_lat = (lat2 - lat1) * constant;
        Acos acos = new Acos();
        Sin sin = new Sin();
        double lat1Constant = lat1 * constant;
        double lat2Constant = lat2 * constant;
        double a = sin.value(lat1Constant)
                * sin.value(lat2Constant)
                + Math.cos(lat1Constant)
                * Math.cos(lat2Constant) * Math.cos(d_long);
        double c = acos.value(a);
        return c * 6372800; // meters
    }

    private static double toEuclideanDistance(
            double lat1,
            double long1,
            double lat2,
            double long2) {
        double diffX = long1 - long2;
        double diffY = lat1 - lat2;
        double diff = diffX * diffX + diffY * diffY;
        if (Math.abs(diff) < 1e-6) {
            return 0;
        } else {
            return Math.sqrt(diff);
        }
    }

    // convert geo distance to eduican distance
    private static double convertDistanceToEd(final double distThres) {
//        double edg1 = 141421.356237; // sqrt(100000^2 + 100000^2)
//        double gd1 = 157293.80897343069;
        double ed1Gd1Ratio = 0.89909041659;
        return ed1Gd1Ratio * (distThres + 5);
    }

    private static void computeNumberOfOverlap(
            final List<Point> points1,
            final List<Point> points2,
            final RTree indexedTraj1,
            final RTree indexedTraj2,
            final double distThres,
            final List<Integer> traj1Overlaps,
            final List<Integer> traj2Overlaps) {
        final float withinDist = (float) convertDistanceToEd(distThres);
        for (int i = 0; i < points1.size(); i++) {
            final int I = i;

            if (points1.get(i).isValid() && points2.get(i).isValid()) {
                Point p1 = points1.get(i);
                float p1x = (float) (p1.getX() * CONSTANT);
                float p1y = (float) (p1.getY() * CONSTANT);
                com.infomatiq.jsi.Point p1r = new com.infomatiq.jsi.Point(p1x, p1y);
                indexedTraj2.nearest(
                        p1r,
                        new TIntProcedure() {
                            public boolean execute(int k) {
                                traj1Overlaps.add(I);
                                return true;
                            }
                        },
                        withinDist);

                Point p2 = points2.get(i);
                float p2x = (float) (p2.getX() * CONSTANT);
                float p2y = (float) (p2.getY() * CONSTANT);
                com.infomatiq.jsi.Point p2r = new com.infomatiq.jsi.Point(p2x, p2y);
                indexedTraj1.nearest(
                        p2r,
                        new TIntProcedure() {
                            @Override
                            public boolean execute(int k) {
                                traj2Overlaps.add(I);
                                return true;
                            }
                        },
                        withinDist);
            }
        }
    }
    
    private int getFirstValidIdx(List<Point> traj){
        int trajStartIx = 0;
        while((!traj.get(trajStartIx).isValid()) && (trajStartIx < traj.size()-1)){
            trajStartIx++;
        }        
        if(trajStartIx >= traj.size()){
            trajStartIx = traj.size()-1;
        }
        return trajStartIx;
    }
    
    private int getEndValidIdx(List<Point> traj){
        int trajEndIdx = traj.size() - 1;
        while((!traj.get(trajEndIdx).isValid()) && (trajEndIdx >= 1)){
            trajEndIdx--;
        }    
       if(trajEndIdx < 0){
            trajEndIdx = 0;
        }
        return trajEndIdx;
    }
    
    private void getOverlappedTrajs(List<Point> traj1, List<Point> traj2, List<Point> retTraj1, List<Point> retTraj2){
        
        // start point of traj1, trja2
        int traj1StartIdx = getFirstValidIdx(traj1);
        System.out.println(traj2.size());
        int traj2StartIdx = getFirstValidIdx(traj2);
        
        // end point of traj1, traj2
        int traj1EndIdx = getEndValidIdx(traj1);
        int traj2EndIdx = getEndValidIdx(traj2);
        
        int overlappedTraj1StartIdx = traj1StartIdx;
        int overlappedTraj2StartIdx = traj2StartIdx;
        Point traj1StartPoint = traj1.get(traj1StartIdx);
        Point traj2StartPoint = traj2.get(traj2StartIdx);
        
        // move startIdx to align
        if(traj1StartPoint.getTime().isAfter(traj2StartPoint.getTime())){
            // move overlappedTraj2StartIdx to the right
            while(
                    traj2.get(overlappedTraj2StartIdx).getTime().isBefore(traj1StartPoint.getTime())){
                overlappedTraj2StartIdx++;
            }
        }else{
            // move overlappedTraj1StartIdx to the right
            while(
                    traj1.get(overlappedTraj1StartIdx).getTime().isBefore(traj2StartPoint.getTime())){
                overlappedTraj1StartIdx++;
            }
        }
        
        // move endIdx to algin
        int overlappedTraj1EndIdx = traj1EndIdx;
        int overlappedTraj2EndIdx = traj2EndIdx;
        Point traj1EndPoint = traj1.get(traj1EndIdx);
        Point traj2EndPoint = traj2.get(traj2EndIdx);
        if(traj1EndPoint.getTime().isBefore(traj2EndPoint.getTime())){
            // move overlappedTraj2EndIdx to the left
            while(
                    traj2.get(overlappedTraj2EndIdx).getTime().isAfter(traj1EndPoint.getTime())){
                overlappedTraj2EndIdx--;
            }
        }else{
            // move overlappedTraj1StartIdx to the right
            while(
                    traj1.get(overlappedTraj1EndIdx).getTime().isAfter(traj2EndPoint.getTime())){
                overlappedTraj1EndIdx--;
            }
        }
        
        // copy points to new trajectory
        for(int i=overlappedTraj1StartIdx; i <= overlappedTraj1EndIdx; i++){
            retTraj1.add(traj1.get(i));
        }
        
        for(int i=overlappedTraj2StartIdx; i <= overlappedTraj2EndIdx; i++){
            retTraj2.add(traj2.get(i));
        }        
    }
    
    private double getSigLevelWithPruning(
            final List<Point> oriPoints1Src,
            final List<Point> oriPoints2Src,
            int round,
            final double distThres,
            final RTree indexedTraj1,
            final RTree indexedTraj2,
            MutableDouble portion) {
        if (round == 0) {
            return 0;
        }
        
        List<Point> oriPoints1 = new ArrayList<Point>();
        List<Point> oriPoints2 = new ArrayList<Point>();
        getOverlappedTrajs(oriPoints1Src, oriPoints2Src, oriPoints1, oriPoints2);
        
//        if (oriPoints1.size() != oriPoints2.size()) {
//            System.out.println("points1.size != points2.size, "
//                    + "points1.size=" + oriPoints1.size() + " points2.size = " + oriPoints2.size());
//            System.exit(0);
//        }

//        printTraj(oriPoints1, "traj1");
//        printTraj(oriPoints2, "traj2");
        
        List<Point> points1 = oriPoints1;
        List<Point> points2 = oriPoints2;

        int numAttraction = 0;
        int numAvoidance = 0;
        int numIndependence = 0;
        int meetingFreq;

        List<Integer> points1Recover = new ArrayList<>();
        List<Integer> points2Recover = new ArrayList<>();

        // points1 "AND" points2
        andSequence(points1, points2, points1Recover, points2Recover);

        // Compute meeting frequency
        meetingFreq = computeMeetingFreq(points1, points2, distThres);

        // Compute the overlaps
        List<Integer> traj1Overlaps = new ArrayList<>();
        List<Integer> traj2Overlaps = new ArrayList<>();
        computeNumberOfOverlap(
                points1,
                points2,
                indexedTraj1,
                indexedTraj2,
                distThres,
                traj1Overlaps,
                traj2Overlaps);

        List<Integer> validP1s = getValidIndexes(points1);
        List<Integer> validP2s = getValidIndexes(points2);


        List<Integer> useOverlaps = traj1Overlaps;
        List<Point> small = points1;
        List<Point> big = points2;
        if (traj1Overlaps.size() > traj2Overlaps.size()) {
            useOverlaps = traj2Overlaps;
            small = points2;
            big = points1;
        }

        // Ideadlly, the following condition would not happen.
        // However, RTree index is not accurate, so the "nearest" method will be off by 
        // a small number error
        if (useOverlaps.size() > validP1s.size()) {
            // All points overlaps 
            useOverlaps = validP1s;
        }

        int lowerBound = validP2s.size();
        initRandSeeds(lowerBound);

        // do randomazation
        for (int i = 0; i < round; i++) {
            double expectedFreq = 0;
            for (int j = 0; j < useOverlaps.size(); j++) {
                int randi = (int) (Math.random() * (lowerBound - j)) + j;
                int tmp = randSeed[j];
                randSeed[j] = randSeed[randi];
                int index1 = useOverlaps.get(j);
                int index2 = validP2s.get(randSeed[randi]);
                randSeed[randi] = tmp;

                if (small.get(index1).toDistance(big.get(index2)) < distThres) {
                    expectedFreq++;
                }

                // pruning rule #1
                if (expectedFreq > meetingFreq) {
                    break;
                }

                // pruning rule #2
                if (expectedFreq + lowerBound - j < meetingFreq) {
                    break;
                }
            }

            // Update counts
            if (meetingFreq == expectedFreq) {
                numIndependence++;
            } else if (meetingFreq > expectedFreq) {
                numAttraction++;
            } else {
                numAvoidance++;
            }
        }

        // Recover the "AND"ed sequences
        recoverSequence(points1, points2, points1Recover, points2Recover);
        
//        System.out.println((numAttraction + numIndependence / 2.0) / (double) round);
        return (numAttraction + numIndependence / 2.0) / (double) round;
    }

    private RTree indexPoints(List<Point> points) {
        RTree rTree = new RTree();
        rTree.init(null);
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).isValid()) {
                float x = (float) (points.get(i).getX() * CONSTANT);
                float y = (float) (points.get(i).getY() * CONSTANT);
                Rectangle rec = new Rectangle(x, y, x, y);
                rTree.add(rec, i);
            }
        }
        return rTree;
    }

    private ArrayList<Point> getValidPoints(ArrayList<Point> points){
        ArrayList<Point> new_points = new ArrayList<>();
        for(Point p : points){
            if(p.isValid()){
                new_points.add(p);
            }
        }
        return new_points;
    }
    
    private void printTraj(List<Point> traj, String id){
        System.out.println(id);
        for(Point p : traj){
            System.out.println(p.getTime() +"\t"+p.getX()+"\t"+p.getY()+"\t"+p.isValid());
        }

    }
    
//    @Override
    protected Void doInBackground() throws Exception {
        setProgress(0);
        long startTime = System.currentTimeMillis();
        // build nodes
        int groupId = 0;
        for (Trajectory traj : trajs) {
            graph.addNode(new ForceNode(traj.getId(), groupId++));
        }
        int n = trajs.size();
        ArrayList<RTree> rTrees = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ArrayList<Point> points = trajs.get(i).getFilledPoints();            
            rTrees.add(indexPoints(points));
        }
        MutableDouble portion = new MutableDouble();

        // build links
        double totalStep = (n - 1) * (n) / 2;
        double curStep = 0;
        csvOutput = new StringBuilder("id1,id2,SigValue\n");        
//        printTraj(trajs.get(0).getPoints(), trajs.get(0).getId());        
//        printTraj(trajs.get(1).getPoints(), trajs.get(1).getId());
        for (int i = 0; i < n; i++) {
            Trajectory traj1 = trajs.get(i);
            this.nodes += ",\n{id: " + i +", label: 'Node "+traj1.getId()+"'}";
            for (int j = i + 1; j < n; j++) {                                
                Trajectory traj2 = trajs.get(j);
//                System.out.println(traj1.getId() + " - " + traj2.getId());
                double sigLevel = getSigLevelWithPruning(
                        traj1.getFilledPoints(),
                        traj2.getFilledPoints(),
                        numRound,
                        distThres,
                        rTrees.get(i),
                        rTrees.get(j),
                        portion);
                if(sigLevel == 0){sigLevel = 1e-10;
                }
                if(sigLevel >= SIGTHRES){
                    this.edges += ",\n{from:" + i 
                                  +", to: "+ j
                                  +", color: 'green'}";                    
                }
                if(sigLevel <= 1 - SIGTHRES){
                    this.edges += ",\n{from:" + i
                                  +", to: "+ j
                                  +", color: 'red'}";  
                
                }

                // no pruning
//                double sigLevel = getSigLevelWithNoPruning(
//                        traj1.getFilledPoints(),
//                        traj2.getFilledPoints(),
//                        numRound,
//                        distThres);

                ForceLink link = new ForceLink(i, j, sigLevel, formatter.format(sigLevel));
                graph.addLink(link);
                curStep++;
                setProgress((int) ((curStep / totalStep) * 100));
                csvOutput.append(traj1.getId())
                        .append(",")
                        .append(traj2.getId())
                        .append(",").append(sigLevel)
                        .append("\n");
            }
        }
        spentTime = System.currentTimeMillis() - startTime;
        System.out.println("spent: " + spentTime + " ms ");
        // create json
        JsonOutput jsonOutput = new JsonOutput();
        jsonOutput.setDisplayMethod("significance level mining");
        jsonOutput.setGraph(graph);
        Gson gson = new Gson();
        String resultJson = gson.toJson(jsonOutput);
        
        

        // output to files
        JsonUtils.writeToFile(this.outputPath+"sigValue.json", resultJson);
        Writer write = new FileWriter(this.outputPath+"sigValue.csv");
        write.write(this.csvOutput.toString());
        write.close();
    
        
        String page = plotNetwork();
        write = new FileWriter(this.outputPath + "network.html" );
        write.write(page);
        write.close();
        setProgress(100);
        return null;
    }

    public long getComputedTime() {
        return spentTime;
    }

    public String getCsv() {
        return csvOutput.toString();
    }
}