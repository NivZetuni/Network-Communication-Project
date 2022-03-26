package bgu.spl.net.impl.BGRSServer;

/**
 * An interface for messages format.
 */

public interface OPMessage {

    OPMessage react (String s);

    int getOpCode();
    String getLoggedInUser();

}