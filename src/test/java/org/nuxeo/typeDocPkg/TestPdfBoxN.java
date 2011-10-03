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
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.PDFTextStripper;
import org.nuxeo.typeDocPkg.PdfDoc;

import junit.framework.TestCase;

public class TestPdfBoxN extends TestCase {

	PDFParser parser;
	String parsedText = null;;
	PDFTextStripper pdfStripper = null;
	PDDocument pdDoc = null;
	COSDocument cosDoc = null;
	File file;
	Integer CurrentPage;

	PdfDoc pdfDoc;

	private boolean setMain(String FileName) throws Exception {
		file = new File(FileName);
		if (!file.isFile()) {
			System.err.println("File " + "test.pdf" + " does not exist.");
			return false;
		}
		try {
			parser = new PDFParser(new FileInputStream(file));
		} catch (IOException e) {
			System.err.println("Unable to open PDF Parser. " + e.getMessage());
			return false;
		}

		try {
			parser.parse();
			cosDoc = parser.getDocument();
			pdfStripper = new PDFTextStripper();
			pdDoc = new PDDocument(cosDoc);
		} catch (Exception e) {
		  return false;
		}

		return true;
	}

   public void create(String message, String outfile) throws IOException, COSVisitorException {
		PDDocument doc = null;
		try {
			doc = new PDDocument();
			PDPage page = new PDPage();
			doc.addPage(page);
			PDPageContentStream contentStream = new PDPageContentStream(doc, page);
			PDFont font = PDType1Font.HELVETICA;
			contentStream.beginText();
			contentStream.setFont(font, 12);
			contentStream.moveTextPositionByAmount(100, 700);
			contentStream.drawString(message);
			contentStream.endText();
			contentStream.close();
			doc.save(outfile);
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	@Override
	public void setUp() throws Exception {
		create("test 1", "test.pdf");
		setMain("test.pdf");
		pdfDoc = new PdfDoc();
	}

	public void testExtractStrFromDoc() throws Exception {
		String actual = pdfDoc.ExtractStrFromDoc("test.pdf");
		assertEquals("test 1", actual.trim());
	}

	public void testNrPages() throws Exception {
		int actual = pdfDoc.NrPages("test.pdf");
		assertEquals(1, actual);
	}

	public void testsetpage() throws Exception {
		pdfDoc.setpage(1);
		assertEquals(1, (int) pdfDoc.currentPage);
	}
}
