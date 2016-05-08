/**
 * Created by Alex on 07.05.2016.
 */
import org.apache.commons.cli.*;

public class ResolvePath {

    private String workingDir = System.getProperty("user.dir");

    private String pathCsvFile = workingDir + "/url.csv";

    private String pathCsvFileResp = pathCsvFile;

    private Integer countThreads = 20;


    public ResolvePath() {
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public String getPathCsvFile() {
        return pathCsvFile;
    }

    public void setPathCsvFile(String pathCsvFile) {
        this.pathCsvFile = pathCsvFile;
    }

    public String getPathCsvFileResp() {
        return pathCsvFileResp;
    }

    public void setPathCsvFileResp(String pathCsvFileResp) {
        this.pathCsvFileResp = pathCsvFileResp;
    }

    public Integer getCountThreads() {
        return countThreads;
    }

    public void setCountThreads(Integer countThreads) {
        this.countThreads = countThreads;
    }

    public void cliHandler(String[] args) {
        Options options = new Options();

        options.addOption("u", "urls", true, "The absolute path of the Url csv file.");
        options.addOption("r", "resp", true, "The absolute path of the Url csv file response.");
        options.addOption("t", "thread", true, "Count threads.");

        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (line.hasOption("u")) {
            setPathCsvFile(line.getOptionValue("u"));
        }
        if (line.hasOption("r")) {
            setPathCsvFileResp(line.getOptionValue("r"));
        }
        if (line.hasOption("t")) {
            setCountThreads(Integer.parseInt(line.getOptionValue("t")));
        }
    }
}

