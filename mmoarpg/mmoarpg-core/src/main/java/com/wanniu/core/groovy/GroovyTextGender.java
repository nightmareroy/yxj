package com.wanniu.core.groovy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.wanniu.core.logfs.Out;

public class GroovyTextGender {
	public static void main(String[] args) {
		GroovyTextGender g = new GroovyTextGender();
		g.parse();

	}
	
	public void parse(){
		String path = System.getProperty("user.dir");
		path+="/src/com/groovy";
		try {
			File file = new File(path+"/GroovyExcutor.java");
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file));
				BufferedReader bufferedReader = new BufferedReader(read);				
				String lineTxt = null;
				boolean isFirst = false;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					if(!isFirst){
						System.out.println("\" "+lineTxt.trim() +"\"");
					}else{
						System.out.println("+\" "+lineTxt.trim() +"\"");
					}
					isFirst = true;
					System.out.println("+\"\\r\\n\\t\"");
				}
				read.close();				
			} 
			
		} catch (Exception e) {	
			Out.error("GroovyTextGender parse", e);
		}
	}
}
