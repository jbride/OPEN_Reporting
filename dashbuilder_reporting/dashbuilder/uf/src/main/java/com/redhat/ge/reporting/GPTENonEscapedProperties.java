package com.redhat.ge.reporting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Properties;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link TreeMap} implementation that mimics the behaviour of the JDK's {@link Properties} class
 * preventing the '/' or ':' chars from being escaped.
* */
public class GPTENonEscapedProperties extends TreeMap<String,String> {

    private static final long serialVersionUID = 1L;
    private Logger logger = LoggerFactory.getLogger(GPTENonEscapedProperties.class);

    public GPTENonEscapedProperties() {
    }

    public GPTENonEscapedProperties(Comparator<? super String> comparator) {
        super(comparator);
    }

    public void load(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = br.readLine()) != null) {
            putLine(line);
        }
    }

    public void load(Path file) throws IOException {
        for (String line : Files.readAllLines(file)) {
            putLine(line);
        }
    }

    public void store(Writer writer, String... comments) throws IOException {
        StringBuilder out = new StringBuilder();
        if (comments != null) {
            for (String comment : comments) {
                out.append("# ").append(comment).append("\n");
            }
            out.append("\n");
        }
        this.forEach((key,value) -> {
            out.append(key).append("=").append(value).append("\n");
        });

        writer.write(out.toString());
        writer.flush();
    }

    private void putLine(String line) {
        if (line != null) {
            String _line = line.trim();
            if (!_line.isEmpty() && !_line.startsWith("#")) {
                /*
                String[] tokens = _line.split("=");
                if (tokens.length == 1) {
                    super.put(tokens[0], "");
                }
                if (tokens.length == 2) {
                    super.put(tokens[0], tokens[1]);
                }
                */
                int i = _line.lastIndexOf("=");
                String firstS = _line.substring(0, i);
                String secondS = _line.substring(i+1);
                logger.debug("putLine() "+firstS+" : "+secondS);
                super.put(firstS, secondS);
            }
        }
    }
}
