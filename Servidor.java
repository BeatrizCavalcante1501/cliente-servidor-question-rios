//importando as bibliotecas
import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Servidor { 	//inicia o servidor
   public static void main(String args[]) {
	    ArrayList<Conexao> lista_clientes = new ArrayList<>(); //guarda ou cria a conexão quando o cliente entra
        DatagramSocket s = null;
        
        try {
            s = new DatagramSocket(8000);
            byte[] buffer = new byte[1024];
            
            while (true) { 	//mantem "ligado" 
            	
                System.out.println("*** Servidor aguardando request");
                DatagramPacket req = new DatagramPacket(buffer, buffer.length); 
                s.receive(req); 	//esperando uma requisição 
                System.out.println("*** Request recebido de: " + req.getSocketAddress()); 	//mostra as infos do pacote
                
                int busca = existe(req.getPort(), lista_clientes); 	//verifica se o cliente ja conectou antes e (se sim) mostra seu endereço
                
                if (busca != -1) { 		//se o cliente ja tiver acessado antes
                	if(lista_clientes.get(busca).adicionarResposta(new String(req.getData()))) { 	//quando ja é a ultima resposta
                		ArrayList<String> gabarito = lista_clientes.get(busca).verificaGabarito(new Questionario().getGabarito()); 	//junta os gabaritos
                		DatagramPacket resp; 	//mostra o gabarito completo
                		Conexao cliente = lista_clientes.get(busca);
                		
                		String mensagem = "OK!";
                		resp = new DatagramPacket(mensagem.getBytes(), mensagem.length(), cliente.getAdress(), cliente.getPorta());
                		s.send(resp);
                		for(String i : gabarito) { 	//manda uma resposta de cada vez
                			resp = new DatagramPacket(i.getBytes(), i.length(), cliente.getAdress(), cliente.getPorta());
                			s.send(resp);
                		}
                	} else {	//adiciona a resposta, confirma e continua
                		Conexao cliente = lista_clientes.get(busca);
                		String mensagem = "OK!";
                		DatagramPacket resp = new DatagramPacket(mensagem.getBytes(), mensagem.length(), cliente.getAdress(), cliente.getPorta());//monta a resosta do cadastro
                		s.send(resp);
                	}
                } else { 	//se for o primeiro acesso
                	lista_clientes.add(new Conexao(req.getPort(), req.getAddress(), new String(req.getData()))); 	//cria um novo cadastro
            		String mensagem = "OK!";
            		DatagramPacket resp = new DatagramPacket(mensagem.getBytes(), mensagem.length(), req.getAddress(), req.getPort()); 	//monta a resosta do cadastro
            		s.send(resp); 	//manda a resposta
                }
            }
        } catch (SocketException e) { 	//exceção para erro de conexão entre as pontas
            System.out.println("Erro de socket: " + e.getMessage());
        } catch (IOException e) { 	//exceção para erro de envio ou recepção
            System.out.println("Erro envio/recepcao pacote: " + e.getMessage());         
        } finally { 	//finaliza o programa
            if (s != null) s.close();
        }     
    }
   
   public static int existe(int porta, ArrayList<Conexao> array) { 	//função que verifica se o cliente ja acessou anteriormente 
	   for (int i = 0; i < array.size(); i++) { 	//percorre todo o vetor 
		   if(array.get(i).getPorta() == porta) return i; 	//se a porta atual for igual a q foi recebida, ele retorna o seu endereço
	   }
	   return -1;
   }
}

class Conexao { 	//estabelece a conexão e seus atribudos 
	//dados do cliente
	private int porta;
	private InetAddress adress;
	private ArrayList<String> respostas = new ArrayList<>();
	
	//construtor da classe que passa os valores quando é criado o objeto
	Conexao(int porta, InetAddress adress, String resposta) {
		this.setPorta(porta);
		this.getRespostas().add(resposta);
		this.setAdress(adress);
	}
	
	public boolean adicionarResposta(String resposta) {  //adiciona a resposta e verifica o gabarito 
		this.getRespostas().add(resposta); 	//adiciona a resposta
		if(this.getRespostas().size() >= Questionario.qtde_respostas) return true; 	//verifica se ja respondeu tudo
		else return false; 	//se não, volta para receber mais
	}
	
	public ArrayList<String> verificaGabarito(ArrayList<String> gabarito) {  //verifica quantos acertos e quantos erros tiveram no final
		ArrayList<String> resultado = new ArrayList<>();
		for(int i = 0; i < gabarito.size(); i++) {
			String[] rGabarito = gabarito.get(i).split(";")[2].split("");
			String[] rResposta = this.getRespostas().get(i).split(";")[2].split("");
			int acertos = 0;
			
			for(int j = 0; j < rGabarito.length; j++) {
				if(rGabarito[j].equals(rResposta[j])) acertos++;
			}
			
			resultado.add(String.valueOf(i+1)+";"+String.valueOf(acertos)+";"+String.valueOf(gabarito.size()-acertos));  //monta a resposta com o numero da questão, quantidade de acertos e de erros
		}
		return resultado; 	//mostra a resposta 
	}

	public int getPorta() { 	//recebe a porta
		return porta;
	}

	public void setPorta(int porta) { 	//envia a porta
		this.porta = porta;
	}

	public InetAddress getAdress() { 	//recebe o endereço
		return adress;
	}

	public void setAdress(InetAddress adress) { 	//envia o endereço
		this.adress = adress;
	}

	public ArrayList<String> getRespostas() { 	//recebe a resposta
		return respostas;
	}

	public void setRespostas(ArrayList<String> respostas) { 	//envia a resposta
		this.respostas = respostas;
	}
}

class Questionario { 	//Guarda as questões e suas respectivas respostas 
	private ArrayList<String> gabarito = new ArrayList<>(); 	//cria o array para o gabarito
	
	public ArrayList<String> getGabarito() { 	//recebe o array de gabarito
		return gabarito;
	}

	public void setGabarito(ArrayList<String> gabarito) { 	//envia o array com o gabarito
		this.gabarito = gabarito;
	}

	public static int qtde_respostas = 5; 	//quantidade de respostas para receber
	
	Questionario() { 	//gabarito
		this.gabarito.add("1;5;VVFFV");
		this.gabarito.add("2;5;VFFFF");
		this.gabarito.add("3;5;VVVVV");
		this.gabarito.add("4;5;FVFFV");
		this.gabarito.add("5;5;VVFVV");
	}
}