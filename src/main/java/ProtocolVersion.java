public enum ProtocolVersion {
    FIX42("FIX42.xml"),
    FIX44("FIX44.xml");

    private final String resourceName;

    ProtocolVersion(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }
}
