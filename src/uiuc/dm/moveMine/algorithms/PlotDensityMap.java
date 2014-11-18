/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uiuc.dm.moveMine.algorithms;

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
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import javax.swing.SwingWorker;
import uiuc.dm.moveMine.domain.Trajectory;

/**
 *
 * @author fxw133-admin
 */
public class PlotDensityMap extends SwingWorker<Void, Void>{
    //private List<Trajectory> trajs;  
     private Trajectory trajs;
    private String outputPath;
    private String datasetName;
    private long spentTime;
    private String id;
    public String get_outputPath(){
        return this.outputPath;
    }
    
    public PlotDensityMap(Trajectory trajs, String dname, String id){
        this.datasetName = dname;
        this.trajs       = trajs;
        this.id          = id;
        Date date       = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");           
        this.outputPath     = "result/Density_"+this.datasetName.substring(0,4).replace(" ","_")+ "_" + dateFormat.format(date)+"/";
        new File(outputPath).mkdirs();
    }

    @Override
    protected Void doInBackground() throws Exception {
        setProgress(0);
        long startTime   = System.currentTimeMillis();

        Trajectory tra   = this.trajs;
      //  for(Trajectory tra : trajs){
            String oneResult = plotDensity(tra);
            String name      = tra.getId();
            String outputfile       = this.outputPath + "density_" + name.replace("\"","") + ".html";
            Writer write = new FileWriter(outputfile);
            write.write(oneResult);
            write.close();
       //     setProgress((int)currentIndex/numberOfTra*100);
      //  }
        setProgress(100);
        spentTime       = System.currentTimeMillis() - startTime;
        System.out.println("spent: "+ spentTime + " ms");
        return null;
    }
    

    public long getComputedTime(){
        return this.spentTime;
    }
    final private String headerFile = "html/template/densityHeader.html";
    final private String footerFile = "html/template/densityFooter.html";
    
    private String plotDensity(Trajectory tra) throws IOException{
        String header  = "";
        String footer  = "";
        String content = "";
        
        System.out.println("generate page");
        header  = loadTemplate(this.headerFile);
        footer  = loadTemplate(this.footerFile);
        for(int i = 0 ; i < tra.getPointsNum() ; i++){
            content += "new google.maps.LatLng(" + tra.getPoint(i).getY() + "," + tra.getPoint(i).getX() + "),";
        }
        content = content.substring(0,content.length()-1);
        return header + content + footer;
    }
    
    private String loadTemplate(String path) throws IOException{
        byte[] encoded = Files.readAllBytes(Paths.get(path));
         String out = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString(); 
        return out;
    }
}
