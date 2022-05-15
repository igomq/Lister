package me.gomq.lister.Utility;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListFile {
    public static class CustomString {
        private String data;
        private final String[] chars;
        public CustomString(String data) {
            this.data = data;
            this.chars = data.split("");
        }

        public void replace(String regex) {
            for (int i = 0; i < regex.length(); i++) {
                for (int j=0; j<chars.length; j++) {
                    if (Objects.equals(chars[j], Character.toString(regex.charAt(i)))) {
                        chars[j] = "\0";
                        break;
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            for (String cVal : chars) {
                if (!Objects.equals(cVal, "\0")) {
                    sb.append(cVal);
                }
            }

            this.data = sb.toString();
        }
        public String toString() {
            return data;
        }
    }

    private static final String listerHome = System.getProperty("user.home") + "\\.gom_list\\";
    private final File listFile;
    private boolean isEnabled = true;
    public ListFile(String fileName) {
        this.listFile = new File(listerHome + fileName + ".omlister");
    }

    public boolean checkFile() throws IOException {
        if (!listFile.exists()) {
            boolean dummy = new File(listerHome).mkdirs();
            dummy = listFile.createNewFile();

            return true;
        }
        return false;
    }

    public boolean removeFile() {
        isEnabled = false;
        return this.listFile.exists() && this.listFile.delete();
    }

    public boolean writeFile(ListText text) throws IOException {
        if (!isEnabled) return false;

        BufferedWriter fileWriter = null;
        try {
            FileWriter __fw = new FileWriter(this.listFile, true);
            fileWriter = new BufferedWriter(__fw);

            fileWriter.append(text.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public String readFile() {
        if (!isEnabled) return "";
        try {
            String listText = getListText();

            String[] listContainers = listText.split("\n__SEP_LINE_GOM_LISTER__\n");
            String[] listTitles = new String[listContainers.length];
            String[] listDescriptions = new String[listContainers.length];
            String[] listDates = new String[listContainers.length];

            for (int i = 1; i < listContainers.length; i++) {
                String list = listContainers[i];

                String[] contents = list.split("\n");
                for (String c : contents) {
                    CustomString cs = new CustomString(c);

                    if (c.contains("__LISTER_TITLE")) {
                        cs.replace("__LISTER_TITLE=");
                        listTitles[i] = cs.toString();
                    } else if (c.contains("__LISTER_DESCRIPTION")) {
                        cs.replace("__LISTER_DESCRIPTION=");
                        listDescriptions[i] = cs.toString();
                    } else if (c.contains("__LISTER_DATE")) {
                        cs.replace("__LISTER_DATE=");
                        listDates[i] = cs.toString();
                    }
                }
            }

            String formattedString;
            StringBuilder textBuilder = new StringBuilder();
            for (int i = 1; i < listContainers.length; i++) {
                textBuilder.append("List No. ").append(i).append("\n\n");
                textBuilder.append("==============").append("\n");
                textBuilder.append("Title: ").append(listTitles[i]).append("\n");
                textBuilder.append("Description: ").append(listDescriptions[i] != null ? listDescriptions[i] : "None").append("\n");
                textBuilder.append(listDates[i] != null ? "Written on: " + listDates[i] + "\n" : "");
                textBuilder.append("\n--------------------------------------\n" +
                        "");
            }
            formattedString = textBuilder.toString();

            return formattedString;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return "";
    }
    public String getListText() throws IOException {
        return String.join("\n", Files.readAllLines(Path.of(this.listFile.getPath())));
    }

    public static boolean isFileExist(String fileName) {
        return Files.exists(Path.of(listerHome + fileName + ".omlister"));
    }
    public static String getLists() {
        List<String> fileList = Stream.of(Objects.requireNonNull(new File(listerHome).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fileList.size(); i++) {
            sb.append("File No. ").append(i).append(" -> ").append(fileList.get(i)).append("\n");
        }

        return sb.toString();
    }
}
