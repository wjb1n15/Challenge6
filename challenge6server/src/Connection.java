import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection extends Thread
{
	protected Socket clntSoc;
	protected ChatServer server;
	protected PrintWriter out;
	protected BufferedReader in;
	protected String nick;
	protected boolean op;
	protected boolean closing = false;
	
	public Connection(ChatServer server, Socket clntSoc) {
		this.clntSoc = clntSoc;
		this.server = server;
	}
	
	public void run()
	{
		try {
			out = new PrintWriter(clntSoc.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clntSoc.getInputStream()));
			
			send("Enter a nickname.");
			nick = in.readLine().split(" ")[0];
			server.send(nick + " has connected.");
			
			while(true) {
				String message = in.readLine();
				if(message.startsWith("/"))
					server.command(this, message.substring(1));
				else
					server.send(nick + ": " + message);
			}
		} catch (Exception e) {
			halt();
		}
	}

	public void halt() {
		if(closing)
			return;
		closing = true;
		try {
			clntSoc.close();
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		server.send(nick + " has disconnected.");
		server.removeConnection(this);
	}
	
	public void send(String message)
	{
		out.println(message);
	}

	public String getNick() {
		return nick;
	}
	
	public boolean isOp()
	{
		return op;
	}
	
	public void op()
	{
		op = true;
		send("You are now op.");
	}
	
	public void deop()
	{
		op = false;
		send("You are no longer op.");
	}

	public void kick() {
		server.send(nick + " has been kicked.");
		halt();
	}
}
