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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Handle a odt documents.
 *
 * @author Antonio Urso
 */
public class ODTDoc implements GenericDoc {
	   private static StringBuffer textBuffer;
       private Integer nrPages = 0;
       private Integer currentPage = -1;

       private static final Log log = LogFactory.getLog(ODTDoc.class);

	   private  void processElement(Object o) {

	       try {
			   if ((o instanceof Element)) {

		           Element e = (Element) o;
		           String elementName = e.getQualifiedName();


		           if (elementName.startsWith("text")) {

		               if (elementName.equals("text:tab")) {
		                   if (currentPage == nrPages) {
	                           textBuffer.append("\\t");
		                   }
		               } else if (elementName.equals("text:s")) {
		            	   if (currentPage == nrPages) {
	                           textBuffer.append(" ");
		            	   }
		               } else {
		                   List children = e.getContent();
		                   Iterator iterator = children.iterator();

		                   while (iterator.hasNext()) {

		                       Object child = iterator.next();
		                       if (child instanceof Text) {
		                           Text t = (Text) child;
		                           if (currentPage == nrPages) {
	                                   textBuffer.append(t.getValue());
		                           }
		                       } else {
		                       processElement(child);
		                       }
		                   }
		               }
		               if (elementName.equals("text:p")) {
		            	   if (currentPage == nrPages) {
	                           textBuffer.append(" \n");
		            	   }
	                   }
		               if (elementName.equals("text:soft-page-break")) {
		            	   nrPages++;
		               }
		           } else {
		               List<?> nontextlist = e.getContent();
		               Iterator<?> it = nontextlist.iterator();
		               while (it.hasNext()) {
		                   Object nontextchild = it.next();
		                   processElement(nontextchild);
		               }
		           }
	       }
	       } catch (Exception e) {
	    	   log.error("Error during the processElement method: ", e);
	       }
	   }

	   /**
	   * return a number of page of document.
	   *
	   * @param fileName
	   *             name of the file
	   * @return numbers of the page
	   */
	   public Integer NrPages(String fileName) {
                try {
                	nrPages = countNrOfPages(fileName);
                    } catch (IOException e) {
         	    	  log.error("Error during the NrPages method: ", e);
					  return 1;
					} catch (JDOMException e) {
	         	      log.error("Error during the NrPages method: ", e);
					  return 1;
					} catch (Exception e) {
	         	      log.error("Error during the NrPages method: ", e);
					  return 1;
					}
				if (nrPages == 0) {
					nrPages = 2;
				}

                return nrPages;
	   }


	   private Integer countNrOfPages(String fileName) throws IOException, JDOMException {
		       try {
			       textBuffer = new StringBuffer();
			       Integer numPage = 0;
			       ZipFile zipFile = new ZipFile(fileName);

			       Enumeration<?> entries = zipFile.entries();
			       ZipEntry entry;

			       while (entries.hasMoreElements()) {
			           entry = (ZipEntry) entries.nextElement();

			           if (entry.getName().equals("content.xml")) {

			               textBuffer = new StringBuffer();
			               StringBuffer out = new StringBuffer();

			               InputStream inputStream = zipFile.getInputStream(entry);
			               byte buf[] = new byte[1024];
			               while (inputStream.read(buf) > 0) {
			            	   out.append(new String(buf, 0, 1024));
			            	   if (out.indexOf("soft-page-break") > 0) {
			            		   numPage++;
			            	   }
			            	   System.out.print(out);
			            	   out.delete(0, out.length());
			               }
			           }
			       }
			       return numPage;
		       } catch (Exception e) {
	         	   log.error("Error during the countNrOfPages method: ", e);
	         	   return 1;
		       }
		  }

	     /**
		   * set the number page of document.
		   *
		   * @param page
		   *             number of page
		   *
		   */
       public void setpage(Integer page) {
              try {
       	         currentPage = page - 1;
                 nrPages = 0;

              } catch (Exception e) {
          	     currentPage = 1;
                 nrPages = 0;
	         	 log.error("Error during the setpage method: ", e);
              }
       }

	     /**
		   * return a text of the document.
		   *
		   * @param fileName
		   *             nome of file
		   * @return text of the page
		   */
       public String ExtractStrFromDoc(String fileName) {
           try {
        	   if (textBuffer != null) {
        		   textBuffer.delete(0, textBuffer.length());
        	   }
    		   exploratesDom(fileName);
    	       return textBuffer.toString();
           } catch (Exception e) {
	           log.error("Error during the ExtractStrFromDoc method: ", e);
        	   return "";
           }
	   }

      private void exploratesDom(String fileName) throws IOException, JDOMException {
    	   try {
        	   textBuffer = new StringBuffer();

    	       //Unzip the openOffice Document
    	       ZipFile zipFile = new ZipFile(fileName);

    	       Enumeration<?> entries = zipFile.entries();
    	       ZipEntry entry;

    	       while (entries.hasMoreElements()) {
    	           entry = (ZipEntry) entries.nextElement();

    	           if (entry.getName().equals("content.xml")) {

    	               textBuffer = new StringBuffer();
    	               SAXBuilder sax = new SAXBuilder();
    	               Document doc = sax.build(zipFile.getInputStream(entry));
    	               Element rootElement = doc.getRootElement();
    	               processElement(rootElement);
    	               break;
    	           }
    	       }
    	   } catch (Exception e) {
	         log.error("Error during the exploratesDom method: ", e);
    	   }
       }
}
