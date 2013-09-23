<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

<%--   <jsp:include page="/jsp/fragments/block_title.jsp"> --%>
<%--     <jsp:param name="titleLocale" value="index.block.myPanelsBlockTitle"/> --%>
<%--     <jsp:param name="helpText" value="<p><b>Omat paneelit</b> -ohjeistus</p>"/> --%>
<%--   </jsp:include> --%>
  
  <c:set var="reportPageData" value="${reportPageDatas[param.reportPageNumber]}" />
  
  <div class="blockContent">
    <div class="blockContent">
      <h2>${reportPageData.queryPage.title}</h2>

      <c:forEach var="field" items="${reportPageData.fields}">
        <c:choose>
          <c:when test="${field.fieldType eq 'TEXT'}">
            <p>
              <div><b>${field.caption}</b></div>
              <div>${field.value}</div>
            </p>
          </c:when>
          <c:when test="${field.fieldType eq 'LIST'}">
            <ed:queryPageChart output="${param.reportChartFormat}" width="${param.reportChartWidth}" height="${param.reportChartHeight}" queryPageId="${reportPageData.queryPage.id}" stampId="${reportPageData.stamp.id}">
              <ed:param name="queryFieldId" value="${field.optionField.id}"/>
              <c:forEach var="filter" items="${reportReplyFilters}">
                <ed:param name="filter:${filter.type}" value="${filter.value}"/>
              </c:forEach>
            </ed:queryPageChart>
          </c:when>
        </c:choose>
      </c:forEach>

      <!-- Applying of comments -->

      <jsp:include page="/jsp/fragments/query_commentlist.jsp">
        <jsp:param value="${reportPageData.queryPage.id}" name="queryPageId"/>
      </jsp:include>
    </div>
  </div>

</div>

