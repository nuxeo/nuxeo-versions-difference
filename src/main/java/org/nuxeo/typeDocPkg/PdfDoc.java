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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Handle a pdf documents.
 *
 * @author Antonio Urso
 */
public class PdfDoc implements GenericDoc {
	private static final Log log = LogFactory.getLog(PdfDoc.class);
	private PDFParser parser;
	private String parsedText = null;;
	private PDFTextStripper pdfStripper = null;
	private PDDocument pdDoc = null;
	private COSDocument cosDoc = null;
	private File file;
	Integer currentPage = 1;

	private boolean setMain(String FileName) throws Exception {
		file = new File(FileName);
		if (!file.isFile()) {
			System.err.println("File " + "output.pdf" + " does not exist.");
			return false;
		}
		try {
			parser = new PDFParser(new FileInputStream(file));
		} catch (IOException e) {
			log.error("Unable to open PDF Parser. ", e);
			return false;
		}

		try {
			parser.parse();
			cosDoc = parser.getDocument();
			pdfStripper = new PDFTextStripper();
			pdDoc = new PDDocument(cosDoc);
		} catch (Exception e) {
			log.error("error in setMain method ", e);
		  return false;
		}

		return true;
	}

    /**
	   * set the number page of document.
	   *
	   * @param page
	   *             number of page
	   *
	   */
	public void setpage(Integer page) throws Exception {
		currentPage = page;
	}

	   /**
	   * return a number of page of document.
	   *
	   * @param FileName
	   *             name of the file
	   * @return number of pages
	   */
	public Integer NrPages(String FileName) {
		try{
			setMain(FileName);
			return pdDoc.getNumberOfPages();
		} catch (Exception e) {
		  log.error("Error during the NrPages method: ", e);
		  return 1;
		}
	}


    /**
	   * return a text of the document.
	   *
	   * @param fileName
	   *             nome of file
	   * @return text of the page
	   */
	public String ExtractStrFromDoc(String fileName) throws Exception {
		try {
			setMain(fileName);
			pdfStripper.setStartPage(currentPage);
			pdfStripper.setEndPage(currentPage);
			parsedText = pdfStripper.getText(pdDoc);
		} catch (Exception e) {
			log.error("An exception occured in parsing the PDF Document. ", e);
			return "";
		} finally {
			try {
				if (cosDoc != null) {
					cosDoc.close();
				}
				if (pdDoc != null) {
					pdDoc.close();
				}
			  return parsedText;
			} catch (Exception e) {
				log.error("Error during the setPageNr method: ", e);
			}
		}
		return parsedText;
	}

}
