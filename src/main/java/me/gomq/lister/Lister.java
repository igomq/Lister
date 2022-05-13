package me.gomq.lister;

import me.gomq.lister.Utility.ListFile;

import java.io.File;
import java.io.IOException;

public class Lister {
    public static void main(String[] args) throws IOException {
        File loadedFile = null;
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        } else {
            for (String command: args) {
                boolean addingEnabled = false;
                ListFile currentFile;
                switch (command) {
                    case "--create", "-c": {
                        String fileName = args[1];
                        if (fileName == null) {
                            printUsage();
                            System.exit(1);
                        }

                        ListFile listFile = new ListFile(fileName);
                        boolean exists = listFile.checkFile();

                        if (exists) System.out.println("Created ListFile " + fileName + ".omlister on your home directory.");
                        else System.out.println("File " + fileName + " already exists. Creation cancelled.");
                    }
                    case "--remove", "-r": {
                        String fileName = args[1];
                        if (fileName == null) {
                            printUsage();
                            System.exit(1);
                        }

                        ListFile listFile = new ListFile(fileName);
                        boolean removed = listFile.removeFile();

                        if (removed) System.out.println("File successfully removed.");
                        else System.out.println("File destruction failed. Maybe file is already removed.");
                    }
                    case "--load", "-l": {
                        String fileName = args[1];
                        if (fileName == null) {
                            printUsage();
                            System.exit(1);
                        }

                        currentFile = new ListFile(fileName);
                        System.out.println("File "+fileName+".omlister loaded. Now you can see your list or add on your list.");
                    }
                    case "--show", "-s": {
                        if (currentFile == null) {
                            System.out.println("File not loaded. Please ")
                        }
                    }
                    case "--add", "-a":
                    case "--description", "-d":
                    case "--date", "-D":
                    case "--unload", "-u":
                    case "--exit", "-X":

                    default:
                        printUsage();
                        System.exit(1);
                }
            }
        }
    }
    public static void printUsage() {
         System.out.println("lister [-c create] [-r remove] fileName\n" +
                            "       [-l load] fileName\n" +
                            "       [-s show]\n" +
                            "       [-a add]\n" +
                            "           [-t title] Title\n" +
                            "           [-d description] Description\n" +
                           "            [-D date] Date (Any Format)\n" +
                            "       [-u unload] [-X exit]\n");
    }
}
