package me.gomq.lister.Utility;

import java.io.*;

public class ListFile {
    public static class CustomString {
        private String data;
        public CustomString(String data) {
            this.data = data;
        }

        public void replace(String regex) {
            for (int i = 0; i < regex.length(); i++) {
                data = data.replace(regex.charAt(i), '\0');
            }
            data = data.replace('=', '\0');
        }
        public String toString() {
            return data;
        }
    }

    private static final String listerHome = System.getProperty("user.home") + "\\.gom_list\\";
    private int isFirstTimeListing = 0;
    private final File listFile;
    private boolean isEnabled = true;
    public ListFile(String fileName) {
        this.listFile = new File(listerHome + fileName + ".omlister");
    }

    public boolean checkFile() throws IOException {
        if (!listFile.exists()) {
            boolean dummy = listFile.mkdirs();
            dummy = listFile.createNewFile();

            isFirstTimeListing = 1;

            return true;
        }
        return false;
    }

    public boolean removeFile() {
        isEnabled = false;
        return listFile.exists() && listFile.delete();
    }

    public boolean writeFile(ListText text) throws IOException {
        if (!isEnabled) return false;
        try {
            FileWriter __fw = new FileWriter(listFile);
            BufferedWriter fileWriter = new BufferedWriter(__fw);

            String listText = getListText();
            if (isFirstTimeListing == 1) {
                fileWriter.write(text.toString());
            } else {
                fileWriter.write(listText + text.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
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

            for (int i = 0; i < listContainers.length; i++) {
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
            for (int i = 0; i < listContainers.length; i++) {
                textBuilder.append("List No. ").append(i);
                textBuilder.append("==============");
                textBuilder.append("Title: ").append(listTitles[i]);
                textBuilder.append("Description: ").append(listDescriptions[i] != null ? listDescriptions[i] : "None");
                textBuilder.append(listDates[i] != null ? "Written on: " + listDates[i] : "");
                textBuilder.append("\n--------------------------------------\n");
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
        FileReader __fr = new FileReader(listFile);
        BufferedReader fileReader = new BufferedReader(__fr);

        StringBuilder listTextBuilder = new StringBuilder();
        String listText;
        int fileCharacter;
        while ((fileCharacter = fileReader.read()) != -1) {
            listTextBuilder.append((char) fileCharacter);
        }

        listText = listTextBuilder.toString();
        return listText;
    }
}
