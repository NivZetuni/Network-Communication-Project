package bgu.spl.net.impl.BGRSServer;

/**
 *  Class that represents error message from the server for handling a message from the client.
 */

public class OP13ErrMessage implements OPMessage {
    private int opcode;
    private short otherOp;
    private String loggedInUser;

    public OP13ErrMessage(int opCode, short otherOp) {
        this.opcode=opCode;
        this.otherOp=otherOp;
        this.loggedInUser=null;
    }

    @Override
    public String toString() {
        return "ERROR " + otherOp + "\n";
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
