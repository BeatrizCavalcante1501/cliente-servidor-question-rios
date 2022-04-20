//importando as bibliotecas
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Cliente { 	//inicio do programa 
	public static void main(String[] args) {
		try {
			DatagramSocket cliente = new DatagramSocket();
			System.out.println("Cliente abrindo conexao na porta: "+String.valueOf(cliente.getLocalAddress()));  //mostra em qual porta o cliente conectou
			
			InetAddress serverAdress = InetAddress.getByName("localhost");  //estabelece a conexão com o servidor
			int porta = 8000;
			
			for(int i = 0; i < 5; i++) { 	//recebimento das respostas
				System.out.print("Digite a resposta "+String.valueOf(i)+": "); 	//pede a resposta da respectiva pergunta
				Scanner in = new Scanner(System.in); 	//pega a resposta do teclado
				String resposta = in.nextLine();
				DatagramPacket req = new DatagramPacket(resposta.getBytes(), resposta.length(), serverAdress, porta); //prepara o pacote
				cliente.send(req); 	//e manda para o servidor
				
				//continua recebendo as outras 4 respostas 
				byte[] buffer = new byte[1024];
				DatagramPacket resp = new DatagramPacket(buffer, buffer.length);
				cliente.receive(resp);
				System.out.println("Resposta do servidor: "+ new String(buffer));  //mostra a resposta do servidor após as 5 entradas
			}
			
			//mostrar o gabarito para o cliente
			System.out.println("Gabarito: "); 
			for (int i = 0; i < 5; i++) {
				byte[] buffer = new byte[1024];
				DatagramPacket resp = new DatagramPacket(buffer, buffer.length); 	//prepara para receber a resposta
				cliente.receive(resp); 	//espera respostas
				System.out.println(new String(buffer)); 	//mostra as 5 respostas
			}
			
			cliente.close(); 	//finaliza o programa
		} catch (Exception e) { 	//tratamento de erro
			e.printStackTrace();
		}
	}
}