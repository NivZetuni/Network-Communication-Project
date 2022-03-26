package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Class that has 2 purposes:
 * 1. Decodes the message from the client by first decoding the first 2 bytes (which represents the OP code),
 * and according to the OP decodes the rest of the message.
 * 2. Encodes the message to the client.
 *
 */


public class OPMessageEncoderDecoder implements MessageEncoderDecoder<OPMessage> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private int opCounter=0;
    private int userInfoCounter=0;
    private HashMap<String,String> info =new HashMap<String, String>();;

    @Override
    public OPMessage decodeNextByte(byte nextByte) {
        if (opCounter > 2 && (Integer.parseInt(info.get("op")))<4)
        {
            OPMessage ans;
            if (nextByte == '\0' && userInfoCounter==0) {
                info.putIfAbsent("userName",popString());
                userInfoCounter++;
            }
            else if(nextByte == '\0'&& userInfoCounter==1){
                info.putIfAbsent("password",popString());
                switch (info.get("op")) {
                    case "1":
                        ans = new OP1AdminRegMessage(Integer.parseInt(info.get("op")), info.get("userName"), info.get("password"));
                        break;
                    case "2":
                        ans = new OP2StudentRegMessage(Integer.parseInt(info.get("op")), info.get("userName"), info.get("password"));
                        break;
                    default:
                        ans = new OP3LoginRequestMessage(Integer.parseInt(info.get("op")), info.get("userName"), info.get("password"));
                        break;
                }
                userInfoCounter = 0;
                info.clear();
                opCounter=0;
                return ans;
            }
        }

        else if (opCounter > 2 && (info.get("op").equals("8"))) {
            OPMessage ans;
            if (nextByte == '\0') {
                info.putIfAbsent("studentUsername", popString());
                ans = new OP8PrintStudStatMessage(Integer.parseInt(info.get("op")), info.get("studentUsername"));
                info.clear();
                opCounter=0;
                return ans;
            }
        }
        else if(opCounter > 2 && (Integer.parseInt(info.get("op"))>4 && Integer.parseInt(info.get("op"))<=10)){
            OPMessage ans;
            if (len==1) {
                pushByte(nextByte);
                info.putIfAbsent("courseNumber", bytesToShort(bytes));
                switch (info.get("op")) {
                    case "5":
                        ans = new OP5RegToCourseMessage(Integer.parseInt(info.get("op")), Integer.parseInt(info.get("courseNumber")));
                        break;
                    case "6":
                        ans = new OP6CheckKdamCourseMessage(Integer.parseInt(info.get("op")), Integer.parseInt(info.get("courseNumber")));
                        break;
                    case "7":
                        ans = new OP7PrintCourseStatMessage(Integer.parseInt(info.get("op")), Integer.parseInt(info.get("courseNumber")));
                        break;
                    case "9":
                        ans = new OP9CheckIfRegMessage(Integer.parseInt(info.get("op")), Integer.parseInt(info.get("courseNumber")));
                        break;
                    default:
                        ans = new OP10UnregCourseMessage(Integer.parseInt(info.get("op")), Integer.parseInt(info.get("courseNumber")));
                        break;
                }
                info.clear();
                opCounter=0;
                return ans;
            }
        }

        pushByte(nextByte);

        if (opCounter==2){
            info.putIfAbsent("op",bytesToShort(bytes));
            OPMessage ans;
            if (info.get("op").equals("4")) {
                ans = new OP4LogoutMessage(Integer.parseInt(info.get("op")));
                info.clear();
                opCounter=0;
                return ans;
            }
            else if (info.get("op").equals("11")) {
                ans = new OP11CheckMyCurrCoursesMessage(Integer.parseInt(info.get("op")));
                info.clear();
                opCounter=0;
                return ans;

            }
        }
        return null; //not a line yet
    }

    @Override
    public byte[] encode(OPMessage message) {
        return (message.toString() ).getBytes(); //uses utf8 by default
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
        opCounter++;
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    public String bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        len = 0;
        String s=""+result;
        return s;
    }
}
