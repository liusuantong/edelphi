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

import { exists, mapValues } from '../runtime';
/**
 * 
 * @export
 * @interface QueryQuestionCommentCategory
 */
export interface QueryQuestionCommentCategory {
    /**
     * Comment category\'s id
     * @type {number}
     * @memberof QueryQuestionCommentCategory
     */
    readonly id?: number;
    /**
     * Comment category\'s name
     * @type {string}
     * @memberof QueryQuestionCommentCategory
     */
    name: string;
    /**
     * Query\'s id where the comment is
     * @type {number}
     * @memberof QueryQuestionCommentCategory
     */
    query_id: number;
    /**
     * Query page\'s id or null if category is query scoped
     * @type {number}
     * @memberof QueryQuestionCommentCategory
     */
    query_page_id?: number;
}

export function QueryQuestionCommentCategoryFromJSON(json: any): QueryQuestionCommentCategory {
    return QueryQuestionCommentCategoryFromJSONTyped(json, false);
}

export function QueryQuestionCommentCategoryFromJSONTyped(json: any, ignoreDiscriminator: boolean): QueryQuestionCommentCategory {
    if ((json === undefined) || (json === null)) {
        return json;
    }
    return {
        
        'id': !exists(json, 'id') ? undefined : json['id'],
        'name': json['name'],
        'query_id': json['queryId'],
        'query_page_id': !exists(json, 'queryPageId') ? undefined : json['queryPageId'],
    };
}

export function QueryQuestionCommentCategoryToJSON(value?: QueryQuestionCommentCategory | null): any {
    if (value === undefined) {
        return undefined;
    }
    if (value === null) {
        return null;
    }
    return {
        
        'name': value.name,
        'queryId': value.query_id,
        'queryPageId': value.query_page_id,
    };
}

