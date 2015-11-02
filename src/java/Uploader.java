import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.opencv.core.Core;
 
 
/**
 * Servlet implementation class Uploader
 */
@WebServlet("/upload")
public class Uploader extends HttpServlet {
  
 private boolean isMultipart;
   private String filePath="C:\\Users\\RaviTeja\\Documents\\NetBeansProjects\\uploadedDocs\\";
   private int maxFileSize = 5000 *1024* 1024;
   private int maxMemSize = 5000*1024 * 1024;
   private File file ;

   public void init( ){
      try{
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
      }catch(Exception e){
          System.out.println(e.toString());
      }// Get the file location where it would be stored.
   }
   public void doPost(HttpServletRequest request, 
               HttpServletResponse response)
              throws ServletException, java.io.IOException {
       //get new folder ready
       float compressionFactor=1;
       long time_string = System.currentTimeMillis();
       filePath=filePath+time_string;
       new File(filePath).mkdir(); //creating directory 
       filePath=filePath    +"\\";
       BufferedInputStream buf=null;
       ServletOutputStream stream=null;
       String pdfFileLink=filePath+"notes.pdf";
       
       System.out.println(filePath);
       
      // Check that we have a file upload request
      isMultipart = ServletFileUpload.isMultipartContent(request);
      response.setContentType("text/html");
      System.out.println("Entered DO POST METHOD");
      
      if( !isMultipart ){
      java.io.PrintWriter out = response.getWriter( );
          out.println("<html>");
         out.println("<head>");
         out.println("<title>Servlet upload</title>");  
         out.println("</head>");
         out.println("<body>");
         out.println("<p>No file uploaded</p>"); 
         out.println("</body>");
         out.println("</html>");
         return;
      }
      DiskFileItemFactory factory = new DiskFileItemFactory();
      // maximum size that will be stored in memory
      factory.setSizeThreshold(maxMemSize);
      // Location to save data that is larger than maxMemSize.
      factory.setRepository(new File("C:\raviteja"));

      // Create a new file upload handler
      ServletFileUpload upload = new ServletFileUpload(factory);
      // maximum file size to be uploaded.
      try{ 
      // Parse the request to get file items.
      List fileItems = upload.parseRequest(request);	
      // Process the uploaded file items
      Iterator i = fileItems.iterator();

      
      while ( i.hasNext () ) 
      {
         FileItem fi = (FileItem)i.next();
         if ( !fi.isFormField () )	
         {
            String fieldName = fi.getFieldName();
            String fileName = fi.getName();
            String contentType = fi.getContentType();
            boolean isInMemory = fi.isInMemory();
            long sizeInBytes = fi.getSize();
            // Write the file
            if( fileName.lastIndexOf("\\") >= 0 ){
               file = new File( filePath +  
               fileName.substring( fileName.lastIndexOf("\\"))) ;
            }else{
               file = new File( filePath + 
               fileName.substring(fileName.lastIndexOf("\\")+1)) ;
            }
            fi.write( file );
         }
      }

      ImagetoPDF converter = new ImagetoPDF();
      converter.convertToPDF(filePath, pdfFileLink, compressionFactor);
      File pdf = new File(pdfFileLink);
      response.setContentType("application/pdf");

      response.addHeader("Content-Disposition", "inline; filename=notes.pdf");
      response.setContentLength((int) pdf.length());
      FileInputStream input = new FileInputStream(pdf);
      buf = new BufferedInputStream(input);
      stream =  response.getOutputStream();
      int readBytes = 0;

      while ((readBytes = buf.read()) != -1)
        stream.write(readBytes);
    } catch (IOException ioe) {
      throw new ServletException(ioe.getMessage());
    } catch (Exception ex) {
         Logger.getLogger(Uploader.class.getName()).log(Level.SEVERE, null, ex);
     } finally {
      if (stream != null)
        stream.close();
      if (buf != null)
        buf.close();
   filePath="C:\\Users\\RaviTeja\\Documents\\NetBeansProjects\\uploadedDocs\\";
      }           
      
      
   }
   
   
   
   
   public void doGet(HttpServletRequest request, 
                       HttpServletResponse response)
        throws ServletException, java.io.IOException {
        
        throw new ServletException("GET method used with " +
                getClass( ).getName( )+": POST method required.");
   }
}