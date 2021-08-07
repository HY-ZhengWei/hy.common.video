package org.hy.common.video;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.hy.common.xml.log.Logger;





/**
 * 读取执行命令输出结果信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-11-14
 * @version     v1.0
 */
public class PrintStream extends Thread
{
    private static final Logger $Logger = new Logger(PrintStream.class);
    
    private BufferedReader    inputBuffer;
    
    private InputStreamReader inputReader;
    
    private InputStream       input;
    
    
    
    public PrintStream()
    {
        
    }
    
    
    
    public PrintStream(InputStream i_Input)
    {
        this.setInputStream(i_Input);
    }
    
    
    
    public PrintStream setInputStream(InputStream i_Input)
    {
        if ( i_Input != null )
        {
            this.input       = i_Input;
            this.inputReader = new InputStreamReader(this.input);
            this.inputBuffer = new BufferedReader(this.inputReader);
        }
        
        return this;
    }
    
    
    
    /**
     * 是否允许输出此行信息。
     * 
     * 使用者，可通过继承此类后，重写本方法来改变输出规则
     * 
     * @param i_LineInfo  行信息
     * @return
     */
    public boolean isPrint(String i_LineInfo)
    {
        return true;
    }
    
    
    
    @Override
    public void run()
    {
        try
        {
            if ( this.inputBuffer == null )
            {
                return;
            }
            
            String v_Line = null;
            while ( this != null && (v_Line=this.inputBuffer.readLine())!=null )
            {
                if ( isPrint(v_Line) )
                {
                    System.out.println(v_Line);
                }
            }
        }
        catch (Exception e)
        {
            $Logger.error(e);
        }
        finally
        {
            if ( this.inputBuffer != null )
            {
                try
                {
                    this.inputBuffer.close();
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
                
                this.inputBuffer = null;
            }
            
            if ( this.inputReader != null )
            {
                try
                {
                    this.inputReader.close();
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
                
                this.inputReader = null;
            }
            
            if ( this.input != null )
            {
                try
                {
                    this.input.close();
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
                
                this.input = null;
            }
        }
    }
}
