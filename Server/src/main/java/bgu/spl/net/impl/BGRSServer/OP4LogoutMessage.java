package bgu.spl.net.impl.BGRSServer;

/**
 *  Class that represents Admin or student logout message actions.
 */

public class OP4LogoutMessage implements OPMessage {
    private int opcode;
    private String loggedInUser;

    public OP4LogoutMessage(int opCode) {
        this.opcode=opCode;
        this.loggedInUser=null;
    }

    @Override
    public OPMessage react(String s) {
        this.loggedInUser = s;
        Database database = Database.getInstance();
        if (loggedInUser == null) {
            return new OP13ErrMessage(13, (short) 4);
        }
        synchronized (database.getUsersInfo()) {
            database.getUsersInfo().get(loggedInUser).logOut();
        }
        return new OP12AckMessage(12, (short) 4, "");
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
