package me.gomq.lister;

import me.gomq.lister.Utility.ListFile;
import me.gomq.lister.Utility.ListText;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Lister {
    public static void main(String[] args) throws IOException {
        boolean addingEnabled = false;
        ListFile currentFile = null;
        ListText tempListText = new ListText();

        Scanner scan = new Scanner(System.in);
        System.out.println("Lister by GomQ\nVersion 1.0a\n\nGithub: https://github.com/igomq/Lister\n");
        for (;;) {
            System.out.print("Lister> ");

            String[] arg = scan.nextLine().split(" ");
            String command = arg[0];

            switch (command) {
                case "": break;
                case "create", "c": {
                    if (arg[1] == null) {
                        printUsage();
                        System.exit(1);
                    }
                    String fileName = arg[1];

                    ListFile listFile = new ListFile(fileName);
                    boolean exists = listFile.checkFile();

                    if (exists) System.out.println("Created ListFile " + fileName + ".omlister on your home directory.");
                    else System.out.println("File " + fileName + " already exists. Creation cancelled.");
                } break;
                case "remove", "r": {
                    if (arg[1] == null) {
                        printUsage();
                        System.exit(1);
                    }
                    String fileName = arg[1];

                    ListFile listFile = new ListFile(fileName);
                    boolean removed = listFile.removeFile();

                    if (removed) System.out.println("File successfully removed.");
                    else System.out.println("File destruction failed. Maybe file is already removed.");

                    currentFile = null;
                } break;
                case "list", "L": {
                    String f = ListFile.getLists();
                    System.out.println(f);
                } break;
                case "load", "l": {
                    if (arg[1] == null) {
                        printUsage();
                        System.exit(1);
                    }

                    String fileName = arg[1];
                    if (!ListFile.isFileExist(fileName)) {
                        System.out.println(fileName + ".omlister not found. You can create file with `create` command");
                        break;
                    }

                    currentFile = new ListFile(fileName);
                    System.out.println("File "+fileName+".omlister loaded. Now you can see your list or add on your list.");
                } break;
                case "show", "s": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command.");
                        break;
                    }
                    String formatted = currentFile.readFile();
                    System.out.println(formatted);
                } break;
                case "add", "a": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command.");
                        break;
                    }
                    addingEnabled = true;
                } break;
                case "title", "t": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command.");
                        break;
                    }
                    if (!addingEnabled) {
                        System.out.println("Adding mode not enabled. Please enabled editing mode by `add` command.");
                        break;
                    }
                    if (arg[1] == null) {
                        System.out.println("Argument required. Please see help message by `help` command.");
                        break;
                    }

                    StringBuilder contentBuilder = new StringBuilder();
                    for (int i=1; i<arg.length; i++) contentBuilder.append(arg[i]).append(" ");

                    String content = contentBuilder.toString();
                    tempListText.setTitle(content);
                } break;
                case "description", "d": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command.");
                        break;
                    }
                    if (!addingEnabled) {
                        System.out.println("Adding mode not enabled. Please enabled editing mode by `add` command.");
                        break;
                    }
                    if (arg[1] == null) {
                        System.out.println("Argument required. Please see help message by `help` command.");
                        break;
                    }

                    StringBuilder contentBuilder = new StringBuilder();
                    for (int i=1; i<arg.length; i++) contentBuilder.append(arg[i]).append(" ");

                    String content = contentBuilder.toString();
                    tempListText.setDescription(content);
                } break;
                case "date", "D": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command.");
                        break;
                    }
                    if (!addingEnabled) {
                        System.out.println("Adding mode not enabled. Please enabled editing mode by `add` command.");
                        break;
                    }
                    if (arg[1] == null) {
                        System.out.println("Argument required. Please see help message by `help` command.");
                        break;
                    }

                    StringBuilder contentBuilder = new StringBuilder();
                    for (int i=1; i<arg.length; i++) contentBuilder.append(arg[i]).append(" ");

                    String content = contentBuilder.toString();
                    tempListText.setDate(content);
                } break;
                case "encrypt", "E": break;
                case "unload", "u": break;
                case "done", "x": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command.");
                        break;
                    }
                    if (!addingEnabled) {
                        System.out.println("Adding mode not enabled. Please enabled editing mode by `add` command.");
                        break;
                    }
                    if (!tempListText.isSetUp()) {
                        System.out.println("List Text is not set-up. Please add information of List Text.");
                        break;
                    }

                    boolean success = currentFile.writeFile(tempListText);
                    System.out.println(success ? "Successfully written List Text on loaded List File." : "FAILED ADDING TEXT.");
                    tempListText = new ListText();
                    addingEnabled = false;
                } break;
                case "trash", "T": break;
                case "help", "h": {
                    System.out.println("GomLister Help\n\n" +
                            "create(c) fileName : Create .omlister List File\n" +
                            "remove(r) fileName : Remove specified List File\n" +
                            "list(L) : Show List File list." +
                            "load(l) fileName : Load .omlister file\n" +
                            "show(s) : Show list's content\n" +
                            "add(a) : Enable adding mode, if not enabled, you cannot edit your file.\n" +
                            "   - title(t) text : Set new list's title. [ REQUIRED ]\n" +
                            "   - description(d) text: Set new list's description. [ OPTIONAL ]\n" +
                            "   - date(D) text: Set new list's description. [ OPTIONAL ]\n" +
                            "done(x) : Disable adding mode, add entered list on loaded file.\n" +
                            "trash(T) title : Remove list which has same title of entered.\n" +
                            "encrypt(E) password : Encrypt List File with AES-256 with entered password(key)\n" +
                            "   ** password max length : 32 characters, only accept alphabets, numbers, !, ?, @ and +.\n" +
                            "unload(u) : Unload List File\n" +
                            "help(h) : Show this Message\n" +
                            "exit(X) : Close program\n");
                } break;
                case "exit", "X": {
                    System.out.println("Shutting down program: GomLister");
                    System.exit(0);
                } break;

                default:
                    printUsage();
                    break;
            }
        }
    }
    public static void printUsage() {
         System.out.println("[c create] [r remove] fileName\n" +
                            "[l load] fileName\n" +
                            "[s show]\n" +
                            "[a add]\n" +
                            "    [t title] Title\n" +
                            "    [d description] Description\n" +
                            "    [D date] Date (Any Format)\n" +
                            "[x done]\n" +
                            "[t trash]\n" +
                            "[E encrypt]\n" +
                            "[h help]\n" +
                            "[u unload] [X exit]\n");
    }
}
