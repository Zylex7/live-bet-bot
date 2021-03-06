package com.zylex.livebetbot.controller.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zylex.livebetbot.service.parser.ParseProcessor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ParseProcessorLogger extends ConsoleLogger {

    private final static Logger LOG = LoggerFactory.getLogger(ParseProcessor.class);

    private AtomicLong parsingStartTime = new AtomicLong(System.currentTimeMillis());

    public void startLogMessage() {
        parsingStartTime.set(System.currentTimeMillis());
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        writeInLine("\nParsing started at " + LocalDateTime.now().format(DATE_TIME_FORMATTER));
        writeLineSeparator();
        LOG.info("Parsing started");
    }

    public void parsingComplete(LogType type) {
        writeInLine(String.format("\nParsing completed in %s", computeTime(parsingStartTime.get())));
        if (type == LogType.OKAY) {
            writeLineSeparator();
        }
        LOG.info("Parsing completed");
    }
}
