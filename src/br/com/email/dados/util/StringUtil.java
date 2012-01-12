package br.com.email.dados.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Eduardo Bregaida
 *
 */
public class StringUtil {

	/**
	 * Verifica atraves de expressao regular se existe a String
	 * .XML no texto passado
	 * @param valor
	 * @return
	 */
	public static boolean validaContemStringXML(String valor){
		
		Pattern pegaJava = Pattern.compile(".XML");  
		 // primeiro Java esta em maiusculo!  
		Matcher m = pegaJava.matcher(valor.toUpperCase());  
		   
		 // enquanto o Matcher encontrar o pattern na String fornecida:  
		 while (m.find()) {  
		     return true;
		}
		return false;
	}
	
	/**
	 * Verifica atraves de expressao regular se existe a String
	 * .ZIP no texto passado
	 * @param valor
	 * @return
	 */
	public static boolean validaContemStringZIP(String valor){
		
		Pattern pegaJava = Pattern.compile(".ZIP");  
		 // primeiro Java esta em maiusculo!  
		Matcher m = pegaJava.matcher(valor.toUpperCase());  
		   
		 // enquanto o Matcher encontrar o pattern na String fornecida:  
		 while (m.find()) {  
		     return true;
		}
		return false;
	}
}
