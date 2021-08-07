package org.hy.common.video;

import org.hy.common.StringHelp;





/**
 * 简化RTSP流的输出结果
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-11-14
 * @version     v1.0
 */
public class RTSPPrintStream extends PrintStream
{
    
    @Override
    public boolean isPrint(String i_RowInfo)
    {
        return StringHelp.isContains(i_RowInfo ,"Opening" ,"for writing");
    }
    
}
