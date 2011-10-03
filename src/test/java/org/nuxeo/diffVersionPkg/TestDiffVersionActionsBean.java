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


package org.nuxeo.diffVersionPkg;

import org.nuxeo.diffVersionPkg.DiffVersionActionsBean;
import org.nuxeo.ecm.core.api.DocumentModel;

import junit.framework.TestCase;

public class TestDiffVersionActionsBean extends TestCase {
	DiffVersionActionsBean cvActBean;
	DocumentModel modelResult;
	
	@Override
	public void setUp() throws Exception {
		cvActBean = new DiffVersionActionsBean();
	}

	public void testsetVersionItemSessionId() throws Exception {
        String actual = cvActBean.setVersionItem("1;1;0.1;123");
        assertEquals("1;1;0.1;123", actual);
	}
	
	public void testisCompareState() throws Exception {
        cvActBean.setVersionItem("1;0.1;1212;1");
        cvActBean.setVersionItem("1;0.2;1212;1");
		boolean result = cvActBean.isCompareState("1212");
		assertTrue(result);
	}
	
	public void testreturnCaption() throws Exception {
        String actual = cvActBean.returnCaption("121", "indietro", "conferma");
        assertEquals("conferma", actual); 
        
        cvActBean.setVersionItem("1;0.1;121;1");
        cvActBean.setVersionItem("1;0.2;121;1");
        actual = cvActBean.returnCaption("121", "indietro", "conferma");
        assertEquals("indietro", actual); 
	}
	
	public void testclear() throws Exception {		
        String actual = cvActBean.returnCaption("121", "indietro", "conferma");
        assertEquals("indietro", actual); 
        cvActBean.clear("121");
        actual = cvActBean.returnCaption("121", "indietro", "conferma");
        assertEquals("conferma", actual); 
	}
	
	public void testHandlePages() throws Exception {
		
        cvActBean.setVersionItem("1;0.1;222;10");

        cvActBean.setNrPages("222", 10);
		String actual = cvActBean.setFirstPageNr("222");
		assertEquals("1", actual);
		assertTrue(cvActBean.isFirstPage("222"));
		
		actual = cvActBean.setLastPageNr("222");
		assertEquals("10", actual);
		assertTrue(cvActBean.isLastPage("222"));

		actual = cvActBean.setPrevPageNr("222");
		assertEquals("9", actual);

		actual = cvActBean.setNextPageNr("222");
		assertEquals("10", actual);
	}
	
	public void testcheckBoxValid(){
		boolean condition = cvActBean.checkBoxValid(modelResult);
		assertFalse(condition);
	}
	
	public void testsetPageNr(){
        cvActBean.setVersionItem("1;0.1;222;10");
        String actual = cvActBean.setPageNr("222;1");
		assertEquals("1", actual);
	}
	
}
