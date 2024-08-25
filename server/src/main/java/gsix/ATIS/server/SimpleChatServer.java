package gsix.ATIS.server;

import java.io.IOException;


public class SimpleChatServer
{
	
	private static SimpleServer server;
    public static void main( String[] args ) throws IOException
    {
        server = new SimpleServer(3003);
        NoVolunteerBGThread bgThread = new NoVolunteerBGThread(server);
        Thread thread = new Thread(bgThread);
        thread.start(); // Starts the background thread
        System.out.println("server is listening...");
        server.listen();
    }
}
