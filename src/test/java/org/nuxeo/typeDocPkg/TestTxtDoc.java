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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.nuxeo.typeDocPkg.TxtDoc;



import junit.framework.TestCase;

public class TestTxtDoc extends TestCase {
	TxtDoc txtDoc;

    public void create(String Filename) throws IOException {
		  //File file = new File(Filename);
		  FileWriter input = new FileWriter(Filename);
		  BufferedWriter bufWrite = new BufferedWriter(input);

		  bufWrite.write("test 1");
		  bufWrite.close();
    }

	@Override
	public void setUp() throws Exception {
		//this.setUp();
		txtDoc = new TxtDoc();
		create("test.txt");
	}

	public void testExtractStrFromDoc() throws Exception {
		String actual = txtDoc.ExtractStrFromDoc("test.txt");
		assertTrue("test 1".equals(actual.trim().toString()));
	}

	public void testNrPages() throws Exception {
		int actual = txtDoc.NrPages("test.txt");
		assertEquals(1, actual);
	}
}
