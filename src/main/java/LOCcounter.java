import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * LOCcounter is used to parse through java files and provide 5 kinds of information:
 * 1. Total number of java files
 * 2. Total number of unique java files
 * 3. Total number of blank lines
 * 4. Total number of comment lines
 * 5. Total number of code lines
 */
public class LOCcounter {

    private long totBlankLines;
    private long totCmtLines;
    private long totCodeLines;
    private String path;
    private ArrayList<String> fileNames;
    private HashMap<Integer, String> uniqueFiles;
    private boolean multiCom = false;


    public LOCcounter(String p) {
        path = p;
        totCodeLines = 0;
        totBlankLines = 0;
        totCmtLines = 0;
        fileNames = new ArrayList<String>();
        uniqueFiles = new HashMap<Integer, String>();

    }

    /**
     * This method is used to initialise the used given path and start counting
     * different properties of java source code.
     */
    public void startCounting() {
        parseInputPath(path);
        processUniqueFiles();
        countLoc();
    }

    /**
     * This method is used to parse the given file/directory and create a list of all java files.
     * @param inputPath
     */
    private void parseInputPath(String inputPath) {
        if(inputPath.endsWith(".java")) {
            fileNames.add(inputPath);
            return;
        }
        try {
            DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(inputPath));
            for (Path path : directoryStream) {
                if(Files.isDirectory(path)) {
                    parseInputPath(path.toString());
                }
                else
                {
                    if(path.toString().endsWith(".java")) {
                        fileNames.add(path.toString());
                    }

                }

            }
        } catch (IOException ex) {
        }
    }

    /**
     * This method is used to create a hashmap of all the unique java files.
     */
    private void processUniqueFiles() {
        for(String path : fileNames) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(path)));
                int hash = content.hashCode();
                if (!uniqueFiles.containsKey(hash)) {
                    uniqueFiles.put(hash, path);
                }
            }
            catch (IOException ex) {

            }

        }
    }

    /**
     * This method is used to count the number of blank lines, comment lines and
     * code lines in the given unique java files.
     */
    private void countLoc() {

        for(String file : uniqueFiles.values()) {
            BufferedReader reader;
            try {
                reader = new BufferedReader( new FileReader(file));
                String line = reader.readLine();
                while(line != null) {
                    processLine(line);
                    line = reader.readLine();
                }

                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is a helper function to identify the type of line in a java source line.
     * @param line
     */
    private void processLine(String line) {
        line = line.trim();


        if(multiCom) {
            if (line.length() > 1 && (line.startsWith("*/") || line.endsWith("*/"))) {
                multiCom = false;
            }
            totCmtLines++;
            return;
        }

        if (line.equals("")) {
            totBlankLines++;
        }

        else if(line.length() > 1 && line.startsWith("//")) {
            totCmtLines++;
        }
        else if (line.length() > 1 && line.startsWith("/*")) {
            if( !line.endsWith("*/")) {
                multiCom = true;
            }
            totCmtLines++;
        }
        else {
            totCodeLines++;
        }
    }

    /**
     * This method return total java files.
     * @return total java file count
     */
    public long getTotalFiles() {
        return fileNames.size();
    }

    /**
     * This method return total unique java files.
     * @return total unique java file count
     */
    public long getUniqueFiles() {
        return uniqueFiles.size();
    }

    /**
     * This method return total code lines.
     * @return totCodeLines :total code lines
     */
    public long getTotalCodeLines () {
        return totCodeLines;
    }

    /**
     * This method return total comment lines.
     * @return totCmtLines :total comment lines
     */
    public long getTotalCommentLines() {
        return totCmtLines;
    }

    /**
     * This method return total blank lines.
     * @return totBlankLines :total blank lines
     */
    public long getTotalBlankLines() {
        return totBlankLines;
    }


    public static void main(String[] args)  {

        LOCcounter l = new LOCcounter(args[0]);
        l.startCounting();

        System.out.println(l.getTotalFiles() + "-" + l.getUniqueFiles()+ "-" + l.getTotalBlankLines() +
                "-" + l.getTotalCommentLines() + "-" + l.getTotalCodeLines());


    }


}
