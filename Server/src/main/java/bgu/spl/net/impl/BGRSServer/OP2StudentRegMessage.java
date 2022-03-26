package bgu.spl.net.impl.BGRSServer;

/**
 *  Class that represents Student registration message actions.
 */

public class OP2StudentRegMessage implements OPMessage {
    private int opcode;
    private String username;
    private String password;
    private String loggedInUser;
    public OP2StudentRegMessage(int opCode, String username, String password) {
        this.opcode=opCode;
        this.username=username;
        this.password = password;
        this.loggedInUser = null;
    }

    @Override
    public OPMessage react(String s) {
        loggedInUser = s;
        Database database = Database.getInstance();
        if (loggedInUser != null) {
            return new OP13ErrMessage(13, (short) 2);
        }
        synchronized (database.getUsersInfo()) {
            if (database.getUsersInfo().containsKey(username)) {
                return new OP13ErrMessage(13, (short) 2);
            }
            database.getUsersInfo().put(username, new User(password, false));
        }
            return new OP12AckMessage(12, (short) 2, "");
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
