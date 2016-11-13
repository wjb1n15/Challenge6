import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient extends Thread
{
	protected String ip;
	protected int port;
	protected Socket soc;
	protected ChatUI ui;
	protected BufferedReader in;
	protected PrintWriter out;
	
	public ChatClient(ChatUI ui, String ip, int port)
	{
		this.ip = ip;
		this.port = port;
		this.ui = ui;
		
		try {
			soc = new Socket(ip, port);
			in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			out = new PrintWriter(soc.getOutputStream(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		while(true) {
			try {
				String message = in.readLine();
				if(message == null) {
					ui.printMessage("You have been disconnected.");
					break;
				}
				ui.printMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void send(String message)
	{
		out.println(message);
	}
}
