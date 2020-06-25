package AnalizadorDOsCero;

import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JOptionPane;

public class Sintak {

	ListaSencilla lex = new ListaSencilla(); // Lo que arroja el analisis lexico
	ListaSencilla elexe = new ListaSencilla(); // Fila de entrantes
	ListaSencilla terminales = new ListaSencilla(); // Columna de terminales
	Stack<String> pila = new Stack<String>();
	String MensajeDeError = "";
	String MensajeDePila = "";
	String Simbol = "";
	String PreviusSimb = "";
	String[][] tabla1;
	int linea = 0, cerra = 0, cera = 0;
	boolean errP = false, inicio = false, bandecoma = false, banderafinal = false, falsa = false, banderaini=false;
	boolean vuelta = false; // controla el recorrido
	int entrada1,entrada2,entrada3, entrada4, entrada5 , entrada6, entrada7;

	// Este metodo llena la fila y columna en los arrays creados para ahorrarnos
	// bucles de búsqueda
	public void llenarFyC() {
		for (int i = 0; i < tabla1.length; i++) {
			terminales.addValue(tabla1[i][0]);
		}
		for (int i = 0; i < tabla1[0].length; i++) {
			elexe.addValue(tabla1[0][i]);
		}
	}

	// Este es el constructor que recibe todo el pedo y inicia lo esensial
	public Sintak() {
		vuelta = false;
		banderafinal = false;
		bandecoma = false;
		falsa = false;
		tablas t = new tablas();
		// tabla1 = t.laperrona;
		tabla1 = t.laperrona2;
		llenarFyC();
		cera = 0;
		pila.push("clas");
	}

	// Revisa si es aceptado
	public boolean aceptado() {
		if (pila.isEmpty() && MensajeDeError.isEmpty()) {
			pila.clear();
			return true;
		} else {
			// MensajeDeError += "Error de Sintaxis3: "+lex.getValor(lex.listLenght()-1)+"
			// después de "+ lex.getValor(lex.listLenght()-1)+" en la línea "+
			// linea+"\n";errP = false;
			return false;
		}
	}

	public int verfinales() {
		return cera;
	}

	// Este es el único metodo que se llama
	public boolean AS(String lexema, int line, String simbol) {
		linea = line;
		Simbol = simbol;
		if (vuelta) {
			MensajeDePila = "";
		} else {
			MensajeDePila += pila;
			vuelta = true;
		}
		lex.addValue(lexema);
		if (lexema.equals("abP") && cerra < 1) {
			cerra++;
		}
		procesoApilAndDesapil(lex.listLenght() - 1);
		if (lexema.equals("ciP")) { // debe poder sacar una llave
			if (cerra > 0) {
				cerra--;
				System.out.println("contador1 " + cerra);
			}
		}
		PreviusSimb = simbol;
		return errP;
	}// Aplicamos el manejo a nivel de frase, ya que es el último elemento

	public boolean finales(String lexema, int line) {
		linea = line;
		// la hace falta varios finales
		falsa = false;// Entrara de manera uniforme
		System.out.println("en este momento hay ceras-----:" + cera);
		lex.addValue("finale");
		Simbol = "final";// Se le asiga el tipo que es
		if (pila.peek().equalsIgnoreCase("modulos") || pila.peek().equalsIgnoreCase("creas")// En dado caso que la
																							// sentencia sea la ultima
																							// puede salir
				|| pila.peek().equalsIgnoreCase("modulop")) {
			apila(terminales.indexOf(pila.peek()), elexe.indexOf(lex.getValor(lex.listLenght() - 1)),
					lex.listLenght() - 1);
		} else {
			MensajeDePila += pila + "\n";
			sacar(lex.getValor(lex.listLenght() - 1));
		}

		MensajeDeError += "Error de Sintaxis: en la linea " + linea + "\n"
				+ "Se esperaba la palabra reservada 'final' para terminar la produccion \n";
		cera--;
		errP = false;

		return errP;
	}

	// Este nos va a servir para llamarlo y mediante recursivida poder llenar hasta
	// que se desapile y concuerde y retorne a AS
	public void procesoApilAndDesapil(int pivote) {
		if (pila.isEmpty()) {
			MensajeDeError += "Error de Sintaxis1: " + Simbol + " después de " + PreviusSimb + " en la línea " + linea
					+ "\n" + "La pila esta vacia\n";
			errP = false;
			// pila.push(" ");
		} else if (terminales.contiene(pila.peek()) && elexe.contiene(lex.getValor(pivote))) {
			apila(terminales.indexOf(pila.peek()), elexe.indexOf(lex.getValor(pivote)), pivote);
		} else if (elexe.contiene(lex.getValor(pivote)) || pila.contains(lex.getValor(pivote))) {// en este caso
																									// determina si el
																									// teminal se
																									// ecuentra el la
																									// produccion y no
																									// pare
			proceso(pivote);
		}
	}

	public void sacar(String cad) {
		do {
			MensajeDePila += pila + "\n";
			pila.pop();
		} while (!cad.equalsIgnoreCase(pila.peek()));
		MensajeDePila += pila + "\n";
		MensajeDeError += "Se encontro un delimitador de tipo ' " + Simbol + " ' recuperandose del error \n";
		pila.pop();// para que saque el ultmo valor
		MensajeDePila += pila + "\n";
	}

	public void proceso(int pos) { // en este caso se ira en el caso de que este en un proceso corrido
		if (lex.getValor(pos).equalsIgnoreCase("finale"))// econtro un final para temrinar
			banderafinal = true;
		if (pila.peek().equalsIgnoreCase(lex.getValor(pos))) {
			pila.pop();
			MensajeDePila += pila + "\n";
			errP = true;
			if (inicio == true) {
				if (lex.getValor(pos).equalsIgnoreCase("finale")) {
					cera--;
					inicio = false;
					banderafinal = false;
					System.out.println("ceraaaaaaaaa::::::::::: "+cera);
				}
			}
		} else {
			if (terminales.indexOf(pila.peek()) == -1) {// checa si no existe la palabra no es un no terminal
				if (pos > 0) {// si el error se encuentra en el inicial, HACE FUNCION DE SACAR EN UNA CADENA
					MensajeDeError += "Error de Sintaxis: " + Simbol + " después de " + PreviusSimb + " en la línea "
							+ linea + "\n" + "Se esperaba un " + pila.peek() + "\n";
					if (lex.getValor(pos).equalsIgnoreCase("puntcoma")
							|| lex.getValor(pos).equalsIgnoreCase("finale")) {
						if (bandecoma == true) {
							sacar(lex.getValor(pos));
							bandecoma = false;
						}
						if (banderafinal == true && falsa == false && !pila.peek().contains("inicio")) {
							if (cera == 1 && lex.getValor(pos).equalsIgnoreCase("finale"))
								cera--;
							sacar(lex.getValor(pos));
							banderafinal = false;
						}

					}if (lex.getValor(pos).equalsIgnoreCase("inicio"))
							banderaini=true;
					System.out.println("ceraaaaaaaaa::::::::::: "+cera);
					errP = false;
				} else {
					System.out.println("ceraaaaaaaaa::::::::::: "+cera);
					MensajeDeError += "Error de Sintaxis3: " + Simbol + " al inicio de la línea 1\n" + "Se esperaba un "
							+ pila.peek() + "\n";
					if (lex.getValor(pos).equalsIgnoreCase("puntcoma")
							|| lex.getValor(pos).equalsIgnoreCase("finale")) {
						if (cera == 1 && lex.getValor(pos).equalsIgnoreCase("finale"))
							cera--;
						sacar(lex.getValor(pos));
					}
					pila.pop();// son diferentes, por esa razon sacamos para que siga
					errP = false;
				}
			} else {
				apila(terminales.indexOf(pila.peek()), elexe.indexOf(lex.getValor(pos)), pos);
			}
		}
	}

	// Aqui apila hasta lo indicado en procesoApilAndDesapil()
	public void apila(int i, int j, int pivote) {
		String interseccion = tabla1[i][j];
		System.out.println("interseccion " + tabla1[i][j]);
		System.out.println("posicion " + i + " , " + j);
		System.out.println("pivote" + pivote);
		if (interseccion == " " || interseccion.equals("saltar")) {
			if (pivote > 0) {
				MensajeDeError += "Error de Sintaxis: " + Simbol + " después de " + PreviusSimb + " en la línea "
						+ linea + "\n" + "Tipo de dato incorrecto\n";
				errP = false;
			} else {
				MensajeDeError += "Error de Sintaxis3: " + Simbol + " al inicio de la línea 1\n"
						+ "Tipo de dato incorrecto\n";
				errP = false;
			}
			errP = false;
			MensajeDePila += pila + "\n";
		} else {
			String[] interseccionArray = interseccion.split(" ");
			if (interseccion.contains("puntcoma")) {// la interseccion tiene un punto y coma que puede seguir
				bandecoma = true;
			}else if (interseccion.contains("endprin") || interseccion.contains("endproc") || interseccion.contains("endf")|| 
				interseccion.contains("endcam") || interseccion.contains("endif") || interseccion.contains("endw") || interseccion.contains("endpara")) {
				metcond(lex.getValor(pivote));
			}

			String tempo = pila.peek();
			if (!((lex.getValor(pivote).equals("ciP")) && cerra == 0)
					|| !((lex.getValor(pivote).equals("finale")) && cerra < 0)) {// el contador de parentesis
				pila.pop();
				for (int k = interseccionArray.length; k > 0; k--) {
					pila.push(interseccionArray[k - 1]);
				}
				if (pila.peek().equalsIgnoreCase("ç") || pila.peek().equalsIgnoreCase("sacar")) {
					if ((tempo.equalsIgnoreCase("T") || tempo.equalsIgnoreCase("E") || tempo.equalsIgnoreCase("F")
							|| tempo.equalsIgnoreCase("L") || tempo.equalsIgnoreCase("R")
							|| tempo.equalsIgnoreCase("Z"))) {
						MensajeDeError += "Error de Sintaxis: Se esperaba un operando despues de: " + PreviusSimb
								+ " en la línea " + linea + "\n";
						errP = false;
					}
					pila.pop();
				}
				if (pila.peek().equalsIgnoreCase(lex.getValor(pivote))) { // encontro el terminal que busca
					MensajeDePila += pila + "\n";
					System.out.println("mesaje de pilona " + pila + "\n");

					if (lex.getValor(pivote).equalsIgnoreCase("puntcoma"))
						bandecoma = false;// termino correctamente
					if (lex.getValor(pivote).equalsIgnoreCase("clase")) {
						cera++;
						inicio = true;
						System.out.println("ceraaaaaaaaa::::::::::: "+cera);
					} else if (lex.getValor(pivote).equalsIgnoreCase("finale") && inicio == true) {// por si nunca paso
						cera--;
						banderafinal = false;
						inicio = false;
						System.out.println("ceraaaaaaaaa::::::::::: "+cera);

					}
					pila.pop();
					System.out.println("mesaje de pilona " + pila + "\n");

					MensajeDePila += pila + "\n";
					errP = true;
				} else { // hara un produce
					System.out.println("ceraaaaaaaaa::::::::::: "+cera);
					System.out.println("mesaje de pilona " + pila + "\n");
					MensajeDePila += pila + "\n";
					procesoApilAndDesapil(pivote);
				}
			} else {
				MensajeDePila += pila + "\n";
				MensajeDeError += "Se cambio la accion de 'sacar' del " + Simbol + " después de " + PreviusSimb
						+ " por la accion 'saltar' produciendo el siguiente mensaje: \n";
				MensajeDeError += "Error de Sintaxis: " + Simbol + " después de " + PreviusSimb + " en la línea "
						+ linea + "\n";
				errP = false;
			}
		}
	}

	public void metcond(String cad) {// validamos si la palabra crea o metodos o funciones
		switch (cad) {
		case "endprin":
			entrada1++;
			break;
		case "endf":
			entrada2++;
			break;
		case "endroc":
			entrada3++;
			break;
		case "endif":
			entrada4++;
			break;
		case "endcam":
			entrada5++;
			break;
		case "endpara":
			entrada6++;
			break;
		case "endw":
			entrada7++;
			break;
		default: break;
		}
	
	}
	public void metcondI(String cad) {// validamos si la palabra crea o metodos o funciones
		switch (cad) {
		case "endprin":
			entrada1--;
			break;
		case "endf":
			entrada2--;
			break;
		case "endroc":
			entrada3--;
			break;
		case "endif":
			entrada4--;
			break;
		case "endcam":
			entrada5--;
			break;
		case "endpara":
			entrada6--;
			break;
		case "endw":
			entrada7--;
			break;
		default: break;
		}
	
	}
}