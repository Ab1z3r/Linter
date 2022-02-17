package example;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;

public class MyFirstLinter {

	// for UML Diagram: Strings are class names,
	HashMap<String, ClassNode> classes;
	UMLCode umlText;

	public MyFirstLinter(String path) {
		classes = new HashMap<String, ClassNode>();
		umlText = new UMLCode(path);
	}


	public static List<FileInputStream> getFileNames(String basePath) {

		File dir = new File(basePath);
		File[] files = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.toLowerCase().endsWith(".class")) {
					return true;
				} else {
					return false;
				}
			}
		});

		List<FileInputStream> fis = new ArrayList<FileInputStream>();

		for (File file : files) {
			FileInputStream fin = null;
			try {
				fin = new FileInputStream(file.getCanonicalPath());
			} catch (FileNotFoundException e) {
				System.out.println("Couldnt Find file: " + file.getName());
			} catch (IOException e) {
				System.out.println("IOException fetching file: " + file.getName());
			}

			fis.add(fin);
		}

		return fis;

	}

	String[] fieldForAnalysisByThisProgram = new String[1];

	/**
	 * Reads in a list of Java Classes and prints fun facts about them.
	 * 
	 * For more information, read: https://asm.ow2.io/asm4-guide.pdf
	 * 
	 * @param args : the names of the classes, separated by spaces. For example:
	 *             java example.MyFirstLinter java.lang.String
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws IOException {
		// TODO: Learn how to create separate Run Configurations so you can run
		// your code on different programs without changing the code each time.

		//Scanner myObj = new Scanner(System.in);
	    //System.out.println("Enter FilePath:");
        //String filePath = myObj.nextLine();
		
	    String filePath = "C:\\Users\\naseema\\New-Eclipse-Workspace\\Assembler\\bin";
	    String outfilePath = "C:\\Users\\naseema\\Desktop\\PlantUml.txt";
	    String pngPath =  "C:\\Users\\naseema\\Desktop\\PlantUml.png";

	    //String basePath = filePath.replace("\\", "\\\\");

		List<FileInputStream> allFiles = getFileNames(filePath);
		
		//System.out.println("Enter Outfile Path:");
	    //String outfilePath = myObj.nextLine();
	    //outfilePath = outfilePath.replace("\\", "\\\\");

	    
	    MyFirstLinter linter = new MyFirstLinter(outfilePath);

//		for (FileInputStream stream : allFiles) {
//			// The 3 steps read in a Java class:
//			// 1. ASM's ClassReader does the heavy lifting of parsing the compiled Java
//			// class.
//			ClassReader reader = new ClassReader(stream);
//
//			
//			// 2. ClassNode is just a data container for the parsed class
//			ClassNode classNode = new ClassNode();
//
//			// 3. Tell the Reader to parse the specified class and store its data in our
//			// ClassNode.
//			// EXPAND_FRAMES means: I want my code to work. (Always pass this flag.)
//			reader.accept(classNode, ClassReader.EXPAND_FRAMES);
//
//			// Now we can navigate the classNode and look for things we are interested in.
//						
//			printClass(classNode);
//
//			printFields(classNode);
//
//			printMethods(classNode);
//			
//		}
//		
	    try {
			linter.populateAllHashes(allFiles);
			linter.writeToFile();
			Thread.sleep(2000);
			linter.getPlantUML(outfilePath, pngPath);
		} catch (IOException e) {
			System.out.println("IOException in populating linter hashes");
		} catch (InterruptedException e) {
			System.out.println("InterruptedException in waiting for outfile");
		}		
	    
	    
		

	}
	
	private void getPlantUML(String plantumlSrcPath, String pngPath) throws IOException {
		
	    File source = new File(plantumlSrcPath);
	    SourceFileReader reader = new SourceFileReader(source);
	    List<GeneratedImage> list = reader.getGeneratedImages();
	    // Generated files
	    File png = list.get(0).getPngFile();
		System.out.println(png.getAbsolutePath());
	}
	
	// Method to populate classes HashMap -> call Class method
	public void populateAllHashes(List<FileInputStream> allFiles) throws IOException {

		for (FileInputStream stream : allFiles) {
			ClassReader read = new ClassReader(stream);
			ClassNode node = new ClassNode();
			read.accept(node, ClassReader.EXPAND_FRAMES);

			String className = node.name;
			String superClassName = node.superName;

			String total = className;
			if (superClassName != null) {
				total.concat(" extends " + superClassName);
			}

			// classes
			classes.put(total, node);
		}
	}
	
	private void writeToFile() throws IOException {
		umlText.writeClassNames(classes);
		umlText.closeWriter();
	}
	
	

	private static void printClass(ClassNode classNode) {
		System.out.println("Class's Internal JVM name: " + classNode.name);
		System.out.println("User-friendly name: " + Type.getObjectType(classNode.name).getClassName());
		System.out.println("public? " + ((classNode.access & Opcodes.ACC_PUBLIC) != 0));

		System.out.println("Extends: " + classNode.superName);
		System.out.println("Implements: " + classNode.interfaces);
		// TODO: how do I write a lint check to tell if this class has a bad name?
	}

	private static void printFields(ClassNode classNode) {
		// Print all fields (note the cast; ASM doesn't store generic data with its
		// Lists)
		List<FieldNode> fields = (List<FieldNode>) classNode.fields;
		for (FieldNode field : fields) {
			System.out.println("	Field: " + field.name);
			System.out.println("	Internal JVM type: " + field.desc);
			System.out.println("	User-friendly type: " + Type.getObjectType(field.desc).getClassName());
			// Query the access modifiers with the ACC_* constants.

			System.out.println("	public? " + ((field.access & Opcodes.ACC_PUBLIC) != 0));
			// TODO: how do you tell if something has package-private access? (ie no access
			// modifiers?)

			// TODO: how do I write a lint check to tell if this field has a bad name?

			System.out.println();
		}
	}

	private static void printMethods(ClassNode classNode) {
		List<MethodNode> methods = (List<MethodNode>) classNode.methods;
		for (MethodNode method : methods) {
			System.out.println("	Method: " + method.name);
			System.out.println("	Internal JVM method signature: " + method.desc);

			System.out.println("	Return type: " + Type.getReturnType(method.desc).getClassName());

			System.out.println("	Args: ");
			for (Type argType : Type.getArgumentTypes(method.desc)) {
				System.out.println("		" + argType.getClassName());
				// FIXME: what is the argument's *variable* name?
			}

			System.out.println("	public? " + ((method.access & Opcodes.ACC_PUBLIC) != 0));
			System.out.println("	static? " + ((method.access & Opcodes.ACC_STATIC) != 0));
			// How do you tell if something has default access? (ie no access modifiers?)

			System.out.println();

			// Print the method's instructions
			// printInstructions(method);
		}
	}

	private static void printInstructions(MethodNode methodNode) {
		InsnList instructions = methodNode.instructions;
		for (int i = 0; i < instructions.size(); i++) {

			// We don't know immediately what kind of instruction we have.
			AbstractInsnNode insn = instructions.get(i);

			// FIXME: Is instanceof the best way to deal with the instruction's type?
			if (insn instanceof MethodInsnNode) {
				// A method call of some sort; what other useful fields does this object have?
				MethodInsnNode methodCall = (MethodInsnNode) insn;
				System.out.println("		Call method: " + methodCall.owner + " " + methodCall.name);
			} else if (insn instanceof VarInsnNode) {
				// Some some kind of variable *LOAD or *STORE operation.
				VarInsnNode varInsn = (VarInsnNode) insn;
				int opCode = varInsn.getOpcode();
				// See VarInsnNode.setOpcode for the list of possible values of
				// opCode. These are from a variable-related subset of Java
				// opcodes.
			}
			// There are others...
			// This list of direct known subclasses may be useful:
			// http://asm.ow2.org/asm50/javadoc/user/org/objectweb/asm/tree/AbstractInsnNode.html

			// TODO: how do I write a lint check to tell if this method has a bad name?
		}
	}
}
