package com.zylex.livebetbot;

import com.zylex.livebetbot.service.Saver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduledParsingTask implements Runnable {

    private Saver saver;

    @Autowired
    public ScheduledParsingTask(Saver saver) {
        this.saver = saver;
    }

    @Override
    public void run() {
        try {
            saver.save();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
