package net.kimleo.rec.loader;

public class LoadingConfig {
    public final String dataFile;
    public final String recFile;

    public LoadingConfig(String dataFile, String recFile) {
        this.dataFile = dataFile;
        this.recFile = recFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoadingConfig that = (LoadingConfig) o;

        if (dataFile != null ? !dataFile.equals(that.dataFile) : that.dataFile != null) return false;
        return recFile != null ? recFile.equals(that.recFile) : that.recFile == null;
    }

    @Override
    public int hashCode() {
        int result = dataFile != null ? dataFile.hashCode() : 0;
        result = 31 * result + (recFile != null ? recFile.hashCode() : 0);
        return result;
    }
}
