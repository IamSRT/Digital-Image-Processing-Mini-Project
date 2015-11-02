
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author RaviTeja
 */
public class ImagetoPDF {
    public void convertToPDF(String folderName, String fileName, float compressionFactor) throws DocumentException, FileNotFoundException, BadElementException, IOException{
        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();
        Image img = Image.getInstance(listOfFiles[0].getAbsolutePath());
        float width, height, temp;
        width=img.getWidth();
        height = img.getHeight();
        if(height<width){
            temp=height;
            height=width;
            width=height;
        }
        Rectangle pageSize=new Rectangle(width, height);
        Document document = new Document(pageSize, 0,0,0,0);
        
        for(int i=0; i<listOfFiles.length; i++){
            enhance(listOfFiles[i].getAbsolutePath());
        }
        float scalar;
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
        writer.open();
        writer.setCompressionLevel(5);
        document.open();
        
        for (int i = 0; i < listOfFiles.length; i++){ 
            img = Image.getInstance(listOfFiles[i].getAbsolutePath());
             if(img.getWidth()>img.getHeight()){
                img.setRotationDegrees(270f);
            }
            scalar = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - 0) / img.getWidth()) * 100;
            img.scalePercent(scalar);
           
            document.add(img);
        }
        document.close();
        writer.close();    
        
    }
    
    public static void compress(String fileName, float compressionFactor) throws IOException{
              File input = new File(fileName);
      BufferedImage image = ImageIO.read(input);

      File compressedImageFile = new File(fileName);
      OutputStream os =new FileOutputStream(compressedImageFile);

      Iterator<ImageWriter>writers =  ImageIO.getImageWritersByFormatName("jpg");
      ImageWriter writer = (ImageWriter) writers.next();

      ImageOutputStream ios = ImageIO.createImageOutputStream(os);
      writer.setOutput(ios);

      ImageWriteParam param = writer.getDefaultWriteParam();
      
      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      param.setCompressionQuality(compressionFactor);
      writer.write(null, new IIOImage(image, null, null), param);
      
      os.close();
      ios.close();
      writer.dispose();
    }
    
    public static void enhance(String fileName) throws IOException{
       
    Mat source = Imgcodecs.imread(fileName,Imgcodecs.CV_LOAD_IMAGE_COLOR);
    Mat destination = new Mat(source.rows(),source.cols(),source.type());
//    Imgproc.cvtColor(source, destination, Imgproc.COLOR_BGR2GRAY);
Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);
Mat imageMat=source;   
Imgproc.GaussianBlur(imageMat, imageMat, new Size(3, 3), 0);
Imgproc.adaptiveThreshold(imageMat, imageMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 5, 4);
Imgcodecs.imwrite(fileName, imageMat);
    }
}
