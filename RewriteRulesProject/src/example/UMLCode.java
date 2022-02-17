package example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class UMLCode {

	File umlCode;
	FileWriter writer;
	HashMap<String, List<MethodNode>> methods;
	HashMap<String, List<FieldNode>> fields;
	HashMap<String, List<String>> interfaces;
	final String lineSeparator = System.lineSeparator();

	public UMLCode(String path) {

		umlCode = new File(path);
		try {
			boolean isMade = umlCode.createNewFile();
			if (!isMade) {
				throw new IOException();
			}
			writer = new FileWriter(path);

			writer.write("@startuml");
			writer.write(lineSeparator);
			writer.write(lineSeparator);
		} catch (IOException e) {
			System.out.println("IOException in creating PlantUML file: " + path);
		} catch (SecurityException e) {
			System.out.println("SecurityException in creating PlantUML file: " + path);
		}
	}

	public void closeWriter() throws IOException {
		this.writer.close();
	}

	public void writeClassNames(HashMap<String, ClassNode> cl) throws IOException {
		for (String name : cl.keySet()) {
			writer.write("class " + name + "{");
			writer.write(lineSeparator);
			writeFields(cl.get(name).fields);
			writer.write(lineSeparator);
			writeMethods(cl.get(name).methods);
			writer.write(lineSeparator);
			writer.write("}");
			writer.write(lineSeparator);
		}
		writer.write("@enduml");
	}

	public void writeFields(List<FieldNode> field) throws IOException {
		for (FieldNode f : field) {

			String fieldstr = "";

			if ((f.access & Opcodes.ACC_PUBLIC) != 0) {
				fieldstr += "+ ";
			} else {
				fieldstr += "- ";
			}
			String[] temp = Type.getObjectType(f.desc).getClassName().split("\\W");
			fieldstr += f.name;
			fieldstr += " : " + temp[temp.length - 1];
			writer.write(fieldstr);
			writer.write(lineSeparator);
		}
	}

	private void writeMethods(List<MethodNode> methods) throws IOException {
		for (MethodNode method : methods) {

			String methodstr = "";

			if (((method.access & Opcodes.ACC_PUBLIC) != 0)) {
				methodstr += "+ ";
			} else {
				methodstr += "- ";
			}

			methodstr += method.name + "(";
			boolean hasparams = false;
			for (Type argType : Type.getArgumentTypes(method.desc)) {
				hasparams = true;
				String[] temp = argType.getClassName().split("\\W");
				methodstr += temp[temp.length - 1] + ", ";
			}
			if (hasparams) {
				methodstr = methodstr.substring(0, methodstr.length() - 2);
			}
			methodstr += ")";

			String[] temp = Type.getReturnType(method.desc).getClassName().split(".");
			if (temp.length == 0) {
				methodstr += " : void";
			} else {
				methodstr += " : " + temp[temp.length - 1];
			}
			writer.write(methodstr);
			writer.write(lineSeparator);
		}
	}
}
