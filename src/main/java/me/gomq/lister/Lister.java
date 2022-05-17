package me.gomq.lister;

import me.gomq.lister.Utility.ConsoleColors;
import me.gomq.lister.Utility.Encryptor;
import me.gomq.lister.Utility.ListFile;
import me.gomq.lister.Utility.ListText;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lister {
    private static String encryptionKey;
    public static void main(String[] args) throws Exception {
        boolean addingEnabled = false;
        boolean exitQuestion = false;
        boolean isCurrentEncrypted = false;
        ListFile currentFile = null;
        ListText tempListText = new ListText();

        System.out.print(ConsoleColors.RESET);
        Scanner scan = new Scanner(System.in);
        System.out.println("Lister by GomQ\nVersion 1.0\n\nGithub: https://github.com/igomq/Lister");

        // Check decrypted List Files and remove them
        {
            List<String> fileList = Stream.of(Objects.requireNonNull(new File(ListFile.listerHome).listFiles()))
                    .filter(file -> !file.isDirectory())
                    .map(File::getName)
                    .filter(name -> name.endsWith(".omlister"))
                    .collect(Collectors.toList());

            for (String s : fileList) {
                if (s.contains("-temp")) {
                    File decFile = new File(ListFile.listerHome + s);
                    boolean dummy = decFile.delete();

                    System.out.println("Deleted " + s + " file for security. (This file is decrypted file)");
                }
            }
        }

        for (;;) {
            System.out.print(ConsoleColors.RESET);
            if (exitQuestion) {
                String answer = scan.nextLine();
                switch (answer.toLowerCase()) {
                    case "y", "yes" -> {
                        System.out.println("Shutting down program.");
                        System.exit(0);
                    }
                    case "n", "no" -> {
                        System.out.println("Sure. Save your list by command `done`." + ConsoleColors.RESET);
                        exitQuestion = false;
                    }

                    default -> {
                        System.out.println("Shutdown cancelled." + ConsoleColors.RESET);
                        exitQuestion = false;
                    }
                }
            }
            System.out.print("\nLister> " + ConsoleColors.GREEN_BRIGHT);

            String[] arg = scan.nextLine().split(" ");
            String command = arg[0];

            System.out.print(ConsoleColors.RESET);

            switch (command) {
                case "": break; // 공백 처리
                case "create", "c": {
                    String fileName;
                    try {
                        fileName = arg[1];
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Argument required. Please see help message by `help` command." + ConsoleColors.RESET);
                        break;
                    }

                    if (fileName.contains(".omlister")) {
                        if (fileName.contains(".encrypted")) {
                            System.out.println("Filename cannot include .omlister or .encrypted. Please choose another name.");
                            break;
                        }
                    }

                    ListFile listFile = new ListFile(fileName);
                    boolean exists = listFile.checkFile();

                    if (exists) System.out.println("Created ListFile " + fileName + ".omlister on your home directory." + ConsoleColors.RESET);
                    else System.out.println("File " + fileName + " already exists. Creation cancelled." + ConsoleColors.RESET);
                } break; // 리스트 파일 생성
                case "remove", "r": {
                    String fileName;
                    try {
                        fileName = arg[1];
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Argument required. Please see help message by `help` command." + ConsoleColors.RESET);
                        break;
                    }

                    ListFile listFile = new ListFile(fileName);
                    boolean removed = listFile.removeFile();

                    if (removed) System.out.println("File successfully removed.");
                    else System.out.println("File destruction failed. Maybe file is already removed." + ConsoleColors.RESET);

                    currentFile = null;
                } break; // 리스트 파일 삭제
                case "list", "L": {
                    String f = ListFile.getLists();
                    System.out.print(f);
                } break; // 리스트 파일 목록
                case "load", "l": {
                    String fileName;
                    try {
                        fileName = arg[1];
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Argument required. Please see help message by `help` command." + ConsoleColors.RESET);
                        break;
                    }
                    if (!ListFile.isFileExist(fileName)) {
                        System.out.println(fileName + ".omlister not found. You can create file with `create` command" +
                                "\n(Addition) See file you try to load is 'Encrypted' by `list` command." +
                                "\n           If that file is encrypted, you should decrypt it first by `decrypt` command.");
                        break;
                    }
                    if (fileName.contains(".encrypted")) {
                        System.out.println("You cannot load encrypted file.");
                        break;
                    }

                    isCurrentEncrypted = ListFile.isFileEncryptedFile(fileName);
                    currentFile = new ListFile(fileName + (isCurrentEncrypted ? "-temp" : ""));
                    System.out.println("File "+fileName+".omlister loaded. Now you can see your list or add on your list.");
                } break; // 리스트 파일 로드 (currentFile 에 저장)
                case "show", "s": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command." + ConsoleColors.RESET);
                        break;
                    }
                    String formatted = currentFile.readFile();
                    System.out.println(formatted);
                } break; // 로드된 리스트 파일 내용
                case "add", "a": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command.");
                        break;
                    }
                    addingEnabled = true;
                } break; // 편집모드 활성화 (addingEnabled)
                case "title", "t": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command." + ConsoleColors.RESET);
                        break;
                    }
                    if (!addingEnabled) {
                        System.out.println("Adding mode not enabled. Please enabled editing mode by `add` command." + ConsoleColors.RESET);
                        break;
                    }

                    StringBuilder contentBuilder = new StringBuilder();
                    for (int i=1; i<arg.length; i++) contentBuilder.append(arg[i]).append(" ");

                    String content = contentBuilder.toString();
                    tempListText.setTitle(content);
                } break; // 편집: 타이틀 지정
                case "description", "d": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command." + ConsoleColors.RESET);
                        break;
                    }
                    if (!addingEnabled) {
                        System.out.println("Adding mode not enabled. Please enabled editing mode by `add` command." + ConsoleColors.RESET);
                        break;
                    }
                    try {
                        if (arg[1] == null) { System.out.print(""); }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Argument required. Please see help message by `help` command." + ConsoleColors.RESET);
                        break;
                    }

                    StringBuilder contentBuilder = new StringBuilder();
                    for (int i=1; i<arg.length; i++) contentBuilder.append(arg[i]).append(" ");

                    String content = contentBuilder.toString();
                    tempListText.setDescription(content);
                } break; // 편집: 설명 지정
                case "date", "D": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command." + ConsoleColors.RESET);
                        break;
                    }
                    if (!addingEnabled) {
                        System.out.println("Adding mode not enabled. Please enabled editing mode by `add` command." + ConsoleColors.RESET);
                        break;
                    }
                    try {
                        if (arg[1] == null) { System.out.print(""); }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Argument required. Please see help message by `help` command." + ConsoleColors.RESET);
                        break;
                    }

                    StringBuilder contentBuilder = new StringBuilder();
                    for (int i=1; i<arg.length; i++) contentBuilder.append(arg[i]).append(" ");

                    String content = contentBuilder.toString();
                    tempListText.setDate(content);
                } break; // 편집: 날짜 지정 (포멧은 알아서)
                case "encrypt", "E": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command." + ConsoleColors.RESET);
                        break;
                    }
                    try {
                        if (arg[1] == null) { System.out.print(""); }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Argument required. Please see help message by `help` command." + ConsoleColors.RESET);
                        break;
                    }

                    String passwd = arg[1];
                    Encryptor encryptor = new Encryptor(passwd);
                    try {
                        String encrypted = encryptor.encrypt(currentFile.getListText());

                        File listFile = currentFile.listFile;
                        String fileName = listFile.getName().replaceFirst(".omlister",".encrypted.omlister");

                        File newFile = new File(ListFile.listerHome + fileName);
                        boolean dummy = newFile.createNewFile();

                        BufferedWriter fileWriter = null;
                        try {
                            FileWriter __fw = new FileWriter(newFile, false);
                            fileWriter = new BufferedWriter(__fw);

                            fileWriter.write(encrypted);
                            fileWriter.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (fileWriter != null) {
                                try {
                                    fileWriter.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        currentFile = null;

                        dummy = listFile.delete();

                        System.out.println("Successfully encrypted List File.\n" +
                                "Please re-load this file.\n" +
                                "Addition: Now if you want to load this file, please decrypt first and load.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } break; // 리스트 파일 암호화
                case "decrypt", "C": {
                    String fileName;
                    String passwd;
                    try {
                        fileName = arg[1];
                        passwd = arg[2];

                        encryptionKey = passwd;
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Argument required. Please see help message by `help` command." + ConsoleColors.RESET);
                        break;
                    }

                    Encryptor encryptor = new Encryptor(passwd);
                    try {
                        File f = new File(ListFile.listerHome + fileName + ".encrypted.omlister");
                        if (!f.exists()) {
                            System.out.println(fileName + ".encrypted.omlister Encrypted List File does not found");
                            break;
                        }

                        ListFile lf = new ListFile(fileName + ".encrypted");
                        String encryptedStr = lf.getListText();
                        String decrypted = encryptor.decrypt(encryptedStr);

                        File newFile = new File(ListFile.listerHome + fileName + "-temp.omlister");
                        boolean dummy = newFile.createNewFile();
                        BufferedWriter fileWriter = null;
                        try {
                            FileWriter __fw = new FileWriter(newFile, false);
                            fileWriter = new BufferedWriter(__fw);

                            fileWriter.write(decrypted);
                            fileWriter.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (fileWriter != null) {
                                try {
                                    fileWriter.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            System.out.println("File successfully decrypted and created " + fileName + "-temp file.\n" +
                                    "Now you load this file by command `load fileName (without -temp)`");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } break; // 리스트 파일 복호화
                case "unload", "u": {
                    if (isCurrentEncrypted && currentFile != null) {
                        encrypt(currentFile, encryptionKey);
                        boolean dummy = currentFile.listFile.delete();
                    }
                    currentFile = null;
                    isCurrentEncrypted = false;
                    System.out.println("Successfully unloaded List File.");
                } break; // 리스트 파일 언로드
                case "done", "x": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command." + ConsoleColors.RESET);
                        break;
                    }
                    if (!addingEnabled) {
                        System.out.println("Adding mode not enabled. Please enabled editing mode by `add` command." + ConsoleColors.RESET);
                        break;
                    }
                    if (!tempListText.isSetUp()) {
                        System.out.println("List Text is not set-up. Please add information of List Text." + ConsoleColors.RESET);
                        break;
                    }

                    boolean success = false;
                    try {
                        success = currentFile.writeFile(tempListText);
                    } catch (ListFile.AlreadyCreatedList e) {
                        e.printStackTrace();
                    }

                    System.out.println(success ? "Successfully written List Text on loaded List File." : "FAILED ADDING TEXT.");
                    tempListText = new ListText();
                    addingEnabled = false;
                } break; // 편집모드 종료 후 저장
                case "trash", "T": {
                    if (currentFile == null) {
                        System.out.println("File not loaded. Please load file by `load` command." + ConsoleColors.RESET);
                        break;
                    }
                    try {
                        if (arg[1] == null) { System.out.print(""); }
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Argument required. Please see help message by `help` command." + ConsoleColors.RESET);
                        break;
                    }

                    StringBuilder contentBuilder = new StringBuilder();
                    for (int i=1; i<arg.length; i++) contentBuilder.append(arg[i]).append(" ");
                    String title = contentBuilder.toString();
                    if (currentFile.removeContent(title)) {
                        System.out.println("Successfully removed List Text");
                    } else {
                        System.out.println("Failed to remove List Text. Maybe List File doesn't contain List Text you entered.");
                    }
                } break; // 로드된 리스트 파일 중 일부 내용 삭제
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
                            "decrypt(C) fileName password : Decrypt file and create temp file." +
                            "unload(u) : Unload List File\n" +
                            "help(h) : Show this Message\n" +
                            "exit(X) : Close program\n");
                } break; // 도움말
                case "exit", "X": {
                    if (isCurrentEncrypted && currentFile != null) {
                        encrypt(currentFile, encryptionKey);
                        boolean dummy = currentFile.listFile.delete();
                    }
                    if (addingEnabled) {
                        System.out.print("It seems you are adding list on List File, are you sure you want to end the program?" +
                                ConsoleColors.RESET + "\n Please Enter : Y(yes) N(no)\n y/n : "
                                + ConsoleColors.RESET);
                        exitQuestion = true;
                        break;
                    }
                    System.out.println("Shutting down program.");
                    System.exit(0);
                } break; // 프로그램 종료

                default:
                    printUsage();
                    break; // 없는 명령어 처리
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
                            "[E encrypt] [C decrypt]\n" +
                            "[h help]\n" +
                            "[u unload] [X exit]\n" + ConsoleColors.RESET);
    }

    private static void encrypt(ListFile lf, String key) {
        Encryptor encryptor = new Encryptor(key);
        try {
            String encrypted = encryptor.encrypt(lf.getListText());

            String fileName = lf.listFile.getName().replaceFirst("-temp.omlister",".encrypted.omlister");

            File newFile = new File(ListFile.listerHome + fileName);
            boolean dummy = newFile.createNewFile();

            BufferedWriter fileWriter = null;
            try {
                FileWriter __fw = new FileWriter(newFile, false);
                fileWriter = new BufferedWriter(__fw);

                fileWriter.write(encrypted);
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            dummy = lf.listFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
