package uiuc.dm.miningTools.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import org.joda.time.Minutes;
import org.joda.time.Period;
import uiuc.dm.miningTools.ui.domain.MiningFunctionParameters;
import uiuc.dm.moveMine.algorithms.PairwiseSignificaneLevelMethod;
import uiuc.dm.moveMine.algorithms.PlotDensityMap;
import uiuc.dm.moveMine.domain.Point;
import uiuc.dm.moveMine.domain.Trajectory;
import uiuc.dm.moveMine.utils.InterpolateData;
import uiuc.dm.moveMine.utils.PlotGoogleEarth;

public class GenerateKMLFile extends JFrame implements ActionListener, PropertyChangeListener {

    private static final Double DEFAULT_DIST_DIFF_THRES = 100.0;
    private static final Double DEFAULT_TIME_DIFF_THRES = 15.0;
    private static final int HELP_WINDOWS_WIDTH = 400;
    private static final int HELP_WINDOWS_HEIGHT = 200;
    private static final int KML_WINDOWS_WIDTH = 400;
    private static final int KML_WINDOWS_HEIGHT = 200;
    private JPanel controlPanel;
    private JPanel onePanel;
    private JPanel densitiesPanel;
    private JPanel paramsPanel;
    private JPanel progressPanel;
    private JPanel progressStatusPanel;
    private JLabel status;
    private JLabel idLabel1;
    private JLabel idLabel2;
    private JLabel densityIDtag;
    private JLabel distanceDiffThresLabel;
    private JLabel timeDiffThresLabel;
    private JTextField distanceDiffThres;
    private JTextField timeDiffThres;
    private JComboBox ids1;
    private JComboBox ids2;
    private JButton generateButton;
    private JButton generateDensityButton;
    private JButton generateDensityButton1;
    private JComboBox traIds;
    private JComboBox densityIds;
    private JButton helpButton;
    private JProgressBar progressBar;
    private GenerateKMLFileTask kmlfileTask;
    private GenerateOneKMLFileTask kmlfileTask2;
    private Container contentPane;
    private Hashtable<String, Trajectory> trajsMap;
    private List<Trajectory> subtraj;
    private String outputPath;
    private MiningFunctionParameters params;
    private class GenerateOneKMLFileTask extends SwingWorker<Void, Void>{
        private String fn;
        private Trajectory traj1;
        private PrintWriter out;
        
        
        private GenerateOneKMLFileTask(Trajectory traj) throws IOException{
            fn = traj.getId()+"_Tra";
            this.traj1 = traj;
            File file = new File(outputPath+"tra_"+traj1.getId().replace("\"", ""));
                String filePath = file.getPath();
                if (!filePath.toLowerCase().endsWith(".kml")) {
                    file = new File(filePath + ".kml");
                }
                out = new PrintWriter(new FileWriter(file));
                System.out.println("Saving kml: " + file.getName());

        }
        private void initGoogleEarthData(String name) {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://earth.google.com/kml/2.0\"> <Document> <name>");
            out.println(name);
            out.println("</name> <Style id=\"green\"><IconStyle><color>ff00ff00</color><colorMode>normal</colorMode></IconStyle></Style> <Style id=\"red\"><IconStyle><color>ff0000ff</color><colorMode>normal</colorMode></IconStyle></Style> <Style id=\"blueline\"><LineStyle><color>afff0000</color><width>1</width></LineStyle></Style> <Style id=\"yellowline\"><LineStyle><color>ff00ffff</color><width>1</width></LineStyle></Style> <open>1</open>");
        }

        private void endGoogleEearthData() {
            out.println("</Document> </kml>");
            out.close();
        }

        private void openFolder(String name) {
            out.print("<Folder><name>");
            out.print(name);
            out.println("</name>");
        }

        private void closeFolder() {
            out.println("</Folder>");
        }

        private void generateGoogleEarthLinks(Trajectory traj1) throws IOException {
            out.println(PlotGoogleEarth.showOneLine(traj1));
        }

        @Override
        protected Void doInBackground() throws Exception {
         
//                List<Point> points1 = traj1.getPoints();
//                List<Point> points2 = traj2.getPoints();
            initGoogleEarthData("dataset[ " + fn + " ]");
            generateGoogleEarthLinks(traj1);
            endGoogleEearthData();
            return null;
        }
         @Override
        public void done() {
            status.setText("Done");
            enablePanel(controlPanel);
            enablePanel(onePanel);
            enablePanel(densitiesPanel);
            enablePanel(progressPanel);
            enablePanel(paramsPanel);
            enablePanel(progressStatusPanel);
            try {
                    //  contentPane.dispatchEvent(new WindowEvent(datasetControl, WindowEvent.WINDOW_CLOSING));
                    Desktop.getDesktop().open(new File(outputPath));
                } catch (IOException ex) {
                    Logger.getLogger(DataVisualization.class.getName()).log(Level.SEVERE, null, ex);
                }
             //  dispose(); 
        }
    
    }
    
    
    private class GenerateKMLFileTask extends SwingWorker<Void, Void> {

        private Trajectory traj1;
        private Trajectory traj2;
        private PrintWriter out;
        private String fn;
        private double timeDiffThres;
        private double distDiffThres;
        private int couldMeetMax = 50000;
        private String outputPath;
        
        public String get_outputPath(){
            return this.outputPath;
        }
        
        private GenerateKMLFileTask(Trajectory traj1, Trajectory traj2, double timeDiff, double distDiff, String datasetName) throws IOException {
            this.traj1 = traj1;
            this.traj2 = traj2;
            this.timeDiffThres = timeDiff;
            this.distDiffThres = distDiff;
            Date date       = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");    
            fn = traj1.getId() + " compares to " + traj2.getId();
            this.outputPath     = "result/MeetingPlace_"+datasetName.substring(0,4).replace(" ","_")+ "_" + dateFormat.format(date)+"/";
            new File(outputPath).mkdirs();
            //FileFilter filter = new FileNameExtensionFilter("KML file", "kml");
           // JFileChooser fc = new JFileChooser();
            //fc.setFileFilter(filter);
            //int returnVal = fc.showSaveDialog(GenerateKMLFile.this);
           // if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = new File(outputPath+"tra_"+traj1.getId().replace("\"", "")+"_"+traj2.getId().replace("\"", ""));
                String filePath = file.getPath();
                if (!filePath.toLowerCase().endsWith(".kml")) {
                    file = new File(filePath + ".kml");
                }
                out = new PrintWriter(new FileWriter(file));
                System.out.println("Saving kml: " + file.getName());


    
            //} else {
            //    System.out.println("Save kml cancelled by user.");
           // }
        }

        private void initGoogleEarthData(String name) {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://earth.google.com/kml/2.0\"> <Document> <name>");
            out.println(name);
            out.println("</name> <Style id=\"green\"><IconStyle><color>ff00ff00</color><colorMode>normal</colorMode></IconStyle></Style> <Style id=\"red\"><IconStyle><color>ff0000ff</color><colorMode>normal</colorMode></IconStyle></Style> <Style id=\"blueline\"><LineStyle><color>afff0000</color><width>1</width></LineStyle></Style> <Style id=\"yellowline\"><LineStyle><color>ff00ffff</color><width>1</width></LineStyle></Style> <open>1</open>");
        }

        private void endGoogleEearthData() {
            out.println("</Document> </kml>");
            out.close();
        }

        private void openFolder(String name) {
            out.print("<Folder><name>");
            out.print(name);
            out.println("</name>");
        }

        private void closeFolder() {
            out.println("</Folder>");
        }

        private void generateGoogleEarthLinks(Trajectory traj1, Trajectory traj2) throws IOException {
            out.println(PlotGoogleEarth.showTwoLines(traj1, traj2));
        }

        @Override
        protected Void doInBackground() {
            try {
//                List<Point> points1 = traj1.getPoints();
//                List<Point> points2 = traj2.getPoints();
                List<Point> points1 = traj1.getPoints();
                List<Point> points2 = traj2.getPoints();
                int couldMeetId = 0;
                int actuallyMeetId = 0;
                initGoogleEarthData("dataset[ " + fn + " ]");
                openFolder(traj1.getId() + " could meet " + traj2.getId());

                generateGoogleEarthLinks(traj1, traj2);

                List<Integer> p1Valid = PairwiseSignificaneLevelMethod.getValidIndexes(points1);
                List<Integer> p2Valid = PairwiseSignificaneLevelMethod.getValidIndexes(points2);
                double total = p1Valid.size() * p2Valid.size();
                System.out.println(total);
                double cur = 0;
                HashSet<Point> couldMeetMapPoints1 = new HashSet<>();
                HashSet<Point> couldMeetMapPoints2 = new HashSet<>();
                HashSet<Point> actuallyMeetMapPoints1 = new HashSet<>();
                HashSet<Point> actuallyMeetMapPoints2 = new HashSet<>();
                StringBuilder actuallyMeetKML = new StringBuilder(PlotGoogleEarth.showTwoLines(traj1, traj2));
                for (int i = 0; i < p1Valid.size(); i++) {
                    for (int j = 0; j < p2Valid.size(); j++) {
                        Point v1 = points1.get(p1Valid.get(i));
                        Point v2 = points2.get(p2Valid.get(j));
                        double distDiff = v1.toDistance(v2);
                        int timeDiff = Math.abs(Minutes.minutesBetween(v1.getTime(), v2.getTime()).getMinutes());
                        if (distDiff < distDiffThres && timeDiff < timeDiffThres) {
                            if (!actuallyMeetMapPoints1.contains(v1)) {
                                actuallyMeetMapPoints1.add(v1);
                                actuallyMeetKML.append(PlotGoogleEarth.buildAMarker(v1, 0, actuallyMeetId, v1.getTime().toDate()));
                            }
                            if (!actuallyMeetMapPoints2.contains(v2)) {
                                actuallyMeetMapPoints2.add(v2);
                                actuallyMeetKML.append(PlotGoogleEarth.buildAMarker(v2, 1, actuallyMeetId, v2.getTime().toDate()));
                            }
                            actuallyMeetId++;
                        }

                        int tid = 0;
                        int tid2 = 0;

                        if (distDiff < distDiffThres) {
                            if (tid + tid2 < couldMeetMax) {
                                if (!couldMeetMapPoints1.contains(v1)) {
                                    couldMeetMapPoints1.add(v1);
                                    out.println(PlotGoogleEarth.buildAMarker(v1, 0, tid++, v1.getTime().toDate()));
                                }
                                if (!couldMeetMapPoints2.contains(v2)) {
                                    couldMeetMapPoints2.add(v2);
                                    out.println(PlotGoogleEarth.buildAMarker(v2, 1, tid2++, v2.getTime().toDate()));
                                }
                            }
                        }
                        cur++;
                        setProgress((int) ((cur / total) * 100));
                    }
                }
                closeFolder();

                openFolder(traj1.getId() + " actually meets " + traj2.getId());
                out.println(actuallyMeetKML.toString());
                closeFolder();
            } catch (Exception e) {
                e.printStackTrace();
            }

            endGoogleEearthData();
            return null;
        }

        @Override
        public void done() {
            status.setText("Done");
            enablePanel(controlPanel);
            enablePanel(onePanel);
            enablePanel(densitiesPanel);
            enablePanel(progressPanel);
            enablePanel(paramsPanel);
            enablePanel(progressStatusPanel);
            try {
                    //  contentPane.dispatchEvent(new WindowEvent(datasetControl, WindowEvent.WINDOW_CLOSING));
                    Desktop.getDesktop().open(new File(outputPath));
                } catch (IOException ex) {
                    Logger.getLogger(DataVisualization.class.getName()).log(Level.SEVERE, null, ex);
                }
             //  dispose(); 
        }
    }

    private static void disablePanel(final JPanel panel) {
        Component[] com = panel.getComponents();
        for (int a = 0; a < com.length; a++) {
            com[a].setEnabled(false);
        }
    }

    private static void enablePanel(final JPanel panel) {
        Component[] com = panel.getComponents();
        for (int a = 0; a < com.length; a++) {
            com[a].setEnabled(true);
        }
    }

    public GenerateKMLFile(
            Hashtable<String, Trajectory> trajsMap, MiningFunctionParameters params, String outputPath, List<Trajectory> tra) { 
        this.outputPath = outputPath;
        this.trajsMap = trajsMap;
        this.params   = params;
       
        //List<Trajectory> trajs = new ArrayList<Trajectory>(trajsMap.values());
        Period gap = Period.minutes(Integer.parseInt(params.getGap()));
        Period thresGap = Period.hours(Integer.parseInt(params.getThresGap()));
            System.out.println("Interpolating trajectory data ...");
            System.out.println("Parameters: ");
            System.out.println("ThresGap: " + thresGap);
            System.out.println("gap: " + gap);
            System.out.println("startTime: " + params.getStartTime());
            System.out.println("endTime: " + params.getEndTime());
            System.out.println("length: ");
        InterpolateData.interpolation(tra, gap, thresGap, params.getStartTime(), params.getEndTime());
        InterpolateData.copy(tra);
        this.subtraj  = tra;
        setTitle("Plotting");
        setSize(KML_WINDOWS_WIDTH , KML_WINDOWS_HEIGHT+250);
          setResizable(true);
        contentPane  = getContentPane();
        controlPanel = new JPanel();
        onePanel = new JPanel();
        densitiesPanel= new JPanel();
        onePanel.setPreferredSize(new Dimension(WIDTH, 130));
                onePanel.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Plot one trajectory"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        densitiesPanel.setPreferredSize(new Dimension(WIDTH, 130));
        densitiesPanel.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Plot density maps for selected individual"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        controlPanel.setPreferredSize(new Dimension(WIDTH, 200));
        controlPanel.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Plot meeting places "),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        progressPanel = new JPanel();
        paramsPanel   = new JPanel();
        progressStatusPanel = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        
        contentPane.add(onePanel);
        contentPane.add(densitiesPanel);
        contentPane.add(controlPanel);
        contentPane.add(progressPanel);
        contentPane.add(progressStatusPanel);

        Set<String> namesSet = trajsMap.keySet();
        String[] names = namesSet.toArray(new String[namesSet.size()]);
        generateButton = new JButton("Plot meeting places");
        this.generateDensityButton = new JButton("Plot Density maps");
        this.traIds            = new JComboBox(names);
        this.densityIds        = new JComboBox(names);
        this.generateDensityButton1= new JButton("Plot one trajectory");
        idLabel1 = new JLabel("ID1: ");
        idLabel2 = new JLabel("ID2: ");
        this.densityIDtag = new JLabel("ID: ");
        distanceDiffThresLabel = new JLabel("distThres(meter): ");
        timeDiffThresLabel = new JLabel("timeThres(min): ");
        distanceDiffThres = new JTextField(5);
        timeDiffThres = new JTextField(5);
        distanceDiffThres.setText(Double.toString(DEFAULT_DIST_DIFF_THRES));
        timeDiffThres.setText(Double.toString(DEFAULT_TIME_DIFF_THRES));
        helpButton = new JButton("?");
        ids1 = new JComboBox(names);
        ids2 = new JComboBox(names);
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        status = new JLabel("");

        controlPanel.add(idLabel1);
        controlPanel.add(ids1);
        controlPanel.add(idLabel2);
        controlPanel.add(ids2);
        controlPanel.add(paramsPanel);
        controlPanel.add(generateButton);
        densitiesPanel.add(this.densityIDtag);
        densitiesPanel.add(this.densityIds);
        densitiesPanel.add(this.generateDensityButton);
        onePanel.add(this.densityIDtag);
        onePanel.add(this.traIds);
        onePanel.add(this.generateDensityButton1);
        
        paramsPanel.add(distanceDiffThresLabel);
        paramsPanel.add(distanceDiffThres);
        paramsPanel.add(timeDiffThresLabel);
        paramsPanel.add(timeDiffThres);
        paramsPanel.add(helpButton);
        helpButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFrame helpTextFrame = new JFrame("Mining Function Help");
                        helpTextFrame.setSize(HELP_WINDOWS_WIDTH, HELP_WINDOWS_HEIGHT);
                        JTextArea text = new JTextArea();
                        text.setEditable(false);
                        text.setCursor(null);
                        text.setOpaque(false);
                        text.setFocusable(false);
                        text.setLineWrap(true);
                        text.setWrapStyleWord(true);
                        text.setPreferredSize(
                                new Dimension(HELP_WINDOWS_WIDTH - 20,
                                HELP_WINDOWS_HEIGHT - 20));
                        text.setText(genHelpText());
                        JPanel panel = new JPanel();
                        panel.setPreferredSize(
                                new Dimension(HELP_WINDOWS_WIDTH,
                                HELP_WINDOWS_HEIGHT));
                        panel.add(text);
                        helpTextFrame.add(panel);
                        helpTextFrame.setVisible(true);
                    }
                });
        progressPanel.add(progressBar, BorderLayout.NORTH);
        progressStatusPanel.add(status, BorderLayout.NORTH);
        generateButton.addActionListener(this);
        this.generateDensityButton.addActionListener(this);
        this.generateDensityButton1.addActionListener(this);
        progressPanel.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getSource().toString());
        if (e.getSource() == generateButton) {
            String id1 = (String) ids1.getSelectedItem();
            String id2 = (String) ids2.getSelectedItem();
            
            if (id1.equals(id2)) {
                status.setText("ID1 = ID2. Please choose a different ID.");
            } else {
                progressPanel.setVisible(true);
                try {
                    
                    status.setText("Generating ...");
                    disablePanel(controlPanel);
                    disablePanel(progressPanel);
                    disablePanel(onePanel);
                    disablePanel(densitiesPanel);
                    disablePanel(paramsPanel);
                    disablePanel(progressStatusPanel);
                    double timeDiffThresInput = Double.parseDouble(timeDiffThres.getText());
                    double distDiffThresInput = Double.parseDouble(distanceDiffThres.getText());
                    kmlfileTask = new GenerateKMLFileTask(
                            trajsMap.get(id1),
                            trajsMap.get(id2),
                            timeDiffThresInput,
                            distDiffThresInput,
                            params.getSelectedDatasetName());
                    kmlfileTask.addPropertyChangeListener(this);
                    kmlfileTask.execute();
                } catch (IOException ex) {
                    Logger.getLogger(GenerateKMLFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                
        } else if (e.getSource() == this.generateDensityButton){
            String id = (String) this.densityIds.getSelectedItem();
            disablePanel(controlPanel);
            disablePanel(onePanel);
            disablePanel(densitiesPanel);
            disablePanel(paramsPanel);
            disablePanel(progressStatusPanel);
            progressPanel.setVisible(true);
            this.status.setText("Please wait .......");
            PlotDensityMap densityMapTask = new PlotDensityMap(trajsMap.get(id), params.getSelectedDatasetName(),id){
                @Override
                protected void done(){
                                status.setText("Done");
            enablePanel(controlPanel);
            enablePanel(onePanel);
            enablePanel(densitiesPanel);
            enablePanel(progressPanel);
            enablePanel(paramsPanel);
            enablePanel(progressStatusPanel);
            try {
                    outputPath = this.get_outputPath();
                    //  contentPane.dispatchEvent(new WindowEvent(datasetControl, WindowEvent.WINDOW_CLOSING));
                    Desktop.getDesktop().open(new File(outputPath));
                } catch (IOException ex) {
                    Logger.getLogger(DataVisualization.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
                    
             }
            };
            densityMapTask.addPropertyChangeListener(this);
            densityMapTask.execute();
        
        } else if(e.getSource() == this.generateDensityButton1){
            String idd = (String) this.traIds.getSelectedItem();
            progressPanel.setVisible(true);
                try {
                    status.setText("Generating ...");
                    disablePanel(controlPanel);
                    disablePanel(progressPanel);
                    disablePanel(onePanel);
                    disablePanel(densitiesPanel);
                    disablePanel(paramsPanel);
                    disablePanel(progressStatusPanel);
                    kmlfileTask2 = new GenerateOneKMLFileTask(
                            trajsMap.get(idd)
                            );
                    kmlfileTask2.addPropertyChangeListener(this);
                    kmlfileTask2.execute();
                } catch (IOException ex) {
                    Logger.getLogger(GenerateKMLFile.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        }
    }

    private String genHelpText() {
        StringBuilder builder = new StringBuilder();
        builder.append("Only plot raw data. Locations that are within certain distance and the time differences are within certain time threshold.\n\n")
                .append("Suppose we have two trajectories A and B, we will plot point Ai if there exist a point Bj, that distance(Ai, Bj) < distance_threshold and Difference(Time(Ai), Time(Bj)) < time_threshold.");
        return builder.toString();
    }
}
