import java.io.*;
import java.util.*;

/**
 *  CrashReportScanner relies on user to provide a CSV of the last crash report (because of the way the crash report CSV is formatted)
 *  Will not run without user oversight and will not read data it cannot understand.
 *  @author Doug Clark
 *  @version 0.9
 */
public class CrashReportScanner
{
    public Object[][] detailsArray;
    private int crashCount = 0;

    /** true if accident meets parameters required by Michelle */
    public static boolean IS_VALID_CRASH = false;

    public static String LAST_ADDRESS = "";

    /** crash must cause more than this amount dollar damage */
    public static final int MIN_DAMAGE = 3000;

    /** if crash causes this amount dollar damage, it is always suitable for mass mailing */
    public static final int MAX_DAMAGE = 7000;

    /** person's physical condition after crash, 5 is fine, 0 is dead. Pateint must be lower than this level */
    public static final int PORD_THRESHOLD = 5;
    
    /**
     *  orchestrates program by making sure input/output files are all clear.
     *  requires: input file is in Local Downloads folder, unless explicity addressed otherwise
     *  @param args Command Line arguments if user knows how to provide them.
     */
    public static void main(String args[])
    {
        Scanner console = new Scanner(System.in); //reads what user types into terminal
        Scanner reportScanner; //temporary placeholder for input file
        File file; //temporary placeholder for output file
        if (args.length == 1) //if user includes input file at runtime
        {
            try
            {
                file = new File(args[0]);
                if (!file.canRead()) // if the file either doesnt exist or has privacy exclusions
                {
                    System.out.println("File Cannot Be Read");
                    reportScanner = promptForFile(console); // prompt user for file name again
                }
                else reportScanner = new Scanner(file); //program found file and it is ok to be read
            }
            catch (FileNotFoundException e) //strange exception, should not be reached, but is safety net
            {
                System.out.println("File Not Found");
                reportScanner = promptForFile(console); //prompt user for file name again
            }
        }
        else //user did not include input file name at runtime
        {
            reportScanner = promptForFile(console); //prompt user for file name
        }
        PrintStream outputFile = getPrintStream(console); //prompt user for output file name
        scanForCrash(reportScanner, outputFile); //search for crashes in input, print them to output
    }
    
    /**
     *	asks user for input file through terminal
     *	@param console The terminal user is typing through
     *	@return either a readable file or null (if file not found)
     */
    public static Scanner promptForFile(Scanner console)
    {
        System.out.println("Download most recent report");
	System.out.println("Open file in exel and Save file as already titled, but change \".xls\" to \".csv\"");
	System.out.print("Enter the name of the file just downloaded to process: ");
        File file = new File(console.next());
        while (!file.exists())
        {
            System.out.print("File does not exist. Enter a file name to process: ");
            file = new File(console.next());
        }
        try
        {
            Scanner fileScanner = new Scanner(file);
            return fileScanner;
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found.");
        }
        return null;
    }
    
    /**
     *  takes input and if crash is valid, prints crash to output
     *	@param reportScanner reader for input file
     *  @param outputFile printer for output file
     */
    public static void scanForCrash(Scanner reportScanner, PrintStream outputFile)
    {
	String crashLine;
        outputFile.println("First, Last, Address, City, State, Zip");
	reportScanner.nextLine(); //dump header
        while (reportScanner.hasNextLine())
        {
            try {
                crashLine = getDetails(new Scanner(reportScanner.nextLine()).useDelimiter(","));
            } catch (NumberFormatException nfe) {
                System.out.println("Int illegal value in CSV.");
            }
            if (IS_VALID_CRASH)
            {
                this.crashCount++;
                outputFile.println(crashLine);
            }
        }
    }

    /**
     *  reads the current line for data as to whether or not crash should be included in crash reports
     *  @param currentLine Scanner of current entry of crash report CSV.
     *  @return formatted address line for use in importing to mass mailings
     */
    public static String getDetails(Scanner currentLine)
    {
        String AF = currentLine.next();
        boolean af = (AF.toLowerCase().contains("af") &&
	!AF.toLowerCase().contains("passenger")) ? true : false;
	boolean ped_shortcut = (!AF.toLowerCase().contains("af") && AF.toLowerCase().contains("pedestrian")) ? true : false;


        String firstName = currentLine.next();
        String lastName = currentLine.next();
        String address = currentLine.next();
        String city = currentLine.next();
        String state = currentLine.next();
        String zip = currentLine.next();
        
        currentLine.next(); //PD
        currentLine.next(); //DOA
        currentLine.next(); //VC
        
        int damage = currentLine.nextInt();
	boolean damage_shortcut = (damage >= MAX_DAMAGE) ? true : false;
        int PorD = currentLine.nextInt();
        
        currentLine.next(); //Race
        currentLine.next(); //Mr/Ms
        
        String ambulance = currentLine.next();
        boolean amb = (ambulance.toLowerCase().contains("y")) ? true : false;
        
        currentLine.next(); //dui
        currentLine.next(); //commercial

	boolean passenger_shortcut = (AF.toLowerCase().contains("passenger") &&
                                      (PorD < PORD_THRESHOLD) && amb) ? true : false;
        
        IS_VALID_CRASH = (((damage >= MIN_DAMAGE && PorD < PORD_THRESHOLD) || damage_shortcut || 
                           ped_shortcut || passenger_shortcut) && !af && !address.equals(LAST_ADDRESS)) ? true : false;
	
	LAST_ADDRESS = address;        

        return firstName + ", " + lastName + ", " + address + ", " + city + ", " + state + ", " + zip;
    }
    
    /**
     * Reads filename from user, then return a PrintStream
     * 
     * @param console
     *            scanner for input from the console
     * @return a PrintStream to write to a file
     */
    public static PrintStream getPrintStream(Scanner console) {
	PrintStream userOut = new PrintStream(System.out);
        String promptFile = ("Enter output file: ");
        String existsError = ("This file exists. Is it ok to overwrite the file? ");
        
        userOut.println("Name output file as todays date \"output\" ex(DDMMYYoutput.csv)");
        userOut.print(promptFile);
        File outputFile = new File(console.next());
        while (outputFile.exists())
        {
            userOut.print(existsError);
            if (console.next().toLowerCase().contains("y"))
            {
                break;
            }
            
            userOut.print(promptFile);
            outputFile = new File(console.next());
        }
        
        try
        {
            return new PrintStream(outputFile);
        }
        catch (FileNotFoundException e)
        {
            userOut.println("An unexpected error has occured: " + e);
            return null;
        }
    }

    /**
     *  NEW METHOD NEEDS TO READ THE INPUT, RUN IT THROUGH THE SCANNER, KEEP TRACK OF NAME/COUNTY/PD, DOWNLOAD PDF FROM REQUIRED COUNTY/PD FROM WEB,
     *  FIND EACH RECORD WITHIN THE PDF, STORE PAGE WITH NAME ON IT +1 PAGE FOR BACK, cHECK FOR FORM 1 OF ? TO DECIDE HOW MANY PAGES,
     *  PRINT ALL THOSE PAGES TO NEW PDF
     */

    public static int getCrashCount() {
        return this.crashCount;
    }

    public static void createDetailsArray() {
        this.detailsArray = new Object[getCrashCount()][4];
    }

}
