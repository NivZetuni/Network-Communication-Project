package bgu.spl.net.impl.BGRSServer;

import java.util.Vector;

/**
 *  Class that represents student request to check what are the kdam courses for a specific course message actions.
 */

public class OP6CheckKdamCourseMessage implements OPMessage {
    private int opcode;
    private int courseNum ;
    private String loggedInUser;
    public OP6CheckKdamCourseMessage(int opCode, int courseNum) {
        this.opcode=opCode;
        this.courseNum=courseNum;
        this.loggedInUser=null;
    }

    @Override
    public OPMessage react(String s) {
        this.loggedInUser = s;
        Database database = Database.getInstance();
        if (loggedInUser == null) {
            return new OP13ErrMessage(13, (short) 6);
        }
        if (database.getUsersInfo().get(loggedInUser).isAdmin()){
            return new OP13ErrMessage(13, (short) 6);
        }
        if (!(database.getCoursesInfo().containsKey(courseNum))){
            return new OP13ErrMessage(13, (short) 6);
        }
            Vector<Integer> kdamVec = database.getCoursesInfo().get(courseNum).getKdamCourses();
            String kdamCurses = "[";
            for (int j = 0; j < kdamVec.size(); j++) {
                kdamCurses = kdamCurses + kdamVec.get(j) + ",";
            }
            if (kdamCurses.length() == 1) {
                return new OP12AckMessage(12, (short) 6, "[]");
            }
            kdamCurses = kdamCurses.substring(0, kdamCurses.length() - 1);
            kdamCurses = kdamCurses + "]";
            return new OP12AckMessage(12, (short) 6, kdamCurses);
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
