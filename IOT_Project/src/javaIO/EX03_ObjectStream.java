package javaIO;

import java.util.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;

// HashMap의 데이터를 File에 저장
public class EX03_ObjectStream {

	public static void main(String[] args) {
		// 1. 로직처리를 통해 만들어진 데이터 구조(저장할)(결과물이 HashMap이라고 가정)
		Map<String, String> map = new HashMap<String, String>();
		map.put("1", "퇴계이황");
		map.put("2", "율곡이이");
		map.put("3", "세종대왕");
		map.put("4", "신사임당");
		
		// file에 저장할 형태
		// 퇴계이황,율곡이이,세종대왕,신사임당
		File file = new File("asset/StringData.txt");
//		try {
//			// 대표적인 출력 Stream
//			PrintWriter pr = new PrintWriter(file);
//			pr.println("이것은 소리없는 아우성!");		// Stream에 데이터 보관 (전달 x)
//			pr.flush();		// Stream의 데이터 전송
//			pr.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);						// FileNotFoundException
			ObjectOutputStream oos = new ObjectOutputStream(fos);	// IOException
			/*  객체직렬화를 통해서 저장하기 원하는 객체를 Stream을 통해서 보낼 수 있다
			 *  Object Serialization
			*/	
			oos.writeObject(map);
			oos.flush();
			
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
