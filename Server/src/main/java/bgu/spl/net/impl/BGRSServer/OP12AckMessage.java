package bgu.spl.net.impl.BGRSServer;

/**
 *  Class that represents acknowledge message from the server for handling a message from the client.
 */

public class OP12AckMessage implements OPMessage {
    private int opcode;
    private String optional;
    private short otherOp;
    private String loggedInUser;
    public OP12AckMessage(int opCode, short otherOp, String optional ) {
        this.opcode=opCode;
        this.otherOp=otherOp;
        this.optional=optional;
        this.loggedInUser=null;
    }

    @Override
    public String toString() {
        if(optional.equals("")){
            return "ACK " + otherOp +"\n";
        }
        return "ACK " + otherOp +"\n" + optional +"\n";
    }

    @Override
    public OPMessage react(String s) {
        return null;
    }

    @Override
    public int getOpCode() {
        return opcode;
    }

    @Override
    public String getLoggedInUser() {
        return loggedInUser;
    }
}
