/**
 * 
 */
package br.com.email.dados.main;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;

import br.com.email.dados.acao.ReadEmails;

/**
 * @author Eduardo Bregaida
 *
 */
public class PrincipalNfe {
	private static Logger logger = Logger.getLogger(PrincipalNfe.class);
	public static void main(String[] args) {
		
		logger.info("####################   INICIANDO O PROCESSAMENTO DOS E-MAILS   ##################");
		System.out.println("####################   INICIANDO O PROCESSAMENTO DOS E-MAILS   ##################");
		ReadEmails readMail;
		try {
				readMail = new ReadEmails();			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		System.out.println("####################   FIM DO PROCESSAMENTO DOS E-MAILS   ##################");
		logger.info("####################      FIM O PROCESSAMENTO DOS E-MAILS      ##################");
	}

}
