// tslint:disable
// eslint-disable
/**
 * eDelphi REST API
 * REST API for eDelphi
 *
 * The version of the OpenAPI document: 1.0.1
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


import * as runtime from '../runtime';
import {
    ErrorResponse,
    ErrorResponseFromJSON,
    ErrorResponseToJSON,
    QueryPage,
    QueryPageFromJSON,
    QueryPageToJSON,
    QueryPageLive2d,
    QueryPageLive2dFromJSON,
    QueryPageLive2dToJSON,
    QueryPageText,
    QueryPageTextFromJSON,
    QueryPageTextToJSON,
} from '../models';

export interface FindQueryPageLive2dRequest {
    panel_id: number;
    query_page_id: number;
}

export interface FindQueryPageTextRequest {
    panel_id: number;
    query_page_id: number;
}

export interface ListQueryPagesRequest {
    panel_id: number;
    query_id?: number;
    include_hidden?: boolean;
}

export interface UpdateQueryPageLive2dRequest {
    query_page_live2d: QueryPageLive2d;
    panel_id: number;
    query_page_id: number;
}

export interface UpdateQueryPageTextRequest {
    query_page_text: QueryPageText;
    panel_id: number;
    query_page_id: number;
}

/**
 * no description
 */
export class QueryPagesApi extends runtime.BaseAPI {

    /**
     * Finds live2d query page by id
     * Find live2d query page.
     */
    async findQueryPageLive2dRaw(requestParameters: FindQueryPageLive2dRequest): Promise<runtime.ApiResponse<QueryPageLive2d>> {
        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling findQueryPageLive2d.');
        }

        if (requestParameters.query_page_id === null || requestParameters.query_page_id === undefined) {
            throw new runtime.RequiredError('query_page_id','Required parameter requestParameters.query_page_id was null or undefined when calling findQueryPageLive2d.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/live2dQueryPages/{queryPageId}`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))).replace(`{${"queryPageId"}}`, encodeURIComponent(String(requestParameters.query_page_id))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => QueryPageLive2dFromJSON(jsonValue));
    }

    /**
     * Finds live2d query page by id
     * Find live2d query page.
     */
    async findQueryPageLive2d(requestParameters: FindQueryPageLive2dRequest): Promise<QueryPageLive2d> {
        const response = await this.findQueryPageLive2dRaw(requestParameters);
        return await response.value();
    }

    /**
     * Finds a text query page by id
     * Find a text query page.
     */
    async findQueryPageTextRaw(requestParameters: FindQueryPageTextRequest): Promise<runtime.ApiResponse<QueryPageText>> {
        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling findQueryPageText.');
        }

        if (requestParameters.query_page_id === null || requestParameters.query_page_id === undefined) {
            throw new runtime.RequiredError('query_page_id','Required parameter requestParameters.query_page_id was null or undefined when calling findQueryPageText.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/textQueryPages/{queryPageId}`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))).replace(`{${"queryPageId"}}`, encodeURIComponent(String(requestParameters.query_page_id))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => QueryPageTextFromJSON(jsonValue));
    }

    /**
     * Finds a text query page by id
     * Find a text query page.
     */
    async findQueryPageText(requestParameters: FindQueryPageTextRequest): Promise<QueryPageText> {
        const response = await this.findQueryPageTextRaw(requestParameters);
        return await response.value();
    }

    /**
     * Lists query pages
     * Lists query pages.
     */
    async listQueryPagesRaw(requestParameters: ListQueryPagesRequest): Promise<runtime.ApiResponse<Array<QueryPage>>> {
        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling listQueryPages.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        if (requestParameters.query_id !== undefined) {
            queryParameters['queryId'] = requestParameters.query_id;
        }

        if (requestParameters.include_hidden !== undefined) {
            queryParameters['includeHidden'] = requestParameters.include_hidden;
        }

        const headerParameters: runtime.HTTPHeaders = {};

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/queryPages`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))),
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(QueryPageFromJSON));
    }

    /**
     * Lists query pages
     * Lists query pages.
     */
    async listQueryPages(requestParameters: ListQueryPagesRequest): Promise<Array<QueryPage>> {
        const response = await this.listQueryPagesRaw(requestParameters);
        return await response.value();
    }

    /**
     * Updates live2d query page
     * Update live2d query page
     */
    async updateQueryPageLive2dRaw(requestParameters: UpdateQueryPageLive2dRequest): Promise<runtime.ApiResponse<QueryPageLive2d>> {
        if (requestParameters.query_page_live2d === null || requestParameters.query_page_live2d === undefined) {
            throw new runtime.RequiredError('query_page_live2d','Required parameter requestParameters.query_page_live2d was null or undefined when calling updateQueryPageLive2d.');
        }

        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling updateQueryPageLive2d.');
        }

        if (requestParameters.query_page_id === null || requestParameters.query_page_id === undefined) {
            throw new runtime.RequiredError('query_page_id','Required parameter requestParameters.query_page_id was null or undefined when calling updateQueryPageLive2d.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/live2dQueryPages/{queryPageId}`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))).replace(`{${"queryPageId"}}`, encodeURIComponent(String(requestParameters.query_page_id))),
            method: 'PUT',
            headers: headerParameters,
            query: queryParameters,
            body: QueryPageLive2dToJSON(requestParameters.query_page_live2d),
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => QueryPageLive2dFromJSON(jsonValue));
    }

    /**
     * Updates live2d query page
     * Update live2d query page
     */
    async updateQueryPageLive2d(requestParameters: UpdateQueryPageLive2dRequest): Promise<QueryPageLive2d> {
        const response = await this.updateQueryPageLive2dRaw(requestParameters);
        return await response.value();
    }

    /**
     * Updates a text query page
     * Update a text query page
     */
    async updateQueryPageTextRaw(requestParameters: UpdateQueryPageTextRequest): Promise<runtime.ApiResponse<QueryPageText>> {
        if (requestParameters.query_page_text === null || requestParameters.query_page_text === undefined) {
            throw new runtime.RequiredError('query_page_text','Required parameter requestParameters.query_page_text was null or undefined when calling updateQueryPageText.');
        }

        if (requestParameters.panel_id === null || requestParameters.panel_id === undefined) {
            throw new runtime.RequiredError('panel_id','Required parameter requestParameters.panel_id was null or undefined when calling updateQueryPageText.');
        }

        if (requestParameters.query_page_id === null || requestParameters.query_page_id === undefined) {
            throw new runtime.RequiredError('query_page_id','Required parameter requestParameters.query_page_id was null or undefined when calling updateQueryPageText.');
        }

        const queryParameters: runtime.HTTPQuery = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        if (this.configuration && this.configuration.apiKey) {
            headerParameters["Authorization"] = this.configuration.apiKey("Authorization"); // bearer authentication
        }

        const response = await this.request({
            path: `/panels/{panelId}/textQueryPages/{queryPageId}`.replace(`{${"panelId"}}`, encodeURIComponent(String(requestParameters.panel_id))).replace(`{${"queryPageId"}}`, encodeURIComponent(String(requestParameters.query_page_id))),
            method: 'PUT',
            headers: headerParameters,
            query: queryParameters,
            body: QueryPageTextToJSON(requestParameters.query_page_text),
        });

        return new runtime.JSONApiResponse(response, (jsonValue) => QueryPageTextFromJSON(jsonValue));
    }

    /**
     * Updates a text query page
     * Update a text query page
     */
    async updateQueryPageText(requestParameters: UpdateQueryPageTextRequest): Promise<QueryPageText> {
        const response = await this.updateQueryPageTextRaw(requestParameters);
        return await response.value();
    }

}