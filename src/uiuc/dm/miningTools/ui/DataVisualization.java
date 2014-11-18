package uiuc.dm.miningTools.ui;

import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import org.joda.time.Period;
import uiuc.dm.miningTools.ui.domain.MiningFunctionParameters;
import uiuc.dm.moveMine.algorithms.DistanceMatrixMethod;
import uiuc.dm.moveMine.algorithms.FollowingPatternMethod;
import uiuc.dm.moveMine.algorithms.PairwiseSignificaneLevelMethod;
import uiuc.dm.moveMine.algorithms.PlotDensityMap;
import uiuc.dm.moveMine.domain.Trajectory;
import uiuc.dm.moveMine.utils.InterpolateData;

public class DataVisualization extends JFrame {

    private static final String UI_RESULT_PAGES_BASE = "html/";
    private final static int DV_WIDTH = 800;
    private final static int DV_HEIGHT = 150;
    private Container contentPane;
    private JProgressBar miningProgressBar;
    private DistanceMatrixMethod distMatrixTask;
    private PlotDensityMap       densityMapTask;
    private PairwiseSignificaneLevelMethod pairSigLevelTask;
    private FollowingPatternMethod followingMiningTask;
    private JPanel progressInfoPanel;
    private JLabel progressInfoLabel;

    private String csvOutput;
    private String outputPath;
    private ArrayList<String> kmls;
    private ArrayList<String> files;
    // pre-condition, trajs is interpolated
    public void finsh_task(ParametersSelectionFrame ds, long spentTime, int flag){
        if(flag==0){
            JOptionPane.showMessageDialog(contentPane, "Result exported to folder: "+ outputPath+"\n"+
                                                    "html file: visualization \n csv: text output\n"+
                                                    "kml folder: animations in Google Earth.");
        } else if(flag==1) {
            JOptionPane.showMessageDialog(contentPane, "Result exported to folder: "+ outputPath+"\n"+
                                                    "html file: visualization (matrix and network) \n csv: text output"
                                                    + ".");
        } else{
             JOptionPane.showMessageDialog(contentPane, "Result exported to folder: "+ outputPath+"\n"+
                                                    "html file: visualization \n csv: text output.");
        }
            
            System.out.println("show update");

               ds.updatePanelsAfterMiningFunction(spentTime);
               try {
                    //  contentPane.dispatchEvent(new WindowEvent(datasetControl, WindowEvent.WINDOW_CLOSING));
                    Desktop.getDesktop().open(new File(outputPath));
                } catch (IOException ex) {
                    Logger.getLogger(DataVisualization.class.getName()).log(Level.SEVERE, null, ex);
                }
               dispose(); 
    }
   
    public DataVisualization(
            List<Trajectory> trajs,
            MiningFunctionParameters params,
            ParametersSelectionFrame datasetControl) {
        setSize(DV_WIDTH, DV_HEIGHT);
        setTitle("Data Visualization - "
                + params.getSelectedFunction()
                + " on " + params.getSelectedDatasetName()
                + " gap: " + params.getGap() + " min(s) thresGap: "
                + params.getThresGap()
                + " hr(s) start-t: "
                + params.getStartTime()
                + " end-t: "
                + params.getEndTime());
       // javafxPanel = new JFXPanel();
        contentPane = getContentPane();
        progressInfoPanel = new JPanel();
        progressInfoLabel = new JLabel("Loading ...");
        progressInfoPanel.setPreferredSize(new Dimension(200, 100));
        miningProgressBar = new JProgressBar(0, 100);
        miningProgressBar.setValue(0);
        miningProgressBar.setStringPainted(true);
        progressInfoPanel.add(miningProgressBar);
        progressInfoPanel.add(progressInfoLabel);
        contentPane.add(progressInfoPanel);
     //   contentPane.add(javafxPanel, "North");
        PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    int progress = (Integer) evt.getNewValue();
                    miningProgressBar.setValue(progress);
                }
            }
        };

        Period gap = Period.minutes(Integer.parseInt(params.getGap()));
        Period thresGap = Period.hours(Integer.parseInt(params.getThresGap()));
        if (params.isNeedInterpolation()) {
            System.out.println("Interpolating trajectory data ...");
            System.out.println("Parameters: ");
            System.out.println("ThresGap: " + thresGap);
            System.out.println("gap: " + gap);
            System.out.println("startTime: " + params.getStartTime());
            System.out.println("endTime: " + params.getEndTime());
            System.out.println("length: ");
            
            InterpolateData.interpolation(trajs, gap, thresGap, params.getStartTime(), params.getEndTime());

            System.out.println("Done!");
        }else{
            InterpolateData.copy(trajs);
        }

        if (trajs.isEmpty() ) {
            System.out.println(trajs.isEmpty());
            miningProgressBar.setValue(100);
            progressInfoLabel.setText("No data points in this timespan");
            datasetControl.updatePanelsAfterMiningFunction(0L);
        } else {
                if (params.getSelectedFunction()
                    .equals("Distance calculation")) {
                final ParametersSelectionFrame ds = datasetControl;
                distMatrixTask = new DistanceMatrixMethod(trajs,params.getSelectedDatasetName()) {
                    @Override
                    public void done() {
                        //(ds, this.getComputedTime(), "distance");
                        csvOutput = this.getCsv();
                        outputPath = this.get_outputPath();
                        finsh_task(ds,this.getComputedTime(),100);
                    }
                };
                distMatrixTask.addPropertyChangeListener(propertyChangeListener);
                distMatrixTask.execute();
            } else if(params.getSelectedFunction()
                    .equals("Following mining")){
                /*Fei*/
                int lMax    = Integer.parseInt(params.getlMax());
                double dMax = Double.parseDouble(params.getdMax());
                double   ml = Double.parseDouble(params.getMinL());
                final ParametersSelectionFrame ds = datasetControl;
                followingMiningTask = new FollowingPatternMethod(trajs,dMax,lMax,ml,params.getSelectedDatasetName()){
                    @Override
                    public void done(){
                        //(ds, this.getComputedTime(), "following" );
                        //csvOutput = this.getCSV();
                        outputPath = this.getOuputpath();
                       // kmls  = this.getKmls();
                        //System.out.println("kmls loaded");
                       // files = this.getFilenames();
                        //System.out.println("filenames loaded");
                         //System.out.println(this.get_page());
                        finsh_task(ds, this.getComputedTime(),0);
                    }


                };
                followingMiningTask.addPropertyChangeListener(propertyChangeListener);
                followingMiningTask.execute();

                
                
            } else if (params.getSelectedFunction()
                    .equals("Attract/Avoid mining")) {
                double distThres = Double.parseDouble(params.getDistThres());
                int numRound = Integer.parseInt(params.getNumRound());
                final ParametersSelectionFrame ds = datasetControl;
                setTitle(getTitle() + " sigLevel-distThres: " + distThres + " meter(s) #rounds: " + numRound);

                pairSigLevelTask = new PairwiseSignificaneLevelMethod(trajs, distThres, numRound, params.getSelectedDatasetName()) {
                    @Override
                    protected  void done() {
                        try {
                            //loadIndexPage(ds, this.getComputedTime(), "sigLevel");
                            System.out.println("getting csv");
                            csvOutput = this.getCsv();
                            System.out.println("csvoutput complete");
                            outputPath = this.get_outputPath();
                            finsh_task(ds,this.getComputedTime(),1);
                        } catch (Exception e) {
                            e.getCause().printStackTrace();
                            String msg = String.format("Unexpected problem: %s",
                                    e.getCause().toString());
                            System.out.println(msg);
                        }
                    }
                };
                pairSigLevelTask.addPropertyChangeListener(propertyChangeListener);
                pairSigLevelTask.execute();
            } else if (params.getSelectedFunction()
                    .equals("Plotting")){
            } else if (params.getSelectedFunction()
                    .equals("Density map")){
            final ParametersSelectionFrame ds = datasetControl;/*
            densityMapTask = new PlotDensityMap(trajs, params.getSelectedDatasetName()){
                @Override
                protected void done(){
                    try{
                        outputPath = this.get_outputPath();
                        finsh_task(ds, this.getComputedTime(),100);
                    } catch (Exception e){
                        e.printStackTrace();
                    }                                
                }
            };
            densityMapTask.addPropertyChangeListener(propertyChangeListener);
            densityMapTask.execute();*/
                
            
            } else {
                throw new UnsupportedOperationException("Invalid mining function name!");
            }
                
        }
    }

 
                         
}
