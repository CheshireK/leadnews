import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class OcrTests {
    @Test
    public void test1() throws TesseractException, IOException {
        File file = new File("D:\\Code\\leadnews\\leadnews-test\\ocr-demo\\src\\main\\resources\\img.png");
        System.out.println(file.exists());
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath("D:\\Code\\leadnews\\leadnews-test\\ocr-demo\\src\\main\\resources\\tessdata");
        tesseract.setLanguage("chi_sim");
        FileInputStream is = new FileInputStream(file);
        BufferedImage image = ImageIO.read(file);
        String ocr = tesseract.doOCR(image);
        String result = ocr.replaceAll("[\\r\\n]", "-");
        System.out.println("result = " + result);
    }
}
