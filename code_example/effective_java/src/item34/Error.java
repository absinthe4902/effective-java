package item34;

/**
 * @author SeungminLee
 * project effective_java
 * date 2020-08-13
 * description
 */
public enum Error {
    OK("Okay", 1),
    ERROR("Error", -1),
    ERROR_DB("Error_db", -2),
    ERROR_SERVER("Error_server", -3),
    ERROR_NO_PARAM("Error_no_param", -4);

    private final String message;
    private final int code;

    Error(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public void printEnum(Error error) {
        System.out.println(error.toString());
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}


