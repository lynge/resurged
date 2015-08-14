package org.resurged.impl.marshalling;

import java.util.HashMap;

@SuppressWarnings("unchecked")
public class MarshallingFactory {
	@SuppressWarnings("rawtypes")
	private static HashMap<Class<?>, Marshaller> cache = new HashMap<Class<?>, Marshaller>();
	
	public static synchronized <L> Marshaller<L> getMarshaller(Class<L> type){
		if(!cache.containsKey(type)){
			cache.put(type, new Marshaller<L>(type));
		}
		return cache.get(type);
	}
}
