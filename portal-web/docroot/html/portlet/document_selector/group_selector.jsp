<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/html/portlet/document_selector/init.jsp" %>

<%
long groupId = ParamUtil.getLong(request, "groupId");

Group group = GroupLocalServiceUtil.fetchGroup(groupId);

String ckEditorFuncNum = DocumentSelectorUtil.getCKEditorFuncNum(request);
String eventName = ParamUtil.getString(request, "eventName");
boolean showGroupsSelector = ParamUtil.getBoolean(request, "showGroupsSelector");
%>

<liferay-ui:icon-menu direction="down" extended="<%= false %>" icon="<%= StringPool.BLANK %>" localizeMessage="<%= false %>" message="<%= HtmlUtil.escape(group.getDescriptiveName()) %>" showWhenSingleIcon="<%= true %>" triggerCssClass="btn btn-default">

	<%
	String refererPortletName = ParamUtil.getString(request, "refererPortletName");

	PortletURL selectGroupURL = renderResponse.createRenderURL();

	selectGroupURL.setParameter("struts_action", "/document_selector/view");
	selectGroupURL.setParameter("tabs1", "pages");
	selectGroupURL.setParameter("ckEditorFuncNum", ckEditorFuncNum);
	selectGroupURL.setParameter("eventName", eventName);
	selectGroupURL.setParameter("showGroupsSelector", String.valueOf(showGroupsSelector));

	for (Group browsableScopeGroup : PortalUtil.getBrowsableScopeGroups(themeDisplay.getUserId(), themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(), refererPortletName)) {
		selectGroupURL.setParameter("groupId", String.valueOf(browsableScopeGroup.getGroupId()));
	%>

		<liferay-ui:icon
			iconCssClass="<%= browsableScopeGroup.getIconCssClass() %>"
			localizeMessage="<%= false %>"
			message="<%= HtmlUtil.escape(browsableScopeGroup.getDescriptiveName(locale)) %>"
			url="<%= selectGroupURL.toString() %>"
		/>

	<%
	}
	%>

</liferay-ui:icon-menu>