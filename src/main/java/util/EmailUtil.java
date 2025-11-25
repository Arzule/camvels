package util;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailUtil {
    
    // Configuración para Gmail
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USER = "rogerin2006@gmail.com";
    private static final String SMTP_PASSWORD = "dzpjzmgadfqklibn";
    
    private static String ultimoError = null;
    
    public static String getUltimoError() {
        return ultimoError;
    }

    public static boolean enviarCorreo(String destinatario, String asunto, String mensaje,
                                      byte[] archivoAdjunto, String nombreArchivo) {
        ultimoError = null;
        try {
            System.out.println("=== INICIANDO ENVÍO DE CORREO ===");
            System.out.println("Destinatario: " + destinatario);
            System.out.println("Asunto: " + asunto);
            System.out.println("Desde: " + SMTP_USER);
            System.out.println("Host: " + SMTP_HOST);
            System.out.println("Puerto: " + SMTP_PORT);
            
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", SMTP_HOST);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.connectiontimeout", "15000");
            props.put("mail.smtp.timeout", "15000");
            props.put("mail.smtp.writetimeout", "15000");
            // Configuraciones adicionales para Gmail
            props.put("mail.smtp.ssl.enable", "false");
            props.put("mail.smtp.ssl.checkserveridentity", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.debug", "false");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
                }
            });
            
            // Validar formato del correo destinatario
            if (destinatario == null || destinatario.trim().isEmpty()) {
                throw new IllegalArgumentException("El correo destinatario no puede estar vacío");
            }
            
            // Validar formato básico de email
            if (!destinatario.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                throw new IllegalArgumentException("Formato de correo destinatario inválido: " + destinatario);
            }
            
            System.out.println("Creando mensaje de correo...");
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USER, "Camvels Minimarket"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setSentDate(new java.util.Date());
            
            // Agregar headers adicionales para mejor entregabilidad
            message.setHeader("X-Mailer", "Camvels Inventario System");
            message.setHeader("X-Priority", "3");
            
            if (archivoAdjunto != null && archivoAdjunto.length > 0 && nombreArchivo != null && !nombreArchivo.trim().isEmpty()) {
                Multipart multipart = new MimeMultipart();
                
                // Parte del mensaje
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(mensaje);
                multipart.addBodyPart(messageBodyPart);
                
                // Parte del adjunto
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.setFileName(nombreArchivo);
                
                // Determinar el tipo MIME del archivo
                String mimeType = determinarTipoMIME(nombreArchivo);
                attachmentPart.setContent(archivoAdjunto, mimeType);
                attachmentPart.setDisposition(Part.ATTACHMENT);
                multipart.addBodyPart(attachmentPart);
                
                message.setContent(multipart);
            } else {
                // Si no hay adjunto, solo enviar el mensaje de texto
                message.setText(mensaje);
            }
            
            System.out.println("Conectando al servidor SMTP " + SMTP_HOST + ":" + SMTP_PORT + "...");
            Transport transport = session.getTransport("smtp");
            try {
                System.out.println("Intentando autenticar con: " + SMTP_USER);
                transport.connect(SMTP_HOST, Integer.parseInt(SMTP_PORT), SMTP_USER, SMTP_PASSWORD);
                System.out.println("✓ Autenticación exitosa con el servidor SMTP");
                
                System.out.println("Preparando mensaje para: " + destinatario);
                System.out.println("Asunto: " + asunto);
                if (archivoAdjunto != null && archivoAdjunto.length > 0) {
                    System.out.println("Adjunto: " + nombreArchivo + " (" + archivoAdjunto.length + " bytes)");
                }
                
                System.out.println("Enviando mensaje...");
                transport.sendMessage(message, message.getAllRecipients());
                
                System.out.println("========================================");
                System.out.println("✓ CORREO ENVIADO EXITOSAMENTE");
                System.out.println("========================================");
                System.out.println("Destinatario: " + destinatario);
                System.out.println("Asunto: " + asunto);
                System.out.println("Remitente: " + SMTP_USER);
                System.out.println("========================================");
                System.out.println("IMPORTANTE:");
                System.out.println("1. El correo fue enviado al servidor SMTP correctamente");
                System.out.println("2. Si no llega, REVISA LA CARPETA DE SPAM del destinatario");
                System.out.println("3. Los correos pueden tardar 1-5 minutos en llegar");
                System.out.println("4. Outlook/Hotmail puede marcar correos como spam automáticamente");
                System.out.println("========================================");
                
                return true;
            } catch (AuthenticationFailedException e) {
                System.err.println("========================================");
                System.err.println("✗ ERROR DE AUTENTICACIÓN");
                System.err.println("========================================");
                System.err.println("El servidor rechazó las credenciales.");
                System.err.println("Correo: " + SMTP_USER);
                System.err.println("Posibles causas:");
                System.err.println("1. La contraseña es incorrecta");
                System.err.println("2. La cuenta requiere autenticación moderna (OAuth2)");
                System.err.println("3. La cuenta está bloqueada o suspendida");
                System.err.println("4. Outlook requiere contraseña de aplicación");
                System.err.println("");
                System.err.println("SOLUCIÓN: Prueba con Gmail o genera contraseña de aplicación");
                System.err.println("========================================");
                throw e;
            } finally {
                try {
                    transport.close();
                    System.out.println("Conexión SMTP cerrada");
                } catch (MessagingException e) {
                    // Ignorar errores al cerrar
                }
            }
        } catch (AuthenticationFailedException e) {
            String errorMsg = "ERROR DE AUTENTICACIÓN: El correo o contraseña son incorrectos. " +
                            "Mensaje: " + e.getMessage() + ". " +
                            "SOLUCIÓN: Verifica las credenciales o cambia a Gmail con contraseña de aplicación.";
            ultimoError = errorMsg;
            System.err.println("=== ERROR DE AUTENTICACIÓN ===");
            System.err.println(errorMsg);
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            String errorMsg = "ERROR AL ENVIAR CORREO: " + e.getMessage();
            if (e.getNextException() != null) {
                errorMsg += " Causa: " + e.getNextException().getMessage();
            }
            ultimoError = errorMsg;
            System.err.println("=== ERROR AL ENVIAR CORREO ===");
            System.err.println("Tipo: " + e.getClass().getName());
            System.err.println(errorMsg);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            String errorMsg = "ERROR INESPERADO: " + e.getMessage() + " (Tipo: " + e.getClass().getName() + ")";
            ultimoError = errorMsg;
            System.err.println("=== ERROR INESPERADO ===");
            System.err.println(errorMsg);
            e.printStackTrace();
            return false;
        }
    }
    
    private static String determinarTipoMIME(String nombreArchivo) {
        if (nombreArchivo == null) {
            return "application/octet-stream";
        }
        
        String extension = nombreArchivo.toLowerCase();
        if (extension.endsWith(".pdf")) {
            return "application/pdf";
        } else if (extension.endsWith(".doc") || extension.endsWith(".docx")) {
            return "application/msword";
        } else if (extension.endsWith(".xls") || extension.endsWith(".xlsx")) {
            return "application/vnd.ms-excel";
        } else if (extension.endsWith(".txt")) {
            return "text/plain";
        } else if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (extension.endsWith(".png")) {
            return "image/png";
        } else if (extension.endsWith(".gif")) {
            return "image/gif";
        } else if (extension.endsWith(".zip")) {
            return "application/zip";
        } else {
            return "application/octet-stream";
        }
    }
}

