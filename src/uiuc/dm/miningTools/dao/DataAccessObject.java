package uiuc.dm.miningTools.dao;

import java.awt.Frame;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
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
import uiuc.dm.moveMine.domain.Point;
import uiuc.dm.moveMine.domain.Trajectory;

/**
 * Manipulate the data source such as Movebank API and can potential add other
 * data source into the system.
 *
 * @author klei2
 */
public class DataAccessObject {

    private MovebankRestClient client;
    private boolean guestMode;

    public DataAccessObject() throws Exception {
        guestMode = true;
    }

    public boolean isGuestMode() {
        return guestMode;
    }

    public DataAccessObject(String username, String password) throws Exception {
        guestMode = false;
        Frame applicationFrame = null;
        client = new MovebankRestClient(
                MovebankRestClient.MOVEBANK_BASE_URL_PROD, username, password,
                applicationFrame);
        client.disableSslChecks();
        client.checkConnection();
    }

    public Object getDataset(int datasetId) {
        return null;
    }

    public MovebankRestClient getClient() {
        return client;
    }

    public List<String> getDatasetNames() throws Exception {
        List<String> datasetNames = new ArrayList<String>();
        RequestBuilderStudy requestBuilderStudy = new RequestBuilderStudy();
        requestBuilderStudy.setSortAttributes(Constants.Attributes.NAME);
        for (Record record : client.readAll(requestBuilderStudy)) {
            if (record.getBooleanValue(Constants.Attributes.I_CAN_SEE_DATA)) {
                datasetNames.add(record
                        .getStringValue(Constants.Attributes.NAME));
            }
        }
        return datasetNames;
    }

    /**
     * Fetch the trajectory from movebank database and output it as a map with
     * individual_id as the key
     *
     * @param datasetName
     * @return
     * @throws Exception
     */
    public Hashtable<String, Trajectory> getTrajectory(String datasetName)
            throws Exception {
        final Hashtable<String, Trajectory> trajsMap = new Hashtable<String, Trajectory>();
        // Define rest call for retrieving a list of studies:
        RequestBuilderStudy requestBuilderStudy = new RequestBuilderStudy();

        // Set filter criterion for finding studies:
        requestBuilderStudy.setName(datasetName);
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

            // Define rest call for retrieving a list of animals within a study:
            RequestBuilderIndividual requestBuilderIndividual = new RequestBuilderIndividual(
                    studyId);
            List<Record> individuals = client.readAll(requestBuilderIndividual);

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
//                                    System.out.println(x + " " + y);
                                } else {
                                    p.setValid(false);
                                }

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
                        });
            }
        }
        return trajsMap;
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
}
