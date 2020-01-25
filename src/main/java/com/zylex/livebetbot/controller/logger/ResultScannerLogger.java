package com.zylex.livebetbot.controller.logger;

import com.zylex.livebetbot.service.ResultScanner;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ResultScannerLogger extends ConsoleLogger {

    private final static Logger LOG = Logger.getLogger(ResultScanner.class);

    public synchronized void startLogMessage() {
        writeInLine("\nResult scanning: ...");
        LOG.info("Result scanning");
    }

    public void endLogMessage(LogType type, int gamesNumber) {
        String output = "";
        if (type == LogType.OKAY) {
            output = String.format("Result scanning: complete (found %d results)", gamesNumber);
            if (gamesNumber == 0) {
                output = "Result scanning: complete (no results found)";
            }
        } else if (type == LogType.NO_GAMES) {
            output = "Result scanning: complete (no games to scan)";
        } else if (type == LogType.ERROR) {
            output = "Result scanning: timeout error";
        }
        writeInLine(StringUtils.repeat("\b", output.length()) + output);
        LOG.info(output);
    }
}
