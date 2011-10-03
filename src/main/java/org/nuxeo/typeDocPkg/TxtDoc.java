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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handle a txt documents.
 *
 * @author Antonio Urso
 */
public class TxtDoc implements GenericDoc {
	  Integer currentPage = 1;
	  private static final Log log = LogFactory.getLog(TxtDoc.class);

	  /**
	   * return a number of page of document.
	   *
	   * @param fileName
	   *             name of the file
	   * @return number of pages
	   */
	  public Integer NrPages(String fileName){
			 return 1;
	  }

	  /**
	   * set the number page of document.
	   *
	   * @param page
	   *             number of page
	   *
	   */
	  public void setpage(Integer page) throws Exception{
			currentPage = 1;
	  }

	  /**
	   * return a text of the document.
	   *
	   * @param fileName
	   *             nome of file
	   * @return text of the page
	   */
	  public String ExtractStrFromDoc(String filename) throws IOException{
		  try {
			  FileReader input = new FileReader(filename);
			  BufferedReader bufRead = new BufferedReader(input);

			  String strLine = "";
			  String result = "";
			  while ((strLine = bufRead.readLine()) != null) {
				  result = result.concat(" \n" + strLine);
			  }
			  bufRead.close();
			  return result;
		  } catch (Exception e) {
			  log.error("Error during the ExtractStrFromDoc method: ", e);
			  return "";
		  }
	}

}
