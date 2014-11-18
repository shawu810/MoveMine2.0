package uiuc.dm.miningTools.exe;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import uiuc.dm.miningTools.dao.DataAccessObject;
import uiuc.dm.miningTools.ui.LoginFrame;
import uiuc.dm.miningTools.ui.ParametersSelectionFrame;

/**
 * Entrance of the software, everything starts from here
 *
 * @author klei2
 */
public class MiningToolsFx{
  public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
//                 use the following for testing
//                DataAccessObject dao;
//                try {
//                    dao = new DataAccessObject("uiuc-dm",
//                            "xxgNRUT4Gz");
//                    // switch to antoher panel
//                    JFrame selectDatasetPanel = new ParametersSelectionFrame(dao);
//                    selectDatasetPanel.setVisible(true);
//                    selectDatasetPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                } catch (Exception ex) {
//                    System.out.println("Cannot connect to the server!");
//                    Logger.getLogger(MiningToolsFx.class.getName()).log(Level.SEVERE, null, ex);
//                }
                
            }
        });
    }

  
    
}
