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
export enum ReportType {
    TEXT = 'TEXT',
    SPREADSHEET = 'SPREADSHEET',
    IMAGES = 'IMAGES'
}

export function ReportTypeFromJSON(json: any): ReportType {
    return ReportTypeFromJSONTyped(json, false);
}

export function ReportTypeFromJSONTyped(json: any, ignoreDiscriminator: boolean): ReportType {
    return json as ReportType;
}

export function ReportTypeToJSON(value?: ReportType | null): any {
    return value as any;
}
