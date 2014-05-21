package fun.guruqu.portal;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.cedarsoftware.util.io.*;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		JsonWriter writer = new JsonWriter(new FileOutputStream("test"));
		writer.write("test1");
		writer.write("test2");
		writer.close();

		JsonReader reader = new JsonReader(new FileInputStream("test"));
		System.out.println(reader.readObject());
		System.out.println(reader.readObject());
		reader.close();
	}

}
