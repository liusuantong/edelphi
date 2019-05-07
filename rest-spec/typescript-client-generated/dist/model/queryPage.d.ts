/**
 * eDelphi REST API
 * REST API for eDelphi
 *
 * OpenAPI spec version: 1.0.1
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { QueryPageLive2DOptions } from './queryPageLive2DOptions';
import { QueryPageType } from './queryPageType';
export interface QueryPage {
    readonly id?: number;
    type?: QueryPageType;
    options?: QueryPageLive2DOptions;
}
export interface QueryPageOpt {
    readonly id?: number;
    type?: QueryPageType;
    options?: QueryPageLive2DOptions;
}