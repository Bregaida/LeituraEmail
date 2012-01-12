package br.com.email.dados.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.mail.MessagingException;
import javax.mail.Part;

import br.com.email.dados.constantes.Constantes;

public class ZipUtil {
	
	/**
	 * Salva o arquivo em uma pasta
	 * 
	 * @param part
	 * @throws MessagingException
	 * @throws IOException
	 */
	public static void salvarArquivoZip(Part part, String nomeFile) throws IOException,MessagingException {
		FileOutputStream fileOutputStream = new FileOutputStream(Constantes.PASTA_XML + nomeFile);
		Object obj = part.getContent();
		if (obj instanceof InputStream) {
			InputStream is = (InputStream) obj;
			int ch = -1;
			while ((ch = is.read()) != -1) {
				fileOutputStream.write(ch);
			}
		}
	}
	
	/**
	 * Extrai o arquivo zip para o diretório informado
	 * @param arquivoZip
	 * @param diretorio
	 * @throws ZipException
	 * @throws IOException
	 */
	 public static void extrairZip(File arquivoZip, File diretorio, String nomeArquivoXml)throws ZipException, IOException {
		 System.out.println("Extraindo arquivo!");
	
		ZipFile zip = null;
		File arquivo = null;
		InputStream is = null;
		OutputStream os = null;
		byte[] buffer = new byte[1000];
		try {
			
			zip = new ZipFile(arquivoZip);
			Enumeration e = zip.entries();
			while (e.hasMoreElements()) {
				ZipEntry entrada = (ZipEntry) e.nextElement();
				arquivo = new File(diretorio, nomeArquivoXml);
				
				try {
					// lê o arquivo do zip e grava em disco
					is = zip.getInputStream(entrada);
					os = new FileOutputStream(arquivo);
					int bytesLidos = 0;
					if (is == null) {
						throw new ZipException("Erro ao ler a entrada do zip: "
								+ entrada.getName());
					}
					while ((bytesLidos = is.read(buffer)) > 0) {
						os.write(buffer, 0, bytesLidos);
					}
					arquivoZip.delete();
				} finally {
					if (is != null) {
							is.close();
					}
					if (os != null) {
							os.close();
					}
				}
			}
		} finally {
			if (zip != null) {
				zip.close();
			}
		}
	}
	 
	 /**
		 * Remove um arquivo
		 * @param nomeDiretorio Diretorio do Arquivo
		 * @param nome Nome do Arquivo
		 */
		public static void removerArquivosZipDiretorio(String nomeDiretorio){
			
			File diretorio = new File(nomeDiretorio);
			
			File arquivos[] = diretorio.listFiles();
			
			for(int i=0;i<arquivos.length;i++) {
				File item = arquivos[i];
			
				if(item.exists() && item.getName().substring(item.getName().length() - 3).equalsIgnoreCase("zip")){
					item.delete();
				}
			}
		}
}
