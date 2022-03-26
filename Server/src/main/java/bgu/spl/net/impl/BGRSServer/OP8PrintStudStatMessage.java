package bgu.spl.net.impl.BGRSServer;

/**
 *  Class that represents Admin request to get the status of a specific student message actions.
 */

public class OP8PrintStudStatMessage implements OPMessage {
    private int opcode;
    private String studUsername ;
    private String loggedInUser;
    public OP8PrintStudStatMessage(int opCode, String studUsername) {
        this.opcode=opCode;
        this.studUsername=studUsername;
        this.loggedInUser=null;
    }

    @Override
    public OPMessage react(String s) {
        this.loggedInUser = s;
        Database database = Database.getInstance();
        if (loggedInUser == null) {
            return new OP13ErrMessage(13, (short) 8);
        }
        if (!(database.getUsersInfo().get(loggedInUser).isAdmin())){
            return new OP13ErrMessage(13, (short) 8);
        }
        synchronized (database.getUsersInfo()) {
            if (!(database.getUsersInfo().containsKey(studUsername))) {
                return new OP13ErrMessage(13, (short) 8);
            }
            if (database.getUsersInfo().get(studUsername).isAdmin()) {
                return new OP13ErrMessage(13, (short) 8);
            }
        }
        synchronized (database.getUsersInfo().get(studUsername).getRegisteredCourses()) {
            User u = database.getUsersInfo().get(studUsername);
            u.getRegisteredCourses().sort((Integer c1, Integer c2) -> database.getCourseOrder().indexOf(c1) - database.getCourseOrder().indexOf(c2));
//        String regCourses = Arrays.toString(u.getRegisteredCourses().toArray());
            String regCourses = "[";
            for (int j = 0; j < u.getRegisteredCourses().size(); j++) {
                regCourses = regCourses + u.getRegisteredCourses().get(j) + ",";
            }
            if (regCourses.length() == 1) {
                String stat = "Student: " + studUsername + "\n" + "Courses: " + "[]";
                return new OP12AckMessage(12, (short) 8, stat);
            }
            regCourses = regCourses.substring(0, regCourses.length() - 1);
            regCourses = regCourses + "]";
            String stat = "Student: " + studUsername + "\n" + "Courses: " + regCourses;
            return new OP12AckMessage(12, (short) 8, stat);
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
