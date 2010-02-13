package org.resurged.classgen.asm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.resurged.jdbc.Select;
import org.resurged.jdbc.Update;

public class AsmAdapter extends ClassAdapter {
	private static final String CLASS_SUFFIX = "Resurged";
	private String internalClassName = null;
	private HashMap<String, Annotation> annotations = new HashMap<String, Annotation>();
	private HashMap<String, Method> methods = new HashMap<String, Method>();

	public AsmAdapter(ClassVisitor classVisitor, Class<?> template) {
		super(classVisitor);

		Method[] declaredMethods = template.getDeclaredMethods();
		for (int i = 0; i < declaredMethods.length; i++) {
			String methodName = declaredMethods[i].getName() + Type.getMethodDescriptor(declaredMethods[i]);
			if(declaredMethods[i].isAnnotationPresent(Select.class)){
				annotations.put(methodName, declaredMethods[i].getAnnotation(Select.class));
				methods.put(methodName, declaredMethods[i]);
			}else if(declaredMethods[i].isAnnotationPresent(Update.class)){
				annotations.put(methodName, declaredMethods[i].getAnnotation(Update.class));
				methods.put(methodName, declaredMethods[i]);
			}
		}
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		internalClassName = name + CLASS_SUFFIX;
		
		// generate empty class
		cv.visit(version, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, internalClassName, null, "java/lang/Object", new String[] { name });
		
		// generate field
		FieldVisitor fv = cv.visitField(Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL, "con", "Ljava/sql/Connection;", null, null);
		fv.visitEnd();
		
		// generate constructor
		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Ljava/sql/Connection;)V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, internalClassName, "con", "Ljava/sql/Connection;");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();

	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		String methodDescriptor = name + desc;
		if(!methods.containsKey(methodDescriptor))
			return null;
		
		Method method = methods.get(methodDescriptor);
		Annotation annotation = annotations.get(methodDescriptor);
		String annotationValue = (annotation instanceof Select)? ((Select) annotation).value() : ((Update) annotation).value();
		
		// generate method signature
		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, name, desc, signature, exceptions);
		
//		AnnotationVisitor av = mv.visitParameterAnnotation("org/resurged/jdbc/Update", "java/lang/String", true);
//		System.out.println(av);
//		mv.visitParameterAnnotation(arg0, arg1, arg2)
//		mv.visitAnnotationDefault()
		
		// generate method body
		mv.visitCode();
		
		//////////////////////////////////////
		//// PREPARE OPERANT STACK VALUES ////
		//////////////////////////////////////
		
		if(annotation instanceof Select){
			java.lang.reflect.Type returnType = method.getGenericReturnType();
			if(returnType instanceof ParameterizedType){
			    ParameterizedType type = (ParameterizedType) returnType;
			    java.lang.reflect.Type[] typeArguments = type.getActualTypeArguments();
			    Class<?> typeArgClass = (Class<?>) typeArguments[0];
			    mv.visitLdcInsn(Type.getType("L" + typeArgClass.getName().replaceAll("\\.", "/") + ";"));
			}
		}
		
		// stack.push "this"
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		
		// stack.pop "this" and stack.push its "con" field
		mv.visitFieldInsn(Opcodes.GETFIELD, internalClassName, "con", "Ljava/sql/Connection;");
		
		// stack.push the query string
		mv.visitLdcInsn(annotationValue);
		
		// stack.push size of parameter array
		mv.visitIntInsn(Opcodes.BIPUSH, method.getParameterTypes().length);
		
		// stack.pop array size and stack.push new array of that size
		mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
		
		// move method parameters to array
		Class<?>[] parameterTypes = method.getParameterTypes();
		int lvarIndex = 1;
		for (int i = 0; i < parameterTypes.length; i++) {
			// stack.dublicate array reference
			mv.visitInsn(Opcodes.DUP);
			
			// stack.push array index
			mv.visitIntInsn(Opcodes.BIPUSH, i);
			
			// stack.push parameter value
			lvarIndex = pushParameterValue(lvarIndex, parameterTypes[i], mv);
			
			// stack.pop index+value and store these in duplicate array reference, which is popped
			mv.visitInsn(Opcodes.AASTORE);
		}

		//////////////////////////////////////
		////        INVOKE METHOD         ////
		//////////////////////////////////////
		
		if(annotation instanceof Update)
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/resurged/QueryEngine", "executeUpdate", "(Ljava/sql/Connection;Ljava/lang/String;[Ljava/lang/Object;)I");
		else
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/resurged/QueryEngine", "executeQuery", "(Ljava/lang/Class;Ljava/sql/Connection;Ljava/lang/String;[Ljava/lang/Object;)Lorg/resurged/jdbc/DataSet;");

		//////////////////////////////////////
		////        RETURN RESULT         ////
		//////////////////////////////////////
		
		if(annotation instanceof Update)
			mv.visitInsn(Opcodes.IRETURN);
		else
			mv.visitInsn(Opcodes.ARETURN);
		
		// Register local variables and frame size. 
		// Numbers are currently ignored, as this is handled by the ClassWriter
		mv.visitMaxs(0, 0);
		
		mv.visitEnd();
		return mv;
	}

	private int pushParameterValue(int i, Class<?> klass, MethodVisitor mv) {		
		if(klass==Boolean.TYPE || klass==Byte.TYPE || klass==Character.TYPE|| klass==Short.TYPE || klass==Integer.TYPE) {
			mv.visitVarInsn(Opcodes.ILOAD, i);
			if(klass==Boolean.TYPE)
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
			else if(klass==Byte.TYPE)
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
			else if(klass==Character.TYPE)
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;");
			else if(klass==Short.TYPE)
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
			else if(klass==Integer.TYPE)
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
		} else if(klass==Long.TYPE) {
			mv.visitVarInsn(Opcodes.LLOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
			i++;
		} else if(klass==Float.TYPE) {
			mv.visitVarInsn(Opcodes.FLOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");
		} else if(klass==Double.TYPE) {
			mv.visitVarInsn(Opcodes.DLOAD, i);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
			i++;
		} else
			mv.visitVarInsn(Opcodes.ALOAD, i);
		return ++i;
	}

	public String getClassName() {
		return internalClassName.replaceAll("/", ".");
	}

}
