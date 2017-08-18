package petproject.server;

class PortFormatException extends Exception {
    public PortFormatException(String port) {
        super("Port \"" + port + "\" is in incorrect format");
    }

    public PortFormatException() {
        super("Port isn't defined");
    }
}
