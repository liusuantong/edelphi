import * as React from "react";
import * as ReactDOM from "react-dom";
import AccessTokenRefresh from "../components/access-token-refresh";
import QueryCommentEditor from "../components/query-comment-editor";
import QueryCommentList from "../components/query-comment-list";
import MqttConnector from "../components/mqtt-connector";
import { createStore } from 'redux';
import { StoreState } from "../types";
import { AppAction } from "../actions";
import { reducer } from "../reducers";
import { Provider } from "react-redux";
import Api from "edelphi-client";
import strings from "../localization/strings";
import "semantic-ui-less/semantic.less";
import Live2dChart from "../components/live-2d-chart";

const location = window.location;

Api.configure(`${location.protocol}//${location.hostname}:${location.port}/api/v1`);
declare function getLocale(): any;
declare const JSDATA: any;

const getAttribute = (element: Element, attributeName: string): string | null => {
  if (!element) {
    return null;
  }

  const attribute = element.attributes.getNamedItem(attributeName);
  if (!attribute) {
    return null;
  }

  return attribute.value;
}

const getIntAttribute = (element: Element, attributeName: string): number | null => {
  const value = getAttribute(element, attributeName);
  return value ? parseInt(value) : null;
}

const getBoolAttribute = (element: Element, attributeName: string): boolean => {
  const value = getAttribute(element, attributeName);
  return value === "true";
}

window.addEventListener('load', () => {
  const locale: string = getLocale().getLanguage();
  strings.setLanguage(locale);

  const queryComments = document.getElementById("query-comments");
  const queryPageLive2D = document.getElementById("query-page-live2d");

  const initalStoreState: StoreState = {
    locale: locale
  };

  const store = createStore<StoreState, AppAction, any, any>(reducer as any, initalStoreState);

  if (queryComments) {
    const panelId: number | null = getIntAttribute(queryComments, "data-panel-id");
    const queryId: number | null = getIntAttribute(queryComments, "data-query-id");
    const pageId: number | null = getIntAttribute(queryComments, "data-page-id");
    const queryReplyId: number | null = getIntAttribute(queryComments, "data-query-reply-id");
    const commentable: boolean = getBoolAttribute(queryComments, "data-commentable");
    const viewDiscussion: boolean = getBoolAttribute(queryComments, "data-view-discussion");
    const canManageComments: boolean = JSDATA['canManageComments'] == 'true';

    if (panelId && queryId && pageId && queryReplyId) {
      const component =
        <Provider store={store}>
          <AccessTokenRefresh />
          <MqttConnector>
            {commentable ? <QueryCommentEditor pageId={pageId} panelId={panelId} queryId={queryId} queryReplyId={queryReplyId} /> : null}
            {viewDiscussion ? <QueryCommentList canManageComments={canManageComments} panelId={panelId} queryId={queryId} pageId={pageId} queryReplyId={queryReplyId} /> : null}
          </MqttConnector>
        </Provider>;

      ReactDOM.render(component, queryComments);
    }
  }

  if (queryPageLive2D) {
    const panelId: number | null = getIntAttribute(queryPageLive2D, "data-panel-id");
    const queryId: number | null = getIntAttribute(queryPageLive2D, "data-query-id");
    const pageId: number | null = getIntAttribute(queryPageLive2D, "data-page-id");
    const queryReplyId: number | null = getIntAttribute(queryPageLive2D, "data-query-reply-id");

    if (panelId && queryId && pageId && queryReplyId) {
      const component =
        <Provider store={store}>
          <AccessTokenRefresh />
          <MqttConnector>
            <Live2dChart pageId={pageId} panelId={panelId} queryId={queryId} queryReplyId={queryReplyId} />
          </MqttConnector>
        </Provider>;

      ReactDOM.render(component, queryPageLive2D);
    }
  }

});