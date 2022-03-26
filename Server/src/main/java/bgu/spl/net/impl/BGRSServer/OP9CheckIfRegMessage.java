package bgu.spl.net.impl.BGRSServer;

/**
 *  Class that represents Student request to check if he is registered to a specific course message actions.
 */

public class OP9CheckIfRegMessage implements OPMessage {
    private int opcode;
    private int courseNum ;
    private String loggedInUser;
    public OP9CheckIfRegMessage(int opCode, int courseNum) {
        this.opcode=opCode;
        this.courseNum=courseNum;
        this.loggedInUser=null;
    }

    @Override
    public OPMessage react(String s) {
        this.loggedInUser = s;
        Database database = Database.getInstance();
        if (loggedInUser == null) {
            return new OP13ErrMessage(13, (short) 9);
        }
        if (database.getUsersInfo().get(loggedInUser).isAdmin()){
            return new OP13ErrMessage(13, (short) 9);
        }
        if (!(database.getCoursesInfo().containsKey(courseNum))){
            return new OP13ErrMessage(13, (short) 9);
        }
        String reg = "NOT REGISTERED";
        synchronized (database.getUsersInfo().get(loggedInUser).getRegisteredCourses()) {
            if (database.getUsersInfo().get(loggedInUser).getRegisteredCourses().contains(courseNum)) {
                reg = "REGISTERED";
            }
        }
        return new OP12AckMessage(12, (short) 9,reg);
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
