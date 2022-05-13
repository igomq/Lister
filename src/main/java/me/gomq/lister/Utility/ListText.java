package me.gomq.lister.Utility;

import static me.gomq.lister.Lister.printUsage;

public class ListText {
    public static class NoTitleError extends RuntimeException {
        private static final String errorMessage = "No Title On Entered Text.";
        public NoTitleError() {
            super(errorMessage);
        }
    }
    public static class UnacceptedCharacterError extends RuntimeException {
        private static final String errorMessage = "Unaccepted Character Entered.";
        public UnacceptedCharacterError() {
            super(errorMessage);
        }
    }

    private String title;
    private String description;
    private String date;

    private void checkUnacceptedCharacter(String characters) {
        if (characters.contains("__SEP_LINE_GOM_LISTER__")
                || characters.contains("__LISTER_TITLE")
                || characters.contains("__LISTER_DESCRIPTION")
                || characters.contains("__LISTER_DATE")
            ) {
            throw new UnacceptedCharacterError();
        }
    }

    public void setTitle(String title) {
        checkUnacceptedCharacter(title);
        this.title = title;
    }
    public void setDescription(String description) {
        checkUnacceptedCharacter(description);
        this.description = description;
    }
    public void setDate(String date) {
        checkUnacceptedCharacter(date);
        this.date = date;
    }

    public String toString() {
        if (title == null) {
            printUsage();
            throw new NoTitleError();
        }
        String formattedString = "\n__SEP_LINE_GOM_LISTER__\n" + "__LISTER_TITLE=" + title + "\n" + "__LISTER_DESCRIPTION=" + description;
        formattedString += date != null ? "\n__LISTER_DATE=" + date : "";
        return formattedString;
    }
}
