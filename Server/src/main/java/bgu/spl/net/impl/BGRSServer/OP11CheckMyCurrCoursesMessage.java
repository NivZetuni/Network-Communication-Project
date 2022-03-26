package bgu.spl.net.impl.BGRSServer;

/**
 *  Class that represents student request to get his courses  message actions.
 */


public class OP11CheckMyCurrCoursesMessage implements OPMessage {
    private int opcode;
    private String loggedInUser;

    public OP11CheckMyCurrCoursesMessage(int opCode) {
        this.opcode=opCode;
        this.loggedInUser=null;
    }

    @Override
    public OPMessage react(String s) {
        this.loggedInUser = s;
        Database database = Database.getInstance();
        if (loggedInUser == null) {
            return new OP13ErrMessage(13, (short) 10);
        }
        if (database.getUsersInfo().get(loggedInUser).isAdmin()) {
            return new OP13ErrMessage(13, (short) 10);
        }
        synchronized (database.getUsersInfo().get(loggedInUser).getRegisteredCourses()) {
            User u = database.getUsersInfo().get(loggedInUser);
            u.getRegisteredCourses().sort((Integer c1, Integer c2) -> database.getCourseOrder().indexOf(c1) - database.getCourseOrder().indexOf(c2));
            String myCourses = "[";
            for (int j = 0; j < u.getRegisteredCourses().size(); j++) {
                myCourses = myCourses + u.getRegisteredCourses().get(j) + ",";
            }
            if (myCourses.length() == 1) {
                return new OP12AckMessage(12, (short) 11, "[]");
            }
            myCourses = myCourses.substring(0, myCourses.length() - 1);
            myCourses = myCourses + "]";
            return new OP12AckMessage(12, (short) 11, myCourses);
        }
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
