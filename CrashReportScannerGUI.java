import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import looks-1.2.2.*;

public class CrashReportScannerGUI implements ActionListener {

    private JFrame frame;
    private int X = 100;

    private JPanel eastFramePanel;

    private JPanel inputPanel;
    private JLabel getReportLabel;
    private JCheckBox multiday;
    private Date date1;
    private Date date2;

    private JPanel outputFilePanel;
    private JLabel locationLabel;
    private JFileChooser fileChooser;

    private JPanel commandPanel;
    private JButton createOutputButton;
    private JButton updateOutputButton;

    private JPanel westFramePanel;

    private JPanel outputPreviewPanel;
    private JLabel outputLabel;
    private JTable outputTable;
    private JButton deleteButton;
    private JButton showReportButton;
    private JButton manualEntryButton;

    private CrashReportScanner crs;

    public CrashReportScannerGui {

        frame = new JFrame();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLocation(X, X);
        frame.setTitle("Crash Report Scanner");
        frame.setLayout(new BorderLayout());

        //set up east frame
        eastFramePanel = new JPanel(new BorderLayout());
        //inputPanel contains northInputPanel, eastInputPanel, and westInputPanel
        inputPanel = new JPanel(new BorderLayout());
        //northInputPanel contains inputLabel and multiday
        JPanel northInputPanel = new JPanel(new FlowLayout());
        inputLabel = new JLabel("Get Reports");
        //multiday is checkbox with "Multiple Days" label and initially unchecked
        multiday = new JCheckBox("Multiple Days", false);
        northInputPanel.add(inputLabel);
        northInputPanel.add(multiday);
        //eastInputPanel contains datePicker1 and is initially enabled
        JPanel eastInputPanel = new JPanel();
        UtilDateModel model1 = new UtilDateModel();
        JDatePanelImpl datePanel1 = new JDatePanelImpl(model1);
        JDatePickerImpl datePicker1 = new JDatePickerImpl(datePanel);
        eastInputPanel.add(datePicker1);
        //westInputPanel contains datePicker2 and is initially disabled until multiday is checked
        JPanel westInputPanel = new JPanel();
        UtilDateModel model2 = new UtilDateModel();
        JDatePanelImpl datePanel2 = new JDatePanelImpl(model2);
        JDatePickerImpl datePicker2 = new JDatePickerImpl(datePanel2);
        westInputPanel.add(datePicker2);
        westInputPanel.setEnabled(false);
        inputPanel.add(northInputPanel, BorderLayout.NORTH);
        inputPanel.add(eastInputPanel, BorderLayout.EAST);
        inputPanel.add(westInputPanel, BorderLayout.WEST);
        //TODO: datePicker2.setEnabled(false); conditional on multiday.isChecked(true)

        //outputFilePanel constains locationLabel and fileChooser
        outputFilePanel = new JPanel(new BoxLayout(outputFilePanel, BoxLayout.Y_AXIS));
        locationLabel = new JLabel("Location of output file and reports");
        fileChooser = new JFileChooser();
        outputFilePanel.add(locationLabel);
        outputFilePanel.add(fileChooser);

        //commandPanel contains createOutputButton and updateOutputButton
        commandPanel = new JPanel(new BoxLayout(outputFilePanel, BoxLayout.Y_AXIS));
        createOutputButton = new JButton("Create Output Files");
        updateOutputButton = new JButton("Update Output Files");
        commandPanel.add(createOutputButton);
        commandPanel.add(updateOutputButton);

        //add inputPanel, outputFilePanel, and commandPanel to eastFramePanel
        eastFramePanel.add(inputPanel, BorderLayout.NORTH);
        eastFramePanel.add(outputFilePanel, BorderLayout.CENTER);
        eastFramePanel.add(commandPanel, BorderLayout.SOUTH);

        //westFramePanel contains outputLabel, outputTable, and southOutputPanel
        westFramePanel = new JPanel(new BorderLayout());
        outputLabel = new JLabel("Output");

        //TODO set up outputTable
        //TODO create CrashReportScanner object [][] detailsArray and createDetailsArray()
        outputTable = new JTable(new detailsTableModel()); 


        //southOutputPanel contains deleteButton, showReportsButton, and manualEntryButton
        JPanel southOutputPanel = new JPanel(new FlowLayout());
        deleteButton = new JButton("Delete Selected");
        showReportButton = new JButton("Show Reports");
        manualEntryButton = new JButton("Manual Entry");
        southOutputPanel.add(deleteButton);
        southOutputPanel.add(showReportsButton);
        southOutputPanel.add(manualEntryButton);

        //add outputLabel, outputTable, and southOutputPanel
        westFramePanel.add(outputLabel, BorderLayout.NORTH);
        westFramePanel.add(outputTable, BorderLayout.CENTER);
        westFramePanel.add(southOutputPanel, BorderLayout.SOUTH);

        //add eastFramePanel and westFramePanel
        frame.add(eastFramePanel, BorderLayout.EAST);
        frame.add(westFramePanel, BorderLayout.WEST);
        frame.setVisible(true);
    }

    class detailsTableModel extends AbstractTableModel {
        private String[] outputTableColumns = {"First", "Last", "Address"};
        private Object[][] details = CrashReportScanner.detailsArray;

        public int getColumnCount() {
            return outputTableColumns.length;
        }

        public int getRowCount() {
            return details.length();
        }

        public String getColumnName(int col) {
            return outputTableColumns[col];
        }

        public Object getValueAt(int row, int col) {
            return details[row][col];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable (int row, int col) {
            return (col == 0);
        }
    }
