pakage com.itheima;
import java.io.*;
import javax.servlet *;
public class FirstServert extends GenericServlet{
	public void service(ServletRequest req,ServletResponse res)throws ServletException,java.io.IOException{
		 res.getWriter().write("hello Servlet!");   
		    
	}
}

//������дxml�ļ�;���ö������·��;