package org.hyperic.hq.hqapi1.tools;

public interface Command {

    int handleCommand(String[] args) throws Exception;
    
    /**
     * 
     * @return The name of the command
     */
    String getName();
}
