package it.grid.storm;

public class Constants {

    public static final Entry BE_VERSION = new Entry("BE-Version", "1.5.3-0.sl4");
    public static Entry NAMESPACE_VERSION = new Entry("Namespace-version", "1.5.0");

    public static class Entry {
        private final String key;
        private final String value;

        private Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

}
