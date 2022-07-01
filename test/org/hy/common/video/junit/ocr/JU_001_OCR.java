package org.hy.common.video.junit.ocr;

import java.io.File;
import java.io.IOException;

import org.hy.common.Help;
import org.hy.common.xml.log.Logger;
import org.junit.Test;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * 测试单元：OCR文字识别
 *
 * @author      ZhengWei(HY)
 * @createDate  2022-07-01
 * @version     v1.0
 */
public class JU_001_OCR
{
    private static final Logger $Logger = new Logger(JU_001_OCR.class ,true);
    
    @Test
    public void helloWorld() throws TesseractException, IOException
    {
        File       v_TessDataHome = new File("C:\\WorkSpace\\hy.common.video\\lib\\OCR\\3.4.8");
        ITesseract v_Tesseract    = new Tesseract();
        
        v_Tesseract.setDatapath(v_TessDataHome.getCanonicalPath() + Help.getSysPathSeparator() + "tessdata");  // 设置Tess4J下的tessdata目录
        v_Tesseract.setLanguage("eng");                                                                    // 指定需要识别的语言
        
        String v_Text = v_Tesseract.doOCR(new File("C:\\WorkSpace\\hy.common.video\\test\\org\\hy\\common\\video\\junit\\openCV\\JU_101_Document.jpg"));
        $Logger.info(v_Text);
    }
}
