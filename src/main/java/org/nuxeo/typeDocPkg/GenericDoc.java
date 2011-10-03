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
/**
 * Handle a  documents.
 *
 * @author Antonio Urso
 */
public interface GenericDoc {

	/**
	   * return a text of page of document.
	   *
	   * @param fileName
	   *             name of the file
	   * @return text of the page
	   */
	public String ExtractStrFromDoc(String fileName) throws Exception;

	/**
	   * return a number of pages of document.
	   *
	   * @param fileName
	   *             name of the file
	   * @return number of pages
	   */
	public Integer NrPages(String fileName);

	/**
	   * set the number of page of document.
	   *
	   * @param page
	   *             name of the file
	   *
	   */
	public void setpage(Integer page) throws Exception;
}
