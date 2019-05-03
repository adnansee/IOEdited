package be.intecbrussel;


import java.nio.file.*;
import java.nio.file.attribute.*;
import java.nio.charset.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.io.*;
import java.util.*;//DeflaterOutputStream;
//import .resources.unsortedFolder.*;


public class FileSorter {

    static Path resources = Paths.get("C:\\Users\\christiaand\\IdeaProjects\\19-05-03-Test_IO\\src\\resources");

    static Path unsortedFolder = resources.resolve("unsortedFolder");
    static Path sortedFolder = resources.resolve("sortedFolder");
    static Path summary = sortedFolder.resolve("summary");
    static Path summaryTxt = summary.resolve("summary.txt");

    static List<String> summaryList = new ArrayList<>();
    static Scanner sc;
    static boolean overwriteAll = false, overwriteNone = false;

    public static void main(String... args) {

        sc = new Scanner(System.in);

        File[] files = new File(String.valueOf(unsortedFolder)).listFiles();

        copyFiles(files);

        files = new File(String.valueOf(sortedFolder)).listFiles();


        summaryList.add("name                |      readable       |      writeable      |\n");

        createSummary(files);
        try {
            Files.createDirectories(summary);
            for (var s : summaryList)
                System.out.println(s);

            Files.write(summaryTxt, summaryList, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
        } catch (IOException se) {
            System.out.println();
            se.printStackTrace();
        }
    }


    public static void copyFiles(File[] files) {    //METHOD TO COPY FILES
        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
                copyFiles(file.listFiles()); // Calls same method again.
            } else {
                //System.out.print("File: " + file.getName() + " is " + (Files.isWritable(file.toPath()) ? "" : "not") + " writable");
                //System.out.print(" -----  hidden ? " + file.isHidden());
                String extension = "";

                int i = file.getName().lastIndexOf('.');
                if (i > 0) {
                    //System.out.println("i = "+i);
                    extension = file.getName().substring(i + 1);
                    //extension = FilenameUtils.getExtension(file.getPath());
                } else // empty name
                    extension = file.getName().substring(1);
                ;
                System.out.println(",  and extension = " + extension);
                Path newPath = sortedFolder.resolve(extension);
                //new File(sortedFolder.getFileName()).mkdirs();
                //sortedFolder
                try {
                    //if (Files.notExists()) {
                    // create the dir
                    //new File(newPath).mkdirs();
                    Files.createDirectories(newPath);
                    // copy file to sortedFolder
                    Path newFile = newPath.resolve(file.getName());
                    // REPLACE_EXISTING ??
                    if ( (new File(String.valueOf(newFile))).exists()) {
                        boolean overwrite = false;
                        if (! overwriteAll && ! overwriteNone ) {
                            System.out.println("File exists, Do you want to overwrite ? No, Yes, Yes to all, No to all");
                            boolean validInput = false;
                            while (!validInput) {
                                validInput = true;
                                String input = sc.nextLine();
                                switch (input) {
                                    case "No":
                                        break;
                                    case "Yes":
                                        overwrite = true;
                                        break;
                                    case "Yes to all":
                                        overwriteAll = true;
                                        break;
                                    case "No to all":
                                        overwriteNone = true;
                                        break;
                                    default:
                                        validInput = false;
                                        System.out.println("Wrong answer. File exists, Do you want to overwrite ? No, Yes, Yes to all, No to all");
                                }
                            }
                        }
                        if (overwrite || overwriteAll ) {
                            Files.copy(file.toPath(), newFile, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println(String.valueOf(newFile) + " overwritten");
                        }
                    }
                    else {
                        Files.copy(file.toPath(), newFile, StandardCopyOption.ATOMIC_MOVE);
                        System.out.println(String.valueOf(newFile) + " written");
                    }

                } catch (IOException se) {

                    se.printStackTrace();
                }
            }
        }

    }

    public static void createSummary(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                if (!file.getPath().equals(String.valueOf(summary))) {
                    //System.out.println("Directory: " + file.getName());
                    summaryList.add(file.getName() + ":");
                    summaryList.add("-----\n");
                    createSummary(file.listFiles()); // Calls same method again.
                    summaryList.add("-----");
                }

            } else { // file
                //summaryList.add(file.getName()+"   |    "+(Files.isWritable(file.toPath()) ? "x" : "")+"   |    "+(Files.isReadable(file.toPath()) ? "x" : ""));
                summaryList.add(String.format("%-20s|%11c          |%11c          |", file.getName(),
                        (Files.isReadable(file.toPath()) ? 'x' : '/'),
                        (Files.isWritable(file.toPath()) ? 'x' : '/')
                ));
            }
        }
    }

}
