module dk.tbsalling.ais.messages {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.apache.commons.cli;
    requires org.apache.commons.csv;
    requires java.logging;
    exports dk.tbsalling.ais.cli.converters;
    exports dk.tbsalling.ais.cli;
    exports dk.tbsalling.aismessages.ais.exceptions;
    exports dk.tbsalling.aismessages.ais.messages.asm;
    exports dk.tbsalling.aismessages.ais.messages.types;
    exports dk.tbsalling.aismessages.ais.messages;
    exports dk.tbsalling.aismessages.ais;
    exports dk.tbsalling.aismessages.nmea.exceptions;
    exports dk.tbsalling.aismessages.nmea.messages;
    exports dk.tbsalling.aismessages.nmea;
    exports dk.tbsalling.aismessages;
}
