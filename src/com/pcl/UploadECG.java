package com.pcl;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@WebServlet("/uploadEcgData")
public class UploadECG extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       try {
           DiskFileItemFactory factory = new DiskFileItemFactory();
           ServletFileUpload upload = new ServletFileUpload(factory);
           factory.setSizeThreshold(100*1024*1024); //100M
           List items = null;
            try {
              items = upload.parseRequest(req);
            }
            catch (FileUploadException e)
            {
                e.printStackTrace();
            }
            assert items != null;

            for (Object item:items)
            {
                FileItem fileItem = (FileItem) item;
                if(!fileItem.isFormField()) //不是普通表单类型，而是File类型
                {
                    String userPhone = fileItem.getName(); //原始文件以手机号命名
                    String filename = System.currentTimeMillis()+".txt";//时间戳命名文件
                    ServletContext context = req.getServletContext();//域对象，获取全局配置参数
                    String folder = context.getRealPath(userPhone.substring(0,11));//存储文件路径
                    File f = new File(folder,filename);//创建存储文件
                    f.getParentFile().mkdirs();
                    {
                        InputStream inputStream = fileItem.getInputStream();//获取上传文件的输入流
                        FileOutputStream fos = new FileOutputStream(f);//存储文件的输出流
                        byte[] b = new byte[100*1024*1024];
                        int length;
                        while (-1!=(length=inputStream.read(b)))
                            fos.write(b,0,length);
                        fos.close();
                    }
                }
            }
       }
       catch (Exception e)
       {
            e.printStackTrace();
       }
    }
}
