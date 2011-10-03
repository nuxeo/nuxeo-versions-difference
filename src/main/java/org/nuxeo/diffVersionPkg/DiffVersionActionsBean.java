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

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.nuxeo.diffMatchPkg.DiffMatch;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.VersionModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.nuxeo.typeDocPkg.GenericDoc;
import org.nuxeo.typeDocPkg.ODTDoc;
import org.nuxeo.typeDocPkg.PdfDoc;
import org.nuxeo.typeDocPkg.TxtDoc;
import org.nuxeo.typeDocPkg.WordDoc;
/**
 * Deals with versioning actions.
 *
 * @author Antonio Urso
 */

@Name("compareVersionsActions")
@Scope(CONVERSATION)
public class DiffVersionActionsBean implements DiffVersionActions, Serializable {
	
	private static final Log log = LogFactory.getLog(DiffVersionActionsBean.class);
	protected static final int BUFFER_SIZE = 1024 * 512;
    private static Map<String,Integer> Nrpages = new HashMap<String, Integer>();
    private Map<String,String> page = new HashMap<String, String>();
    private static final Map<VersionsMapStrUsr, Boolean>
       versionsWSessionId = new HashMap<VersionsMapStrUsr, Boolean>();
    private static final long serialVersionUID = 75409841629876L;

    @In(create = true)
    protected transient NavigationContext navigationContext;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;


    @Create
    public void initialize() {
     }

    /**
     * set the actual page of document.
     *
     * @param paramStr
     *             this param contain a sessionId
     *             and the page number separated from
     *             ';'
     * @return String
     */
    @WebRemote
    public String setPageNr(String paramStr) {
    	String result = "";
    	try {
    		String[] tokens = paramStr.split(";");
        	for (Map.Entry<VersionsMapStrUsr, Boolean>
        	              entry : versionsWSessionId.entrySet()) {
        		VersionsMapStrUsr vms = entry.getKey();
        		if (vms.getSessionId().toString().equals(
        				               tokens[0].toString())) {
        			vms.setPage(Integer.parseInt(
        					         tokens[1].toString()));
        			result = vms.getPage().toString();
                }
        	}
        	return result;
    	} catch (Exception e) {
            log.error("Error during the setPageNr method: ", e);
    		return "1";
    	}
    }

    /**
     * set the previous page of document.
     *
     * @param sessionId
     *  the session id of the user
     * @return String
     */
    public String setPrevPageNr(String sessionId) {
    	String result = "";
    	try {
        	for (Map.Entry<VersionsMapStrUsr, Boolean>
        	              entry : versionsWSessionId.entrySet()) {
        		VersionsMapStrUsr vms = entry.getKey();
        		if (vms.getSessionId().toString().equals(sessionId)) {
        			if (vms.getPage() <= 1) {
        				setPagePr(sessionId, "1");
        			} else {
            			vms.setPage(vms.getPage() - 1);
            			setPagePr(sessionId, vms.getPage().toString());
            			result = vms.getPage().toString();
        			}
                }
            }
        	return result;
    	} catch (Exception e) {
            log.error("Error during the setPrevPageNr method: ", e);
        	return "1";
    	}
    }

    /**
     * set the next page of document.
     *
     * @param sessionId
     *             the session id of the user
     * @return String
     */
    public String setNextPageNr(String sessionId) {
    	String result = "1";
    	try {
        	for (Map.Entry<VersionsMapStrUsr, Boolean>
        	                 entry : versionsWSessionId.entrySet()) {
        		VersionsMapStrUsr vms = entry.getKey();
        		if (vms.getSessionId().toString().equals(sessionId)) {
        			if (vms.getPage() < getNrPages(sessionId)) {
                		vms.setPage(vms.getPage() + 1);
        			}
        			setPagePr(sessionId, vms.getPage().toString());
            		result = vms.getPage().toString();
                }
            }
        	return result;
    	} catch (Exception e) {
            log.error("Error during the setNextPageNr method: ", e);
        	return "1";
    	}
    }

    /**
     * set the last page of document.
     *
     * @param sessionId
     *             the session id of the user
     * @return String
     */
    public String setLastPageNr(String sessionId) {
    	String result = "1";
    	try {
        	for (Map.Entry<VersionsMapStrUsr, Boolean>
        	              entry : versionsWSessionId.entrySet()) {
        		VersionsMapStrUsr vms = entry.getKey();
        		if (vms.getSessionId().toString().equals(sessionId)) {
                	vms.setPage(getNrPages(sessionId));
                	setPagePr(sessionId, vms.getPage().toString());
            		result = vms.getPage().toString();
                }
            }
        	return result;
    	} catch (Exception e) {
    		log.error("Error during the setLastPageNr method: ", e);
    		return "1";
    	}
    }

    /**
     * set the first page of document.
     *
     * @param sessionId
     *             the session id of the user
     * @return String
     */
    public String setFirstPageNr(String sessionId) {
    	String result = "1";
    	try {
        	for (Map.Entry<VersionsMapStrUsr, Boolean>
        	          entry : versionsWSessionId.entrySet()) {
        		VersionsMapStrUsr vms = entry.getKey();
        		if (vms.getSessionId().toString().equals(sessionId)) {
                	vms.setPage(1);
            		setPagePr(sessionId, vms.getPage().toString());
            		result = vms.getPage().toString();
                }
            }
        	return result;
    	} catch (Exception e) {
    		log.error("Error during the setFirstPageNr method: ", e);
        	return "1";
    	}
    }

    /**
     * set the  page of document.
     *
     * @param sessionId
     *             the session id of the user
     * @param inPage the page to set
     */
	private void setPagePr(String sessionId, String inPage) {
		boolean found = false;
		for (Map.Entry<String, String> entry : this.page.entrySet()) {
			if (entry.getKey().equals(sessionId)) {
				entry.setValue(inPage);
			}
		}
		if (!found) {
			this.page.put(sessionId, inPage);
		}
	}

    /**
     * clear the selection of file to compare.
     *
     * @param sessionId
     *             the session id of the user
     * @return String
     */
    @WebRemote
    public final String clear(final String sessionId) {
    	try {
        	for (Map.Entry<VersionsMapStrUsr, Boolean>
        	                  entry : versionsWSessionId.entrySet()) {
                if (entry.getKey().getSessionId().
                		          toString().equals(sessionId)) {
                	entry.setValue(false);
                }
            }
        	return sessionId;
    	} catch (Exception e) {
    		log.error("Error during the clear method: ", e);
        	return "-1";
    	}
    }

    /**
     * set the item to choose to perform the difference of files.
     *
     * @param sessionId
     *              the session id of the user
     * @param caption1
     *             the caption of button before the show
     *             of difference of files
     * @param caption2
     *             the caption of button after the show
     *             of difference of files
     * @return String
     */
    @WebRemote
    public final String returnCaption(final String sessionId,
    		             final String caption1, final String caption2) {
    	try {
    		if (isCompareState(sessionId)) {
         	   return caption1;
         	} else {
         	   return caption2;
         	}

    	} catch (Exception e) {
    	  log.error("Error during the returnCaption method: ", e);
    	  return caption1;
    	}
    }

    /**
     * set the item to choose to perform the difference of files.
     *
     * @param selectedVersionStr
     *             this param contain a document ref
     *             e version and the sessionId separated from
     *             ';'
     * @return String
     */
    @WebRemote
    public final String setVersionItem(final String selectedVersionStr) {
    	String[] tokens = selectedVersionStr.split(";");

    	try {
        	Integer vPage = Integer.parseInt(tokens[3]);

        	for (Map.Entry<VersionsMapStrUsr, Boolean>
        	              entry : versionsWSessionId.entrySet()) {
        		VersionsMapStrUsr item = entry.getKey();
                if (item.getRef().toString().equals(tokens[0]) && //ref
                	item.getVersion().toString().equals(
                			            tokens[1]) && //version
                	item.getSessionId().toString().equals(
                			            tokens[2])) { //sessionId
                	item.setPage(vPage);
                	entry.setValue(tokens[4].equals("1"));
                	return entry.getKey().toString();
                }
            }
        	versionsWSessionId.put(
        			 new VersionsMapStrUsr(tokens[0],
        					                tokens[1],
        					                 tokens[2],
        					                 vPage), true);
        	return selectedVersionStr;
    	} catch (Exception e) {
      	    log.error("Error during the setVersionItem method: ", e);
    		return "-1";
    	}
    }

    /**
     * check if there are two files to compare.
     *
     * @param sessionId
     *             the session id of the user
     * @return boolean
     */
    public final boolean isCompareState(final String sessionId) {
    	Integer idx = 0;
    	try {
    		for (Map.Entry<VersionsMapStrUsr, Boolean>
    		                 entry : versionsWSessionId.entrySet()) {
    			VersionsMapStrUsr item = entry.getKey();
                if (item.getSessionId().toString().equals(sessionId)
                		&& entry.getValue()) {
                	idx++;
                	if (idx == 2) {
                		return true;
                	}
                }
            }
        	return false;
    	} catch (Exception e) {
      	    log.error("Error during the isCompareState method: ", e);
        	return false;
    	}
    }

    /**
     * return a Document model typer for the version label.
     *
     * @param document
     *             the current document
     * @param label
     *             the label version
     * @return DocumentModel
     */
    public final DocumentModel getItemVersioningHistory(
    			final DocumentModel document, final String label) {
        List<VersionModel> versions = Collections.emptyList();
        try {
            versions = documentManager.getVersionsForDocument(
            					document.getRef());
            for (VersionModel model : versions) {
                if (model.getLabel().equals(label.toString())) {
                	return documentManager.getDocumentWithVersion(
                			    document.getRef(), model);
                }
            }
        } catch (ClientException e) {
      	    log.error("Error during the getItemVersioningHistory method: ", e);
        	return document;
        }
        return document;
    }

    /**
     * return a Document type .
     *
     * @param typeDoc
     *             the current document
     * @return GenericDoc
     */
     private GenericDoc returnDocType(final String typeDoc) {
    	 try  {
    		GenericDoc docAttached;
        	if (typeDoc.toUpperCase().equals("PDF")) {
        		docAttached = new PdfDoc();
        	} else if (typeDoc.toUpperCase().equals("ODT")) {
        		docAttached = new ODTDoc();
        	} else if (typeDoc.toUpperCase().equals("DOC")) {
        		docAttached = new WordDoc();
        	} else if (typeDoc.toUpperCase().equals("TXT")) {
        		docAttached = new TxtDoc();
        	} else {
        		docAttached = new TxtDoc();
        	}
    		return docAttached;

    	} catch (Exception e) {
      	    log.error("Error during the returnDocType method: ", e);
    		return null;
    	}
    }

     /**
      * return a temporany file .
      *
      * @param outPutStr
      *             the stream that contain a information of the file
      * @param ext
      *             the document extention

      * @return File
      */
    private File createTempFile(Blob outPutStr, String ext) {
        try {
        	File tempDir = new File(System.getProperty("java.io.tmpdir"),
            "testExternalBlobDir");
        	if (!tempDir.exists()) {
        		tempDir.mkdir();
        		tempDir.deleteOnExit();
        	}
        	File file = File.createTempFile("testExternalBlob",
        			"." + ext, tempDir);
        	file.deleteOnExit();

        	InputStream in = null;
        	OutputStream out3 = new FileOutputStream(file);

        	in = outPutStr.getStream();
        	byte[] buffer = new byte[BUFFER_SIZE];
        	int read;
        	while ((read = in.read(buffer)) != -1) {
        		out3.write(buffer, 0, read);
        		out3.flush();
        	}
        	return file;

        } catch (Exception e) {
      	    log.error("Error during the createTempFile method: ", e);
            return null;
        }
    }

    /**
     * return  the number of pages.
     *
     */
    @WebRemote
    public Map<String, Object> getListPages() {
    	try {
        	Map<String , Object> result =
        		 new LinkedHashMap<String , Object>();
        	DocumentModel docMod = navigationContext.getCurrentDocument();
        	int lenghtStr =
        		 docMod.getPropertyValue("file:content/name").toString().length() - 3;
        	String TypeDoc =
        		 docMod.getPropertyValue("file:content/name").toString().substring(lenghtStr, lenghtStr + 3);
        	GenericDoc docAttached = returnDocType(TypeDoc);
        	File file =
        		 createTempFile((Blob) docMod.getProperty("file", "content"), TypeDoc);
        	for (Integer i = 1; i < docAttached.NrPages(file.getAbsolutePath()) + 1; i++) {
        		result.put(i.toString(), i.toString());
        	}
        	setNrPages(docMod.getSessionId(), docAttached.NrPages(file.getAbsolutePath()));
        	return result;

    	} catch (Exception e) {
      	    log.error("Error during the getListPages method: ", e);
        	return null;
    	}
    }

    private Integer getNrPages(String sessionId) {
    	try {
        	for (Map.Entry<String, Integer> entry : Nrpages.entrySet()) {
        		if (entry.getKey().equals(sessionId)){
        			return entry.getValue();
        		}
        	}
            return 1;

    	} catch (Exception e) {
      	    log.error("Error during the getNrPages method: ", e);
            return 1;
    	}
    }

    /**
     * set number of pages.
     *
     * @param sessionId
     *             the current document
     * @param nrPages
     *             number of pages
     */
    public void setNrPages(String sessionId, Integer nrPages ) {
    	try {
        	Boolean found = false;
        	for (Map.Entry<String, Integer> entry : Nrpages.entrySet()) {
        		if (entry.getKey().equals(sessionId)){
        			found = true;
        			entry.setValue(nrPages);
        			break;
        		}
        	}
            if (!found) {
            	Nrpages.put(sessionId, nrPages);
            }
    	} catch (Exception e) {
      	    log.error("Error during the setNrPages method: ", e);
    	}
    }

    /**
     * check if the first page.
     *
     * @param sessionId
     *             the current document
     */
    public Boolean isFirstPage(String sessionId) {
    	try {
        	return getPagePr(sessionId).toString().equals("1");
    	} catch (Exception e) {
      	    log.error("Error during the isFirstPage method: ", e);
    		return true;
    	}
    }

    /**
     * check if the last page.
     *
     * @param sessionId
     *             the current document
     */
    public Boolean isLastPage(String sessionId) {
    	try {
    	return getPagePr(sessionId).toString().equals(
    			  getNrPages(sessionId).toString());
    	} catch (Exception e) {
      	    log.error("Error during the isLastPage method: ", e);
      	    return false;
    	}
    }

	private String getPagePr(String sessionId) {
    	return page.get(sessionId);
    }


    /**
     * return a page number.
     *
     */
	public String getPage() {
    	return getPagePr(navigationContext.getCurrentDocument().getSessionId());
    }

    /**
     * set a page number.
     *
     * @param inPage
     *            the page to set
     */
	public void setPage(String inPage) {
		setPagePr(navigationContext.getCurrentDocument().getSessionId(), inPage);
	}

    private String getStrFromFile(DocumentModel docVersion, Integer Currentpage) throws Exception {
    	try {
        	int lenghtStr =
        		docVersion.getPropertyValue("file:content/name").toString().length() - 3;
        	String typeDoc =
        		docVersion.getPropertyValue("file:content/name").toString().substring(lenghtStr, lenghtStr + 3);
        	GenericDoc docAttached = returnDocType(typeDoc);
        	File file = createTempFile((Blob) docVersion.getProperty("file", "content"), typeDoc);
        	docAttached.setpage(Currentpage);
        	return docAttached.ExtractStrFromDoc(file.getAbsolutePath());
    	} catch (Exception e) {
      	  log.error("Error during the getStrFromFile method: ", e);
    	  return "";
    	}
    }

    /**
     * return a Document model typer for the version label.
     *
     * @param doc
     *             the current document
     */
    public Boolean checkBoxValid(DocumentModel doc) {
    	Boolean result = false;
    	try {
    		result = !doc.getPropertyValue("file:content/name").toString().equals("");
    	} catch (Exception e) {
      	    log.error("Error during the checkBoxValid method: ", e);
    		return false;
    	}
    	return result;
    }

    /**
     * return a String that show the difference between two versions of file.
     *
     * @param doc
     *             the current document
     */
    @WebRemote
    public String compareRes(DocumentModel doc) throws Exception {
    	try {
        	DiffMatch dmp = new DiffMatch();
        	String firstFile = "";
        	String secondFile = "";
        	Double firstfileVersion = 0.0;
        	Double secondfileVersion = 0.0;

            for (Map.Entry<VersionsMapStrUsr, Boolean> entry : versionsWSessionId.entrySet()) {

            	VersionsMapStrUsr item = entry.getKey();
            	if (doc.getRef().toString().equals(item.getRef()) &&
            		doc.getSessionId().equals(item.getSessionId())){
                	DocumentModel docVersion = getItemVersioningHistory(doc, item.getVersion());
                	if (entry.getValue()){
              		    if (firstFile.length() == 0) {
                  		    firstFile=getStrFromFile(docVersion, item.getPage());
              		    	firstfileVersion = Double.parseDouble(item.getVersion()) * 10.0;
              		    } else {
              			   if (secondFile.length() == 0) {
                			   secondFile =
                				   getStrFromFile(docVersion, item.getPage());
              				   secondfileVersion =
              					   Double.parseDouble(item.getVersion()) * 10.0;
              			   }
              		    }

              		    if ((secondFile.length() > 0) && (firstFile.length() > 0)) {
              		      LinkedList<DiffMatch.Diff> diffs = null;

              		      if (firstfileVersion > secondfileVersion) {
              			      diffs =
              			    	  dmp.diff_main(secondFile, firstFile);
              		      } else {
              		    	  diffs = dmp.diff_main(firstFile, secondFile);
              		      }
                      	  return dmp.diff_prettyHtml(diffs);
              		    }
              	    }
            	}
        	}
            return "";
    	} catch (Exception e) {
      	    log.error("Error during the compareRes method: ", e);
            return "";
    	}
    }
}
