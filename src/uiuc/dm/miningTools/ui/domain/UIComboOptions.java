package uiuc.dm.miningTools.ui.domain;

import java.util.ArrayList;

public class UIComboOptions {

    private String[] functionOptions = {"Distance calculation", "Attract/Avoid mining", "Plotting","Following mining"};
    private String[] datasetOptions = {"monkey", "coyote"};
    private String[] dvOption = {"grid"};
    private ArrayList<String> gapOption;
    private ArrayList<String> thresGapOption;
    private ArrayList<String> numRoundOption;
    private String[] withinDistances = {"5", "10"};

    public UIComboOptions() {
        gapOption = new ArrayList<String>();
        for (Integer i = 10; i <= 60; i += 5) {
            gapOption.add(i.toString());
        }
        thresGapOption = new ArrayList<String>();
        for (Integer i = 1; i <= 20; i++) {
            thresGapOption.add(i.toString());
        }
        numRoundOption = new ArrayList<String>();
        for (Integer i = 100; i <= 1000; i += 100) {
            numRoundOption.add(i.toString());
        }
    }

    public String[] getFunctionOptions() {
        return functionOptions;
    }

    public String[] getDatasetOptions() {
        return datasetOptions;
    }

    public String[] getDvOption() {
        return dvOption;
    }

    public void setDvOption(String[] dvOption) {
        this.dvOption = dvOption;
    }

    public String[] getGapOption() {
        return gapOption.toArray(new String[gapOption.size()]);
    }

    public String[] getThresGapOption() {
        return thresGapOption.toArray(new String[thresGapOption.size()]);
    }

    public String[] getNumRoundOption() {
        return numRoundOption.toArray(new String[numRoundOption.size()]);
    }

    public String[] getWithinDistances() {
        return withinDistances;
    }
}
