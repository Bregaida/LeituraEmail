package br.com.email.dados.email;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import org.apache.log4j.Logger;

import br.com.email.dados.constantes.Constantes;

/**
 * Classe para controle das informacoes referentes a email
 * @author Eduardo Bregaida
 */
public class ManipularEmail {
	
	
	private static Logger logger = Logger.getLogger(ManipularEmail.class);
	
	/**
	 * Autenticacao e conexao com o Servidor de e-mail
	 * 
	 * @return
	 * @throws NoSuchProviderException
	 * @throws MessagingException
	 */
	public static Store conectar() throws NoSuchProviderException,MessagingException {
		logger.info("Conectando ao servidor de e-mail");
		Session session;
		Store store;
		Properties prop = new Properties();
		session = Session.getInstance(prop);
		URLName url = new URLName(Constantes.IMAP, Constantes.HOST,Constantes.PORTA, Constantes.ARQUIVO_MSG, Constantes.LOGIN,Constantes.SENHA);
		store = session.getStore(url);
		store.connect();
		logger.info("Conexão Ativa");

		return store;
	}

	
	/**
	 * Acessa a Caixa de Entrada (Inbox)
	 * 
	 * @param store
	 * @return
	 * @throws MessagingException
	 */
	public static Folder recuperarCaixaEntrada(Store store) throws MessagingException {
		Folder folder;
		folder = store.getFolder(Constantes.PASTA_PRINCIPAL);
		folder.open(Folder.READ_WRITE);
		return folder;
	}
	
	/**
	 * Acessa a Pasta Auxiliar
	 * 
	 * @param store
	 * @return
	 * @throws MessagingException
	 */
	public static Folder recuperarPastaAuxiliar(Store store) throws MessagingException {
		Folder folder;
		folder = store.getFolder(Constantes.PASTA_BACKUP);
		folder.open(Folder.READ_WRITE);
		return folder;
	}
	
	/**
	 * @param messages
	 * @param i
	 * @throws MessagingException
	 */
	public static void excluirMensagemInbox(Message[] messages, int i)throws MessagingException {
		messages[i].setFlag(Flags.Flag.DELETED, true);
		System.out.println("Excluir: " + messages[i]);
		
	}
}
