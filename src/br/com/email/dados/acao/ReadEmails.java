package br.com.email.dados.acao;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.ReadOnlyFolderException;
import javax.mail.Store;
import javax.mail.StoreClosedException;

import org.apache.log4j.Logger;

import br.com.email.dados.constantes.Constantes;
import br.com.email.dados.email.ManipularEmail;
import br.com.email.dados.util.StringUtil;
import br.com.email.dados.util.ZipUtil;

import com.sun.mail.util.BASE64DecoderStream;

/**
 * @author Eduardo Bregaida
 * 
 */
public class ReadEmails {

	private static final String FILE_NFE_XML = "-TROCAR_PELO_NOME_DO_ARQUIVO_XML.xml";

	private Store store = null;
	private Folder folder = null;
	private Message message = null;
	private Message[] messages = null;
	private Object msgObj = null;
	private Multipart multipart = null;
	private Part part = null;
	private static Logger logger = Logger.getLogger(ReadEmails.class);
	
	public ReadEmails() throws MessagingException {
		store  = ManipularEmail.conectar();
		folder = ManipularEmail.recuperarCaixaEntrada(store);
		messages = folder.getMessages();
		
		processMail();
	}
	
	/**
	 * Processa o e-mail
	 * 
	 */
	public void processMail() throws MessagingException {	
		
		SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
		
		try {
			logger.info("Quantida de de e-mails encontrados na caixa de entrada: " + messages.length);
			
			System.out.println("Existem na caixa de entrada: "+messages.length+" para serem tratados!");
			
			for (int messageNumber = 0; messageNumber < messages.length; messageNumber++) {
				
				System.out.println("Tratando e-mail:"+(messageNumber+1)+" de "+messages.length);
				logger.info("Tratando e-mail:"+(messageNumber+1)+" de "+messages.length);
				
				message = messages[messageNumber];
				System.out.println("Content Type: " + message.getContentType());
				msgObj = message.getContent();
				
				

				// Determine o tipo de email
				if (msgObj instanceof Multipart) {
					multipart = (Multipart) message.getContent();

					for (int i = 0; i < multipart.getCount(); i++) {

						part = multipart.getBodyPart(i);
						// pegando um tipo do conteudo
//						String contentType = part.getContentType();
						
						String fileName = part.getFileName();
						System.out.println("Nome Arquivo: " +fileName);
						
						/*Caso for um aqruivo zip, joga para a pasta o arquivo
						 * recupera o arquivo que esta compactado e por fim deleta o zip
						 */
						if (fileName != null && StringUtil.validaContemStringZIP(fileName)){
							
							System.out.println("Arquivo do Tipo Zip encontrado!");
							
							ZipUtil.salvarArquivoZip(part, fileName);
							
							File arquivoZip = new File(Constantes.PASTA_XML + fileName);
							File diretorio = new File(Constantes.PASTA_XML);
							
							String nomeFile = df.format(Calendar.getInstance().getTime())+ FILE_NFE_XML;
							ZipUtil.extrairZip(arquivoZip, diretorio,nomeFile);
							
							Message[] mensagensXML = { message };
							enviaArquivoPastaAuxiliar(store, folder,mensagensXML, messageNumber);
							mensagensXML = null;
							
						}
						if (fileName != null && StringUtil.validaContemStringXML(fileName)) {
							String nomeFile;
							nomeFile = df.format(Calendar.getInstance().getTime())+ FILE_NFE_XML;
							
							salvarArquivo(part, nomeFile);
							Message[] mensagensXML = { message };
							enviaArquivoPastaAuxiliar(store, folder,mensagensXML, messageNumber);
							mensagensXML = null;
							
						}
												
					}
				} else if (msgObj instanceof BASE64DecoderStream) {
					String nomeFile;
					nomeFile = df.format(Calendar.getInstance().getTime())+ FILE_NFE_XML;

					BASE64DecoderStream base = (BASE64DecoderStream) message.getContent();

					FileOutputStream fileOutputStream = new FileOutputStream(Constantes.PASTA_XML + nomeFile);					

					int c = -1;

					while ((c = base.read()) != -1) {
						fileOutputStream.write(c);
					}

					Message[] mensagensXML = { message };
					enviaArquivoPastaAuxiliar(store, folder, mensagensXML,messageNumber);
					mensagensXML = null;
				} else {
					logger.info("E-mail: ["+message.getSubject()+"] não contém arquivo em anexo.");
				}
				ZipUtil.removerArquivosZipDiretorio(Constantes.PASTA_XML);
				
			}
			// Fecha a pasta
			folder.close(true);
			// Historico de mensagens
			store.close();
		} catch (AuthenticationFailedException e) {
			store.close();
			logger.error("Falha na Autenticação: "+ e.getMessage());
		} catch (FolderClosedException e) {
			store.close();
			logger.error("Falha no fechamento da pasta: "+ e.getMessage());
		} catch (FolderNotFoundException e) {
			store.close();
			logger.error("Pasta não encontrada: "+ e.getMessage());
		} catch (NoSuchProviderException e) {
			store.close();
			logger.error("NoSuchProviderException: "+ e.getMessage());
		} catch (ReadOnlyFolderException e) {
			store.close();
			logger.error("Pasta com permissão de somente leitura: "+ e.getMessage());
		} catch (StoreClosedException e) {
			store.close();
			logger.error("Erro ao fechar pasta auxiliar: "+ e.getMessage());
		} catch (Exception e) {
			store.close();
			logger.error("Erro no método Principal: "+ e.getMessage());
		}
	}
	

	/**
	 * Envia os arquivos da pasta princial para a pasta reserva
	 * 
	 * @param store
	 * @param folder
	 * @param messages
	 * @throws MessagingException
	 */
	private void enviaArquivoPastaAuxiliar(Store store, Folder folder,Message[] messages, int i) throws MessagingException {
		System.out.println("Movendo e-mail para pasta backup!");
		Folder folderAux;
		folderAux = ManipularEmail.recuperarPastaAuxiliar(store);
		folder.copyMessages(messages, folderAux);
		folderAux.close(true);
		try {
			ManipularEmail.excluirMensagemInbox(this.messages, i);
		} catch (Exception e) {
			logger.error("Erro ao excluir e-mail: " + i + e.getMessage());
		}
	}

	/**
	 * Salva o arquivo em uma pasta
	 * 
	 * @param part
	 * @throws MessagingException
	 * @throws IOException
	 */
	private void salvarArquivo(Part part, String nomeFile) throws IOException,MessagingException {
		System.out.println("Salvando arquivo em disco!");
		FileOutputStream fileOutputStream = new FileOutputStream(Constantes.PASTA_XML + nomeFile);
		Object obj = part.getContent();
		if (obj instanceof InputStream) {
			InputStream is = (InputStream) obj;
			int ch = -1;
			while ((ch = is.read()) != -1) {
				fileOutputStream.write(ch);
			}
		}if(obj instanceof String){
			InputStream is = new ByteArrayInputStream(((String) obj).getBytes("UTF-8"));
			int ch = -1;
			while ((ch = is.read()) != -1) {
				fileOutputStream.write(ch);
			}
		}
		logger.info("XML ["+nomeFile+"]salvo no Backup");
	}

	

	

}
