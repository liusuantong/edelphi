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


export interface Panel { 
    readonly id?: number;
    name: string;
    readonly path?: string;
    description?: string;
    accessLevel: Panel.AccessLevelEnum;
    state: Panel.StateEnum;
}
export interface PanelOpt { 
    readonly id?: number;
    name?: string;
    readonly path?: string;
    description?: string;
    accessLevel?: Panel.AccessLevelEnum;
    state?: Panel.StateEnum;
}
export namespace Panel {
    export type AccessLevelEnum = 'OPEN' | 'CLOSED';
    export const AccessLevelEnum = {
        OPEN: 'OPEN' as AccessLevelEnum,
        CLOSED: 'CLOSED' as AccessLevelEnum
    };
    export type StateEnum = 'DESIGN' | 'IN_PROGRESS' | 'ENDED';
    export const StateEnum = {
        DESIGN: 'DESIGN' as StateEnum,
        INPROGRESS: 'IN_PROGRESS' as StateEnum,
        ENDED: 'ENDED' as StateEnum
    };
}
