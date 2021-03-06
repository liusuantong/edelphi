<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <fmt:message key="panel.admin.manageMaterials.pageTitle">
        <fmt:param>${panel.name}</fmt:param>
      </fmt:message> 
    </title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/ckeditor_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/dragdrop_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/draft_support.jsp"></jsp:include>
    
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_blocks/materiallistingblockcontroller.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/localdocumenteditorblockcontroller.js"></script>
    
    <script type="text/javascript">
      document.observe("dom:loaded", function() {
        initDrafting($('panelAdminDocumentEditorBlock'));
      });
    </script>
  </head>
  <body class="panel_admin index">
    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.createLocalDocument"/></c:set>
    
    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/panel/admin/createlocaldocument.page?panelId=${panel.id}" name="breadcrumbPageUrl"/>
    </jsp:include>

    <div class="GUI_pageWrapper">

      <div class="GUI_pageContainer">
      
        <jsp:include page="/jsp/fragments/page_title.jsp">
	        <jsp:param name="titleLocale" value="panel.admin.manageMaterials.pageTitle"/>
          <jsp:param name="titleLocaleParam" value="${panel.name}"/>
		    </jsp:include>

        <div class="GUI_adminManageMaterialsNarrowColumn">
          <jsp:include page="/jsp/blocks/panel_admin/material_listing.jsp"></jsp:include>
        </div>
        
        <div class="GUI_adminManageMaterialsWideColumn">
          <ed:include page="/jsp/blocks/panel_admin/localdocumenteditor.jsp">
            <ed:param name="localDocumentId" value="${localDocument.id}"/>
            <ed:param name="parentFolderId" value="${panel.rootFolder.id}"/>
            <ed:param name="pageCount" value="${fn:length(localDocumentPages)}"/>
            <c:forEach var="localDocumentPage" items="${localDocumentPages}" varStatus="vs">
              <ed:param name="pageTitle.${vs.index}" value="${localDocumentPage.title}"/>
              <ed:param name="pageContent.${vs.index}" value="${localDocumentPage.content}"/>
            </c:forEach>
          </ed:include>
        </div>
        
        <div class="clearBoth"></div>
        
		  </div>
		    
	  </div>
  </body>
</html>