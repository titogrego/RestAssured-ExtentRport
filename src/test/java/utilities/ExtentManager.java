package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
//import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.Platform;

import java.io.File;

//**********************************************************************************************************
//Author: Onur Baskirt
//Description: ExtentReports related operations are done by this class. I added extra functionality such as
//"getCurrentPlatform". In this way, framework can create a report folder and file based on OS.
//Reference: http://extentreports.com/docs/versions/3/java/
//**********************************************************************************************************
public class ExtentManager {
    private static ExtentReports extent;
    private static Platform platform;
    private static String reportFileName = "report.html";
    private static String macPath = System.getProperty("user.dir")+ File.separator+"TestReport";
    private static String windowsPath = System.getProperty("user.dir")+ File.separator+"TestReport";
    private static String macReportFileLoc = macPath+ File.separator + reportFileName;
    private static String winReportFileLoc = windowsPath + File.separator + reportFileName;

    public static ExtentReports getInstance() {
        if (extent == null)
            createInstance();
        return extent;
    }

    //Create an extent report instance
    public static ExtentReports createInstance() {

        String css ="#img{width: 40px;\n" +
                "    height: 40px;\n" +
                "    float: left;\n" +
                "    position: absolute;\n" +
                "    margin-left: -71px;\n" +
                "    margin-top: 5px;}";

        String js = " $(document).ready(function() {\n" +


                "          $('head').append('<link rel=\"icon\" href=\"https://frwk.com.br/wp-content/uploads/2017/11/framework-icon-150x150.png\" sizes=\"32x32\">');\n" +

                "        });";
        platform = getCurrentPlatform();
        String fileName = getReportFileLocation(platform);
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(fileName);

        htmlReporter.config().setTheme(Theme.DARK);
        htmlReporter.config().setDocumentTitle("Teste API  Quarta Frame");
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setReportName(

                "<img id='img' src='https://frwk.com.br/wp-content/uploads/2017/11/framework-icon-150x150.png' />\n" +
                        "\t<span style='margin-left:10px'>Relatório de execução dos testes</span>\n" );
        htmlReporter.config().setJS(js);
        htmlReporter.config().setCSS(css);

        htmlReporter.config().setTimeStampFormat("dd/MM/yyyy hh:mm:ss");

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        return extent;
    }

    //Select the extent report file location based on platform
    private static String getReportFileLocation (Platform platform) {
        String reportFileLocation = null;
        switch (platform) {
            case MAC:
                reportFileLocation = macReportFileLoc;
                createReportPath(macPath);
                System.out.println("ExtentReport Path for MAC: " + macPath + "\n");
                break;
            case WINDOWS:
                reportFileLocation = winReportFileLoc;
                createReportPath(windowsPath);
                System.out.println("ExtentReport Path for WINDOWS: " + windowsPath + "\n");
                break;
            default:
                System.out.println("ExtentReport path has not been set! There is a problem!\n");
                break;
        }
        return reportFileLocation;
    }

    //Create the report path if it does not exist
    private static void createReportPath (String path) {
        File testDirectory = new File(path);
        if (!testDirectory.exists()) {
            if (testDirectory.mkdir()) {
                System.out.println("Directory: " + path + " is created!" );
            } else {
                System.out.println("Failed to create directory: " + path);
            }
        } else {
            System.out.println("Directory already exists: " + path);
        }
    }

    //Get current platform
    private static Platform getCurrentPlatform () {
        if (platform == null) {
            String operSys = System.getProperty("os.name").toLowerCase();
            if (operSys.contains("win")) {
                platform = Platform.WINDOWS;
            } else if (operSys.contains("nix") || operSys.contains("nux")
                    || operSys.contains("aix")) {
                platform = Platform.LINUX;
            } else if (operSys.contains("mac")) {
                platform = Platform.MAC;
            }
        }
        return platform;
    }

}
