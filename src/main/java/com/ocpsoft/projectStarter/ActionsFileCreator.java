package com.ocpsoft.projectStarter;

import java.io.FileOutputStream;
import java.io.PrintStream;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;

import com.ocpsoft.utils.Utility;

public class ActionsFileCreator {

	public final static String className = "Actions";
	
	public static void createActionsClassViaParser(String filePath, String packageName) {
		JavaClass actionsClass = JavaParser.create(JavaClass.class);

		actionsClass
			.setName(className)
			.setPublic()
			.setPackage(packageName);
		
		try{
			PrintStream writetoTest = new PrintStream(
			new FileOutputStream(filePath + className + ".java")); 
			writetoTest.print(Utility.formatJavaClassFile(actionsClass));
			writetoTest.close();
			System.out.println("Successfully created " + className + ".java file in " + packageName);
		}
		catch (Exception e) {
			System.err.println("Failure in ActionFileCreator: " + e);
		}
	}
	
}
