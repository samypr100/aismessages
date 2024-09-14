package dk.tbsalling.ais.cli.converters;

public interface Converter {
    void convert(java.io.InputStream in, java.io.OutputStream out);
}
