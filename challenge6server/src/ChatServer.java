import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ChatServer extends Thread
{
	protected int port;
	protected ArrayList<Connection> connections;
	protected ServerSocket svrSoc;
	
	public ChatServer(int port)
	{
		this.port = port;
		connections = new ArrayList<Connection>();
	}

	public void run()
	{
		try {
			svrSoc = new ServerSocket(port);
			System.out.println("Running");
			while(true) {
				Socket clntSoc = svrSoc.accept();
				Connection client = new Connection(this, clntSoc);
				client.start();
				connections.add(client);
			}
		} catch(SocketException e) {

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void halt()
	{
		try {
			svrSoc.close();
			for(Connection c : connections)
				c.halt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void send(String message)
	{
		for(Connection connection : connections) {
			connection.send(message);
		}
		
		System.out.println("SENT: " + message);
	}
	
	public void removeConnection(Connection toRemove)
	{
		connections.remove(toRemove);
	}

	public void command(Connection sender, String message) {
		if(sender != null)
			System.out.println("Command from " + sender.getNick() + ": " + message);
		
		String[] words = message.split(" ");
		
		if(words[0].equals("me"))
			comMe(sender, message);
		else if(words[0].equals("say"))
			comSay(sender, message);
		else if(words[0].equals("op")) {
			comOp(sender, words);
		} else if(words[0].equals("deop")) {
			comDeop(sender, words);
		} else if(words[0].equals("kick")) {
			comKick(sender, words);
		} else if(words[0].equals("list")) {
			comList(sender);
		}
	}

	private void comList(Connection sender) {
		for(Connection connection : connections) {
			if(sender == null)
				System.out.println(connection.getNick());
			else
				sender.send(connection.getNick());
		}
	}

	private void comKick(Connection sender, String[] words) {
		if(sender == null || sender.isOp())
			for(Connection connection : connections) {
				if(connection.getNick().equals(words[1])) {
					connection.kick();
				}
			}
	}

	private void comDeop(Connection sender, String[] words) {
		if(sender == null || sender.isOp())
			for(Connection connection : connections) {
				if(connection.getNick().equals(words[1])) {
					connection.deop();
					System.out.println(words[1] + " is no longer op.");
				}
			}
	}

	private void comOp(Connection sender, String[] words) {
		if(sender == null || sender.isOp())
			for(Connection connection : connections) {
				if(connection.getNick().equals(words[1])) {
					connection.op();
					System.out.println(words[1] + " is now op.");
				}
			}
	}

	private void comSay(Connection sender, String message) {
		if(sender == null) {
			send("Server:" + message.substring(message.indexOf(' ')));
		} else {
			send(sender.getNick() + ":" + message.substring(message.indexOf(' ')));
		}
	}

	private void comMe(Connection sender, String message) {
		if(sender == null) {
			send("Server" + message.substring(message.indexOf(' ')));
		} else {
			send(sender.getNick() + message.substring(message.indexOf(' ')));
		}
	}
	
	
}
