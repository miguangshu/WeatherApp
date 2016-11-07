package com.way.common.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtils {
	public static String sSDRootPath = Environment.getExternalStorageDirectory().getPath()+ File.separator;
	private static String TAG = "FileUtils";
	public static ArrayList<String> readFileByLines(String fileName) {
		ArrayList<String> readStringList=new ArrayList<String>();
		//String writeString = "";
		File file = new File(fileName);
		BufferedReader reader = null;
		//int line = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = "";
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				//System.out.println("line " + line + ": " + tempString);
				 //line++;
				 
				// ???
				//findLuanMa(tempString);
				readStringList.add(tempString);

			}
			//System.out.println("line:" + line);
			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return readStringList;
	}

	public static void writeToFile(String filePath,String contentString){
		FileOutputStream fos = null;
		File file=new File(filePath);
		try {
			fos = new FileOutputStream(file);
			fos.write(contentString.getBytes());
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (fos != null)
					fos.close();
			} catch (Exception e) {
				//
			}
		}
	}

	public static ArrayList<String> getFilePathList(String dirPath) {
		ArrayList<String> filePathList=new ArrayList<String>();
		File dirFile = new File(dirPath);
		File[] files = dirFile.listFiles();
		if (files == null) {
			return null;
		}
		for (int i = 0; i < files.length; i++) {
			if(files[i].getName().charAt(0)=='.'){
				Log.d(TAG, "该文件是隐藏文件");
			}else if (files[i].isDirectory()) {
				ArrayList<String> subPathList=getFilePathList(files[i].getAbsolutePath());
				filePathList.addAll(subPathList);
			} else {
				String strFileName = files[i].getAbsolutePath();
				System.out.println(strFileName);
				filePathList.add(strFileName);
			}
		}
		return filePathList;

	}

	public static ArrayList<String> getParentMeFormatList(String filePath){
		ArrayList<String> parentMeFormatList=new ArrayList<String>();
		String parentPath = "";

		if(new File(filePath).isDirectory()){
			String[] layerArray = filePath.split("/");
			int layer = layerArray.length;
			for(int i=0;i<layer-1;i++){
				if(layerArray[i] != ""){
					parentPath += layerArray[i] + "/";
				}
			}
			parentMeFormatList.add(0, parentPath);
			parentMeFormatList.add(1, layerArray[layer - 1]);
			parentMeFormatList.add(2, "/");
		} else {
			String[] formatArray = filePath.split("\\.");
			String[] layerArray=formatArray[0].split("/");
			int layer = layerArray.length;
			for(int i=0;i<layer-1;i++){
				parentPath+=layerArray[i]+"/";
			}
			parentMeFormatList.add(parentPath);
			parentMeFormatList.add(layerArray[layer - 1]);
			parentMeFormatList.add(formatArray[1]);
		}

		return parentMeFormatList;
	}

	/**
	 * 根据传入的文件路径创建文件
	 * @param filePath
	 * @return
	 */
	public static File createFile(String filePath){
		ArrayList<String> parentMeFormatList = getParentMeFormatList(filePath);
		String dirPath = parentMeFormatList.get(0);
		File dirFile = new File(dirPath);
		if(!dirFile.exists()||!dirFile.isDirectory()){
			if(!dirFile.mkdirs()){
				Log.e(TAG,"create directory:" + dirPath+" failed");
				throw new RuntimeException("create directory:"+dirPath+"failed");
			}
		}
		File newFile =new File(filePath);
		return newFile;
	}
}
