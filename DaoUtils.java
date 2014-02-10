/*
 * Android Cursor and ContentValues mapper
 * https://github.com/barteksc/android-dao-utils
 *
 * Copyright 2014, Bartosz Schiller
 *
 * Licensed under the MIT license:
 * http://www.opensource.org/licenses/MIT
 */
 
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import android.content.ContentValues;
import android.database.Cursor;

public class DaoUtils {
	
	public static <T> T fromCursor(Cursor c, Class<T> clazz){
		
		if(c == null || c.isClosed())
			return null;
		
		Field[] fields = clazz.getDeclaredFields();
		T obj = null;
		try{
		
			obj = clazz.newInstance();
			
			Annotation ann;
			String serializeName;
			Class<?> fieldType;
			int index;
			
			for(Field f : fields){
				
				if(Modifier.isStatic(f.getModifiers()) 
						|| Modifier.isTransient(f.getModifiers())
						|| Modifier.isFinal(f.getModifiers())){
					continue;
				}
				
				ann = f.getAnnotation(SerializedName.class);
				
				
				if(ann != null){
					serializeName = ((SerializedName)ann).value();
				} else{
					serializeName = f.getName();
				}
				
				index = c.getColumnIndex(serializeName);
				if(index > -1){
					f.setAccessible(true);
					fieldType = f.getType();
					
					if(!fieldType.isPrimitive() && c.isNull(index)){
						f.set(obj, null);
					} else if(fieldType.isAssignableFrom(String.class)){
						f.set(obj, c.getString(index));
					} else if(fieldType.isAssignableFrom(Integer.class) || fieldType.isAssignableFrom(int.class)){
						f.set(obj, c.getInt(index));
					} else if(fieldType.isAssignableFrom(Boolean.class) || fieldType.isAssignableFrom(boolean.class)){
						f.set(obj, c.getInt(index) > 0);
					} else if(fieldType.isAssignableFrom(Date.class)){
						f.set(obj, new Date(c.getLong(index)));
					}else if(fieldType.isAssignableFrom(Float.class) || fieldType.isAssignableFrom(float.class)){
						f.set(obj, c.getFloat(index));
					} else if(fieldType.isAssignableFrom(Double.class) || fieldType.isAssignableFrom(double.class)){
						f.set(obj, c.getDouble(index));
					} else if(fieldType.isAssignableFrom(Long.class) || fieldType.isAssignableFrom(long.class)){
						f.set(obj, c.getLong(index));
					}
					
					f.setAccessible(false);
					
				}
					
			}
		
		} catch(IllegalAccessException ex){
			ex.printStackTrace();
		} catch(InstantiationException ex){
			ex.printStackTrace();
		}
		
		return obj;
	}
	
	public static <T> ContentValues toContentValues(T obj){
		
		ContentValues values = new ContentValues();
		
		Annotation ann;
		String serializeName;
		Class<?> fieldType;
		
		Field[] fields = obj.getClass().getDeclaredFields();
		
		for(Field f : fields){
			
			if(Modifier.isStatic(f.getModifiers()) 
					|| Modifier.isTransient(f.getModifiers())
					|| Modifier.isFinal(f.getModifiers())){
				continue;
			}
			
			ann = f.getAnnotation(SerializedName.class);
			
			
			if(ann != null){
				serializeName = ((SerializedName)ann).value();
			} else{
				serializeName = f.getName();
			}
			
			f.setAccessible(true);
			fieldType = f.getType();
			
			try{
				if(!fieldType.isPrimitive() && f.get(obj) == null){
					values.putNull(serializeName);
				} else if(fieldType.isAssignableFrom(String.class)){
					values.put(serializeName, (String)f.get(obj));
				} else if(fieldType.isAssignableFrom(Integer.class)){
			                values.put(serializeName, (Integer)f.get(obj));
				} else if(fieldType.isAssignableFrom(int.class)){
					values.put(serializeName, f.getInt(obj));
				} else if(fieldType.isAssignableFrom(Boolean.class)){
					values.put(serializeName, (Boolean)f.get(obj));
				} else if(fieldType.isAssignableFrom(boolean.class)){
					values.put(serializeName, f.getBoolean(obj));
				} else if(fieldType.isAssignableFrom(Date.class)){
					values.put(serializeName, ((Date)f.get(obj)).getTime());
				} else if(fieldType.isAssignableFrom(Float.class)){
					values.put(serializeName, (Float)f.get(obj));
				} else if(fieldType.isAssignableFrom(float.class)){
				  values.put(serializeName, f.getFloat(obj));
				} else if(fieldType.isAssignableFrom(Double.class)){
					values.put(serializeName, (Double)f.get(obj));
				} else if(fieldType.isAssignableFrom(double.class)){
					values.put(serializeName, f.getDouble(obj));
				} else if(fieldType.isAssignableFrom(Long.class)){
					values.put(serializeName, (Long)f.get(obj));
				} else if(fieldType.isAssignableFrom(long.class)){
					values.put(serializeName, f.getLong(obj));
				}
				
				
			} catch(IllegalAccessException ex){
				ex.printStackTrace();
			}
			
		    f.setAccessible(false);
		}	
		
		return values;
		
	}
}
