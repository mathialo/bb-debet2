/*
 * Copyright (c) 2018. Mathias Lohne
 */

package bbdebet2.kernel.plugins;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public interface Plugin {
    public void run() throws Exception;
    public String getOutput();
    public String getOutputMethod();
    public void setOnSucceed(EventHandler<WorkerStateEvent> handler);
}
