import javax.swing.SwingWorker;
import java.util.Collections;
import java.awt.Dimension;
import java.awt.Component;
import java.io.File;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UIManager.*;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.DefaultListModel;
import java.util.List;
import java.util.ArrayList;
import org.imgscalr.Scalr;
import java.util.Comparator;


class PhotoCellRenderer extends JPanel implements ListCellRenderer {

    public JLabel icon = new JLabel();
    public JLabel similarity = new JLabel();
    public JLabel path = new JLabel();
    public JPanel details = new JPanel();

    public PhotoCellRenderer(){
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        icon.setMinimumSize(new Dimension(150, 150));
        add(icon);
        add(Box. createRigidArea(new Dimension(20, 20)));
        add(details);
        similarity.setMinimumSize(new Dimension(100, 200));
        similarity.setFont(new Font("Serif", Font.PLAIN, 40));
        details.add(similarity);
        details.add(path);

    }
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        PhotoResult item = (PhotoResult) value;
        MyUtils.log("##### LOG: ADD ",item.title," TO CELL RENDERER " );
        try{
        icon.setIcon(item.getImage());
        }
        catch(Exception e){
        MyUtils.log("##### LOG: FAILED WITH ",item.title);
        }
        similarity.setText(String.format("%.2f", item.similarity));
        path.setText(item.title);
        return this;
    }
}

public class FindSimilarApp extends JFrame{
    File archiveFile;

    private JTextField photoField = new JTextField();
    private JTextField archiveField = new JTextField();
    private JTextField cropField = new JTextField("0");
    private JTextField gridSize = new JTextField("1024");
    private JPanel basic = new JPanel();
    private JButton photoButton = new JButton("Photo");
    private JButton searchButton = new JButton("Search");
    private JButton archiveButton = new JButton("Archive");
    public JList list = new JList();
    public DefaultListModel model = new DefaultListModel();

    public FindSimilarApp () {

        initUI();
    }

    public final void initUI(){

        setSize(700,500);
        setTitle("Find Similar Photos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));
        add(basic);

        JPanel controls = new JPanel();
        controls.setLayout( new BoxLayout(controls, BoxLayout.X_AXIS));
        basic.add(controls);

        photoButton.setMaximumSize(new Dimension(150,30));
        photoButton.setPreferredSize(new Dimension(150,30));
        JPanel inputs = new JPanel();
        inputs.setLayout(new BoxLayout(inputs, BoxLayout.Y_AXIS));
        controls.add(inputs);
        JPanel photoInputPane = new JPanel();
        photoInputPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
        photoInputPane.setLayout(new BoxLayout(photoInputPane, BoxLayout.X_AXIS));
        photoInputPane.add(photoField);
        photoInputPane.add(photoButton);

        archiveButton.setMaximumSize(new Dimension(150,30));
        archiveButton.setPreferredSize(new Dimension(150,30));
        JPanel archiveInputPane = new JPanel();
        archiveInputPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
        archiveInputPane.setLayout(new BoxLayout(archiveInputPane, BoxLayout.X_AXIS));
        archiveInputPane.add(this.archiveField);
        archiveInputPane.add(archiveButton);


        JPanel miscSettingsPanel = new JPanel();
        miscSettingsPanel.setBorder(BorderFactory.createEmptyBorder(20,0,0,10)); 
        miscSettingsPanel.setLayout(new BoxLayout(miscSettingsPanel, BoxLayout.X_AXIS));
        JLabel cropLabel  = new JLabel("Crop Percentage");
        miscSettingsPanel.add(cropLabel);
        miscSettingsPanel.add(this.cropField);
        miscSettingsPanel.add(Box.createRigidArea(new Dimension(20, 20)));
        JLabel gridSizeLabel = new JLabel("Grid Size");
        miscSettingsPanel.add(gridSizeLabel);
        miscSettingsPanel.add(gridSize);

        inputs.add(photoInputPane);
        inputs.add(archiveInputPane);
        inputs.add(miscSettingsPanel);



        list = new JList();
        list.setCellRenderer(new PhotoCellRenderer());
        controls.add(searchButton);
        searchButton.setMaximumSize(new Dimension(150,60));
        searchButton.setPreferredSize(new Dimension(150,60));
        controls.setBorder(BorderFactory.createEmptyBorder(20,20,0,20));
        controls.setMaximumSize(new Dimension(2000,60));

        JScrollPane pane = new JScrollPane();
        pane.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        basic.add(pane);
        pane.getViewport().add(list);


        photoButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                JFileChooser fileopen = new JFileChooser();
                fileopen.setDialogTitle("Select Photo");

                int ret = fileopen.showDialog(basic, "Open file");

                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    photoField.setText(file.getPath());
                }

            }
        });

        archiveButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                JFileChooser fileopen = new JFileChooser();
                fileopen.setDialogTitle("Archive");
                fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int ret = fileopen.showDialog(basic, "Open file");

                if (ret == JFileChooser.APPROVE_OPTION) {
                    archiveFile = fileopen.getSelectedFile();
                    archiveField.setText(archiveFile.getPath());
                }
            }
        });


        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                SwingWorker worker = new SwingWorker<Integer, Void>() {
                    @Override
                    public Integer doInBackground() {
                        try{
                        MyUtils.log("##### LOG: DISABLE SEARCH BUTTON " );
                        searchButton.setEnabled(false);
                        CompareImageV2 comparator = new CompareImageV2();
                        MyUtils.log("##### LOG: GET IMAGES" );
                        List images = comparator.findMatches(photoField.getText(), archiveField.getText(), 20, cropField.getText(), gridSize.getText());
                        MyUtils.log("##### LOG: GOT IMAGES" );
                        list.clearSelection();
                        MyUtils.log("##### LOG: CLEARED SELECTION" );
                        list.setListData(images.toArray());
                        MyUtils.log("##### LOG: ADDED TO LIST " );
                        }
                        catch (Exception e){
                            MyUtils.log("##### LOG: CRAPPED " );
                            e.printStackTrace();
                        }
                        return 1;
                    }
                    public void done(){
                        MyUtils.log("##### LOG: ENABLE SEARCH BUTTON " );
                        searchButton.setEnabled(true);
                    }
            };
                worker.execute();
            }
        });
    }



    public static void main (String[] args){
        MyUtils.init();

        if (args.length == 0 ){

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                    } 
                    catch (UnsupportedLookAndFeelException e) {
                    }
                    catch (ClassNotFoundException e) {
                    }
                    catch (InstantiationException e) {
                    }
                    catch (IllegalAccessException e) {
                    }

                    FindSimilarApp app = new FindSimilarApp();
                    app.setVisible(true);
                }
            });
        }
        else {
            CompareImageV2 comparator = new CompareImageV2();
            comparator.start = System.currentTimeMillis();

            String requestedPhoto = args[0];
            String archiveDir = args[1];

            List results = comparator.findMatches(requestedPhoto, archiveDir, 20);
            for (int i=0; i< results.size(); i++){
                System.out.println(((PhotoResult)results.get(i)).title);
            }

        }
    }
}
