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

/**
 * 
 * @export
 * @enum {string}
 */
export enum QueryPageLive2DColor {
    RED = 'RED',
    GREEN = 'GREEN',
    BLUE = 'BLUE'
}

export function QueryPageLive2DColorFromJSON(json: any): QueryPageLive2DColor {
    return QueryPageLive2DColorFromJSONTyped(json, false);
}

export function QueryPageLive2DColorFromJSONTyped(json: any, ignoreDiscriminator: boolean): QueryPageLive2DColor {
    return json as QueryPageLive2DColor;
}

export function QueryPageLive2DColorToJSON(value?: QueryPageLive2DColor | null): any {
    return value as any;
}
