package bgu.spl.net.impl.BGRSServer;

/**
 *  Class that represents Admin or Student login message actions.
 */


public class OP3LoginRequestMessage implements OPMessage {
    private int opcode;
    private String username;
    private String password;
    private String loggedInUser;
    public OP3LoginRequestMessage(int opCode, String username, String password) {
        this.opcode=opCode;
        this.username=username;
        this.password = password;
        this.loggedInUser = null;
    }


    @Override
    public OPMessage react(String s) {
        this.loggedInUser = s;
        Database database = Database.getInstance();
        synchronized (database.getUsersInfo()) {
            if (!(database.getUsersInfo().containsKey(username))) {
                return new OP13ErrMessage(13, (short) 3);
            }
            if (!(database.getUsersInfo().get(username).getPassword().equals(password))) {
                return new OP13ErrMessage(13, (short) 3);
            }
            if (!(this.loggedInUser == null)) {
                return new OP13ErrMessage(13, (short) 3);
            }
            if (database.getUsersInfo().get(username).isLoggedIn()) {
                return new OP13ErrMessage(13, (short) 3);
            }
            database.getUsersInfo().get(username).logIn();

        }
        this.loggedInUser = username;
        return new OP12AckMessage(12, (short) 3, "");

    }


    @Override
    public int getOpCode() {
        return opcode;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }
}
