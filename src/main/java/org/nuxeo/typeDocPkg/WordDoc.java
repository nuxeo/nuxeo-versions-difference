/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Davide Taibi & Antonio Urso
 *     Università dell'Insubria
 */


package org.nuxeo.typeDocPkg;

import java.io.FileInputStream;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Handle a odt documents.
 *
 * @author Antonio Urso
 */
public class WordDoc implements GenericDoc {
	private static final Log log = LogFactory.getLog(WordDoc.class);
	private Integer CurrentPage = 1;
    private Integer LengthOfPage= 800;

	   /**
	   * return a number of page of document.
	   *
	   * @param filename
	   *             name of the file
	   * @return number of pages
	   */
    public Integer NrPages(String filename){
    	try {
        	Integer result = 0;
        	Integer counterChar = 0;

        	HWPFDocument doc = getHWPFDocument(filename);
        	Range r = doc.getRange();
        	for (int k = 0; k < r.numParagraphs(); k++) {
        		Paragraph p = r.getParagraph(k);
        		counterChar += p.text().length();
        		if (counterChar > LengthOfPage) {
        			result++;
        			counterChar = 0;
        		}
        	}
        	return result == 0 ? 1 : result;
    	} catch (Exception e) {
    	  log.error("Error during the NrPages method: ", e);
    	  return 1;
    	}
	}


    private HWPFDocument getHWPFDocument(String filename){
  	  POIFSFileSystem fs = null;
        try {
             fs = new POIFSFileSystem(new FileInputStream(filename));

             return  new HWPFDocument(fs);

           } catch (Exception e) {
         	  log.error("Error during the getHWPFDocument method: ", e);
         	  return null;
           }
     }

    /**
	   * set the number page of document.
	   *
	   * @param page
	   *             number of page
	   *
	   */
	public void setpage(Integer page) throws Exception{
		CurrentPage = page;
	}

    /**
	   * return a text of the document.
	   *
	   * @param filename
	   *             name of file
	   * @return text of page
	   */
	public String ExtractStrFromDoc(String filename){
      String result = "";
      try {
          Integer counterChar = 0;
          Integer nrPages = 1;

          HWPFDocument doc = getHWPFDocument(filename);
          Range r = doc.getRange();

          for (int k = 0; k < r.numParagraphs(); k++) {
              Paragraph p = r.getParagraph(k);
              counterChar += p.text().length();
              if (nrPages == CurrentPage) {
            	  result = result.concat(p.text());
              }
              if (counterChar > LengthOfPage) {
            	nrPages++;
            	if (nrPages > CurrentPage) {
            		return result;
            	}
              	counterChar = 0;
              	result = "";
              }
          }

          return result;
     } catch (Exception e) {
    	 log.error("Error during the ExtractStrFromDoc method: ", e);
         return "";
     }
	}
}

