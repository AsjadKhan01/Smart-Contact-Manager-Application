package com.smart.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class SaveImgDB {

	
	public byte[] saveImgInDB(MultipartFile file) throws IOException {
		
		if (!file.isEmpty()) {
			// save Image in DB
			File saveInFile = new ClassPathResource("static/image").getFile();
			Path path = Paths.get(saveInFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
			byte[] imgData = file.getBytes();

			// write in local system
			FileOutputStream fos = new FileOutputStream(path.toString());
			fos.write(imgData);
			fos.close();
			// read in local
			FileInputStream fis = new FileInputStream(path.toString());
			byte data[] = new byte[fis.available()];
			fis.read(data);
			System.out.println("Image Upladed successfully..");
			return data;
		} else {
			// work when file is empty input by forms
			File saveInFile = new ClassPathResource("static/image").getFile();
			Path path = Paths.get(saveInFile.getAbsolutePath() + File.separator + "defaultUser.jpg");

			// read in local
			FileInputStream fis = new FileInputStream(path.toString());
			byte data[] = new byte[fis.available()];
			fis.read(data);
			System.out.println("Default Image Upladed successfully..");
			return data;
			
		}
	}
}
