package me.gomq.lister.Utility;

public class FileEncryptor {
    private static volatile FileEncryptor INSTANCE;

    private final String cipherKey;
    public FileEncryptor(String cipherKey) {
        this.cipherKey = formatKey(cipherKey);
    }

    private String formatKey(String sval) {
        String result = "";
        
        for(int i=0; i<sval.length(); i++) {
            if (String.valueOf(sval.charAt(i)).matches("[a-zA-Z0-9!@+?]")) {
                result += sval.charAt(i);
            }
        }
        result = result.replaceAll(" ", "");

        for (int i=result.length(); i<32-result.length(); i++) {
            result += "0";
        }

        return result;
    }


}
