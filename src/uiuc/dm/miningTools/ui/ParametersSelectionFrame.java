package uiuc.dm.miningTools.ui;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.apache.commons.codec.digest.DigestUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.joda.time.DateTime;
import org.movebank.client.rest.Constants;
import org.movebank.client.rest.MovebankRestClient;
import org.movebank.client.rest.Record;
import org.movebank.client.rest.RecordCallbackDefault;
import org.movebank.client.rest.RequestBuilderEvent;
import org.movebank.client.rest.RequestBuilderIndividual;
import org.movebank.client.rest.RequestBuilderStudy;
import org.movebank.client.rest.StaticDataBrowser;
import org.movebank.client.rest.StudyBrowser;
import uiuc.dm.miningTools.dao.DataAccessObject;
import uiuc.dm.miningTools.ui.domain.MiningFunctionParameters;
import uiuc.dm.miningTools.ui.domain.UIComboOptions;
import uiuc.dm.moveMine.domain.Dataset;
import uiuc.dm.moveMine.domain.Point;
import uiuc.dm.moveMine.domain.Trajectory;


/**
 * The parameter selection panel. It enables the different dataset, parameters
 * and algorithm selection event.
 *
 * @author klei2
 */
public class ParametersSelectionFrame extends JFrame implements ActionListener, PropertyChangeListener {

    private final static int MAIN_WIDTH = 550;
    private final static int PANEL_WIDTH_GAP = 30;
    private final static int SUB_PANEL_WIDTH_GAP = 20;
    private final static int MAIN_HEIGHT = 800;
    private final static int HELP_TEXT_JFRAME_HEIGHT = 500;
    private final static int HELP_TEXT_JFRAME_WIDTH = 400;
    private final static int DATA_SELECTION_PANEL_HEIGHT = 120;
    private final static int DATA_SELECTION_PANEL_WIDTH = MAIN_WIDTH - PANEL_WIDTH_GAP;
    private final static String DATA_SELECTION_PANEL_TITLE = "Data Set";
    private final static int SELECT_IND_PANEL_HEIGHT = 50;
    private final static int SELECT_TIMESPAN_PANEL_HEIGHT = 40;
    private final static int DOWNLOAD_PROGRESS_PANEL_WIDTH = MAIN_WIDTH - PANEL_WIDTH_GAP - SUB_PANEL_WIDTH_GAP;
    private final static int DOWNLOAD_PROGRESS_PANEL_HEIGHT = 50;
    private final static int SELECT_IND_PANEL_WIDTH = MAIN_WIDTH - PANEL_WIDTH_GAP - SUB_PANEL_WIDTH_GAP;
    private final static int SELECT_TIMESPAN_PANEL_WIDTH = MAIN_WIDTH - PANEL_WIDTH_GAP - SUB_PANEL_WIDTH_GAP;
    private final static int DISPLAY_STATS_PANEL_HEIGHT = 220;
    private final static int DISPLAY_STATS_PANEL_WIDTH = MAIN_WIDTH - PANEL_WIDTH_GAP;
    private final static String DISPLAY_STATS_PANEL_TITLE = "Data Information";
    private final static int INTERPOLATE_PARAMS_PANEL_HEIGHT = 90;
    private final static int INTERPOLATE_PARAMS_PANEL_WIDTH = MAIN_WIDTH - PANEL_WIDTH_GAP;
    private final static String INTERPOLATE_PARAMS_PANEL_TITLE = "Data Interpolation Parameters";
    private final static int MINING_FUNC_SELECTION_PANEL_HEIGHT = 80;
    private final static int MINING_FUNC_SELECTION_PANEL_WIDTH = MAIN_WIDTH - PANEL_WIDTH_GAP;
    private final static String MINING_FUNC_SELECTION_PANEL_TITLE = "Mining Functions";
    private final static int MINING_FUNC_PARAMS_SELECTION_PANEL_HEIGHT = 110;
    private final static int MINING_FUNC_PARAMS_SELECTION_PANEL_WIDTH = MAIN_WIDTH - PANEL_WIDTH_GAP;
    private final static int SIG_LEVEL_PARAMS_PANEL_HEIGHT = 40;
    private final static int SIG_LEVEL_PARAMS_PANEL_WIDTH = MAIN_WIDTH - PANEL_WIDTH_GAP - SUB_PANEL_WIDTH_GAP;
    private final static int PLOT_MAP_PARAMS_PANEL_HEIGHT = 40;
    private final static int PLOT_MAP_PARAMS_PANEL_WIDTH = MAIN_WIDTH - PANEL_WIDTH_GAP - SUB_PANEL_WIDTH_GAP;
    private final static String MINING_FUNC_PARAMS_SELECTION_PANEL_TITLE = "Mining Function Parameters";
    private final static int DISPLAY_INSTRUCTION_PANEL_HEIGHT = 100;
    private final static int DISPLAY_INSTRUCTION_PANEL_WIDTH = MAIN_WIDTH - PANEL_WIDTH_GAP;
    private final static int DISPLAY_PERFORMANCE_PANEL_HEIGHT = 80;
    private final static int DISPLAY_PERFORMANCE_PANEL_WIDTH = MAIN_WIDTH - PANEL_WIDTH_GAP;
    private final static double DEFAULT_DIST_THRES = 20;
    private final static double DEFAULT_DIST_THRES_ATT = 100;
    /*Fei*/
    private final static double DEFAULT_TIME_THRES = 40;
    private final static int FOLLOWING_PARAMS_PANEL_WIDTH  = SIG_LEVEL_PARAMS_PANEL_WIDTH;
    private final static int FOLLOWING_PARAMS_PANEL_HEIGHT = 60;
    private final static int DEFAULT_MIN_LENGTH = 100;
    /**/
    private Container contentPane;
    private JPanel mainPanel;
    private JPanel datasetSelectionPanel;
    private JPanel displayStatsPanel;
    private JPanel interpolationParamsSelectionPanel;
    private JPanel miningFunctionSelectionPanel;
    private JPanel miningFunctionParamsSelectionPanel;
    private JPanel displayInstructionPanel;
    private JPanel performancePanel;
    private JPanel downloadDatasetProgressPanel;
    private JPanel selectIndividualsPanel;
    private JPanel selectTimespanPanel;
    private JPanel sigLevelParamsPanel;
    /*Fei*/
    private boolean interpFlag = true;
    private JPanel followingParamsPanel;
    private JPanel plotMapParamsPanel;
    private JCheckBox interpBox;
    private JPanel plotDensityMapPanel;
    private JButton saveDensityMapButton;
    /**/
    private JComboBox datasetCombo;
    private JComboBox functionCombo;
    private JComboBox displayMethodCombo;
    private JComboBox gapCombo;
    private JComboBox thresGapCombo;
    private JComboBox numRoundCombo;
    private JButton loadDatasetButton;
    private JButton goButton;
    private JButton useLocalCopyButton;
    private JButton downloadOnlineButton;
    private JButton interpolationNextButton;
    private JButton dataInterpolationHelpButton;
    private JButton miningFucntionHelpButton;
    private JButton miningFucntionParamsHelpButton;
    
    private JButton miningFunctionParamsHelpButtonFollowing;
    
    private JLabel datasetLabel;
    private JLabel functionLabel;
    private JLabel dvLabel;
    private JLabel gapLabel;
    private JLabel thresGapLabel;
    
    private JLabel instructionLabel;
    private JLabel numRoundLabel;
    private JLabel distThresLabel;
    private JLabel stats;
    /*Fei*/
    private JLabel interpLabel;
    private JLabel dMaxThresLabel;
    private JLabel lMaxThresLabel;
    private JLabel minIntervalLengthLabel;
    private JTextField dMaxLabelTextedField;
    private JTextField lMaxLabelTextedField;
    private JTextField minIntervalLengthField;
    private JButton saveKmlButtonF;
    private double aveSampling;
    /**/
    
    private JButton selectIndividualsButton;
    private JLabel selectStartTimeLabel;
    private JLabel selectEndTimeLabel;
    private JXDatePicker selectStartTimePicker;
    private JXDatePicker selectEndTimePicker;
    private JButton saveKmlButton;
    private JTextField distThresTextedField;
    private JProgressBar downloadDatasetProgressBar;
    private UIComboOptions comboOptions;
    private DataAccessObject dao;
    private String curSelectedDatasetName;
    private Hashtable<String, Trajectory> trajsMap;
    private int[] selectedIndividualIndicesForDv;
    private static Kryo kryo;
    private DownloadDatasetTask downloadTask;
    private String[] individualNames;
    private Hashtable<String, Integer> nameMapId;

    public ParametersSelectionFrame(DataAccessObject dao) {
        kryo = new Kryo();
        kryo.register(Point.class);
        kryo.register(Trajectory.class);
        kryo.register(List.class);
        kryo.register(ArrayList.class);

        this.dao = dao;
        setSize(MAIN_WIDTH, MAIN_HEIGHT);
        setTitle("MoveMine 2.0");
        setResizable(true);
        curSelectedDatasetName = "";
        try {
            initComponents();
            initDatasetSelectionPanel();
            initSelectIndividualsPanel();
            initSelectTimespanPanel();
            initDisplayStatsPanel();
            initInterpolationParametersPanel();
            initMiningFunctionsSelectionPanel();
            initMiningFunctionParametersSelectionPanel();
            initDisplayInstructionPanel();
            initPerformancePanel();
            addSubPanelsToMain();
        } catch (Exception ex) {
            Logger.getLogger(ParametersSelectionFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            downloadDatasetProgressBar.setValue(progress);
        }
    }

    public static boolean isFileDownloaded(String datasetName) {
        try {
            String fn = getfileName(datasetName);
            return isfileExists(fn);
        } catch (Exception e) {
            return false;
        }
    }

    class DownloadDatasetTask extends SwingWorker<Void, Void> {

        private String selectedDatasetName;
        private boolean donwloadAgain;

        public DownloadDatasetTask(String selectedDatasetName, boolean donwloadAgain) {
            this.selectedDatasetName = selectedDatasetName;
            this.donwloadAgain = donwloadAgain;
        }

        public DownloadDatasetTask(String selectedDatasetName) {
            this.selectedDatasetName = selectedDatasetName;
            donwloadAgain = false;
        }

        @Override
        protected Void doInBackground() throws Exception {
            downloadDatasetProgressPanel.setVisible(true);
            String fn;
            try {
                fn = getfileName(selectedDatasetName);
                // first time, need to do interpolation
                curSelectedDatasetName = selectedDatasetName;
                if (!isfileExists(fn) || donwloadAgain) {
                    MovebankRestClient client = dao.getClient();

                    trajsMap = new Hashtable<String, Trajectory>();
                    // Define rest call for retrieving a list of studies:
                    RequestBuilderStudy requestBuilderStudy = new RequestBuilderStudy();

                    // Set filter criterion for finding studies:
                    requestBuilderStudy.setName(curSelectedDatasetName);
                    List<Record> studies = client.readAll(requestBuilderStudy);

                    // Assumes that a study was found:
                    Record study = studies.get(0);
                    // System.out.println(study.getValue(Constants.Attributes.NAME));
                    String studyId = study.getValue(Constants.Attributes.ID);

                    // Utility class for accessing study metadata:
                    StudyBrowser studyBrowser = new StudyBrowser(studyId, client);

                    // Utility class for accessing static data:
                    StaticDataBrowser staticDataBrowser = new StaticDataBrowser(client);
                    
                    // Sensor types are, e.g., GPS, Radio Telemetry etc.
                    for (String sensorTypeId : studyBrowser.getSensorTypeIds()) {
                        // Resolve the name of a sensor type for display:
                        final String sensorTypeName = staticDataBrowser.getSensorTypeById(
                                sensorTypeId).getStringValue(Constants.Attributes.NAME);
                        System.out.println(sensorTypeName);
                        //System.out.println(sensorTypeName);
                        //if (sensorTypeName.equals("GPS") || sensorTypeName.equals("Radio Transmitter")) {
                        if (sensorTypeName.equals("GPS")) {
                            // Define rest call for retrieving a list of animals within a study:
                            RequestBuilderIndividual requestBuilderIndividual = new RequestBuilderIndividual(
                                    studyId);
                            List<Record> individuals = client.readAll(requestBuilderIndividual);
                            stats.setText("Downloading individuals from sensor type: " + sensorTypeName);
                            int progress = 0;
                            setProgress(0);
                            int total = individuals.size();                            
                            // Loop over animals:
                            for (Record individual : individuals) {
                                final String individualId = individual
                                        .getStringValue(Constants.Attributes.ID);
                                final String individualLocalIdentifier = individual
                                        .getStringValue(Constants.Attributes.LOCAL_IDENTIFIER);
                                // Define rest call for retrieving tracking data:
                                RequestBuilderEvent requestBuilderEvent = studyBrowser
                                        .getRequestBuilderEvent(sensorTypeId);

                                // Restrict tracking data to one animal:
                                requestBuilderEvent.setIndividualId(individualId);

                                // Restrict list of fields to be downloaded:
                                requestBuilderEvent.setSelectAttributes(
                                        Constants.Attributes.TIMESTAMP,
                                        Constants.Attributes.LOCATION_LONG,
                                        Constants.Attributes.LOCATION_LAT);                                
                                client.sendRequest(requestBuilderEvent,
                                        new RecordCallbackDefault() {
                                            @Override
                                            protected void record() throws Exception {                                                  
                                               Point p = new Point();
                                                p.setId(individualLocalIdentifier);
                                                String dateValue = getValue(Constants.Attributes.TIMESTAMP);
                                                DateTime dt = tryParseDate(dateValue);
                                                if (dt != null) {
                                                    p.setTime(dt);

                                                } else {
                                                    throw new IllegalArgumentException(
                                                            "Received Illegel date format: "
                                                            + dateValue);
                                                }
                                                String x = getValue(Constants.Attributes.LOCATION_LONG);
                                                String y = getValue(Constants.Attributes.LOCATION_LAT);
                                                if (tryParseDouble(x) && tryParseDouble(y)) {
                                                    p.setX(Double.parseDouble(x));
                                                    p.setY(Double.parseDouble(y));
                                                    p.setValid(true);
                                                } else {
                                                    p.setValid(false);
                                                }
//                                                System.out.println(p.getId()+"\t"+p.getTime() +"\t"+p.getX()+"\t"+p.getY());
                                                if(p.isValid()){
                                                    // add the trajectory to corresponding
                                                    // trajectory based on its Id
                                                    if (trajsMap
                                                            .containsKey(individualLocalIdentifier)) {
                                                        trajsMap.get(individualLocalIdentifier)
                                                                .addPoint(p);
                                                        trajsMap.get(individualLocalIdentifier).updateBoxRange(p);
                                                    } else {
                                                        Trajectory traj = new Trajectory();
                                                        traj.addPoint(p);
                                                        if (p.isValid()) {
                                                            traj.setBoxRange(p.getX(), p.getX(), p.getY(), p.getY());
                                                        }
                                                        traj.setId(individualLocalIdentifier);
                                                        trajsMap.put(individualLocalIdentifier,
                                                                traj);
                                                    }
                                                }
                                            }
                                        });
                                progress++;
                                int update = (int) (((float) progress) / total * 100);
                                setProgress(update);
                            }
                        }
                    }

                    ArrayList<Trajectory> trajs = new ArrayList(trajsMap.values());
                    if (trajs.isEmpty()) {
                        stats.setText("No trajectories are found in this dataset!");
                    } else {
                        Dataset dataset = new Dataset();
                        dataset.storeData(trajs, trajsMap);
                        dataset.setDatasetName(curSelectedDatasetName);
                        initIndividualNames();
                        stats.setText("Serializing data set ...");
                        serializeDataset(fn, dataset);
                        downloadDatasetProgressPanel.setVisible(false);
                        if (dataset.getNumTrajectory() > 1) {
                            showStats(dataset);
                            initSelectTimespanRange(dataset.getStartTime(), dataset.getEndTime());
                            showPanel(interpolationParamsSelectionPanel, INTERPOLATE_PARAMS_PANEL_TITLE);
                            showPanel(selectIndividualsPanel);
                            showPanel(selectTimespanPanel);
                        } else {
                            showStatsOnly(dataset);
                        }
                    }
                } else {
                    stats.setText(
                            "Deserialize dataset ...");
                    Dataset dataset = deserializeDataset(fn);
                    downloadDatasetProgressPanel.setVisible(false);
                    if (dataset.getNumTrajectory() > 1) {
                        showStats(dataset);
                        initSelectTimespanRange(dataset.getStartTime(), dataset.getEndTime());
                        showPanel(interpolationParamsSelectionPanel, INTERPOLATE_PARAMS_PANEL_TITLE);
                        showPanel(selectIndividualsPanel);
                        showPanel(selectTimespanPanel);
                    } else {
                        showStatsOnly(dataset);
                    }
                    trajsMap = dataset.getTrajsMap();
                    initIndividualNames();
                }
            } catch (Exception ex) {
                Logger.getLogger(ParametersSelectionFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        @Override
        public void done() {
            datasetCombo.setEnabled(true);
            setCursor(null); //turn off the wait cursor
            useLocalCopyButton.setEnabled(true);
            downloadOnlineButton.setEnabled(true);
            loadDatasetButton.setEnabled(true);
        }
    }

    private void initComponents() throws Exception {
        comboOptions = new UIComboOptions();
        if (!dao.isGuestMode()) {
            List<String> datasetNames = dao.getDatasetNames();
            // create inputs fields
            datasetCombo = new JComboBox(
                    datasetNames.toArray(new String[datasetNames.size()]));
            datasetCombo.setPrototypeDisplayValue("White-faced capuchin, ARTS Crofoot Barro Colorado Island, Panama");
            datasetCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index,
                            isSelected, cellHasFocus);
                    if (index == -1) {
                        datasetCombo.setToolTipText(value.toString());
                        return this;
                    }

                    setToolTipText(value.toString());
                    Rectangle textRect =
                            new Rectangle(datasetCombo.getSize().width,
                            getPreferredSize().height);
                    String shortText = SwingUtilities.layoutCompoundLabel(this,
                            getFontMetrics(getFont()),
                            value.toString(), null,
                            getVerticalAlignment(), getHorizontalAlignment(),
                            getHorizontalTextPosition(), getVerticalTextPosition(),
                            textRect, new Rectangle(), textRect,
                            getIconTextGap());
                    setText(shortText);
                    return this;
                }
            });
            AutoCompleteDecorator.decorate(datasetCombo);
            datasetLabel = new JLabel("Dataset: ");
        }
        /*Fei*/
        this.dMaxThresLabel= new JLabel("Dist thres(meters): ");
        this.lMaxThresLabel= new JLabel("Time thres(seconds): ");
        this.minIntervalLengthLabel = new JLabel("Min Interval Length(seconds): ");
        this.dMaxLabelTextedField = new JTextField(5);
        this.lMaxLabelTextedField = new JTextField(5);
        this.minIntervalLengthField = new JTextField(5);
        /**/
        functionCombo = new JComboBox(
                comboOptions.getFunctionOptions());
        displayMethodCombo = new JComboBox(
                comboOptions.getDvOption());
        gapCombo = new JComboBox(
                comboOptions.getGapOption());
        thresGapCombo = new JComboBox(
                comboOptions.getThresGapOption());
        numRoundCombo = new JComboBox(
                comboOptions.getNumRoundOption());
        functionLabel = new JLabel("Mining function: ");
        dvLabel       = new JLabel("Display method: ");
        gapLabel      = new JLabel("Gap(min): ");
        thresGapLabel = new JLabel("ThresGap(hr): ");
        numRoundLabel = new JLabel("# Rounds: ");
        instructionLabel = new JLabel("Please select a dataset and some parameters");
        distThresLabel   = new JLabel("Dist thres(meters): ");
        this.interpLabel = new JLabel("Interpolate: "); 
        distThresTextedField = new JTextField(5);
        downloadDatasetProgressBar = new JProgressBar(0, 100);
        downloadDatasetProgressBar.setValue(0);
        downloadDatasetProgressBar.setStringPainted(true);
        dataInterpolationHelpButton = new JButton("?");
        miningFucntionHelpButton = new JButton("?");
        miningFucntionParamsHelpButton = new JButton("?");
        this.miningFunctionParamsHelpButtonFollowing = new JButton("?");
        selectIndividualsButton = new JButton("Select individuals");
        selectIndividualsButton.addActionListener(this);
        saveKmlButton = new JButton("Get KML file");
        saveKmlButton.addActionListener(this);
        saveDensityMapButton = new JButton("Get density map");
        saveDensityMapButton.addActionListener(this);
        
        
        
    }

    private void initDatasetSelectionPanel() {
        datasetSelectionPanel = new JPanel();
        datasetSelectionPanel.setPreferredSize(
                new Dimension(DATA_SELECTION_PANEL_WIDTH,
                DATA_SELECTION_PANEL_HEIGHT));
        datasetSelectionPanel.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Data Selection"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        if (!dao.isGuestMode()) {
            datasetSelectionPanel.add(datasetLabel);
            datasetSelectionPanel.add(datasetCombo);

            datasetCombo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    hidePanel(interpolationParamsSelectionPanel);
                    hidePanel(miningFunctionSelectionPanel);
                    hidePanel(miningFunctionParamsSelectionPanel);
                    hidePanel(displayInstructionPanel);
                    hidePanel(performancePanel);
                    hidePanel(displayStatsPanel);
                    hidePanel(selectIndividualsPanel);
                    hidePanel(selectTimespanPanel);

                    useLocalCopyButton.setEnabled(true);
                    downloadOnlineButton.setEnabled(true);
                    instructionLabel.setText("");
                    String selectedDataset = (String) datasetCombo.getSelectedItem();
                    if (isFileDownloaded(selectedDataset)) {
                        useLocalCopyButton.setVisible(true);
                    } else {
                        useLocalCopyButton.setVisible(false);
                    }
                }
            });

            useLocalCopyButton = new JButton("Use local copy");
            downloadOnlineButton = new JButton("Download from server");
            useLocalCopyButton.setVisible(false);
            datasetSelectionPanel.add(useLocalCopyButton);
            datasetSelectionPanel.add(downloadOnlineButton);

            useLocalCopyButton.addActionListener(this);
            downloadOnlineButton.addActionListener(this);

        }
        functionCombo.addActionListener(this);
        loadDatasetButton = new JButton("Load local dataset");
        datasetSelectionPanel.add(loadDatasetButton);
        loadDatasetButton.addActionListener(this);
    }

    private void initSelectIndividualsPanel() {
        selectIndividualsPanel = new JPanel();
        selectIndividualsPanel.setPreferredSize(
                new Dimension(SELECT_IND_PANEL_WIDTH,
                SELECT_IND_PANEL_HEIGHT));
        selectIndividualsPanel.add(selectIndividualsButton);
        selectIndividualsPanel.setVisible(false);
    }

    private void initSelectTimespanPanel() {
        selectTimespanPanel = new JPanel();
        selectTimespanPanel.setPreferredSize(
                new Dimension(SELECT_TIMESPAN_PANEL_WIDTH,
                SELECT_TIMESPAN_PANEL_HEIGHT));
        selectStartTimeLabel = new JLabel("Start time: ");
        selectEndTimeLabel = new JLabel("End time: ");
    }

    private void updateTimeSpanRange(List<Trajectory> trajs) {
        Date start;
        Date end;
        if (trajs.size() > 0) {
            start = trajs.get(0).getPoint(0).getTime().toDate();
            int size = trajs.get(0).getPointsNum();
            end = trajs.get(0).getPoint(size - 1).getTime().toDate();
            for (int i = 1; i < trajs.size(); i++) {
                List<Point> points = trajs.get(i).getPoints();
                if (points.get(0).getTime().toDate().compareTo(start) < 0) {
                    start = points.get(0).getTime().toDate();
                }
                if (points.get(points.size() - 1).getTime().toDate().compareTo(end) > 0) {
                    end = points.get(points.size() - 1).getTime().toDate();
                }
            }
        } else {
            throw new IllegalArgumentException("trajs.size == 0");
        }

        System.out.println("** Update time span to: " + start + " to " + end);

        selectStartTimePicker.getMonthView().setLowerBound(start);
        selectStartTimePicker.getMonthView().setUpperBound(end);
        selectEndTimePicker.getMonthView().setLowerBound(start);
        selectEndTimePicker.getMonthView().setUpperBound(end);
        selectStartTimePicker.setDate(start);
        selectEndTimePicker.setDate(end);
    }

    private void initSelectTimespanRange(Date start, Date end) {
        if (selectTimespanPanel != null) {
            selectTimespanPanel.removeAll();
        }
        selectStartTimePicker = new JXDatePicker(start);
        selectEndTimePicker = new JXDatePicker(end);
        selectStartTimePicker.getMonthView().setLowerBound(start);
        selectStartTimePicker.getMonthView().setUpperBound(end);
        selectEndTimePicker.getMonthView().setLowerBound(start);
        selectEndTimePicker.getMonthView().setUpperBound(end);
        selectTimespanPanel.add(selectStartTimeLabel);
        selectTimespanPanel.add(selectStartTimePicker);
        selectTimespanPanel.add(selectEndTimeLabel);
        selectTimespanPanel.add(selectEndTimePicker);
    }

    private void initDisplayStatsPanel() {
        displayStatsPanel = new JPanel();
        displayStatsPanel.setPreferredSize(
                new Dimension(DISPLAY_STATS_PANEL_WIDTH,
                DISPLAY_STATS_PANEL_HEIGHT));
        displayStatsPanel.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(DISPLAY_STATS_PANEL_TITLE),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        stats = new JLabel();
        downloadDatasetProgressPanel = new JPanel();
        downloadDatasetProgressPanel.setPreferredSize(
                new Dimension(DOWNLOAD_PROGRESS_PANEL_WIDTH,
                DOWNLOAD_PROGRESS_PANEL_HEIGHT));
        downloadDatasetProgressPanel.add(downloadDatasetProgressBar);
        displayStatsPanel.add(downloadDatasetProgressPanel);
        displayStatsPanel.add(stats);
        displayStatsPanel.add(selectIndividualsPanel);
        displayStatsPanel.add(selectTimespanPanel);
        hidePanel(displayStatsPanel);
    }

    private void initInterpolationParametersPanel() {
        interpolationParamsSelectionPanel = new JPanel();
        interpolationParamsSelectionPanel.setPreferredSize(
                new Dimension(INTERPOLATE_PARAMS_PANEL_WIDTH,
                INTERPOLATE_PARAMS_PANEL_HEIGHT));
        this.interpBox = new JCheckBox("Enable");
        this.interpBox.setSelected(true);
        this.interpBox.addActionListener( 
            new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                   if(interpBox.isSelected()){
                       interpFlag = true;
                       gapCombo.setEnabled(true);
                       thresGapCombo.setEnabled(true);
                   }else{
                       interpFlag = false;
                       gapCombo.setEnabled(false);
                       thresGapCombo.setEnabled(false);
                   }
                }                    
            }
                
        );
        
        //this.interpBox.addChangeListener(null);
       // interpolationParamsSelectionPanel.add(this.interpLabel);
        interpolationParamsSelectionPanel.add(this.interpBox);
        interpolationParamsSelectionPanel.add(gapLabel);
        interpolationParamsSelectionPanel.add(gapCombo);
        
        interpolationParamsSelectionPanel.add(thresGapLabel);
        interpolationParamsSelectionPanel.add(thresGapCombo);

        

        interpolationNextButton = new JButton("Next");
        interpolationParamsSelectionPanel.add(interpolationNextButton);
        interpolationParamsSelectionPanel.add(dataInterpolationHelpButton);

        interpolationNextButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showPanel(miningFunctionSelectionPanel, MINING_FUNC_SELECTION_PANEL_TITLE);
                        showPanel(displayInstructionPanel);
                    }
                });

        dataInterpolationHelpButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String workingDir = System.getProperty("user.dir").replace("\\", "/");
                            
                            Desktop.getDesktop().browse(new URI("file://"+workingDir+"/index.html#interpolate"));
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(ParametersSelectionFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(ParametersSelectionFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
        hidePanel(interpolationParamsSelectionPanel);
    }

    private void initMiningFunctionsSelectionPanel() {
        miningFunctionSelectionPanel = new JPanel();
        miningFunctionSelectionPanel.setPreferredSize(
                new Dimension(MINING_FUNC_SELECTION_PANEL_WIDTH,
                MINING_FUNC_SELECTION_PANEL_HEIGHT));
        miningFunctionSelectionPanel.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Mining Function"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        miningFunctionSelectionPanel.add(functionLabel);
        miningFunctionSelectionPanel.add(functionCombo);
        miningFunctionSelectionPanel.add(miningFucntionHelpButton);
        miningFucntionHelpButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String workingDir = System.getProperty("user.dir").replace("\\", "/");
                            
                            Desktop.getDesktop().browse(new URI("file://"+workingDir+"/index.html#functions"));
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(ParametersSelectionFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(ParametersSelectionFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
        hidePanel(miningFunctionSelectionPanel);
    }

    private void initMiningFunctionParametersSelectionPanel() {
        miningFunctionParamsSelectionPanel = new JPanel();
        sigLevelParamsPanel  = new JPanel();
        plotMapParamsPanel   = new JPanel();
       
        plotDensityMapPanel  = new JPanel();
        plotDensityMapPanel.setPreferredSize(
                new Dimension(PLOT_MAP_PARAMS_PANEL_WIDTH,
                PLOT_MAP_PARAMS_PANEL_HEIGHT));
        /*Fei*/
        followingParamsPanel = new JPanel();
        followingParamsPanel.setPreferredSize(
                new Dimension(FOLLOWING_PARAMS_PANEL_WIDTH,
                FOLLOWING_PARAMS_PANEL_HEIGHT));
        /**/
        miningFunctionParamsSelectionPanel.setPreferredSize(
                new Dimension(MINING_FUNC_PARAMS_SELECTION_PANEL_WIDTH,
                MINING_FUNC_PARAMS_SELECTION_PANEL_HEIGHT));
        sigLevelParamsPanel.setPreferredSize(
                new Dimension(SIG_LEVEL_PARAMS_PANEL_WIDTH,
                SIG_LEVEL_PARAMS_PANEL_HEIGHT));
        plotMapParamsPanel.setPreferredSize(
                new Dimension(PLOT_MAP_PARAMS_PANEL_WIDTH,
                PLOT_MAP_PARAMS_PANEL_HEIGHT));
        miningFunctionParamsSelectionPanel.setBorder(
                BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Mining Function Parameters"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // add sigLevel params comps to its panel
        sigLevelParamsPanel.add(numRoundLabel);
        sigLevelParamsPanel.add(numRoundCombo);
        sigLevelParamsPanel.add(distThresLabel);
        distThresTextedField.setText(Double.toString(DEFAULT_DIST_THRES_ATT));
        sigLevelParamsPanel.add(distThresTextedField);
        sigLevelParamsPanel.add(miningFucntionParamsHelpButton);
        sigLevelParamsPanel.setVisible(false);

        // add plot map params comps to its panel
        plotMapParamsPanel.add(saveKmlButton);
        plotMapParamsPanel.setVisible(false);
        /*Fei*/
        // add following params comps to its panel
        followingParamsPanel.add(this.lMaxThresLabel);
        followingParamsPanel.add(this.lMaxLabelTextedField);

        this.dMaxLabelTextedField.setText(Double.toString(DEFAULT_DIST_THRES));
        this.lMaxLabelTextedField.setText(Double.toString(DEFAULT_TIME_THRES));
        this.minIntervalLengthField.setText(Double.toString(DEFAULT_MIN_LENGTH));
        followingParamsPanel.add(this.dMaxThresLabel);
        followingParamsPanel.add(this.dMaxLabelTextedField);
        followingParamsPanel.add(this.minIntervalLengthLabel);
        followingParamsPanel.add(this.minIntervalLengthField);
        followingParamsPanel.add(this.miningFunctionParamsHelpButtonFollowing);
        followingParamsPanel.setVisible(false);
        miningFunctionParamsSelectionPanel.add(followingParamsPanel);
        
        
        plotDensityMapPanel.add(saveDensityMapButton);
        plotDensityMapPanel.setVisible(false);
        miningFunctionParamsSelectionPanel.add(plotDensityMapPanel);
                
        this.miningFunctionParamsHelpButtonFollowing.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String workingDir = System.getProperty("user.dir").replace("\\", "/");
                            
                            Desktop.getDesktop().browse(new URI("file://"+workingDir+"/index.html#following"));
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(ParametersSelectionFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(ParametersSelectionFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
          
                    }
                });
        /**/

        miningFunctionParamsSelectionPanel.add(sigLevelParamsPanel);
        miningFunctionParamsSelectionPanel.add(plotMapParamsPanel);
     /*   miningFucntionParamsHelpButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFrame helpTextFrame = new JFrame("Mining Function Parameters Help");
                        helpTextFrame.setSize(HELP_TEXT_JFRAME_WIDTH, HELP_TEXT_JFRAME_HEIGHT);
                        JTextArea text = new JTextArea();
                        text.setEditable(false);
                        text.setCursor(null);
                        text.setOpaque(false);
                        text.setFocusable(false);
                        text.setLineWrap(true);
                        text.setWrapStyleWord(true);
                        text.setPreferredSize(
                                new Dimension(HELP_TEXT_JFRAME_WIDTH - 20,
                                HELP_TEXT_JFRAME_HEIGHT - 20));
                        text.setText(genMiningFunctionParameterHelpText());
                        JPanel panel = new JPanel();
                        panel.setPreferredSize(
                                new Dimension(HELP_TEXT_JFRAME_WIDTH,
                                HELP_TEXT_JFRAME_HEIGHT));
                        panel.add(text);
                        helpTextFrame.add(panel);
                        helpTextFrame.setVisible(true);
                        panel.setPreferredSize(
                                new Dimension(HELP_TEXT_JFRAME_WIDTH,
                                HELP_TEXT_JFRAME_HEIGHT));
                        panel.add(text);
                        helpTextFrame.add(panel);
                        helpTextFrame.setVisible(true);
                    }
                });*/
        
               miningFucntionParamsHelpButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            //JFrame help = new Browser();
                            // help.setLocation(this.getX() + this.getWidth(), this.getY());
                            // help.setVisible(true);
                            //javafxPanel.setScene(scene);
                            String workingDir = System.getProperty("user.dir").replace("\\", "/");
                            
                            Desktop.getDesktop().browse(new URI("file://"+workingDir+"/index.html#significance"));
                            // long spentTime2 = 1;
                            
                            
                            
                            //javafxPanel.setScene(scene);
                            
                            //javafxPanel.setVisible(true);
                        } catch (IOException ex) {
                            Logger.getLogger(ParametersSelectionFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(ParametersSelectionFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
        hidePanel(miningFunctionParamsSelectionPanel);
    }

    private void initDisplayInstructionPanel() {
        displayInstructionPanel = new JPanel();
        displayInstructionPanel.setPreferredSize(
                new Dimension(DISPLAY_INSTRUCTION_PANEL_WIDTH,
                DISPLAY_INSTRUCTION_PANEL_HEIGHT));
        goButton = new JButton("Go");
        this.saveKmlButtonF = new JButton("Save intervals as kml files");
        this.displayInstructionPanel.add(this.saveKmlButtonF);
        displayInstructionPanel.add(goButton);
        this.saveKmlButtonF.addActionListener(this);
        this.saveKmlButtonF.setVisible(false);
        goButton.addActionListener(this);
        hidePanel(displayInstructionPanel);
    }

    private void initPerformancePanel() {
        performancePanel = new JPanel();
        performancePanel.setPreferredSize(
                new Dimension(DISPLAY_PERFORMANCE_PANEL_WIDTH,
                DISPLAY_PERFORMANCE_PANEL_HEIGHT));
        hidePanel(performancePanel);
    }

    private void addSubPanelsToMain() {
        contentPane = getContentPane();
        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(MAIN_WIDTH, MAIN_HEIGHT));
        mainPanel.add(instructionLabel);
        mainPanel.add(datasetSelectionPanel, "Center");
        mainPanel.add(displayStatsPanel, "Center");
        mainPanel.add(interpolationParamsSelectionPanel, "Center");
        mainPanel.add(miningFunctionSelectionPanel, "Center");
        mainPanel.add(miningFunctionParamsSelectionPanel, "Center");
        mainPanel.add(displayInstructionPanel, "Center");
        mainPanel.add(performancePanel, "Center");
        contentPane.add(mainPanel, "North");
    }

    private void showPanel(JPanel panel) {
        if (panel != null) {
            panel.setVisible(true);
        }
    }

    private void showPanel(JPanel panel, String title) {
        if (panel != null) {
            panel.setBorder(
                    BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(title),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            panel.setVisible(true);
        }
    }

    private void hidePanel(JPanel panel) {
        if (panel != null) {
            panel.setBorder(null);
            panel.setVisible(false);

            if (miningFunctionSelectionPanel != null && panel == miningFunctionSelectionPanel) {
                if (functionCombo != null) {
                    functionCombo.setSelectedIndex(0);
                }
            }
        }
    }

    private void serializeDataset(String fn, Dataset dataset) throws FileNotFoundException {
        Output output = new Output(new FileOutputStream("dataset/" + fn + ".data"));
        kryo.writeObject(output, dataset);
        output.close();
    }

    private Dataset deserializeDataset(String fn) throws FileNotFoundException {
        Input input = new Input(new FileInputStream("dataset/" + fn + ".data"));
        Dataset dataset = kryo.readObject(input, Dataset.class);
        input.close();
        return dataset;
    }

    private static boolean isfileExists(String fn) {
        File file = new File("dataset/" + fn + ".data");
        return file.exists();
    }

    private static String getfileName(String datasetName) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        StringBuilder builder = new StringBuilder()
                .append(datasetName);
        return DigestUtils.sha1Hex(builder.toString());
    }

    private void showStats(Dataset dataset) {
        StringBuilder builder = new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("d MMM, yyyy");
        DecimalFormat doubleFormatter = new DecimalFormat("#.##");
        builder.append("<html>Data set name: ");
        builder.append(dataset.getDatasetName());
        builder.append("<br/>Total number of individuals: ");
        builder.append(dataset.getNumTrajectory());
        builder.append("<br/>Timespan: ");
        builder.append(formatter.format(dataset.getStartTime()));
        builder.append(" to ");
        builder.append(formatter.format(dataset.getEndTime()));
        builder.append("<br/>Sampling rate: ");
        this.aveSampling = dataset.getSampleRate()/60; // in mins
        builder.append(doubleFormatter.format(dataset.getSampleRate()/60));
        builder.append(" min(s) ");
      //  builder.append("<br/>Sampling Standard Deviation: ");
      //  builder.append(doubleFormatter.format(dataset.getSampleVariance()));
        builder.append("</html>");
        stats.setText(builder.toString());
        showPanel(selectIndividualsPanel);
        showPanel(selectTimespanPanel);
    }

    private void showStatsOnly(Dataset dataset) {
        StringBuilder builder = new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy");
        DecimalFormat doubleFormatter = new DecimalFormat("#.##");
        builder.append("<html>Data set name: ");
        builder.append(dataset.getDatasetName());
        builder.append("<br/>Total number of individuals: ");
        builder.append(dataset.getNumTrajectory());
        builder.append("<br/>Timespan: ");
        builder.append(formatter.format(dataset.getStartTime()));
        builder.append(" to ");
        builder.append(formatter.format(dataset.getEndTime()));
        builder.append("<br/>Sampling rate: ");
        builder.append(doubleFormatter.format(dataset.getSampleRate()/60));
        builder.append(" min(s) <br/> No operations can be applied on this data set.</html>");
        builder.append("<br/>Sampling rate (variance) : ");
        stats.setText(builder.toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String selectedFunction = (String) functionCombo.getSelectedItem();
        if (e.getSource() == functionCombo) {
            if (selectedFunction.equals(comboOptions.getFunctionOptions()[0])) {
                // show necessary input arguments for computing distance matrix
                showPanel(displayInstructionPanel);
                hidePanel(performancePanel);
                hidePanel(miningFunctionParamsSelectionPanel);
                hidePanel(plotDensityMapPanel);
            } else if (selectedFunction.equals(comboOptions.getFunctionOptions()[1])) {
                // show necessary input arguments for computing sigLevel
                showPanel(miningFunctionParamsSelectionPanel, MINING_FUNC_PARAMS_SELECTION_PANEL_TITLE);
                showPanel(sigLevelParamsPanel);
                showPanel(displayInstructionPanel);
                hidePanel(plotMapParamsPanel);
                hidePanel(performancePanel);
                hidePanel(this.followingParamsPanel);
                hidePanel(this.plotDensityMapPanel);
            } else if (selectedFunction.equals(comboOptions.getFunctionOptions()[2])) {
                // show necessary input arguments for ploting a map
                showPanel(miningFunctionParamsSelectionPanel, MINING_FUNC_PARAMS_SELECTION_PANEL_TITLE);
                showPanel(plotMapParamsPanel);
                hidePanel(sigLevelParamsPanel);
                hidePanel(performancePanel);
                hidePanel(displayInstructionPanel);
                hidePanel(this.followingParamsPanel);
                hidePanel(this.plotDensityMapPanel);
            } else if (selectedFunction.equals(comboOptions.getFunctionOptions()[3])) {
                showPanel(miningFunctionParamsSelectionPanel, MINING_FUNC_PARAMS_SELECTION_PANEL_TITLE);
                showPanel(this.followingParamsPanel);
                showPanel(displayInstructionPanel);
                hidePanel(plotMapParamsPanel);
                hidePanel(sigLevelParamsPanel);
                hidePanel(performancePanel);      
                hidePanel(this.plotDensityMapPanel);
            } else if (selectedFunction.equals(comboOptions.getFunctionOptions()[4])){
                showPanel(miningFunctionParamsSelectionPanel, MINING_FUNC_PARAMS_SELECTION_PANEL_TITLE);
                showPanel(this.plotDensityMapPanel);
                hidePanel(this.followingParamsPanel);
                hidePanel(displayInstructionPanel);
                hidePanel(plotMapParamsPanel);
                hidePanel(sigLevelParamsPanel);
                hidePanel(performancePanel);          
            }
        }

        if (e.getSource() == useLocalCopyButton) {
            useLocalCopyButton.setEnabled(false);
            loadDatasetButton.setEnabled(false);
            downloadOnlineButton.setEnabled(false);
            stats.setText("");
            showPanel(downloadDatasetProgressPanel);
            datasetCombo.setEnabled(false);
            downloadDatasetProgressBar.setValue(0);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            showPanel(selectTimespanPanel);
            showPanel(displayStatsPanel, DISPLAY_STATS_PANEL_TITLE);
            stats.setText("Fetching License Terms from server ...");
            String selectedDatasetName = (String) datasetCombo.getSelectedItem();
            downloadTask = new DownloadDatasetTask(selectedDatasetName);
            downloadTask.addPropertyChangeListener(this);
            downloadTask.execute();
        }

        if (e.getSource() == downloadOnlineButton) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            useLocalCopyButton.setEnabled(false);
            loadDatasetButton.setEnabled(false);
            downloadOnlineButton.setEnabled(false);
            hidePanel(interpolationParamsSelectionPanel);
            hidePanel(miningFunctionSelectionPanel);
            hidePanel(miningFunctionParamsSelectionPanel);
            hidePanel(displayInstructionPanel);
            hidePanel(performancePanel);
            hidePanel(displayStatsPanel);
            hidePanel(selectIndividualsPanel);
            hidePanel(selectTimespanPanel);

            stats.setText("");

            showPanel(downloadDatasetProgressPanel);
            showPanel(displayStatsPanel, DISPLAY_STATS_PANEL_TITLE);
            instructionLabel.setText("");
            datasetCombo.setEnabled(false);
            downloadDatasetProgressBar.setValue(0);

            stats.setText("Fetching License Terms from server ...");
            String selectedDatasetName = (String) datasetCombo.getSelectedItem();
            downloadTask = new DownloadDatasetTask(selectedDatasetName, true);
            downloadTask.addPropertyChangeListener(this);
            downloadTask.execute();
        }
        
    
        if (e.getSource() == loadDatasetButton) {

            if (useLocalCopyButton != null) {
                useLocalCopyButton.setEnabled(false);
            }
            if (downloadOnlineButton != null) {
                downloadOnlineButton.setEnabled(false);
            }
            loadDatasetButton.setEnabled(false);

            JFileChooser fc = new JFileChooser();
            String workingDir = System.getProperty("user.dir");
            fc.setCurrentDirectory(new File(workingDir));
            int returnVal = fc.showOpenDialog(ParametersSelectionFrame.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                final File file = fc.getSelectedFile();
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                System.out.println("Opening " + file.getName());
                downloadDatasetProgressBar.setVisible(true);
                downloadDatasetProgressBar.setIndeterminate(true);
                downloadDatasetProgressBar.setStringPainted(false);

                hidePanel(interpolationParamsSelectionPanel);
                hidePanel(miningFunctionSelectionPanel);
                hidePanel(miningFunctionParamsSelectionPanel);
                hidePanel(displayInstructionPanel);
                hidePanel(performancePanel);
                hidePanel(displayStatsPanel);
                hidePanel(selectIndividualsPanel);
                hidePanel(selectTimespanPanel);

                stats.setText("");

                showPanel(downloadDatasetProgressPanel);
                showPanel(displayStatsPanel, DISPLAY_STATS_PANEL_TITLE);
                instructionLabel.setText("");

                LoadDatasetTask loadDatasetTask = new LoadDatasetTask(file.getAbsolutePath()) {
                    @Override
                    public void done() {
                        if (this.isFailToParse()) {
                            stats.setText(this.getErrorMsg());
                            downloadDatasetProgressPanel.setVisible(false);
                            hidePanel(interpolationParamsSelectionPanel);
                            hidePanel(miningFunctionSelectionPanel);
                            hidePanel(miningFunctionParamsSelectionPanel);
                            hidePanel(displayInstructionPanel);
                            hidePanel(performancePanel);
                            hidePanel(selectIndividualsPanel);
                            hidePanel(selectTimespanPanel);
                        } else {
                            trajsMap = this.getTrajsMap();
                            ArrayList<Trajectory> trajs = new ArrayList(trajsMap.values());

                            Dataset dataset = new Dataset();
                            dataset.storeData(trajs, trajsMap);
                            dataset.setDatasetName(file.getName());
                            curSelectedDatasetName = file.getName();
                            initIndividualNames();
                            downloadDatasetProgressPanel.setVisible(false);
                            showStats(dataset);
                            initSelectTimespanRange(dataset.getStartTime(), dataset.getEndTime());

                            showPanel(selectTimespanPanel);
                            showPanel(interpolationParamsSelectionPanel, INTERPOLATE_PARAMS_PANEL_TITLE);
                            showPanel(selectIndividualsPanel);
                            showPanel(selectTimespanPanel);


                            setCursor(null); //turn off the wait cursor
                            downloadDatasetProgressBar.setIndeterminate(false);
                            downloadDatasetProgressBar.setStringPainted(true);
                        }
                    }
                };
                loadDatasetTask.addPropertyChangeListener(this);
                loadDatasetTask.execute();
            } else {
                System.out.println("Open command cancelled by user.");
            }
            loadDatasetButton.setEnabled(true);
            setCursor(null);
            if (useLocalCopyButton != null) {
                useLocalCopyButton.setEnabled(true);
            }
            if (downloadOnlineButton != null) {
                downloadOnlineButton.setEnabled(true);
            }
        }

        if (e.getSource() == goButton) {
            showPanel(performancePanel);
//            String selectedDatasetName = (String) datasetCombo
//                    .getSelectedItem();
            String selectedDatasetName = curSelectedDatasetName;
            instructionLabel.setText("Loading ...");
            String displayMethod = (String) displayMethodCombo
                    .getSelectedItem();
            String gap = (String) gapCombo.getSelectedItem();
            String thresGap = (String) thresGapCombo.getSelectedItem();
            String numRound = (String) numRoundCombo.getSelectedItem();
            String distThres = distThresTextedField.getText();
            
            /*Fei*/
            String ml       = (String) this.minIntervalLengthField.getText();
            String lMax     = (String) this.lMaxLabelTextedField.getText();
            String dMax     = (String) this.dMaxLabelTextedField.getText();
            /**/
            boolean needInterpolation = this.interpFlag;
            try {
                MiningFunctionParameters params;
                instructionLabel.setText("Running mining algorithm ...");
                params = new MiningFunctionParameters();
                params.setSelectedDatasetName(selectedDatasetName);
                params.setSelectedFunction(selectedFunction);
                params.setDisplayMethod(displayMethod);
                params.setNeedInterpolation(needInterpolation);
                params.setGap(gap);
                params.setThresGap(thresGap);
                params.setNumRound(numRound);
                params.setDistThres(distThres);
                /*Fei*/
                int lmax_int = (int) Math.floor(Double.parseDouble(lMax));
                params.setlMax(""+lmax_int);
                params.setdMax(dMax);
                params.setMinL(ml);
                /**/
                params.setStartTime(selectStartTimePicker.getDate());
                params.setEndTime(selectEndTimePicker.getDate());
                ArrayList<Trajectory> trajs = getSelectedTrajectories(selectedIndividualIndicesForDv);
                List<Trajectory> subTrajs = getSubTrajectory(trajs,
                        selectStartTimePicker.getDate(),
                        selectEndTimePicker.getDate());
                if(selectedFunction.equalsIgnoreCase("Following mining") && trajs.size()!=2){
                    JOptionPane.showMessageDialog(this.contentPane, "Please select two animals for following mining.");
                } else if(!selectedFunction.equalsIgnoreCase("Following mining") && needInterpolation==false){
                    JOptionPane.showMessageDialog(this.contentPane, "Selected function requires interpolation. Please check the interpolation option.");
                } else if(!selectedFunction.equalsIgnoreCase("Plotting") && trajs.size()<2){
                    JOptionPane.showMessageDialog(this.contentPane, "Please select at least two animals.");
                    
                }else{
                    boolean dothejob = false;
                    int dialogresult = JOptionPane.YES_OPTION;
                    if(this.aveSampling>=1 && selectedFunction.equalsIgnoreCase("Following mining")){
                      dialogresult =   JOptionPane.showConfirmDialog(this.contentPane, "Recommend to use dataset having higher sampling rate (less than 1 minper sample) for this function. \n Do you still want to proceed?","",JOptionPane.YES_NO_OPTION );        
                    }
                    if(dialogresult == JOptionPane.YES_OPTION) dothejob =true;
                    if(dothejob){
                    goButton.setEnabled(false);
                    disablePanel(datasetSelectionPanel);
                    disablePanel(interpolationParamsSelectionPanel);
                    disablePanel(miningFunctionSelectionPanel);
                    disablePanel(miningFunctionParamsSelectionPanel);
                    disablePanel(selectIndividualsPanel);
                    disablePanel(sigLevelParamsPanel);
                    disablePanel(selectTimespanPanel);
                    /*Fei*/
                    disablePanel(this.followingParamsPanel);
                    /**/   

                        JFrame dvPanel = new DataVisualization(subTrajs, params, this);
                        dvPanel.setLocation(this.getX() + this.getWidth(), this.getY());
                        dvPanel.setVisible(true);
                    }
                    
                    //this.saveKmlButtonF.setVisible(true);
                }


            } catch (Exception ex) {
                Logger.getLogger(ParametersSelectionFrame.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(e.getSource() == saveDensityMapButton){
           String selectedDatasetName = curSelectedDatasetName;
           String displayMethod       = (String) displayMethodCombo.getSelectedItem();
           MiningFunctionParameters params = new MiningFunctionParameters();
           String gap = (String) gapCombo.getSelectedItem();
           String thresGap = (String) thresGapCombo.getSelectedItem();
           boolean needInterpolation = false;
           params.setSelectedDatasetName(selectedDatasetName);
           params.setSelectedFunction(selectedFunction);
           params.setDisplayMethod(displayMethod);
           params.setNeedInterpolation(needInterpolation);
           params.setGap(gap);
           params.setThresGap(thresGap);

           
          
           ArrayList<Trajectory> trajs = getSelectedTrajectories(selectedIndividualIndicesForDv);
           List<Trajectory> subTrajs   = getSubTrajectory(trajs,
                        selectStartTimePicker.getDate(),
                        selectEndTimePicker.getDate());
           JFrame dvPanel = new DataVisualization(subTrajs, params, this);
           dvPanel.setLocation(this.getX() + this.getWidth(), this.getY());
           dvPanel.setVisible(true);
        }
        
        
        if (e.getSource() == selectIndividualsButton) {
            ArrayList<String> selectedNames = ListDialog.showDialog(this,
                    selectIndividualsButton,
                    "All the individuals in the data set: ( hold Ctrl key to select multiple objects)",
                    "Select individuals: ",
                    individualNames,
                    selectedIndividualIndicesForDv,
                    "Index 1234567890");
            selectedIndividualIndicesForDv = getSelectedIndices(selectedNames);
            ArrayList<Trajectory> trajs = getSelectedTrajectories(selectedIndividualIndicesForDv);
            updateTimeSpanRange(trajs);
        }
        



        if (e.getSource() == saveKmlButton) {
                String selectedDatasetName = curSelectedDatasetName;
                String displayMethod = (String) displayMethodCombo
                        .getSelectedItem();
                String gap = (String) gapCombo.getSelectedItem();
                String thresGap = (String) thresGapCombo.getSelectedItem();
                String numRound = (String) numRoundCombo.getSelectedItem();
                String distThres = distThresTextedField.getText();
                boolean needInterpolation = this.interpFlag;
            
                MiningFunctionParameters params = new MiningFunctionParameters();
                params.setSelectedDatasetName(selectedDatasetName);
                params.setSelectedFunction(selectedFunction);
                params.setDisplayMethod(displayMethod);
                params.setNeedInterpolation(needInterpolation);
                params.setGap(gap);
                params.setThresGap(thresGap);
                params.setNumRound(numRound);
                params.setDistThres(distThres);
                
                params.setStartTime(selectStartTimePicker.getDate());
                params.setEndTime(selectEndTimePicker.getDate());
                DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
	   //get current date time with Date()
	   Date date = new Date();
            String outputPath ="result/"+"Plot_"+params.getSelectedDatasetName().substring(0,4).replace(" ","_")+"_"+dateFormat.format(date)+"/";
            new File(outputPath).mkdirs();
            ArrayList<Trajectory> trajs = getSelectedTrajectories(selectedIndividualIndicesForDv);
            List<Trajectory> subTrajs   = getSubTrajectory(trajs,
                        selectStartTimePicker.getDate(),
                        selectEndTimePicker.getDate());
            JFrame computeKML = new GenerateKMLFile(trajsMap, params,outputPath,subTrajs);
            computeKML.setVisible(true);
        }
    }

    private List<Trajectory> getSubTrajectory(List<Trajectory> trajs, Date start, Date end) {
        DateTime startDateTime = new DateTime(start);
        startDateTime = startDateTime.minusHours(12);
        DateTime endDateTime = new DateTime(end);
        endDateTime = endDateTime.plusHours(12);
        List<Trajectory> subs = new ArrayList<>();
        for (Trajectory traj : trajs) {
            Trajectory sub = new Trajectory();
            sub.setId(traj.getId());
            for (Point p : traj.getPoints()) {
                // if pTime >= start and pTime <= end;
                if ((p.getTime().equals(startDateTime)
                        || p.getTime().isAfter(startDateTime))
                        && (p.getTime().equals(endDateTime)
                        || p.getTime().isBefore(endDateTime))) {
                    sub.addPoint(p);
                }
            }
            subs.add(sub);
        }
        return subs;
    }

    private void initIndividualNames() {
        // get a list of individual names
        nameMapId = new Hashtable<String, Integer>();
        Set<String> names = trajsMap.keySet();
        individualNames = new String[names.size()];
        selectedIndividualIndicesForDv = new int[names.size()];
        int nameCount = 0;
        for (String name : names) {
            individualNames[nameCount] = name;
            nameMapId.put(name, nameCount);
            selectedIndividualIndicesForDv[nameCount] = nameCount;
            nameCount++;
        }
    }

    private ArrayList<Trajectory> getSelectedTrajectories(int[] indces) {
        ArrayList<Trajectory> trajs = new ArrayList<Trajectory>();
        for (int i = 0; i < indces.length; i++) {
            trajs.add(trajsMap.get(individualNames[indces[i]]));
        }
        return trajs;
    }

    private int[] getSelectedIndices(ArrayList<String> names) {
        int[] indices = new int[names.size()];
        for (int i = 0; i < names.size(); i++) {
            indices[i] = nameMapId.get(names.get(i));
        }
        return indices;
    }

    private DateTime tryParseDate(String dateStr) {
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

    private static boolean tryParseDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
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
  
    private static String genMiningFunctionParameterHelpText() {
        StringBuilder builder = new StringBuilder();
        builder.append("The significance value test is carried using randomization method. The more rounds that the randomization has, the more accurate that the result is. \n\n")
                .append("The DistThres is used to define a meeting event (how close that two animals could indicate that they meet each other). \n\n")
                .append("The algorithm is outlined as following. Suppose #Rounds = 100;\n\n")
                .append("1. Calculate the meeting frequency of two animals (how many timestamps that two animals have a meeting event).\n")
                .append("2. Randomize the movement sequences for these two animals.\n")
                .append("3. Calculate the meeting frequency of the randomized the movement sequences. \n")
                .append("4. Go back to step 2 until we do the randomization for 100 rounds.\n")
                .append("5. Output within 100 rounds, how many rounds that the meeting frequency over the randomized sequences is LOWER than the that of the original sequences.\n");
        return builder.toString();
    }
    
    private static String genMiningFunctionParameterHelpTextFollowing() {
        StringBuilder builder = new StringBuilder();
        builder.append("The function takes in two parameters to find following time intervals. ")
                .append("The time thres parameter is the time constraint on the time lag.")
                .append("The dist thres parameter is the distance constraint when determine whehter two points are spatilly close.")
                .append("In addition, the min interval length parameter is used to specify the minimum interval length.\n\n")
                .append("The algorithm is outlined as the following: \n")
                .append("1. For each point in one trajectory, find a point in the other trajectoy that is closest to the point. In addition, the other point should satisify the distance constraint.\n")
                .append("2. If the closest point in the other trajectory satisfys the time constraint, mark the point as 1, as 0 otherwise. \n")
                .append("3. Find the time intervals that maximize discrepancy between the interval length and number of 1's in the intervals. \n ");
        return builder.toString();
    }

    private static String genMiningFunctionHelpText() {
        StringBuilder builder = new StringBuilder();
        builder.append("Distance calculation: The average of Eculidean distances (in meters) between two animals.\n\n")
                .append("Significance value mining: The significance value of the interactions between two animals. If the value is approaching 1, it means they have strong ATTRACTION relationship. If the value is approaching 0, it means they have strong AVOIDANCE relationship. If the value is around 0.5, it means they are INDEPENDENT.\n\n")
                .append("Following mining: This function mines the time intervals that following relationships occur. The function takes the trajectories of two moving object as input. \n\n");
        return builder.toString();
    }

    private static String genDataInterpolationHelpText() {
        StringBuilder builder = new StringBuilder();
        builder.append("Gap: If Gap(min)=10, it means the movement is linearly interpolated every 10 minutes.\n\n")
                .append("ThresGap: If ThresGap(hr)=1, it means that if two consecutive locations have time gap larger than 1 hour, the locations between these two consecutive locations will not be interpolated.\n\n")
                .append("To set these two parameters, you could use the Avg. Sampling Rate reported above as a reference.\n");
        return builder.toString();
    }

    private static String toMins(double ms) {
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        return decimalFormat.format((ms / 1000.0) / 60.0);
    }

    public void updatePanelsAfterMiningFunction(long spentTime) {
        enablePanel(datasetSelectionPanel);
        enablePanel(interpolationParamsSelectionPanel);
        enablePanel(miningFunctionSelectionPanel);
        enablePanel(miningFunctionParamsSelectionPanel);
        enablePanel(selectIndividualsPanel);
        enablePanel(sigLevelParamsPanel);
        /*Fei*/
        enablePanel(this.followingParamsPanel);
        /**/
        enablePanel(selectTimespanPanel);
        goButton.setEnabled(true);
        instructionLabel.setText("Mining method spends " + toMins(spentTime) + " mins");
    }
}
