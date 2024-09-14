package dk.tbsalling.ais.cli.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dk.tbsalling.aismessages.AISInputStreamReader;
import dk.tbsalling.aismessages.ais.messages.AISMessage;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

public class JsonConverter implements Converter {

    public JsonConverter() {
    }

    @Override
    public void convert(InputStream in, OutputStream out) {
        final PrintStream output = out instanceof PrintStream ? (PrintStream) out : new PrintStream(out);
        final BufferedInputStream input = in instanceof BufferedInputStream ? (BufferedInputStream) in : new BufferedInputStream(in);

        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        AISInputStreamReader streamReader = new AISInputStreamReader(
                input,
                ais -> {
                    try {
                        List<AISMessage> msgs = Collections.singletonList(ais);
                        output.println(mapper.writeValueAsString(msgs));
                    } catch (JsonProcessingException e) {
                        System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                }
        );

        streamReader.run();
    }

}
