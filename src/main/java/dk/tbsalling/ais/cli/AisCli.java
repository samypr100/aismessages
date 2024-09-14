package dk.tbsalling.ais.cli;

import dk.tbsalling.ais.cli.converters.CsvConverter;
import dk.tbsalling.ais.cli.converters.JsonConverter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.LogManager;

public class AisCli {

    public static final String OPTION_FROMFILE = "file";
    public static final String OPTION_FROMFILE_ABBR = "f";
    public static final String OPTION_INPUT_STRING = "input";
    public static final String OPTION_INPUT_STRING_ABBR = "i";
    public static final String OPTION_OUTPUT = "output";
    public static final String OPTION_OUTPUT_ABBR = "o";
    public static final String OPTION_OUTPUT_FORMAT = "format";
    public static final String OPTION_VERBOSE = "verbose";
    public static final String OPTION_VERBOSE_ABBR = "v";

    public static boolean verbose = false;

    private static final Options OPTIONS = new Options();

    public static void main(String[] args) {
        addOptions();

        if (args.length == 0) {
            help();
            System.exit(-1);
        }

        try {
            parseOptions(args);
            System.exit(0);
        } catch (ParseException e) {
            System.err.println("Command line parsing failed: " + e.getMessage());
            help();
            System.exit(-1);
        }
    }

    private static void addOptions() {
        OPTIONS.addOption(
            Option.builder(OPTION_VERBOSE_ABBR).longOpt(OPTION_VERBOSE)
                .desc("Produce verbose output.")
                .build()
        );
        OPTIONS.addOption(
            Option.builder(OPTION_OUTPUT_ABBR).longOpt(OPTION_OUTPUT)
                .hasArg().argName(OPTION_OUTPUT_FORMAT)
                .desc("Output received messages on stdout in given format ('csv', 'json'). Defaults to 'json'.")
                .build()
        );
        OPTIONS.addOption(
            Option.builder(OPTION_INPUT_STRING_ABBR).longOpt(OPTION_INPUT_STRING)
                .hasArg().argName("string")
                .desc("Decode AIS input from a input string or stdin (via -).")
                .build()
        );
        OPTIONS.addOption(
            Option.builder(OPTION_FROMFILE_ABBR).longOpt(OPTION_FROMFILE)
                .hasArg().argName("file")
                .desc("Decode AIS inputs from a given file.")
                .build()
        );
    }

    private static void help() {
        HelpFormatter formatter = new HelpFormatter();
        String executableName = getExecutableName();
        formatter.printHelp(executableName, OPTIONS, true);
    }

    private static void parseOptions(String[] args) throws ParseException {
        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine cmdLine = commandLineParser.parse(OPTIONS, args);

        if (cmdLine.hasOption(OPTION_VERBOSE)) {
            verbose = true;
        } else {
            LogManager.getLogManager().reset();
        }

        InputStream inputStream = null;
        if (cmdLine.hasOption(OPTION_FROMFILE)) {
            // Handle file input
            String filePath = cmdLine.getOptionValue(OPTION_FROMFILE);
            try {
                inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)));
            } catch (IOException e) {
                System.err.println("Failed to open input file: " + e.getMessage());
                System.exit(-1);
            }
        } else if (cmdLine.hasOption(OPTION_INPUT_STRING)) {
            // Handle string input
            String inputString = cmdLine.getOptionValue(OPTION_INPUT_STRING);
            if ("-".equals(inputString)) {
                // Read from stdin
                inputStream = System.in;
            } else {
                // Handle input as a string
                inputStream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            System.err.println("No valid input option specified.");
            help();
            System.exit(-1);
        }

        // Default to 'json' if no output format is specified
        String format = cmdLine.getOptionValue(OPTION_OUTPUT, "json");

        switch (format) {
            case "csv":
                new CsvConverter().convert(inputStream, System.out);
                break;
            case "json":
                new JsonConverter().convert(inputStream, System.out);
                break;
            default:
                System.err.println("Unknown output format: " + format);
                System.exit(-1);
        }
    }

    private static String getExecutableName() {
        try {
            String execPath = new File(AisCli.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI())
                .getPath();
            return new File(execPath).getName();
        } catch (Exception e) {
            return AisCli.class.getSimpleName(); // Fallback to the current class name if an error occurs
        }
    }

}
