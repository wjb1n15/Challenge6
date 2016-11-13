import java.util.Scanner;

public class Main 
{
	
	public static void main(String[] args) {
		int port;

		try(Scanner sc = new Scanner(System.in)) {
			if(args.length == 0) {
				System.out.print("Port: ");
				port = sc.nextInt();
			} else {
				port = Integer.parseInt(args[0]);
			}
			ChatServer server = new ChatServer(port);
			server.start();

			while(true) {
				server.command(null, sc.nextLine());
			}
		}
	}

}
