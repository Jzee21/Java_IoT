package javaHadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class EX01_SimpleFileRW {

	public static void main(String[] args) {
		
		/* 1. Hadoop 실행 환경 설정 조회 */
		Configuration conf = new Configuration();
		
		try {
			FileSystem hdfs = FileSystem.get(conf);
			
			String filename = "test.txt";
			String contents = "소리없는 아우성";
			
			Path path = new Path(filename);
			
			if(hdfs.exists(path)) {
				hdfs.delete(path, true);
			}
			
			FSDataOutputStream out = hdfs.create(path);
			out.writeUTF(contents);
//			out.flush();
			out.close();
			
			FSDataInputStream in = hdfs.open(path);
			String data = in.readUTF();
			in.close();
			
			System.out.println("읽은 내용은 : " + data);
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}

}
