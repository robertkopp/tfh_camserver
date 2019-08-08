/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.robertkopp.autocam.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author robert kopp
 */
public class ZipFileCreatorService {
 
    
    /*
     File file = new File(
                    String.format("shot_%s/%s.zip", shotid,shotid 
                    ));
    */
    
    String SOURCE_FOLDER = "";
    
    public void create(String shotid){
    
        File zipfile = new File(
                    String.format("shot_%s/%s.zip", shotid,shotid 
                    ));
            
        File folder = new File(
                    String.format("shot_%s", shotid 
                    ));
            SOURCE_FOLDER= folder.getAbsolutePath();
        
        String OUTPUT_ZIP_FILE = zipfile.getAbsolutePath();
    
        
        generateFileList(new File(SOURCE_FOLDER));
    	zipIt(OUTPUT_ZIP_FILE);    
            
  
        
    }
    
    List<String> fileList;
    
	
  public  ZipFileCreatorService(){
	fileList = new ArrayList<String>();
    }
	
    
    
    /**
     * Zip it
     * @param zipFile output ZIP file location
     */
    public void zipIt(String zipFile){

     byte[] buffer = new byte[1024];
    	
     try{
    		
    	FileOutputStream fos = new FileOutputStream(zipFile);
    	ZipOutputStream zos = new ZipOutputStream(fos);
    		
    	System.out.println("Output to Zip : " + zipFile);
    		
    	for(String file : this.fileList){
    			
    		System.out.println("File Added : " + file);
    		ZipEntry ze= new ZipEntry(file);
        	zos.putNextEntry(ze);
               
        	FileInputStream in = 
                       new FileInputStream(SOURCE_FOLDER + File.separator + file);
       	   
        	int len;
        	while ((len = in.read(buffer)) > 0) {
        		zos.write(buffer, 0, len);
        	}
               
        	in.close();
                zos.closeEntry();
    	}
    		
    	
    	//remember close it
    	zos.close();
         fos.flush();
         fos.close();
    	System.out.println("Done");
    }catch(IOException ex){
       ex.printStackTrace();   
    }
   }
    
    /**
     * Traverse a directory and get all files,
     * and add the file into fileList  
     * @param node file or directory
     */
    public void generateFileList(File node){

    	//add file only
	if(node.isFile()){
		fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
	}
		
	if(node.isDirectory()){
		String[] subNote = node.list();
		for(String filename : subNote){
			generateFileList(new File(node, filename));
		}
	}
 
    }

    /**
     * Format the file path for zip
     * @param file file path
     * @return Formatted file path
     */
    private String generateZipEntry(String file){
    	return file.substring(SOURCE_FOLDER.length()+1, file.length());
    }
    
}
