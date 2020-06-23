package AnalizadorDOsCero;

import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JOptionPane;

public class Sintak {
	
	ListaSencilla lex= new ListaSencilla();         	// Lo que arroja el analisis lexico
	ListaSencilla elexe= new ListaSencilla();			// Fila de entrantes
	ListaSencilla terminales= new ListaSencilla();		// Columna de terminales
	Stack<String> pila = new Stack<String>();
	String MensajeDeError = "";
	String MensajeDePila = "";
	String Simbol = "";
	String PreviusSimb = "";
	String [][] tabla1;
	int linea = 0, cerra=0, cera=0;
	boolean errP = false, inicio=false, bandecoma=false, banderafinal=false; 
	boolean vuelta = false; //controla el recorrido 
	
	//Este metodo llena la fila y columna en los arrays creados para ahorrarnos bucles de b�squeda
	public void llenarFyC() {
		for (int i = 0; i < tabla1.length; i++) {
			terminales.addValue(tabla1[i][0]);
		}
		for (int i = 0; i < tabla1[0].length; i++) {
			elexe.addValue(tabla1[0][i]);
		}
	}
	
	//Este es el constructor que recibe todo el pedo y inicia lo esensial
	public Sintak() {
		vuelta=false;
		banderafinal=false;
		bandecoma=false;
		tablas t = new tablas();
		//tabla1 = t.laperrona;
		tabla1 = t.laperrona2;
		llenarFyC();
		cera=0;
		pila.push("clas");
	}
	
	
	//Revisa si es aceptado
	public boolean aceptado() {
		if (pila.isEmpty() && MensajeDeError.isEmpty()) {
			pila.clear();
			return true;
		}else {
			//MensajeDeError += "Error de Sintaxis3: "+lex.getValor(lex.listLenght()-1)+" despu�s de "+ lex.getValor(lex.listLenght()-1)+" en la l�nea "+ linea+"\n";errP = false;
			return false;
		}
	}
	public int verfinales() {
		return cera;
	}
	//Este es el �nico metodo que se llama
	public boolean AS(String lexema, int line, String simbol) {
		linea = line;
		Simbol = simbol;
		if (vuelta) {
			MensajeDePila = "";
		}else {
			MensajeDePila += pila;
			vuelta = true;
		}
		lex.addValue(lexema);
		if(lexema.equals("abP") && cerra<1) {
			cerra ++;
		}
		procesoApilAndDesapil(lex.listLenght()-1);
		if(lexema.equals("ciP")) { //debe poder sacar una llave
			if (cerra > 0) {
				cerra --;
				System.out.println("contador1 "+cerra);
			}
		}
		PreviusSimb = simbol;
		return errP;
	}//Aplicamos el manejo a nivel de frase, ya que es el �ltimo elemento
	public boolean finales(String lexema, int line) {
		linea=line;
		System.out.println("en este momento hay ceras-----:"+cera);
				lex.addValue("finale");
				/*apila(terminales.indexOf(pila.peek()), elexe.indexOf(lex.getValor(lex.listLenght()-1)),lex.listLenght()-1);
				*/MensajeDeError += "Error de Sintaxis: en la linea "+linea+"\n"
						+ "Se esperaba la palabra reservada 'final' para terminar la produccion \n";
				cera--;
				errP=false;
				Simbol="final";//Se le asiga el tipo que es
				if (pila.peek().equalsIgnoreCase("modulos") ||pila.peek().equalsIgnoreCase("creas") || pila.peek().equalsIgnoreCase("modulop")) {
					apila(terminales.indexOf(pila.peek()), elexe.indexOf(lex.getValor(lex.listLenght()-1)),lex.listLenght()-1);
				MensajeDePila += pila+"\n";
	}
				else
					sacar(lex.getValor(lex.listLenght()-1));
		return errP;
	}
	
	//Este nos va a servir para llamarlo y mediante recursivida poder llenar hasta que se desapile y concuerde y retorne a AS
	public void procesoApilAndDesapil (int pivote) {
		if(pila.isEmpty()) {
			MensajeDeError += "Error de Sintaxis1: "+Simbol+" despu�s de "+ PreviusSimb+" en la l�nea "+ linea+"\n"
					+ "La pila esta vacia\n";errP = false;
			//pila.push(" ");
		}else if (terminales.contiene(pila.peek()) && elexe.contiene(lex.getValor(pivote))) {
			apila(terminales.indexOf(pila.peek()), elexe.indexOf(lex.getValor(pivote)),pivote);
		}else if(elexe.contiene(lex.getValor(pivote)) || pila.contains(lex.getValor(pivote))) {//en este caso determina si el teminal se ecuentra el la produccion y no pare
			proceso(pivote);
		}
	}
	public void sacar (String cad) {
		do {
			MensajeDePila += pila+"\n";
			pila.pop();
		}while (!cad.equalsIgnoreCase(pila.peek()));
		MensajeDePila += pila+"\n";
		MensajeDeError += "Se encontro un delimitador de tipo ' "+Simbol+" ' recuperandose del error \n";
		pila.pop();//para que saque el ultmo valor
		MensajeDePila += pila+"\n";
	}
	public void proceso(int pos) { //en este caso se ira en el caso de que este en un proceso corrido
		if (lex.getValor(pos).equalsIgnoreCase("finale"))//econtro un final para temrinar
			banderafinal=true;
		if (pila.peek().equalsIgnoreCase(lex.getValor(pos))) {
			pila.pop();
			MensajeDePila += pila+"\n";errP = true;
			if (inicio==true) {
				if (lex.getValor(pos).equalsIgnoreCase("finale")) {
					cera--;
					inicio=false;
					banderafinal=false;
				}
			}
		} else {
			if (terminales.indexOf(pila.peek())==-1) {//checa si no existe la palabra no es un no terminal
				if (pos > 0) {//si el error se encuentra en el inicial, HACE FUNCION DE SACAR EN UNA CADENA 
					MensajeDeError += "Error de Sintaxis: "+Simbol+" despu�s de "+ PreviusSimb +" en la l�nea "+ linea+"\n"
							+ "Se esperaba un "+pila.peek()+"\n";
					if (lex.getValor(pos).equalsIgnoreCase("puntcoma") || lex.getValor(pos).equalsIgnoreCase("finale")) {
						if (bandecoma==true) {
							sacar(lex.getValor(pos));
							bandecoma=false;
						}if (banderafinal==true) {
							if (cera==1 && lex.getValor(pos).equalsIgnoreCase("finale"))
								cera--;
							sacar(lex.getValor(pos));
							banderafinal=false;
						}
						

					}
					errP = false;
				}else {
					MensajeDeError += "Error de Sintaxis3: "+Simbol+" al inicio de la l�nea 1\n"
							+ "Se esperaba un "+pila.peek()+"\n";
					if (lex.getValor(pos).equalsIgnoreCase("puntcoma") || lex.getValor(pos).equalsIgnoreCase("finale")) {
						if (cera==1 && lex.getValor(pos).equalsIgnoreCase("finale"))
							cera--;
						sacar(lex.getValor(pos));
					}
					pila.pop();//son diferentes, por esa razon sacamos para que siga
					errP = false;
				}
			}else {
				apila(terminales.indexOf(pila.peek()), elexe.indexOf(lex.getValor(pos)), pos);
			}
		}
	}
	
	//Aqui apila hasta lo indicado en procesoApilAndDesapil()
	public void apila(int i, int j, int pivote) {
		String interseccion = tabla1[i][j];
		System.out.println("interseccion "+tabla1[i][j]);
		System.out.println("posicion "+i+" , "+j);
		System.out.println("pivote"+pivote);
		if (interseccion == " " || interseccion.equals("saltar")) {
			if (pivote > 0) {
				MensajeDeError += "Error de Sintaxis: "+Simbol+" despu�s de "+ PreviusSimb +" en la l�nea "+ linea+"\n"
						+ "Tipo de dato incorrecto\n" ; errP = false;
			}else {
				MensajeDeError += "Error de Sintaxis3: "+Simbol+" al inicio de la l�nea 1\n"
						+ "Tipo de dato incorrecto\n" ; errP = false;
			}
			errP = false;
			MensajeDePila += pila+"\n";
		}else {
			String[] interseccionArray = interseccion.split(" ");
			if (interseccion.contains("puntcoma")) {//la interseccion tiene un puntco y coma que puede seguir
				bandecoma=true;
			}
			String tempo=pila.peek();
			if (!((lex.getValor(pivote).equals("ciP")) && cerra == 0) || !((lex.getValor(pivote).equals("finale")) && cerra < 0)) {//el contador de parentesis
			pila.pop();
			for (int k = interseccionArray.length; k > 0; k--) {
				pila.push(interseccionArray[k - 1]);
			}
			if (pila.peek().equalsIgnoreCase("�") ||pila.peek().equalsIgnoreCase("sacar")) {
				if((tempo.equalsIgnoreCase("T")||tempo.equalsIgnoreCase("E")||tempo.equalsIgnoreCase("F") ||tempo.equalsIgnoreCase("L")|| tempo.equalsIgnoreCase("R") || tempo.equalsIgnoreCase("Z"))) {
					MensajeDeError+="Error de Sintaxis: Se esperaba un operando despues de: "+ PreviusSimb +" en la l�nea "+ linea +"\n";
					errP=false;
				}			
				pila.pop();
			}
			if (pila.peek().equalsIgnoreCase(lex.getValor(pivote))) { //encontro el terminal que busca
				MensajeDePila += pila+"\n";
				System.out.println("mesaje de pilona "+pila+"\n");

				if (lex.getValor(pivote).equalsIgnoreCase("puntcoma"))
					bandecoma=false;//termino correctamente
				if (lex.getValor(pivote).equalsIgnoreCase("clase")) {
					System.out.println("contador de ceras"+cera);
					cera++;
					inicio=true;
				}else if (lex.getValor(pivote).equalsIgnoreCase("finale") && inicio==true) {//por si nunca paso
					cera--;
					banderafinal=false;
					inicio=false;
				}
				pila.pop();
				System.out.println("mesaje de pilona "+pila+"\n");

				MensajeDePila += pila+"\n";
				errP = true;
			} else { //hara un produce
				System.out.println("mesaje de pilona "+pila+"\n");
				MensajeDePila += pila+"\n";
				procesoApilAndDesapil(pivote);
			}}else {
				MensajeDePila += pila+"\n";
				MensajeDeError += "Se cambio la accion de 'sacar' del "+Simbol+" despu�s de "+ PreviusSimb +" por la accion 'saltar' produciendo el siguiente mensaje: \n"; 	
				MensajeDeError += "Error de Sintaxis: "+Simbol+" despu�s de "+ PreviusSimb +" en la l�nea "+ linea +"\n" ; 	
				errP=false;
			}
		}
	}
}