package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.Server;


public class TPCMain {

    public static void main(String[] args) {
        BaseServer server = (BaseServer) Server.threadPerClient(
                Integer.decode(args[0]),  //port
                () -> new OPMessageProtocol(),
                () -> new OPMessageEncoderDecoder());
        Database.getInstance().initialize("./Courses.txt");
        server.serve();
    }

    }