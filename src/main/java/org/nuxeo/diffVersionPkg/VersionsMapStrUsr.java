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
/**
 * class to handle the versions of document attached.
 *
 * @author Antonio Urso
 */
public class VersionsMapStrUsr { 
 private String ref = "";
 private String version = "";
 private String sessionId = "";
 private Integer page = 0;

 /**
  * initialization of class.
  *
  * @param ref  reference of document
  * @param version version of document
  * @param sessionId sessionId of connection
  * @param page page of document
  */
 public VersionsMapStrUsr(String ref, String version, String sessionId, Integer page) {
  setRef(ref);
  setVersion(version);
  setSessionId(sessionId);
  setPage(page);
 }

 /**
  * set the reference document.
  *
  * @param inRef
  *  the reference of document
  * @return String
  */
 public void setRef(String inRef) {
  ref = inRef;
 }

 /**
  * set the version document.
  *
  * @param inVersion
  *  the reference of document
  */
 public void setVersion(String inVersion) {
  version = inVersion;
 }
 
 /**
  * set the sessionId.
  *
  * @param inSessionId
  *  the sessionId of internet connection
  */
 public void setSessionId(String inSessionId) {
  sessionId = inSessionId;
 }

 /**
  * set the current page of  document.
  *
  * @param inPage
  *  the current page of document
  */
 public void setPage(Integer inPage) {
  page = inPage;
 }

 /**
  * return the reference of document.
  *
  * @return String
  */
 public String  getRef() {
  return ref;
 }

 /**
  * return the version of document.
  *
  * @return String
  */
 public String getVersion() {
  return version;
 }
 
 /**
  * return the sessionId linked to the document.
  *
  * @return String
  */
 public String getSessionId() {
  return sessionId;
 }

 /**
  * return the current page of  document.
  *
  * @return String
  */
 public Integer getPage(){
	 return page;
 }
}
