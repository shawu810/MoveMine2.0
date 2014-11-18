/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uiuc.dm.moveMine.algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import uiuc.dm.miningTools.ui.DataVisualization;
import uiuc.dm.moveMine.domain.FollowingResult;
import uiuc.dm.moveMine.domain.Trajectory;

/**
 *
 * @author fxw133-admin
 */
public class FollowingPatternMethod extends SwingWorker<Void, Void>  {
    final private int MAX_NUM_INTERVALS = 10;
    private List<Trajectory> trajs;
    private double dMax;
    private int lMax;
    private long spentTime;
    private StringBuilder csvOutput;
    public static final float CONSTANT = 1000.0f;
    private double minL;
    private String Dataset_name;
    public ArrayList<String> filenames;
    private ArrayList<String> kmls;

    public String getOuputpath() {
                        return this.outputpath; //To change body of generated methods, choose Tools | Templates.
    }
    public ArrayList<String> getFilenames(){
        return this.filenames;
    }
    public ArrayList<String> getKmls(){
        return this.kmls;
    }
    public FollowingPatternMethod(List<Trajectory> trajs,
            double dMax, int lMax,double ml,String dname) {
        this.Dataset_name = dname;
        this.trajs = trajs;
        this.lMax  = lMax;
        this.dMax  = dMax;
        this.minL  = ml;
        kmls       = new ArrayList<>();
        filenames  = new ArrayList<>();
        csvOutput  = new StringBuilder();
    }
    
    
    public long getComputedTime(){
        return spentTime;
    }
    
    public ArrayList<FollowingResult> find_following(final double dist_thres, final int l_max,
                    int leader_index, int follower_index){
            System.out.println("dist_thres:"+dist_thres);
            
            System.out.println("time_thres:"+l_max);
            Trajectory A = this.trajs.get(follower_index);
            Trajectory B = this.trajs.get(leader_index);
            int n = A.getFilledPointsNum()>B.getFilledPointsNum()?B.getFilledPointsNum():A.getFilledPointsNum();
            //int m = B.get_length();
            int[] match           = new int[n];
            int[] valid           = new int[n];
            int[] j_min_set       = new int[n];
            double[] dist_min_set = new double[n];
            for(int i = 0; i < n; i++){
              System.out.println(i + "|" + n);
                    if(!B.getFilledPoint(i).valid){
                            continue;
                    }
                    double dist_min = 1e9;
                    int j_min       = -1;
                    for(int j = 0; j < n; j++){
                   // for(int j = Math.max(0,i-l_max); j < Math.min(n,i+l_max); j++){
                        double fl = (A.getFilledPoint(j).getTime().getMillis()-B.getFilledPoint(i).getTime().getMillis())/1000 ;
                        if(fl>l_max){break;}
                        if(Math.abs(fl)>l_max){continue;}
                            if(!A.getFilledPoint(j).valid){
                                    continue;
                            }
                            double dist = B.getFilledPoint(i).toDistance( A.getFilledPoint(j));
                            
            
                            if (dist < dist_min){
                                    dist_min = dist;
                                    j_min = j;
                            }// end if dist < dist_min

                    }
                    dist_min_set[i] = dist_min;
                    //System.out.println("distance:"+dist_min+"||i:"+i);
                    if (dist_min < dist_thres ){
                            valid[i] = 1;
                            if (A.getFilledPoint(j_min).getTime().getMillis() > B.getFilledPoint(i).getTime().getMillis()){
                                    double dist_min2 = 1e9;
                                    int k_min        = 0;
                                   // for(int k = Math.max(0, j_min-l_max); k < Math.min(n, j_min+l_max); k++){
                                        for(int k = 0 ; k <n ; k++ ){
                                            double fl = (A.getFilledPoint(j_min).getTime().getMillis()-B.getFilledPoint(k).getTime().getMillis())/1000 ;
                                               if(fl > l_max) {continue;}
                                               if(Math.abs(fl)>l_max){break;}
                                            double dist2 = B.getFilledPoint(k).toDistance(A.getFilledPoint(j_min));
                                            if(dist2 < dist_min2){
                                                    k_min     = k ;
                                                    dist_min2 = dist2;
                                            }
                                    }
                                    if(B.getFilledPoint(k_min).getTime().getMillis() < A.getFilledPoint(j_min).getTime().getMillis()){
                                            match[i] = 1;			
                                    }else{
                                            match[i] = 0;
                                    }
                            }else{
                                    match[i] = -1;
                            } // end if j_min < i
                            j_min_set[i] = j_min - 1;
                    }// end if dist_min < dist_thres			
            }// end for i
                // B is leader; A is follower
            ArrayList<FollowingResult>  result = find_interval(match,valid,A,B);
            
            return result;
    }
    private ArrayList<FollowingResult> find_interval(int[] match, int[] valid, Trajectory A, Trajectory B){

            int n               = match.length;
            double n2           = sum(valid);
            ArrayList<FollowingResult> interval = new ArrayList<>();
            int k        = 0;
            while(true){
                    k++;
                    double s     = 0;
                    double s_max = 0;
                    int ii       = 0;
                    int max_i    = 0;
                    int max_j    = 0;
                    for(int i = 0; i < n; i++){
                            if(valid[i] == 1){
                                    if(match[i] == 1)
                                            s += 1.0/n2;
                                    else if(match[i] == 0)
                                            s += 0.0;
                                    else if(match[i] == -1)
                                            s -= 1.0/n2;

                                    if(s > s_max){
                                            s_max = s;
                                            max_i = ii;
                                            max_j = i;
                                    }					
                            }// end if valid[i] == 1
                            if((s < 0) || (valid[i] == 0)){
                                    s = 0;
                                    ii = i+1;
                            }
                    }
                    if((s_max == 0) || (k > MAX_NUM_INTERVALS) ){//)
                            break;
                    }
                    if(max_j-max_i > 0)	interval.add(new FollowingResult(max_i, max_j, s_max,A,B,n));
                    for(int i = max_i; i< max_j; i++){
                            valid[i] = 0;
                    }
            }
            return interval;
    }
	    
    private int sum(int[] aa){
            int summ = 0;
            for(int i = 0; i < aa.length; i++)
                    summ += aa[i];
            return summ;
    }
    private int sum(int[] aa, int s, int e){
            int summ = 0;
            for(int i = s; i < e; i++)
                    summ += aa[i];
            return summ;
    }
   // @Override
    protected Void doInBackground() throws Exception {
        setProgress(0);
        csvOutput      = new StringBuilder("follower_id,leader_id,start_time,end_time,length,score\n");     
        long startTime = System.currentTimeMillis();
        ArrayList<FollowingResult> results_AfB = 
                find_following(this.dMax, this.lMax,
                                0, 1);
        this.updateResult(results_AfB);
        setProgress(33);
        ArrayList<FollowingResult> results_BfA = 
                find_following(this.dMax, this.lMax,
                                1, 0);
        setProgress(66);        
        this.updateResult(results_BfA);
        LinePlotTemplate(results_AfB,results_BfA,trajs);
        spentTime = System.currentTimeMillis()-startTime;

        setProgress(100);
        System.out.println("spent: " + spentTime + " ms ");
        return null;
    }
    
    public String getCSV(){
        return this.csvOutput.toString();
    }
    
    private void updateResult(ArrayList<FollowingResult> results) throws IOException{
        int count = 0;
           for(FollowingResult re : results){
            csvOutput.append(re.get_follower())
                    .append(",")
                    .append(re.get_leader())
                    .append(",")
                    .append(re.get_start_time())
                    .append(",")
                    .append(re.get_end_time())
                    .append(",")
                    .append(re.get_interal_length())
                    .append(",")
                    .append(re.getScore())
                    .append("\n");
            if(re.get_interal_length() >= this.minL){
                 String filename = re.get_rel()+"_"+ re.get_start_time()+".kml";
                 this.filenames.add(filename.replace("\"", "").replace(":",""));
                 this.kmls.add(this.export_as_kml(this.trajs.get(0), this.trajs.get(1),re.get_start_index(),re.get_end_index()));
            }

        }
    }
    
    private String xAxis = "time Axis";
    private String yAxis = "1 indicates following";
    private String title1 ="";
    private String title2 ="";
    private int[] data1;
    private int[] data2;
    private String outputpath = "result/";
    final private String headerFile = "html/template/followingHeader.html";
    final private String footerFile = "html/template/followingFooter.html";
    private String page ="";


    private void LinePlotTemplate(ArrayList<FollowingResult> result1, ArrayList<FollowingResult> result2, List<Trajectory> obs) throws IOException {
        System.out.println("hello");
        this.title1 = obs.get(0).getId()+" follows "+obs.get(1).getId();
        this.title2 = obs.get(1).getId()+" follows "+obs.get(0).getId();
        this.data1 = this.extractData(result1);
        this.data2 = this.extractData(result2);
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
	   //get current date time with Date()
	Date date = new Date();
        this.outputpath = this.outputpath + "Follow_"+this.Dataset_name.substring(0,4).replace(" ","_")+"_"+dateFormat.format(date)+"/";
         new File(this.outputpath).mkdirs();
        for(int i =0 ; i < data2.length ; i++){
            data2[i] = data2[i]*2;
        }
        System.out.println("extraction done");
        if(Math.abs((data1.length -data2.length))>=100){
            generateEmptyPage();
        }else{
            generateIndexPage();
        }
        System.out.println("generation done");

    }
     private void generateEmptyPage() throws IOException{
        String content = "";
        String header  = "";
        String footer  = "";   
        
        System.out.println("generateIndexpage");
        header = loadTemplate(this.headerFile);
        footer = loadTemplate(this.footerFile);
        //String leg          = "'"+this.title1+"','"+this.title2+"'";
        String g1 ="<p> Unsychronized data </p>";
        content = g1+"<br></br>";
        this.page = header+content+footer;
        System.out.println("dummping page");
        Writer write = new FileWriter(this.outputpath+"following.html");
        write.write(this.page);
        write.close();
        write = new FileWriter(this.outputpath+"following.csv");
        write.write(this.csvOutput.toString());
        write.close();
        String folder = this.outputpath+"kml/";        
        new File(folder).mkdir();
        saveKml(folder);

               
    }

    
    private void generateIndexPage() throws IOException{
        String content = "";
        String header  = "";
        String footer  = "";   
        
        System.out.println("generateIndexpage");
        header = loadTemplate(this.headerFile);
        footer = loadTemplate(this.footerFile);
        String tick_string = getTickString(this.data1,0);
        String data1_string = intA2String(this.data1, 0);
        String data2_string = intA2String(this.data2, 1);
        String leg          = "'"+this.title1+"','"+this.title2+"'";
        String g1 = "<div id='chart1' style='height:300px; width:650px; margin:0 auto;'></div>\n" +
            "<script class='code' type='text/javascript'>\n" +
            "$(document).ready(function(){\n" +
                            "var tickss = ["+tick_string+"];\n"+
            "var line1 = ["+data1_string+"];\n" +
            "var line2 = ["+data2_string+"];\n" +
            "var legendLabels = ["+leg+"]\n"+
            "var plot1 = $.jqplot('chart1', [line2, line1], {\n" +
            "      title: 'Following Timeline', \n" +
            "seriesDefaults:{renderer:$.jqplot.MekkoRenderer, rendererOptions: {showBorders: false}},\n" +
            "	  legend:{\n" +
            "            show:true,\n" +
            "			location: 'e',\n" +
            "			labels: legendLabels,\n" +
            "			rendererOptions:{numberRows: 1, placement: \"inside\"}\n" +
            "	  },\n" +
            "	  axes: {\n" +
            "	  yaxis:{\n" +
            "			tickOptions: {\n" +
            "				show: false,\n" +
            "			},\n" +
            "			rendererOptions: {\n" +
            "				drawBaseline: false\n" +
            "			},\n" +
            "			min:0.5,\n" +
            "			max:2.5\n" +
            "		},\n" +
            "		xaxis:{renderer:$.jqplot.DateAxisRenderer, ticks:tickss,\n" +
                            "tickRenderer: $.jqplot.CanvasAxisTickRenderer ,\n" +
            "		tickOptions: {\n" +
            "          angle: -40,\n" +
            "          fontSize: '10pt'\n" +
            "        } }"+
            "	  },\n" +
            "      seriesDefaults: { \n" +
            "        //showMarker:true,\n" +
            "        pointLabels: { show:true },\n" +
            "			\n" +
            "      },"+
            "	  series:[  \n" +
            "          { \n" +
            "            // Use (open) circlular markers.\n" +
            "            markerOptions: { style:'filledSquare', size:15},\n" +
            "			showLine: false\n" +
            "          }, \n" +
            "          {\n" +
            "            // Use a thicker, 5 pixel line and 10 pixel\n" +
            "            // filled square markers.\n" +
            "            //lineWidth:5, \n" +
            "			showLine: false,\n" +
            "            markerOptions: { style:'filledSquare', size:15 }\n" +
            "          }\n" +
            "      ]\n" +
            "  });"     +
                "});\n" +
                "</script>";
        content = g1+"<br></br>";
        this.page = header+content+footer;
        System.out.println("dummping page");
        Writer write = new FileWriter(this.outputpath+"following.html");
        write.write(this.page);
        write.close();
        write = new FileWriter(this.outputpath+"following.csv");
        write.write(this.csvOutput.toString());
        write.close();
        String folder = this.outputpath+"kml/";        
        new File(folder).mkdir();
        saveKml(folder);

               
    }
    public String get_page(){
        return this.page;
    }
    private int[] extractData(ArrayList<FollowingResult> result){
        int length = 0;
        if(result.size()!=0){
            length = result.get(0).get_total_length();
 //           if(length<result.get(1).get_total_length()) length = result.get(1).get_total_length();
        }
        int[] data = new int[length+20];
        for(FollowingResult re : result){
            int start = re.get_start_index();
            int end   = re.get_end_index();
            if(re.get_interal_length() >= this.minL){
            for(int i = start; i <= end; i++){
                data[i] = 1;
            }
            }
        }
        return data;
    }
    
    private String loadTemplate(String path) throws IOException{
        byte[] encoded = Files.readAllBytes(Paths.get(path));
         String out = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString(); 
        return out;
    }
    
    private String getTickString(int [] data, int index){
        String h = "";
        int space = (int) Math.floor(data.length/20);
        for(int i = 0 ; i < data.length; i++ ){
            int v = data[i];
            if(i>= this.trajs.get(index).getFilledPointsNum()) break;
            if(i%space ==0)
            h+= "["+i+",'"+this.trajs.get(index).getFilledPoint(i).getTime().toString("MM-dd-HH:mm:ss")+"'],";
            
            
        }
        h = h.substring(0, h.length()-1);
        return h;
    }
    private String intA2String(int[] data,int index){
        
        String h = "";
        for(int i = 0 ; i < data.length; i++ ){
            int v = data[i];
            if(i>= this.trajs.get(index).getFilledPointsNum()) break;
           // h+= "['"+this.trajs.get(index).getFilledPoint(i).getTime().toString("MM-dd-HH:mm:ss")+"',"+v+"],";
            h+= v+",";
            
        }
        h = h.substring(0, h.length()-1);
        return h;
    }
    
    public String export_as_kml(Trajectory follower, Trajectory leader, int start, int end) throws IOException{
        String kml_header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>         <kml xmlns=\"http://earth.google.com/kml/2.0\"> " +
                        "         <Document>" +
                        "        <Style id =\"blue\"><LineStyle> <color>ffff0000</color><width>4</width></LineStyle>   " +
                        " <IconStyle><scale>0.2</scale>			<Icon>				<href>http://www.personal.psu.edu/~fxw133/Icon_Blue_Dot.png</href></Icon>		" +
                        "</IconStyle></Style>        " +
                        "<Style id =\"red\">        <LineStyle> <color>ff0000ff</color><width>4</width></LineStyle><IconStyle>" +
                        "			<scale>0.2</scale>			<Icon>				<href>http://www.personal.psu.edu/~fxw133/Icon_Red_Dot.png</href></Icon>" +
                        "		</IconStyle>    </Style>";

        String leader_points   = "";
        String follower_points = "";
        for(int i = start; i < end ; i++){
                leader_points   += "<Placemark><styleUrl>#red</styleUrl>" +
                                                   "<TimeStamp><when>"+leader.getPoint(i).getTime().toString()+"</when></TimeStamp>"+
                                                   "<Point><coordinates>"+leader.getPoint(i).getX()+","+leader.getPoint(i).getY()+",60</coordinates></Point> </Placemark>\n";
                follower_points += "<Placemark><styleUrl>#blue</styleUrl>" +
                                                   "<TimeStamp><when>"+follower.getPoint(i).getTime().toString()+"</when></TimeStamp>"+
                                                   "<Point><coordinates>"+follower.getPoint(i).getX()+","+follower.getPoint(i).getY()+",20</coordinates></Point> </Placemark>\n";

        }
        String kml = kml_header + leader_points ;
        kml += follower_points;
        kml += "</Document></kml>";

        return kml;
    }

        private void saveKml(String folder) {
        for(int i = 0; i < this.filenames.size();i++){
            String filename = this.filenames.get(i);
            FileWriter fw;
            try {
                fw = new FileWriter(folder+filename);
                BufferedWriter bw;
                bw = new BufferedWriter(fw);
                bw.write(this.kmls.get(i));
                bw.close();


            } catch (IOException ex) {
                Logger.getLogger(DataVisualization.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Saved csv: " + folder+filename);
        }        
        
    }
}
