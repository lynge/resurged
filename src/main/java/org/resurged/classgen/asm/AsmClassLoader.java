package org.resurged.classgen.asm;
public class AsmClassLoader<T> extends ClassLoader {
	@SuppressWarnings("unchecked")
	public Class<T> defineClass(String name, byte[] b) {
		return (Class<T>) defineClass(name, b, 0, b.length);
	}
}