package uiuc.dm.miningTools.ui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import uiuc.dm.miningTools.dao.DataAccessObject;

/**
 * Generate a user login UI
 *
 * @author klei2
 */
public class LoginFrame extends JFrame implements ActionListener {

    private int WIDTH = 320;
    private int HEIGHT = 250;
    private Container contentPane;
    private JPanel panel1;
    private JPanel panel2;
    private JButton guestBt;
    private JButton loginBt;
    private JButton cancelBt;
    private JButton exitBt;
    private JTextField pwd;
    private JTextField uname;
    private JLabel l1;
    private JLabel loginLabel;
    private JLabel pwdLabel;

    public LoginFrame() {
        setSize(WIDTH, HEIGHT);
        setTitle("MoveBank Login");
        setResizable(true);

        contentPane = getContentPane();
        panel1 = new JPanel();
        panel2 = new JPanel();

        loginBt = new JButton("Login");
        cancelBt = new JButton("Cancel ");
        guestBt = new JButton("Guest");
        exitBt = new JButton("Exit");
        uname = new JTextField(20);
        pwd = new JPasswordField(20);
        //uname = new JTextField("JessieLzh");
        //pwd = new JPasswordField("Aw5eeCh7");
        l1 = new JLabel("MoveBank login");
        loginLabel = new JLabel("Login ID : ");
        pwdLabel = new JLabel("Password : ");

        loginBt.addActionListener(this);
        cancelBt.addActionListener(this);
        guestBt.addActionListener(this);
        exitBt.addActionListener(this);

        contentPane.add(panel1, "North");
        contentPane.add(panel2, "Center");

        panel1.add(l1);
        panel2.add(loginLabel);
        panel2.add(uname);
        panel2.add(pwdLabel);
        panel2.add(pwd);
        panel2.add(loginBt);
        panel2.add(cancelBt);
        panel2.add(guestBt);
        panel2.add(exitBt);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == loginBt) {
            try {
                DataAccessObject dao = new DataAccessObject(uname.getText(),
                        pwd.getText());
                this.setVisible(false);

                // switch to antoher panel
                JFrame selectDatasetPanel = new ParametersSelectionFrame(dao);
                selectDatasetPanel.setVisible(true);
                selectDatasetPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Incorrect Login ID or password");
            }
        }
        if (e.getSource() == cancelBt) {
            JOptionPane.showMessageDialog(this, "Login cancelled");
            System.exit(0);
        }
        if (e.getSource() == exitBt) {
            JOptionPane.showMessageDialog(null/*this*/, "Session is terminated");
            System.exit(0);
        }

        if (e.getSource() == guestBt) {
            try {
                DataAccessObject dao = new DataAccessObject();
                this.setVisible(false);
                // switch to antoher panel
                JFrame selectDatasetPanel = new ParametersSelectionFrame(dao);
                selectDatasetPanel.setVisible(true);
                selectDatasetPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Login failed");
            }
        }
    }
}
