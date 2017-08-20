package petproject.server.utils;

public class StarterArgumentsHandler {
    private static final String PORT_PROPERTY_KEY = "-p";
    public static final int PROPERTY_NOT_DEFINED = -1;

    public static int getPort(String[] args) throws PortFormatException {
        int portKeyIndex = findPortPropertyKeyArgumentIndex(args);
        if (portKeyIndex == PROPERTY_NOT_DEFINED) {
            return PROPERTY_NOT_DEFINED;
        }

        int nextArgumentIndex = portKeyIndex + 1;
        if (nextArgumentIndex < args.length) {
            try {
                return Integer.valueOf(args[nextArgumentIndex]);
            } catch (NumberFormatException e) {
                throw new PortFormatException(args[nextArgumentIndex]);
            }
        }
        throw new PortFormatException();
    }

    private static int findPortPropertyKeyArgumentIndex(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (PORT_PROPERTY_KEY.equals(args[i])) {
                return i;
            }
        }
        return PROPERTY_NOT_DEFINED;
    }
}
